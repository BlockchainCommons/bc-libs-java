package com.bc.sskr;

class SSKRJni {

    static {
        System.loadLibrary("bc-sskr-jni");
    }

    static native int SSKR_count_shares(int groupThreshold, SSKRGroupDescriptor[] groups);

    static native int SSKR_generate(int groupThreshold,
                                    SSKRGroupDescriptor[] groups,
                                    byte[] secret,
                                    int[] shareLength,
                                    byte[] output,
                                    RandomFunc randomFunc);

    static native int SSKR_combine(byte[][] shares,
                                   int shareLength,
                                   int shareCount,
                                   byte[] secret);

}
