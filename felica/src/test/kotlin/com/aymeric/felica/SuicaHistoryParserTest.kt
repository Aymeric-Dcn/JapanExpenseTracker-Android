package com.aymeric.felica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SuicaHistoryParserTest {

    @Test
    fun `block fields are decoded`() {
        val block = historyBlock(
            processCode = 0x01, year = 2026, month = 11, day = 26,
            balanceYen = 1234, sequence = 0x0142
        )

        val entry = SuicaHistoryParser.decodeBlock(block)!!

        assertEquals(CardDate(2026, 11, 26), entry.date)
        assertEquals(1234, entry.balanceYen)
        assertEquals(0x0142, entry.sequence)
        assertEquals(ProcessType.FARE, entry.processType)
    }

    @Test
    fun `balance is little endian`() {
        // 0xD2 0x04 -> 0x04D2 = 1234 yen; read big endian it would be 53764.
        val block = historyBlock(0x46, 2026, 1, 1, balanceYen = 1234, sequence = 1)
        assertEquals(0xD2.toByte(), block[10])
        assertEquals(0x04.toByte(), block[11])
        assertEquals(1234, SuicaHistoryParser.decodeBlock(block)!!.balanceYen)
    }

    @Test
    fun `date edge values are decoded`() {
        assertEquals(CardDate(2000, 1, 1), SuicaHistoryParser.decodeDate(0x00, 0x21))
        assertEquals(CardDate(2127, 12, 31), SuicaHistoryParser.decodeDate(0xFF, 0x9F))
    }

    @Test
    fun `bit 7 of the process type is ignored`() {
        val block = historyBlock(0x80 or 0x02, 2026, 3, 4, balanceYen = 3000, sequence = 9)
        val entry = SuicaHistoryParser.decodeBlock(block)!!
        assertEquals(0x02, entry.processCode)
        assertEquals(ProcessType.CHARGE, entry.processType)
    }

    @Test
    fun `empty blocks are skipped`() {
        assertNull(SuicaHistoryParser.decodeBlock(ByteArray(16)))
    }

    @Test
    fun `amounts are the balance difference with the previous transaction`() {
        // Newest first, like on the card.
        val blocks = listOf(
            historyBlock(0x46, 2026, 11, 26, balanceYen = 1530, sequence = 12), // shop: -450
            historyBlock(0x02, 2026, 11, 25, balanceYen = 1980, sequence = 11), // top-up: +1000
            historyBlock(0x01, 2026, 11, 25, balanceYen = 980, sequence = 10),  // train: -220
            historyBlock(0x01, 2026, 11, 24, balanceYen = 1200, sequence = 9),  // oldest
            ByteArray(16)
        )

        val card = SuicaHistoryParser.parse(TEST_IDM, blocks)

        assertEquals("0114B30A520F772C", card.idm)
        assertEquals(1530, card.balanceYen)
        assertEquals(listOf(-450, 1000, -220, null), card.history.map { it.amountYen })
        assertTrue(card.history[0].isPayment)
        assertFalse(card.history[1].isPayment)
        assertFalse(card.history[3].isPayment)
    }

    @Test
    fun `card without history has no balance`() {
        val card = SuicaHistoryParser.parse(TEST_IDM, List(10) { ByteArray(16) })
        assertNull(card.balanceYen)
        assertTrue(card.history.isEmpty())
    }

    @Test
    fun `unknown process codes map to OTHER`() {
        assertEquals(ProcessType.OTHER, ProcessType.fromCode(0x7E))
        assertEquals(ProcessType.BUS, ProcessType.fromCode(0x0D))
        assertEquals(ProcessType.PURCHASE, ProcessType.fromCode(0x46))
    }
}
