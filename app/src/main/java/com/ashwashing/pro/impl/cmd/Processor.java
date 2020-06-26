package com.ashwashing.pro.impl.cmd;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Processor {
    static String ascii2string(String str) {
        int length = str.length() / 2;
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            String valueOf = String.valueOf((char) ((int) ((long) hex2decimal(str.substring(i2, i2 + 2)))));
            str2.append(valueOf);
        }
        return str2.toString();
    }

    static String string2ascii(String str) {
        int length = str.length();
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String toHexString = Integer.toHexString(str.charAt(i));
            str2.append(toHexString);
        }
        return str2.toString();
    }

    static String decimal2hex(long j, int i) {
        String toHexString = Long.toHexString(j);
        if (toHexString.length() % 2 == 1) {
            toHexString = "0" + toHexString;
        }
        return patchHexString(toHexString.toUpperCase(), i);
    }

    private static int binary2decimal(String str) {
        int length = str.length();
        int i = 0;
        for (int i2 = length; i2 > 0; i2--) {
            int charAt = str.charAt(i2 - 1) - 48;
            double d = (double) i;
            double pow = Math.pow(2.0d, (double) (length - i2));
            double d2 = (double) charAt;
            //Double.isNaN(d2);
            pow *= d2;
            // Double.isNaN(d);
            i = (int) (d + pow);
        }
        return i;
    }


    static String bytes2hex(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String toHexString = Integer.toHexString(b & 255);
            if (toHexString.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(toHexString);
        }
        return stringBuilder.toString();
    }


    private static byte char2byte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    static byte[] copyByte(ArrayList<byte[]> arrayList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                byteArrayOutputStream.write(arrayList.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    static int hex2decimal(String str) {
        str = str.toUpperCase();
        int length = str.length();
        int i = 0;
        for (int i2 = length; i2 > 0; i2--) {
            char charAt = str.charAt(i2 - 1);
            int i3 = (charAt < '0' || charAt > '9') ? charAt - 55 : charAt - 48;
            double d = (double) i;
            double pow = Math.pow(16.0d, (double) (length - i2));
            double d2 = (double) i3;
            //Double.isNaN(d2);
            pow *= d2;
            // Double.isNaN(d);
            i = (int) (d + pow);
        }
        return i;
    }

    private static String hex2binary(String str) {
        str = str.toUpperCase();
        int length = str.length();
        String str2 = "";
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            StringBuilder stringBuilder;
            switch (charAt) {
                case '0':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0000");
                    str2 = stringBuilder.toString();
                    break;
                case '1':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0001");
                    str2 = stringBuilder.toString();
                    break;
                case '2':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0010");
                    str2 = stringBuilder.toString();
                    break;
                case '3':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0011");
                    str2 = stringBuilder.toString();
                    break;
                case '4':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0100");
                    str2 = stringBuilder.toString();
                    break;
                case '5':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0101");
                    str2 = stringBuilder.toString();
                    break;
                case '6':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0110");
                    str2 = stringBuilder.toString();
                    break;
                case '7':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("0111");
                    str2 = stringBuilder.toString();
                    break;
                case '8':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("1000");
                    str2 = stringBuilder.toString();
                    break;
                case '9':
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append("1001");
                    str2 = stringBuilder.toString();
                    break;
                default:
                    switch (charAt) {
                        case 'A':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1010");
                            str2 = stringBuilder.toString();
                            break;
                        case 'B':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1011");
                            str2 = stringBuilder.toString();
                            break;
                        case 'C':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1100");
                            str2 = stringBuilder.toString();
                            break;
                        case 'D':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1101");
                            str2 = stringBuilder.toString();
                            break;
                        case 'E':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1110");
                            str2 = stringBuilder.toString();
                            break;
                        case 'F':
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append("1111");
                            str2 = stringBuilder.toString();
                            break;
                        default:
                            break;
                    }
            }
        }
        return str2;
    }

    public static byte[] dumpBytesList(List<List<Byte>> bytesArray) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (List<Byte> bytes : bytesArray) {
            for (byte b : bytes) {
                byteArrayOutputStream.write(b);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    static byte[] dumpBytesArray(List<byte[]> byteArrays) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte[] byteArray : byteArrays) {
            byteArrayOutputStream.write(byteArray, 0, byteArray.length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    static byte[] hex2byte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        str = hex2binary(str);
        int i = 0;
        while (i < length) {
            int i2 = i * 8;
            int i3 = i + 1;
            bArr[i] = (byte) binary2decimal(str.substring(i2 + 1, i3 * 8));
            if (str.charAt(i2) == '1') {
                bArr[i] = (byte) (0 - bArr[i]);
            }
            i = i3;
        }
        return bArr;
    }

    static byte[] hex2bytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        str = str.toUpperCase();
        int length = str.length() / 2;
        char[] toCharArray = str.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (char2byte(toCharArray[i2 + 1]) | (char2byte(toCharArray[i2]) << 4));
        }
        return bArr;
    }


    private static String patchHexString(String str, int i) {
        StringBuilder str2 = new StringBuilder();
        for (int i2 = 0; i2 < i - str.length(); i2++) {
            str2.insert(0, "0");
        }
        String stringBuilder2 = str2 + str;
        return stringBuilder2.substring(0, i);
    }

    static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(bArr2));
            @SuppressLint("GetInstance") Cipher instance = Cipher.getInstance("DES");
            instance.init(1, generateSecret, secureRandom);
            return instance.doFinal(bArr);
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

}
