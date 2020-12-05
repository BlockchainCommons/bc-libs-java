package com.bc.cryptobase;

class CryptoBaseJni {

    static {
        System.loadLibrary("bc-crypto-base-jni");
    }

    static native void sha256_raw(byte[] message, byte[] digest);

    static native void sha512_raw(byte[] message, byte[] digest);

    static native void hmac256(byte[] key, byte[] message, byte[] hmac);

    static native void hmac512(byte[] key, byte[] message, byte[] hmac);

    static native void pbkdf2_hmac_sha256(byte[] pass,
                                          byte[] salt,
                                          long iteration,
                                          byte[] key);

    static native long crc32(byte[] bytes);

    static native long crc32n(byte[] bytes);
}
