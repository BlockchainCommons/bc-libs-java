package com.bc.sskr;

class SSKRJni {

    static {
        System.loadLibrary("bc-sskr-jni");
    }

    public static native int SSKR_count_shares(int groupThreshold, SSKRGroupDescriptor[] groups);

    public static native int SSKR_generate(int groupThreshold,
                                           SSKRGroupDescriptor[] groups,
                                           byte[] secret,
                                           int[] shareLength,
                                           byte[] output,
                                           RandomFunc randomFunc);

    public static native int SSKR_combine(byte[][] shares,
                                          int shareLength,
                                          int shareCount,
                                          byte[] secret);

}
