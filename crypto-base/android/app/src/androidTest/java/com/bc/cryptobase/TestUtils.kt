package com.bc.cryptobase

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

fun Long.toBigEndian() = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(this).array()

fun ByteArray.removeLeadingZeros() = this.dropWhile { it == 0.toByte() }.toByteArray()