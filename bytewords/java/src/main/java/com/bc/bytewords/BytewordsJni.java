package com.bc.bytewords;

class BytewordsJni {

    static {
        System.loadLibrary("bc-bytewords-jni");
    }

    static native String bytewords_get_word(int index);

    static native String bytewords_encode(BytewordsStyle style, byte[] bytes);

    static native byte[] bytewords_decode(BytewordsStyle style, String encoded);

}
