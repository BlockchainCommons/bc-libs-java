package com.bc.bytewords.util;

import java.util.Arrays;

public class TestUtils {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(String msg,
                                                       Class<T> expectedError,
                                                       Callable callable) {
        try {
            callable.call();
            throw new AssertionError(msg);
        } catch (Throwable e) {
            if (expectedError.isAssignableFrom(e.getClass()))
                return (T) e;
            throw e;
        }
    }

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            int firstIndex = ((int) b & 0xF0) >> 4;
            int secondIndex = (int) b & 0x0F;
            result.append(HEX_CHARS[firstIndex]);
            result.append(HEX_CHARS[secondIndex]);
        }

        return result.toString();
    }

    public static byte[] hex2Bytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) +
                                  Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] slice(byte[] bytes, int count) {
        return slice(bytes, 0, count);
    }

    public static byte[] slice(byte[] bytes, int start, int end) {
        return Arrays.copyOfRange(bytes, start, end);
    }
}
