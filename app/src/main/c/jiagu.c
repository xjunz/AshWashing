//
// Created by Haleclipse on 2019/5/31.
//

#include <jni.h>
#include <malloc.h>

#include "path_helper.h"
#include "unzip_helper.h"
#include "pkcs7_helper.h"


JNIEXPORT jbyteArray JNICALL
Java_com_ashwashing_pro_util_ApiUtils_getBluetoothId(JNIEnv *env, jobject this) {
//Java_路径_函数名
    char *path = pathHelperGetPath();

    if (!path) {
        return NULL;
    }

    size_t len_in = 0;
    size_t len_out = 0;
    unsigned char *content = unzipHelperGetCertificateDetails(path, &len_in);

    if (!content) {
        free(path);
        return NULL;
    }

    unsigned char *res = pkcs7HelperGetSignature(content, len_in, &len_out);

    jbyteArray jbArray = NULL;
    if (NULL != res || len_out != 0) {
        jbArray = (*env)->NewByteArray(env, len_out);
        (*env)->SetByteArrayRegion(env, jbArray, 0, len_out, (jbyte *) res);
    }
    free(content);
    free(path);
    pkcs7HelperFree();
    return jbArray;
}