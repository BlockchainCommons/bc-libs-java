#include <jni.h>
#include <bc-bip39.h>
#include <jni-utils.c>

// com/bc/bip39/Bip39Exception
static bool throw_new_bip39_exception(JNIEnv *env, char *msg) {
    return throw_new(env, "com/bc/bip39/Bip39Exception", msg);
}

JNIEXPORT jstring JNICALL
Java_com_bc_bip39_Bip39Jni_bip39_1mnemonic_1from_1word(JNIEnv *env, jclass clazz, jint word) {

    if (word < 0 || word >= 2048) {
        throw_new_bip39_exception(env, "BIP39 word index out of range");
        return NULL;
    }

    char mnemonic[9];
    bip39_mnemonic_from_word((uint16_t) word, mnemonic);

    return (*env)->NewStringUTF(env, mnemonic);
}

JNIEXPORT jint JNICALL
Java_com_bc_bip39_Bip39Jni_bip39_1word_1from_1mnemonic(JNIEnv *env,
                                                       jclass clazz,
                                                       jstring mnemonic) {

    const char *c_mnemonic = (*env)->GetStringUTFChars(env, mnemonic, 0);

    int16_t word = bip39_word_from_mnemonic(c_mnemonic);
    if (word == -1) {
        (*env)->ReleaseStringUTFChars(env, mnemonic, c_mnemonic);
        throw_new_bip39_exception(env, "bip39_word_from_mnemonic error");
        return JNI_ERR;
    }

    (*env)->ReleaseStringUTFChars(env, mnemonic, c_mnemonic);

    return (jint) word;
}

JNIEXPORT jbyteArray JNICALL
Java_com_bc_bip39_Bip39Jni_bip39_1seed_1from_1string(JNIEnv *env, jclass clazz, jstring string) {

    const char *c_string = (*env)->GetStringUTFChars(env, string, 0);
    uint8_t *seed = calloc(BIP39_SEED_LEN, sizeof(uint8_t));

    bip39_seed_from_string(c_string, seed);

    jbyteArray result = create_jbyteArray(env, seed, BIP39_SEED_LEN);

    free(seed);
    (*env)->ReleaseStringUTFChars(env, string, c_string);

    return result;

}

JNIEXPORT jstring JNICALL
Java_com_bc_bip39_Bip39Jni_bip39_1mnemonics_1from_1secret(JNIEnv *env,
                                                          jclass clazz,
                                                          jbyteArray secret,
                                                          jint max_mnemonic_length) {
    if (secret == NULL) {
        throw_new_bip39_exception(env, "secret is NULL");
        return NULL;
    }

    uint8_t *c_secret = to_uint8_t_array(env, secret);
    jsize secret_len = (*env)->GetArrayLength(env, secret);
    char *mnemonic = calloc((size_t) max_mnemonic_length, sizeof(char));

    size_t written = bip39_mnemonics_from_secret(c_secret,
                                                 secret_len,
                                                 mnemonic,
                                                 (size_t) max_mnemonic_length);

    if (!written) {
        free(c_secret);
        free(mnemonic);
        throw_new_bip39_exception(env, "bip39_mnemonics_from_secret error");
        return NULL;
    }

    char *c_result = calloc(written + 1, sizeof(char));
    strncpy(c_result, mnemonic, written);
    c_result[written] = 0;

    jstring result = (*env)->NewStringUTF(env, c_result);

    free(c_secret);
    free(mnemonic);
    free(c_result);

    return result;
}

JNIEXPORT jbyteArray JNICALL
Java_com_bc_bip39_Bip39Jni_bip39_1secret_1from_1mnemonics(JNIEnv *env,
                                                          jclass clazz,
                                                          jstring mnemonic,
                                                          jint max_secret_length) {

    const char *c_mnemonic = (*env)->GetStringUTFChars(env, mnemonic, 0);
    uint8_t *c_secret = calloc(max_secret_length, sizeof(uint8_t));

    size_t written = bip39_secret_from_mnemonics(c_mnemonic, c_secret, (size_t) max_secret_length);
    if (!written) {
        free(c_secret);
        (*env)->ReleaseStringUTFChars(env, mnemonic, c_mnemonic);
        return NULL;
    }

    jbyteArray result = create_jbyteArray(env, c_secret, written);

    free(c_secret);
    (*env)->ReleaseStringUTFChars(env, mnemonic, c_mnemonic);

    return result;
}