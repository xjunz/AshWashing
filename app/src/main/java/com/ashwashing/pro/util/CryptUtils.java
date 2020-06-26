package com.ashwashing.pro.util;

import com.github.megatronking.stringfog.IStringFog;

/**
 * Created by ashwashing on 2017/3/9.
 */

public class CryptUtils  {

    private static native String jniencrypt(byte[] bytes);//加密返回javautf8string

    public static native String QvQ(String str);//加密返回javautf8string 字符串资源加密

    private static native byte[] jnidecrypt(String str);

    public static native byte[] FLDencrypt(String str, String str2);//解密

    public static native String FLDdecrypt(byte[] bytes, String str);//加密

    public static native String QAQ(String str);//pwdMD5

    public static String encrypt(String src) {
        return jniencrypt(src.getBytes());
    }

    public static String encrypt(byte[] src) {
        return jniencrypt(src);
    }

    public static String decryptToString(String base64) {
        return new String(jnidecrypt(base64));
    }

    public static byte[] decrypt(String base64) {
        return jnidecrypt(base64);
    }


    static String specEncrypt(String src, String timestamp) {//加密
        return FLDdecrypt(src.getBytes(), timestamp);
    }

    public static String specDecrypt(String base64, String timestamp) {//解密
        return new String(FLDencrypt(base64, timestamp));
    }



}
