package com.bc.sskr;

class SSKRJni {

    static {
        System.loadLibrary("bc-sskr-jni");
    }

    static native int SSKR_count_shards(int groupThreshold, SSKRGroupDescriptor[] groups);

    static native int SSKR_generate(int groupThreshold,
                                    SSKRGroupDescriptor[] groups,
                                    byte[] secret,
                                    int[] shardLength,
                                    byte[] output,
                                    RandomFunc randomFunc);

    static native int SSKR_combine(byte[][] shards,
                                   int shardLength,
                                   int shardsCount,
                                   byte[] secret);

}
