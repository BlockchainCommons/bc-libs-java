#include <jni.h>
#include "../../../../../deps/bc-sskr/src/bc-sskr.h"
#include "../../../../../base-jni/jni-utils.c"

typedef struct SSKR_ctx {
    JNIEnv *env;
    jobject random_func;
} SSKR_ctx;

// com/bc/sskr/SSKRGroupDescriptor
static jclass find_sskr_group_descriptor_jclass(JNIEnv *env) {
    return find_jclass(env, "com/bc/sskr/SSKRGroupDescriptor");
}

static jmethodID get_sskr_group_descriptor_get_count_jmethodID(JNIEnv *env) {
    jclass clazz = find_sskr_group_descriptor_jclass(env);
    return get_methodID(env, clazz, "getCount", "()I");
}

static jmethodID get_sskr_group_descriptor_get_threshold_jmethodID(JNIEnv *env) {
    jclass clazz = find_sskr_group_descriptor_jclass(env);
    return get_methodID(env, clazz, "getThreshold", "()I");
}

static sskr_group_descriptor *to_sskr_group_descriptor_array(JNIEnv *env, jobjectArray groups) {
    jint group_count = (*env)->GetArrayLength(env, groups);
    sskr_group_descriptor
            *ret = (sskr_group_descriptor *) calloc(group_count, sizeof(sskr_group_descriptor));
    for (jint i = 0; i < group_count; i++) {
        jobject obj = (*env)->GetObjectArrayElement(env, groups, i);
        jint count =
                (*env)->CallIntMethod(env, obj, get_sskr_group_descriptor_get_count_jmethodID(env));
        jint threshold = (*env)->CallIntMethod(env,
                                               obj,
                                               get_sskr_group_descriptor_get_threshold_jmethodID(env));
        sskr_group_descriptor
                *group = (sskr_group_descriptor *) calloc(1, sizeof(sskr_group_descriptor));
        group->count = count;
        group->threshold = threshold;
        ret[i] = *group;
        free(group);
    }

    return ret;
}

// com/bc/sskr/RandomFunc
static jclass find_sskr_random_func_jclass(JNIEnv *env) {
    return find_jclass(env, "com/bc/sskr/RandomFunc");
}

static jmethodID get_sskr_random_func_random_jmethodID(JNIEnv *env) {
    jclass clazz = find_sskr_random_func_jclass(env);
    return get_methodID(env, clazz, "random", "(I)[B");
}

static void sskr_random(uint8_t *buf, size_t count, void *ctx) {
    SSKR_ctx *origin_cxt = (SSKR_ctx *) ctx;
    JNIEnv *env = origin_cxt->env;
    jobject random_func = origin_cxt->random_func;

    jmethodID mid = get_sskr_random_func_random_jmethodID(env);
    jbyteArray array = (jbyteArray) (*env)->CallObjectMethod(env, random_func, mid, count);
    uint8_t *result = to_uint8_t_array(env, array);

    memcpy(buf, result, count);
    free(result);
}

// com/bc/sskr/SSKRException
static bool throw_new_sskr_exception(JNIEnv *env, char *msg) {
    return throw_new(env, "com/bc/sskr/SSKRException", msg);
}

JNIEXPORT jint JNICALL
Java_com_bc_sskr_SSKRJni_SSKR_1count_1shares(JNIEnv *env, jclass clazz, jint group_threshold,
                                             jobjectArray groups) {
    if (groups == NULL) {
        throw_new_sskr_exception(env, "groups is NULL");
        return JNI_ERR;
    }

    sskr_group_descriptor *c_groups = to_sskr_group_descriptor_array(env, groups);
    jint c_group_length = (*env)->GetArrayLength(env, groups);
    int ret = sskr_count_shards(group_threshold, c_groups, c_group_length);
    free(c_groups);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_bc_sskr_SSKRJni_SSKR_1generate(JNIEnv *env, jclass clazz, jint group_threshold,
                                        jobjectArray groups, jbyteArray secret,
                                        jintArray share_length, jbyteArray output,
                                        jobject random_func) {
    if (group_threshold < 0) {
        throw_new_sskr_exception(env, "group_threshold is negative number");
        return JNI_ERR;
    }

    if (groups == NULL) {
        throw_new_sskr_exception(env, "groups is NULL");
        return JNI_ERR;
    }

    if (secret == NULL) {
        throw_new_sskr_exception(env, "secret is NULL");
        return JNI_ERR;
    }

    if (share_length == NULL) {
        throw_new_sskr_exception(env, "share_length is NULL");
        return JNI_ERR;
    }

    if (output == NULL) {
        throw_new_sskr_exception(env, "output is NULL");
        return JNI_ERR;
    }

    if (random_func == NULL) {
        throw_new_sskr_exception(env, "random_func is NULL");
        return JNI_ERR;
    }

    sskr_group_descriptor *c_groups = to_sskr_group_descriptor_array(env, groups);
    jint c_group_length = (*env)->GetArrayLength(env, groups);
    uint8_t *c_secret = to_uint8_t_array(env, secret);
    jint c_secret_length = (*env)->GetArrayLength(env, secret);
    jint c_share_length_len = (*env)->GetArrayLength(env, share_length);
    size_t *c_share_length = (size_t *) malloc(sizeof(size_t));
    jint c_output_length = (*env)->GetArrayLength(env, output);
    uint8_t *c_output = calloc(c_output_length, sizeof(uint8_t));

    SSKR_ctx *c_ctx = malloc(sizeof(SSKR_ctx));
    c_ctx->env = env;
    c_ctx->random_func = random_func;

    int ret = sskr_generate(group_threshold,
                            c_groups,
                            c_group_length,
                            c_secret,
                            c_secret_length,
                            c_share_length,
                            c_output,
                            c_output_length,
                            c_ctx,
                            sskr_random
    );

    copy_to_jbyteArray(env, output, c_output, c_output_length);
    copy_to_jintArray(env, share_length, c_share_length, c_share_length_len);

    free(c_groups);
    free(c_secret);
    free(c_output);
    free(c_share_length);
    free(c_ctx);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_bc_sskr_SSKRJni_SSKR_1combine(JNIEnv *env, jclass clazz, jobjectArray shares,
                                       jint share_length, jint share_count,
                                       jbyteArray secret) {
    if (shares == NULL) {
        throw_new_sskr_exception(env, "shares is NULL");
        return JNI_ERR;
    }

    if (secret == NULL) {
        throw_new_sskr_exception(env, "secret is NULL");
        return JNI_ERR;
    }

    if (share_length < 0 || share_count < 0) {
        throw_new_sskr_exception(env, "share_length or share_count is negative number");
        return JNI_ERR;
    }

    uint8_t **c_shares = to_uint8_t_2dimension_array(env, shares);
    jint c_secret_length = (*env)->GetArrayLength(env, secret);
    uint8_t *c_secret = calloc(c_secret_length, sizeof(uint8_t));

    int ret = sskr_combine((const uint8_t **) c_shares,
                           share_length,
                           share_count,
                           c_secret,
                           c_secret_length);

    copy_to_jbyteArray(env, secret, c_secret, c_secret_length);

    free(c_shares);
    free(c_secret);
    return ret;
}
