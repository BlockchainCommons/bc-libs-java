package com.bc.bip39;

class Bip39Jni {

    static {
        System.loadLibrary("bc-bip39-jni");
    }

    static native String bip39_mnemonic_from_word(int word);

    static native int bip39_word_from_mnemonic(String mnemonic);

    static native byte[] bip39_seed_from_string(String string);

    static native String bip39_mnemonics_from_secret(byte[] secret, int maxMnemonicLength);

    static native byte[] bip39_secret_from_mnemonics(String mnemonic, int maxSecretLength);
}
