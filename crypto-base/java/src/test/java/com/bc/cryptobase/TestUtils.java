package com.bc.cryptobase;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestUtils {

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] hex2Bytes(String hex) {
        byte[] ret = new BigInteger(hex, 16).toByteArray();
        if (hex.length() % 2 == 0 && hex.startsWith("00")) {
            return concatenate(new byte[]{0x00}, ret);
        }
        return ret;
    }

    public static byte[] concatenate(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] toBigEndian(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(value).array();
    }

    public static byte[] removeLeadingZeros(byte[] array) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                index = i;
                break;
            }
        }

        if (index > 0) {
            int len = array.length - index;
            byte[] ret = new byte[len];
            System.arraycopy(array, index, ret, 0, len);
            return ret;
        }
        return array;
    }
}
