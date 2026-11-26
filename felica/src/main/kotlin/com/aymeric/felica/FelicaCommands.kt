package com.aymeric.felica

class FelicaException(message: String) : Exception(message)

/**
 * Builds and parses raw FeliCa frames (JIS X 6319-4) for the commands this app needs.
 *
 * On Android the frames are sent with `NfcF.transceive()`, which expects the length
 * byte at the start of the frame but adds and strips the preamble, sync code and CRC
 * itself.
 */
object FelicaCommands {
    const val IDM_LENGTH = 8
    const val BLOCK_SIZE = 16

    /** Upper bound used by this app; cards may accept fewer blocks per command. */
    const val MAX_BLOCKS_PER_READ = 15

    private const val READ_WITHOUT_ENCRYPTION = 0x06
    private const val READ_WITHOUT_ENCRYPTION_RESPONSE = 0x07

    /** Block list element flag: 2-byte element, access mode 0, service list index 0. */
    private const val BLOCK_ELEMENT_2_BYTES = 0x80

    /**
     * Read Without Encryption for a single service.
     *
     * Frame: LEN | 0x06 | IDm(8) | number of services | service code (LE) |
     *        number of blocks | block list (2 bytes per block)
     */
    fun readWithoutEncryption(idm: ByteArray, serviceCode: Int, blocks: List<Int>): ByteArray {
        require(idm.size == IDM_LENGTH) { "IDm must be $IDM_LENGTH bytes, got ${idm.size}" }
        require(serviceCode in 0..0xFFFF) { "Service code out of range: $serviceCode" }
        require(blocks.size in 1..MAX_BLOCKS_PER_READ) {
            "Between 1 and $MAX_BLOCKS_PER_READ blocks per read, got ${blocks.size}"
        }
        require(blocks.all { it in 0..0xFF }) { "Block numbers must fit in one byte" }

        val length = 14 + 2 * blocks.size
        val frame = ByteArray(length)
        var i = 0
        frame[i++] = length.toByte()
        frame[i++] = READ_WITHOUT_ENCRYPTION.toByte()
        idm.copyInto(frame, i)
        i += IDM_LENGTH
        frame[i++] = 1
        frame[i++] = (serviceCode and 0xFF).toByte()
        frame[i++] = (serviceCode shr 8 and 0xFF).toByte()
        frame[i++] = blocks.size.toByte()
        for (block in blocks) {
            frame[i++] = BLOCK_ELEMENT_2_BYTES.toByte()
            frame[i++] = block.toByte()
        }
        return frame
    }

    /**
     * Parses a Read Without Encryption response and returns the 16-byte blocks.
     *
     * Frame: LEN | 0x07 | IDm(8) | status flag 1 | status flag 2 |
     *        number of blocks | block data (16 bytes per block)
     *
     * The number of blocks and the data are only present when status flag 1 is 0x00.
     */
    fun parseReadResponse(response: ByteArray, expectedIdm: ByteArray): List<ByteArray> {
        if (response.size < 12) throw FelicaException("Response too short: ${response.size} bytes")
        if (response[0].toUnsigned() != response.size) {
            throw FelicaException("Length byte ${response[0].toUnsigned()} does not match ${response.size}")
        }
        if (response[1].toUnsigned() != READ_WITHOUT_ENCRYPTION_RESPONSE) {
            throw FelicaException("Unexpected response code 0x%02X".format(response[1].toUnsigned()))
        }
        if (!response.copyOfRange(2, 2 + IDM_LENGTH).contentEquals(expectedIdm)) {
            throw FelicaException("Response comes from another card")
        }

        val status1 = response[10].toUnsigned()
        val status2 = response[11].toUnsigned()
        if (status1 != 0) {
            throw FelicaException("Card returned status 0x%02X 0x%02X".format(status1, status2))
        }
        if (response.size < 13) throw FelicaException("Missing block count")

        val count = response[12].toUnsigned()
        val expectedSize = 13 + count * BLOCK_SIZE
        if (response.size != expectedSize) {
            throw FelicaException("Expected $expectedSize bytes for $count blocks, got ${response.size}")
        }
        return List(count) { index ->
            val start = 13 + index * BLOCK_SIZE
            response.copyOfRange(start, start + BLOCK_SIZE)
        }
    }
}

internal fun Byte.toUnsigned(): Int = toInt() and 0xFF

fun ByteArray.toHex(): String = joinToString("") { "%02X".format(it.toUnsigned()) }
