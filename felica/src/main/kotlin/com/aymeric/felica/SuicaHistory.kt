package com.aymeric.felica

/** A calendar date as stored on the card. java.time is avoided so the code runs on Android API 24. */
data class CardDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = "%04d-%02d-%02d".format(year, month, day)
}

/**
 * Kind of transaction, from the "process type" byte of a history block.
 *
 * The codes are not published by JR East; this mapping follows the tables documented
 * by the FeliCa reverse-engineering community and only keeps the common cases.
 */
enum class ProcessType(val label: String) {
    FARE("Train fare"),
    BUS("Bus fare"),
    CHARGE("Top-up"),
    PURCHASE("Purchase"),
    OTHER("Other");

    companion object {
        private val FARE_CODES = setOf(0x01, 0x03, 0x04, 0x05, 0x13, 0x23)
        private val BUS_CODES = setOf(0x0D, 0x0F)
        private val CHARGE_CODES = setOf(0x02, 0x14, 0x15, 0x1F, 0x49)
        private val PURCHASE_CODES = setOf(0x46, 0x4B)

        fun fromCode(code: Int): ProcessType = when (code) {
            in FARE_CODES -> FARE
            in BUS_CODES -> BUS
            in CHARGE_CODES -> CHARGE
            in PURCHASE_CODES -> PURCHASE
            else -> OTHER
        }
    }
}

data class SuicaHistoryEntry(
    /** Transaction counter written by the terminal. */
    val sequence: Int,
    val date: CardDate,
    val processCode: Int,
    val processType: ProcessType,
    /** Card balance right after this transaction, in yen. */
    val balanceYen: Int,
    /**
     * Balance change caused by this transaction, in yen: negative for a payment,
     * positive for a top-up. Null for the oldest entry, which has no previous balance.
     */
    val amountYen: Int?
) {
    val isPayment: Boolean get() = amountYen != null && amountYen < 0
}

data class SuicaCard(
    val idm: String,
    /** Current balance: the balance of the most recent history entry. */
    val balanceYen: Int?,
    /** Most recent entry first. */
    val history: List<SuicaHistoryEntry>
)

/**
 * Decodes the history service of Japanese transit IC cards (Suica, Pasmo, ICOCA...).
 *
 * Layout of one 16-byte history block, as documented by the community:
 *
 * | Byte  | Content                                         |
 * |-------|-------------------------------------------------|
 * | 0     | terminal type                                   |
 * | 1     | process type (bit 7 is a flag, bits 0-6 = code) |
 * | 2-3   | payment / entry-exit types                      |
 * | 4-5   | date, big endian: yyyyyyym mmmddddd (year 2000+)|
 * | 6-9   | stations or time, depending on the process type |
 * | 10-11 | balance after the transaction, little endian    |
 * | 12    | unknown                                         |
 * | 13-14 | sequence number, big endian                     |
 * | 15    | region                                          |
 *
 * Reading this service needs no key, which is why any NFC phone can show the balance.
 */
object SuicaHistoryParser {
    /** Service code of the history, readable without encryption. */
    const val HISTORY_SERVICE_CODE = 0x090F

    /** Suica keeps the last 20 transactions. */
    const val HISTORY_BLOCK_COUNT = 20

    fun parse(idm: ByteArray, blocks: List<ByteArray>): SuicaCard {
        val decoded = blocks.mapNotNull(::decodeBlock)
        val history = decoded.mapIndexed { index, entry ->
            val previous = decoded.getOrNull(index + 1)
            entry.copy(amountYen = previous?.let { entry.balanceYen - it.balanceYen })
        }
        return SuicaCard(
            idm = idm.toHex(),
            balanceYen = history.firstOrNull()?.balanceYen,
            history = history
        )
    }

    /** Returns null for an unused (all zero) block. */
    fun decodeBlock(block: ByteArray): SuicaHistoryEntry? {
        require(block.size == FelicaCommands.BLOCK_SIZE) { "History blocks are 16 bytes, got ${block.size}" }
        if (block.all { it == 0.toByte() }) return null

        val processCode = block[1].toUnsigned() and 0x7F
        return SuicaHistoryEntry(
            sequence = (block[13].toUnsigned() shl 8) or block[14].toUnsigned(),
            date = decodeDate(block[4].toUnsigned(), block[5].toUnsigned()),
            processCode = processCode,
            processType = ProcessType.fromCode(processCode),
            balanceYen = block[10].toUnsigned() or (block[11].toUnsigned() shl 8),
            amountYen = null
        )
    }

    internal fun decodeDate(high: Int, low: Int): CardDate {
        val value = (high shl 8) or low
        return CardDate(
            year = 2000 + (value shr 9 and 0x7F),
            month = value shr 5 and 0x0F,
            day = value and 0x1F
        )
    }
}
