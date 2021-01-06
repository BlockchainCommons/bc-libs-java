package com.bc.bip39;

import static com.bc.bip39.Bip39Jni.bip39_mnemonic_from_word;
import static com.bc.bip39.Bip39Jni.bip39_mnemonics_from_secret;
import static com.bc.bip39.Bip39Jni.bip39_secret_from_mnemonics;
import static com.bc.bip39.Bip39Jni.bip39_seed_from_string;
import static com.bc.bip39.Bip39Jni.bip39_word_from_mnemonic;

public class Bip39 {

    public static String identify() {
        return "BIP39";
    }

    public static String word(int index) {
        return bip39_mnemonic_from_word(index);
    }

    public static int index(String mnemonic) {
        return bip39_word_from_mnemonic(mnemonic);
    }

    public static byte[] seed(String mnemonics) {
        return bip39_seed_from_string(mnemonics);
    }

    public static String encode(byte[] data) {
        return bip39_mnemonics_from_secret(data, 300);
    }

    public static byte[] decode(String mnemonic) {
        return bip39_secret_from_mnemonics(mnemonic, 32);
    }

}
