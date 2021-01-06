package com.bc.bip39.util

val HEX_CHARS = "0123456789abcdef".toCharArray()

fun ByteArray.toHex(): String {

    val result = StringBuilder()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun String.hex2Bytes(): ByteArray {
    val data = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        data[i / 2] =
            ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
    }
    return data
}

inline fun <reified T : Throwable> assertThrows(
    msg: String = "",
    callable: () -> Unit
): T {
    try {
        callable()
        throw AssertionError(msg)
    } catch (e: Throwable) {
        if (e is T) return e
        throw e
    }
}

fun slice(bytes: ByteArray, count: Int): ByteArray {
    return slice(bytes, 0, count)
}

fun slice(bytes: ByteArray, start: Int, end: Int): ByteArray {
    return bytes.copyOfRange(start, end)
}