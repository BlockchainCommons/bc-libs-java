#include <jni.h>
#include <bc-bytewords.h>
#include <jni-utils.c>
#include <limits.h>

// com/bc/bytewords/BytewordsException
static bool throw_new_bytewords_exception(JNIEnv *env, char *msg) {
    return throw_new(env, "com/bc/bytewords/BytewordsException", msg);
}

// com/bc/bytewords/BytewordsStyle
static jclass find_bw_style_jclass(JNIEnv *env) {
    return find_jclass(env, "com/bc/bytewords/BytewordsStyle");
}

static bw_style to_c_bw_style(JNIEnv *env, jobject j_style) {
    jclass clazz = find_bw_style_jclass(env);

    jmethodID get_name_mid = get_methodID(env, clazz, "name", "()Ljava/lang/String;");

    jstring name = (jstring) (*env)->CallObjectMethod(env, j_style, get_name_mid);
    const char *c_name = (*env)->GetStringUTFChars(env, name, NULL);
    bw_style c_style = -1;
    if (strcmp(c_name, "STANDARD") == 0) {
        c_style = bw_standard;
    } else if (strcmp(c_name, "URI") == 0) {
        c_style = bw_uri;
    } else if (strcmp(c_name, "MINIMAL") == 0) {
        c_style = bw_minimal;
    }

    (*env)->ReleaseStringUTFChars(env, name, c_name);

    return c_style;
}


JNIEXPORT jstring JNICALL
Java_com_bc_bytewords_BytewordsJni_bytewords_1get_1word(JNIEnv *env, jclass clazz, jint index) {

    if (index < 0 || index > UINT8_MAX) {
        throw_new_bytewords_exception(env, "invalid index");
        return NULL;
    }

    char *c_words = calloc(5, sizeof(char));
    bytewords_get_word((uint8_t) index, c_words);

    jstring result = (*env)->NewStringUTF(env, c_words);
    free(c_words);

    return result;
}

JNIEXPORT jbyteArray JNICALL
Java_com_bc_bytewords_BytewordsJni_bytewords_1decode(JNIEnv *env,
                                                     jclass clazz,
                                                     jobject style,
                                                     jstring encoded) {

    if (style == NULL) {
        throw_new_bytewords_exception(env, "style is NULL");
        return NULL;
    }

    if (encoded == NULL) {
        throw_new_bytewords_exception(env, "encoded is NULL");
        return NULL;
    }

    bw_style c_style = to_c_bw_style(env, style);
    if (c_style == -1) {
        throw_new_bytewords_exception(env, "invalid style");
        return NULL;
    }

    const char *c_encoded = (*env)->GetStringUTFChars(env, encoded, NULL);
    uint8_t *output = calloc(512, sizeof(uint8_t));
    size_t out_len = 0;

    bool ret = bytewords_decode(c_style, c_encoded, &output, &out_len);
    if (!ret || out_len == 0) {
        free(output);
        (*env)->ReleaseStringUTFChars(env, encoded, c_encoded);
        throw_new_bytewords_exception(env, "bytewords_decode error");
        return NULL;
    }

    jbyteArray result = create_jbyteArray(env, output, out_len);

    free(output);
    (*env)->ReleaseStringUTFChars(env, encoded, c_encoded);

    return result;
}

JNIEXPORT jstring JNICALL
Java_com_bc_bytewords_BytewordsJni_bytewords_1encode(JNIEnv *env,
                                                     jclass clazz,
                                                     jobject style,
                                                     jbyteArray bytes) {
    if (style == NULL) {
        throw_new_bytewords_exception(env, "style is NULL");
        return NULL;
    }

    if (bytes == NULL) {
        throw_new_bytewords_exception(env, "bytes is NULL");
        return NULL;
    }

    bw_style c_style = to_c_bw_style(env, style);
    if (c_style == -1) {
        throw_new_bytewords_exception(env, "invalid style");
        return NULL;
    }

    uint8_t *c_bytes = to_uint8_t_array(env, bytes);
    size_t bytes_len = (*env)->GetArrayLength(env, bytes);

    char *encoded = bytewords_encode(c_style, c_bytes, bytes_len);

    jstring result = (*env)->NewStringUTF(env, encoded);

    free(encoded);
    free(c_bytes);

    return result;

}