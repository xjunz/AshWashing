#include <jni.h>
#include <string>
#include "aes.h"
#include "md5.h"
#include <sys/ptrace.h>
#include <android/log.h>

static const char *app_packageName = "com.ashwashing.pro";
static const int app_signature_hash_code = 2146108806;//这个code是预先获取了我们的签名文件来的，然后用那个签名签名的apk就能是这个hash值，它通过检查java层的签名变了没有然后检查这个hash，变了直接就exit了
static const uint8_t AES_KEY[] = "xS544RXNm0P4JVLHIEsTqJNzDbZhiLjr";
static const uint8_t AES_IV[] = "KXTUDEdBs9zGlvy7";
static const string PWD_MD5_KEY = "4J9lKuR2c8OuDPBAniEy5USFQdSM0An4";


static jobject getApplication(JNIEnv *env) {
    jobject application = NULL;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != NULL) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != NULL) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        }
    }
    return application;
}

static bool checkSignature(JNIEnv *env) {
    jobject context = getApplication(env);
    //Context的类
    jclass context_clazz = env->GetObjectClass(context);
    // 得到 getPackageManager 方法的 ID
    jmethodID methodID_getPackageManager = env->GetMethodID(context_clazz, "getPackageManager",
                                                            "()Landroid/content/pm/PackageManager;");
    // 获得PackageManager对象
    jobject packageManager = env->CallObjectMethod(context, methodID_getPackageManager);
    // 获得 PackageManager 类
    jclass pm_clazz = env->GetObjectClass(packageManager);
    // 得到 getPackageInfo 方法的 ID
    jmethodID methodID_pm = env->GetMethodID(pm_clazz, "getPackageInfo",
                                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // 得到 getPackageName 方法的 ID
    jmethodID methodID_pack = env->GetMethodID(context_clazz,
                                               "getPackageName", "()Ljava/lang/String;");
    // 获得当前应用的包名
    jstring application_package = (jstring) env->CallObjectMethod(context, methodID_pack);
    const char *package_name = env->GetStringUTFChars(application_package, 0);
    // 获得PackageInfo
    jobject packageInfo = env->CallObjectMethod(packageManager, methodID_pm, application_package,
                                                64);
    jclass packageinfo_clazz = env->GetObjectClass(packageInfo);
    jfieldID fieldID_signatures = env->GetFieldID(packageinfo_clazz,
                                                  "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signature_arr = (jobjectArray) env->GetObjectField(packageInfo,
                                                                    fieldID_signatures);
    //Signature数组中取出第一个元素
    jobject signature = env->GetObjectArrayElement(signature_arr, 0);
    //读signature的hashcode
    jclass signature_clazz = env->GetObjectClass(signature);
    jmethodID methodID_hashcode = env->GetMethodID(signature_clazz, "hashCode", "()I");
    jint hashCode = env->CallIntMethod(signature, methodID_hashcode);

    if (strcmp(package_name, app_packageName) != 0) {
        exit(-1);//这个是直接退出
    }
    if (hashCode != app_signature_hash_code) {
        exit(-2);//这个会卡在那里白屏
    }
    return true;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    ptrace(PTRACE_TRACEME, 0, 0, 0);
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    if (checkSignature(env)) {
        return JNI_VERSION_1_6;
    }
    return JNI_ERR;
    // return JNI_VERSION_1_6;
}

uint8_t *jstringTostring(JNIEnv *env, jstring jstr) {
    uint8_t *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (uint8_t *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

int key[] = {1, 2, 3, 4, 5};//加密字符密钥

//异或
void xor_go(char *pstr, int *pkey) {
    int len = strlen(pstr);//获取长度
    for (int i = 0; i < len; i++) {
        *(pstr + i) = ((*(pstr + i)) ^ (pkey[i % 5]));
    }
}

/**
 * 执行异或加密
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashwashing_pro_util_CryptUtils_QvQ(JNIEnv *env, jclass type, jstring str) {
    const char *buf_name = env->GetStringUTFChars(str, 0);
    if (buf_name == NULL) {
        return NULL;
    }
    int len = strlen(buf_name);
    char value[len];
    strcpy(value, buf_name);
    //2. 释放内存
    env->ReleaseStringUTFChars(str, buf_name);
    char *p = value;
    xor_go(value, key);
    return env->NewStringUTF(p);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashwashing_pro_util_CryptUtils_jniencrypt(JNIEnv *env, jclass type, jbyteArray jbArr) {

    char *str = NULL;
    jsize alen = env->GetArrayLength(jbArr);
    jbyte *ba = env->GetByteArrayElements(jbArr, JNI_FALSE);
    str = (char *) malloc(alen + 1);
    memcpy(str, ba, alen);
    str[alen] = '\0';
    env->ReleaseByteArrayElements(jbArr, ba, 0);

    char *result = AES_ECB_PKCS7_Encrypt(str, AES_KEY);//AES ECB PKCS7Padding加密
//    char *result = AES_CBC_PKCS7_Encrypt(str, AES_KEY, AES_IV);//AES CBC PKCS7Padding加密
    return env->NewStringUTF(result);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_ashwashing_pro_util_CryptUtils_jnidecrypt(JNIEnv *env, jclass type, jstring out_str) {

    const char *str = env->GetStringUTFChars(out_str, 0);
    char *result = AES_ECB_PKCS7_Decrypt(str, AES_KEY);//AES ECB PKCS7Padding解密
//    char *result = AES_CBC_PKCS7_Decrypt(str, AES_KEY, AES_IV);//AES CBC PKCS7Padding解密
    env->ReleaseStringUTFChars(out_str, str);

    jsize len = (jsize) strlen(result);
    jbyteArray jbArr = env->NewByteArray(len);
    env->SetByteArrayRegion(jbArr, 0, len, (jbyte *) result);
    return jbArr;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashwashing_pro_util_CryptUtils_QAQ(JNIEnv *env, jclass type, jstring out_str) {
//pwdMD5
    const char *str = env->GetStringUTFChars(out_str, 0);
    string result = MD5(MD5(PWD_MD5_KEY + string(str)).toStr()).toStr();//加盐后进行两次md5
    env->ReleaseStringUTFChars(out_str, str);
    return env->NewStringUTF(("xx" + result + "oo").data());//最后再加三个#
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_ashwashing_pro_util_CryptUtils_FLDdecrypt(JNIEnv *env, jclass type, jbyteArray jbArr,
                                                   jstring tag) {
//加密  -  参数顺序，message,key,iv
    char *str = NULL;
    jsize alen = env->GetArrayLength(jbArr);
    jbyte *ba = env->GetByteArrayElements(jbArr, JNI_FALSE);
    str = (char *) malloc(alen + 1);
    memcpy(str, ba, alen);
    str[alen] = '\0';
    env->ReleaseByteArrayElements(jbArr, ba, 0);

    const char *kv = env->GetStringUTFChars(tag, 0);
    string kvhash = MD5(string(kv)).toStr();
    uint8_t *k = (uint8_t *) kvhash.substr(0, 16).c_str();
    uint8_t *v = (uint8_t *) kvhash.substr(16, 16).c_str();

//    char *result = AES_ECB_PKCS7_Encrypt(str, AES_KEY);//AES ECB PKCS7Padding加密
    char *result = AES_CBC_PKCS7_Encrypt(str, k, v);//AES CBC PKCS7Padding加密
    return env->NewStringUTF(result);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_ashwashing_pro_util_CryptUtils_FLDencrypt(JNIEnv *env, jclass type, jstring out_str,
                                                   jstring tag) {
//解密  - 参数顺序，message,key,iv

    const char *str = env->GetStringUTFChars(out_str, 0);

    const char *kv = env->GetStringUTFChars(tag, 0);
    string kvhash = MD5(string(kv)).toStr();
    uint8_t *k = (uint8_t *) kvhash.substr(0, 16).c_str();
    uint8_t *v = (uint8_t *) kvhash.substr(16, 16).c_str();
//    char *result = AES_ECB_PKCS7_Decrypt(str, AES_KEY);//AES ECB PKCS7Padding解密
    char *result = AES_CBC_PKCS7_Decrypt(str, k, v);//AES CBC PKCS7Padding解密
    env->ReleaseStringUTFChars(out_str, str);


    jsize len = (jsize) strlen(result);
    jbyteArray jbArr = env->NewByteArray(len);
    env->SetByteArrayRegion(jbArr, 0, len, (jbyte *) result);
    return jbArr;
}
