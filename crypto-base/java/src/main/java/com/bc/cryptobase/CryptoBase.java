package com.bc.cryptobase;

public class CryptoBase {

    public static byte[] sha256(byte[] message) {
        final byte[] digest = new byte[32];
        CryptoBaseJni.sha256_raw(message, digest);
        return digest;
    }

    public static byte[] sha512(byte[] message) {
        final byte[] digest = new byte[64];
        CryptoBaseJni.sha512_raw(message, digest);
        return digest;
    }

    public static byte[] hmacSHA256(byte[] key, byte[] message) {
        final byte[] hmac = new byte[32];
        CryptoBaseJni.hmac256(key, message, hmac);
        return hmac;
    }

    public static byte[] hmacSHA512(byte[] key, byte[] message) {
        final byte[] hmac = new byte[64];
        CryptoBaseJni.hmac512(key, message, hmac);
        return hmac;
    }

    public static byte[] pbkdf2HMACSHA256(byte[] pass,
                                          byte[] salt,
                                          long iterations,
                                          int keyLength) {
        final byte[] key = new byte[keyLength];
        CryptoBaseJni.pbkdf2_hmac_sha256(pass, salt, iterations, key);
        return key;
    }

    public static long crc32(byte[] bytes) {
        return CryptoBaseJni.crc32(bytes);
    }

    public static long crc32n(byte[] bytes) {
        return CryptoBaseJni.crc32n(bytes);
    }

}
