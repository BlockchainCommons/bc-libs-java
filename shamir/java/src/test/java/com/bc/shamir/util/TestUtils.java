package com.bc.shamir.util;

import java.math.BigInteger;

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

    public static <T extends Throwable> T assertThrows(Class<T> expectedError, Callable callable) {
        return assertThrows("test failed", expectedError, callable);
    }
}
