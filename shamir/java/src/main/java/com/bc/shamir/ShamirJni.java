package com.bc.shamir;

class ShamirJni {

    static {
        System.loadLibrary("bc-shamir-jni");
    }

    static native int split_secret(short threshold,
                                   short shardsCount,
                                   byte[] secret,
                                   byte[] output,
                                   RandomFunc randomFunc);

    static native int recover_secret(short threshold,
                                     byte[] indexes,
                                     byte[][] shards,
                                     int shardLength,
                                     byte[] secret);

}
