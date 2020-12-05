package com.bc.shamir

import java.math.BigInteger

fun bytes2Hex(bytes: ByteArray): String {
    val builder = StringBuilder()
    for (b in bytes) {
        builder.append(String.format("%02x", b))
    }
    return builder.toString()
}

fun hex2Bytes(hex: String): ByteArray {
    val bytes = BigInteger(hex, 16).toByteArray()
    if (hex.length % 2 == 0 && hex.startsWith("00")) {
        return byteArrayOf(0x00) + bytes
    }
    return bytes
}

inline fun <reified T : Throwable> assertThrows(
    msg: String = "test failed",
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