#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>

static jclass find_jclass(JNIEnv *env, char *className) {
    jclass clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        fprintf(stderr, "JNIEnv::FindClass error");
        return NULL;
    }

    return clazz;
}

static jmethodID get_methodID(JNIEnv *env, jclass clazz, char *methodName, char *methodSig) {
    jmethodID methodID = (*env)->GetMethodID(env, clazz, methodName, methodSig);
    if (methodID == NULL) {
        fprintf(stderr, "JNIEnv::GetMethodID error");
        return NULL;
    }

    return methodID;
}

static bool throw_new(JNIEnv *env, char *className, char *msg) {
    jclass clazz = find_jclass(env, className);
    const jint rs = (*env)->ThrowNew(env, clazz, msg);
    if (rs != JNI_OK) {
        fprintf(stderr, "throw_new error");
        return (*env)->ExceptionCheck(env);
    }

    return true;
}

static uint8_t *to_uint8_t_array(JNIEnv *env, jbyteArray array) {
    jint count = (*env)->GetArrayLength(env, array);
    jbyte *elements = (*env)->GetByteArrayElements(env, array, JNI_FALSE);
    uint8_t *ret = calloc(count, sizeof(uint8_t));
    memcpy(ret, elements, count);
    (*env)->ReleaseByteArrayElements(env, array, elements, JNI_ABORT);
    return ret;
}

static uint8_t **to_uint8_t_2dimension_array(JNIEnv *env, jobjectArray array) {
    jint count = (*env)->GetArrayLength(env, array);
    uint8_t **ret = (uint8_t **) calloc(count, sizeof(uint8_t *));
    for (int i = 0; i < count; i++) {
        jbyteArray obj = (jbyteArray) (*env)->GetObjectArrayElement(env, array, i);
        ret[i] = to_uint8_t_array(env, obj);
    }
    return ret;
}

static void copy_to_jbyteArray(JNIEnv *env, jbyteArray dst, const uint8_t *src, size_t src_len) {
    jint count = (*env)->GetArrayLength(env, dst);
    if (count != src_len) {
        fprintf(stderr, "the length between src and dst is different");
        return;
    }

    jbyte *jbytes = (*env)->GetByteArrayElements(env, dst, JNI_FALSE);
    for (int i = 0; i < count; ++i) {
        jbytes[i] = src[i];
    }
    (*env)->ReleaseByteArrayElements(env, dst, jbytes, 0);
}

static void copy_to_jintArray(JNIEnv *env, jintArray dst, const size_t *src, size_t src_len) {
    jint count = (*env)->GetArrayLength(env, dst);
    if (count != src_len) {
        fprintf(stderr, "the length between src and dst is different");
        return;
    }

    jint *jints = (*env)->GetIntArrayElements(env, dst, JNI_FALSE);
    for (int i = 0; i < count; ++i) {
        jints[i] = src[i];
    }
    (*env)->ReleaseIntArrayElements(env, dst, jints, 0);
}