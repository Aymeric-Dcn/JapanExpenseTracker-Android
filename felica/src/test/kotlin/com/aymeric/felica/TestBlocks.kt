package com.aymeric.felica

/** Builds history blocks in the card layout, so tests do not depend on a real card dump. */
internal fun historyBlock(
    processCode: Int,
    year: Int,
    month: Int,
    day: Int,
    balanceYen: Int,
    sequence: Int,
    terminalType: Int = 0x16
): ByteArray {
    val date = ((year - 2000) shl 9) or (month shl 5) or day
    return ByteArray(16).also {
        it[0] = terminalType.toByte()
        it[1] = processCode.toByte()
        it[4] = (date shr 8).toByte()
        it[5] = date.toByte()
        it[10] = balanceYen.toByte()
        it[11] = (balanceYen shr 8).toByte()
        it[13] = (sequence shr 8).toByte()
        it[14] = sequence.toByte()
    }
}

internal fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { values[it].toByte() }

internal val TEST_IDM = bytes(0x01, 0x14, 0xB3, 0x0A, 0x52, 0x0F, 0x77, 0x2C)
