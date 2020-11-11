package com.bc.shamir;

class ShamirJni {

    static {
        System.loadLibrary("bc-shamir-jni");
    }

    public static native int split_secret(short threshold,
                                          short shareCount,
                                          byte[] secret,
                                          byte[] output,
                                          RandomFunc randomFunc);

    public static native int recover_secret(short threshold,
                                            byte[] indexes,
                                            byte[][] shares,
                                            int shareLength,
                                            byte[] secret);

}
