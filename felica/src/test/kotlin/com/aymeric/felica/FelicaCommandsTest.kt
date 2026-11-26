package com.aymeric.felica

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FelicaCommandsTest {

    @Test
    fun `read command has the expected layout`() {
        val frame = FelicaCommands.readWithoutEncryption(
            idm = TEST_IDM,
            serviceCode = SuicaHistoryParser.HISTORY_SERVICE_CODE,
            blocks = listOf(0, 1)
        )

        val expected = bytes(
            0x12,                                           // length: 18 bytes
            0x06,                                           // Read Without Encryption
            0x01, 0x14, 0xB3, 0x0A, 0x52, 0x0F, 0x77, 0x2C, // IDm
            0x01,                                           // one service
            0x0F, 0x09,                                     // service code 0x090F, little endian
            0x02,                                           // two blocks
            0x80, 0x00,                                     // block 0
            0x80, 0x01                                      // block 1
        )
        assertArrayEquals(expected, frame)
    }

    @Test
    fun `read command length grows by two bytes per block`() {
        val frame = FelicaCommands.readWithoutEncryption(TEST_IDM, 0x090F, (0 until 10).toList())
        assertEquals(34, frame.size)
        assertEquals(34, frame[0].toInt())
    }

    @Test
    fun `read command rejects a bad IDm or block list`() {
        assertThrows(IllegalArgumentException::class.java) {
            FelicaCommands.readWithoutEncryption(ByteArray(4), 0x090F, listOf(0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            FelicaCommands.readWithoutEncryption(TEST_IDM, 0x090F, emptyList())
        }
        assertThrows(IllegalArgumentException::class.java) {
            FelicaCommands.readWithoutEncryption(TEST_IDM, 0x090F, (0 until 16).toList())
        }
    }

    @Test
    fun `response blocks are extracted in order`() {
        val first = ByteArray(16) { 0xA0.toByte() }
        val second = ByteArray(16) { it.toByte() }
        val response = response(status1 = 0, blocks = listOf(first, second))

        val blocks = FelicaCommands.parseReadResponse(response, TEST_IDM)

        assertEquals(2, blocks.size)
        assertArrayEquals(first, blocks[0])
        assertArrayEquals(second, blocks[1])
    }

    @Test
    fun `error status is reported`() {
        val response = bytes(0x0C, 0x07, *TEST_IDM.toIntArray(), 0x01, 0xA2)

        val error = assertThrows(FelicaException::class.java) {
            FelicaCommands.parseReadResponse(response, TEST_IDM)
        }
        assertEquals("Card returned status 0x01 0xA2", error.message)
    }

    @Test
    fun `response from another card is rejected`() {
        val response = response(status1 = 0, blocks = listOf(ByteArray(16)))
        val otherIdm = ByteArray(8) { 0x7F }

        assertThrows(FelicaException::class.java) {
            FelicaCommands.parseReadResponse(response, otherIdm)
        }
    }

    @Test
    fun `truncated response is rejected`() {
        val response = response(status1 = 0, blocks = listOf(ByteArray(16)))
        val truncated = response.copyOf(response.size - 3).also { it[0] = it.size.toByte() }

        assertThrows(FelicaException::class.java) {
            FelicaCommands.parseReadResponse(truncated, TEST_IDM)
        }
    }

    private fun response(status1: Int, blocks: List<ByteArray>): ByteArray {
        val size = 13 + 16 * blocks.size
        val header = bytes(size, 0x07, *TEST_IDM.toIntArray(), status1, 0x00, blocks.size)
        return blocks.fold(header) { acc, block -> acc + block }
    }

    private fun ByteArray.toIntArray(): IntArray = IntArray(size) { this[it].toInt() and 0xFF }
}
