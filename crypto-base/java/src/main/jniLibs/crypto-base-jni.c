#include <jni.h>
#include "../../../../../deps/bc-crypto-base/src/bc-crypto-base.h"
#include "../../../../../base-jni/jni-utils.c"

// com/bc/cryptobase/CryptoBaseException
static bool throw_new_crypto_base_exception(JNIEnv *env, char *msg) {
    return throw_new(env, "com/bc/cryptobase/CryptoBaseException", msg);
}

JNIEXPORT void JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_sha256_1raw(JNIEnv *env, jclass clazz, jbyteArray message,
                                                 jbyteArray digest) {
    if (message == NULL) {
        throw_new_crypto_base_exception(env, "message is NULL");
        return;
    }

    if (digest == NULL) {
        throw_new_crypto_base_exception(env, "digest is NULL");
        return;
    }

    jint digest_length = (*env)->GetArrayLength(env, digest);
    if (digest_length != SHA256_DIGEST_LENGTH) {
        char *msg = malloc(30 * sizeof(char));
        sprintf(msg, "digest length must be %d", SHA256_DIGEST_LENGTH);
        throw_new_crypto_base_exception(env, msg);
        free(msg);
        return;
    }

    uint8_t *c_message = to_uint8_t_array(env, message);
    jint c_message_length = (*env)->GetArrayLength(env, message);
    uint8_t *c_digest = calloc(SHA256_DIGEST_LENGTH, sizeof(uint8_t));

    sha256_Raw(c_message, c_message_length, c_digest);
    copy_to_jbyteArray(env, digest, c_digest, SHA256_DIGEST_LENGTH);

    free(c_digest);
    free(c_message);
}

JNIEXPORT void JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_sha512_1raw(JNIEnv *env, jclass clazz, jbyteArray message,
                                                 jbyteArray digest) {
    if (message == NULL) {
        throw_new_crypto_base_exception(env, "message is NULL");
        return;
    }

    if (digest == NULL) {
        throw_new_crypto_base_exception(env, "digest is NULL");
        return;
    }

    jint digest_length = (*env)->GetArrayLength(env, digest);
    if (digest_length != SHA512_DIGEST_LENGTH) {
        char *msg = malloc(30 * sizeof(char));
        sprintf(msg, "digest length must be %d", SHA512_DIGEST_LENGTH);
        throw_new_crypto_base_exception(env, msg);
        free(msg);
        return;
    }

    uint8_t *c_message = to_uint8_t_array(env, message);
    jint c_message_length = (*env)->GetArrayLength(env, message);
    uint8_t *c_digest = calloc(SHA512_DIGEST_LENGTH, sizeof(uint8_t));

    sha512_Raw(c_message, c_message_length, c_digest);
    copy_to_jbyteArray(env, digest, c_digest, SHA512_DIGEST_LENGTH);

    free(c_digest);
    free(c_message);
}

JNIEXPORT void JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_hmac256(JNIEnv *env, jclass clazz, jbyteArray key,
                                             jbyteArray message, jbyteArray hmac) {

    if (key == NULL) {
        throw_new_crypto_base_exception(env, "key is NULL");
        return;
    }

    if (message == NULL) {
        throw_new_crypto_base_exception(env, "message is NULL");
        return;
    }

    if (hmac == NULL) {
        throw_new_crypto_base_exception(env, "hmac is NULL");
        return;
    }

    jint hmac_length = (*env)->GetArrayLength(env, hmac);
    if (hmac_length != SHA256_DIGEST_LENGTH) {
        char *msg = malloc(30 * sizeof(char));
        sprintf(msg, "hmac length must be %d", SHA256_DIGEST_LENGTH);
        throw_new_crypto_base_exception(env, msg);
        free(msg);
        return;
    }

    uint8_t *c_key = to_uint8_t_array(env, key);
    jint c_key_length = (*env)->GetArrayLength(env, key);
    uint8_t *c_message = to_uint8_t_array(env, message);
    jint c_message_length = (*env)->GetArrayLength(env, message);
    uint8_t *c_hmac = calloc(SHA256_DIGEST_LENGTH, sizeof(uint8_t));

    hmac_sha256(c_key, c_key_length, c_message, c_message_length, c_hmac);
    copy_to_jbyteArray(env, hmac, c_hmac, SHA256_DIGEST_LENGTH);

    free(c_hmac);
    free(c_key);
    free(c_message);
}

