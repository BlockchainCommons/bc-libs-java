package com.bc.bytewords;

import static com.bc.bytewords.BytewordsJni.bytewords_decode;
import static com.bc.bytewords.BytewordsJni.bytewords_encode;
import static com.bc.bytewords.BytewordsJni.bytewords_get_word;

public class Bytewords {

    public static String word(int index) {
        return bytewords_get_word(index);
    }

    public static String encode(BytewordsStyle style, byte[] bytes) {
        return bytewords_encode(style, bytes);
    }

    public static byte[] decode(BytewordsStyle style, String encoded) {
        return bytewords_decode(style, encoded);
    }

}
