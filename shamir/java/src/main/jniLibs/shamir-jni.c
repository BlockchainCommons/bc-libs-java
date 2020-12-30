#include <jni.h>
#include <bc-shamir.h>
#include <jni-utils.c>
#include <limits.h>

typedef struct Shamir_ctx {
    JNIEnv *env;
    jobject random_func;
} Shamir_ctx;

// com/bc/shamir/RandomFunc
static jclass find_shamir_random_func_jclass(JNIEnv *env) {
    return find_jclass(env, "com/bc/shamir/RandomFunc");
}

static jmethodID get_shamir_random_func_random_jmethodID(JNIEnv *env) {
    jclass clazz = find_shamir_random_func_jclass(env);
    return get_methodID(env, clazz, "random", "(I)[B");
}

static void shamir_random(uint8_t *buf, size_t count, void *ctx) {
    Shamir_ctx *origin_cxt = (Shamir_ctx *) ctx;
    JNIEnv *env = origin_cxt->env;
    jobject random_func = origin_cxt->random_func;

    jmethodID mid = get_shamir_random_func_random_jmethodID(env);
    jbyteArray array = (jbyteArray) (*env)->CallObjectMethod(env, random_func, mid, count);
    uint8_t *result = to_uint8_t_array(env, array);

    memcpy(buf, result, count);
    free(result);
}

// com/bc/shamir/ShamirException
static bool throw_new_shamir_exception(JNIEnv *env, char *msg) {
    return throw_new(env, "com/bc/shamir/ShamirException", msg);
}

JNIEXPORT jint JNICALL
Java_com_bc_shamir_ShamirJni_split_1secret(JNIEnv *env, jclass clazz, jshort threshold,
                                           jshort shards_count, jbyteArray secret,
                                           jbyteArray output,
                                           jobject random_func) {
    if (secret == NULL) {
        throw_new_shamir_exception(env, "secret is NULL");
        return JNI_ERR;
    }

    if (output == NULL) {
        throw_new_shamir_exception(env, "output is NULL");
        return JNI_ERR;
    }

    if (random_func == NULL) {
        throw_new_shamir_exception(env, "random_func is NULL");
        return JNI_ERR;
    }

    if (threshold < 0 || shards_count < 0) {
        throw_new_shamir_exception(env, "threshold or shards_count is negative number");
        return JNI_ERR;
    }

    if (threshold > UINT8_MAX || shards_count > UINT8_MAX) {
        throw_new_shamir_exception(env, "threshold or shards_count is too large");
        return JNI_ERR;
    }

    Shamir_ctx *c_ctx = malloc(sizeof(Shamir_ctx));
    c_ctx->env = env;
    c_ctx->random_func = random_func;

    uint8_t *c_secret = to_uint8_t_array(env, secret);
    jint c_secret_length = (*env)->GetArrayLength(env, secret);
    jint c_output_length = (*env)->GetArrayLength(env, output);
    uint8_t *c_output = calloc(c_output_length, sizeof(uint8_t));

    int ret = split_secret((uint8_t) threshold,
                           (uint8_t) shards_count,
                           c_secret,
                           c_secret_length,
                           c_output,
                           c_ctx,
                           shamir_random);

    copy_to_jbyteArray(env, output, c_output, c_output_length);

    free(c_secret);
    free(c_output);

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_bc_shamir_ShamirJni_recover_1secret(JNIEnv *env, jclass clazz, jshort threshold,
                                             jbyteArray indexes, jobjectArray shards,
                                             jint shard_length,
                                             jbyteArray secret) {
    if (indexes == NULL) {
        throw_new_shamir_exception(env, "indexes is NULL");
        return JNI_ERR;
    }

    if (shards == NULL) {
        throw_new_shamir_exception(env, "shards is NULL");
        return JNI_ERR;
    }

    if (secret == NULL) {
        throw_new_shamir_exception(env, "secret is NULL");
        return JNI_ERR;
    }

    if (threshold < 0) {
        throw_new_shamir_exception(env, "threshold is negative number");
        return JNI_ERR;
    }

    if (threshold > UINT8_MAX) {
        throw_new_shamir_exception(env, "threshold is too large");
        return JNI_ERR;
    }

    uint8_t **c_shards = to_uint8_t_2dimension_array(env, shards);
    uint8_t *c_indexes = to_uint8_t_array(env, indexes);
    jint c_secret_length = (*env)->GetArrayLength(env, secret);
    uint8_t *c_secret = calloc(c_secret_length, sizeof(uint8_t));

    int ret = recover_secret((uint8_t) threshold, c_indexes,
                             (const uint8_t **) c_shards, (uint32_t) shard_length, c_secret);
    copy_to_jbyteArray(env, secret, c_secret, c_secret_length);

    free(c_shards);
    free(c_secret);
    free(c_indexes);

    return ret;
}