JNIEXPORT void JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_hmac512(JNIEnv *env, jclass clazz, jbyteArray key,
                                             jbyteArray message, jbyteArray hmac) {
    if (key == NULL) {
        throw_new_crypto_base_exception(env, "key is NULL");
        return;
    }

    if (message == NULL) {
        throw_new_crypto_base_exception(env, "message is NULL");
        return;
    }

    if (hmac == NULL) {
        throw_new_crypto_base_exception(env, "hmac is NULL");
        return;
    }

    jint hmac_length = (*env)->GetArrayLength(env, hmac);
    if (hmac_length != SHA512_DIGEST_LENGTH) {
        char *msg = malloc(30 * sizeof(char));
        sprintf(msg, "hmac length must be %d", SHA512_DIGEST_LENGTH);
        throw_new_crypto_base_exception(env, msg);
        free(msg);
        return;
    }

    uint8_t *c_key = to_uint8_t_array(env, key);
    jint c_key_length = (*env)->GetArrayLength(env, key);
    uint8_t *c_message = to_uint8_t_array(env, message);
    jint c_message_length = (*env)->GetArrayLength(env, message);
    uint8_t *c_hmac = calloc(SHA512_DIGEST_LENGTH, sizeof(uint8_t));

    hmac_sha512(c_key, c_key_length, c_message, c_message_length, c_hmac);
    copy_to_jbyteArray(env, hmac, c_hmac, SHA512_DIGEST_LENGTH);

    free(c_hmac);
    free(c_key);
    free(c_message);
}

JNIEXPORT void JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_pbkdf2_1hmac_1sha256(JNIEnv *env, jclass clazz,
                                                          jbyteArray pass, jbyteArray salt,
                                                          jlong iteration, jbyteArray key) {
    if (pass == NULL) {
        throw_new_crypto_base_exception(env, "pass is NULL");
        return;
    }

    if (salt == NULL) {
        throw_new_crypto_base_exception(env, "salt is NULL");
        return;
    }

    if (key == NULL) {
        throw_new_crypto_base_exception(env, "key is NULL");
        return;
    }

    uint8_t *c_pass = to_uint8_t_array(env, pass);
    jint c_pass_length = (*env)->GetArrayLength(env, pass);
    uint8_t *c_salt = to_uint8_t_array(env, salt);
    jint c_salt_length = (*env)->GetArrayLength(env, salt);
    jint c_key_length = (*env)->GetArrayLength(env, key);
    uint8_t *c_key = calloc(c_key_length, sizeof(uint8_t));

    pbkdf2_hmac_sha256(c_pass,
                       c_pass_length,
                       c_salt,
                       c_salt_length,
                       (uint32_t) iteration,
                       c_key,
                       c_key_length);

    copy_to_jbyteArray(env, key, c_key, c_key_length);

    free(c_pass);
    free(c_salt);
    free(c_key);
}

JNIEXPORT jlong JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_crc32(JNIEnv *env, jclass clazz, jbyteArray bytes) {

    if (bytes == NULL) {
        throw_new_crypto_base_exception(env, "bytes is NULL");
        return JNI_ERR;
    }

    jint c_bytes_length = (*env)->GetArrayLength(env, bytes);
    uint8_t *c_bytes = to_uint8_t_array(env, bytes);

    jlong checksum = (jlong) crc32(c_bytes, c_bytes_length);

    free(c_bytes);

    return checksum;
}

JNIEXPORT jlong JNICALL
Java_com_bc_cryptobase_CryptoBaseJni_crc32n(JNIEnv *env, jclass clazz, jbyteArray bytes) {
    if (bytes == NULL) {
        throw_new_crypto_base_exception(env, "bytes is NULL");
        return JNI_ERR;
    }

    jint c_bytes_length = (*env)->GetArrayLength(env, bytes);
    uint8_t *c_bytes = to_uint8_t_array(env, bytes);

    jlong checksum = (jlong) crc32n(c_bytes, c_bytes_length);

    free(c_bytes);

    return checksum;
}


