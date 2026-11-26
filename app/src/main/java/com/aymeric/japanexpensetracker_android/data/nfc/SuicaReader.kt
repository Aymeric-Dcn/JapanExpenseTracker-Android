package com.aymeric.japanexpensetracker_android.data.nfc

import android.nfc.Tag
import android.nfc.tech.NfcF
import com.aymeric.felica.FelicaCommands
import com.aymeric.felica.FelicaException
import com.aymeric.felica.SuicaCard
import com.aymeric.felica.SuicaHistoryParser

/**
 * Reads the balance and history of a transit IC card through Android's NfcF API.
 * All protocol logic lives in the pure `felica` module; this class only does the I/O.
 *
 * Blocking: call it from a background thread.
 */
class SuicaReader {

    fun read(tag: Tag): SuicaCard {
        val nfcF = NfcF.get(tag) ?: throw FelicaException("This card is not a FeliCa card")
        val idm = tag.id

        return nfcF.use { card ->
            card.connect()
            card.timeout = TIMEOUT_MS

            val blocks = mutableListOf<ByteArray>()
            for (first in 0 until SuicaHistoryParser.HISTORY_BLOCK_COUNT step BLOCKS_PER_READ) {
                val blockNumbers = (first until first + BLOCKS_PER_READ).toList()
                val command = FelicaCommands.readWithoutEncryption(
                    idm, SuicaHistoryParser.HISTORY_SERVICE_CODE, blockNumbers
                )
                try {
                    blocks += FelicaCommands.parseReadResponse(card.transceive(command), idm)
                } catch (e: FelicaException) {
                    // Some cards keep fewer than 20 entries: keep what was already read.
                    if (blocks.isEmpty()) throw e
                    break
                }
            }
            SuicaHistoryParser.parse(idm, blocks)
        }
    }

    private companion object {
        const val BLOCKS_PER_READ = 10
        const val TIMEOUT_MS = 1000
    }
}
