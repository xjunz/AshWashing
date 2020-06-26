//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ashwashing.pro.impl.analyze;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DigitalTrans {
    public DigitalTrans() {
    }

    public static String AsciiStringToString(String var0) {
        int var1 = var0.length() / 2;
        String var2 = "";

        for(int var3 = 0; var3 < var1; ++var3) {
            int var4 = var3 * 2;
            char var5 = (char)((int)((long)hexStringToAlgorism(var0.substring(var4, var4 + 2))));
            StringBuilder var6 = new StringBuilder();
            var6.append(var2);
            var6.append(String.valueOf(var5));
            var2 = var6.toString();
        }

        return var2;
    }

    public static String StringToAsciiString(String var0) {
        int var1 = var0.length();
        String var2 = "";

        for(int var3 = 0; var3 < var1; ++var3) {
            String var4 = Integer.toHexString(var0.charAt(var3));
            StringBuilder var5 = new StringBuilder();
            var5.append(var2);
            var5.append(var4);
            var2 = var5.toString();
        }

        return var2;
    }

    public static String algorismToHEXString(int var0) {
        String var1 = Integer.toHexString(var0);
        String var2 = var1;
        if (var1.length() % 2 == 1) {
            StringBuilder var3 = new StringBuilder();
            var3.append("0");
            var3.append(var1);
            var2 = var3.toString();
        }

        return var2.toUpperCase();
    }

    public static String algorismToHEXString(long var0, int var2) {
        String var3 = Long.toHexString(var0);
        String var4 = var3;
        if (var3.length() % 2 == 1) {
            StringBuilder var5 = new StringBuilder();
            var5.append("0");
            var5.append(var3);
            var4 = var5.toString();
        }

        return patchHexString(var4.toUpperCase(), var2);
    }

    public static int binaryToAlgorism(String var0) {
        int var1 = var0.length();
        int var2 = var1;

        int var3;
        for(var3 = 0; var2 > 0; --var2) {
            char var4 = var0.charAt(var2 - 1);
            double var5 = (double)var3;
            double var7 = Math.pow(2.0D, (double)(var1 - var2));
            double var9 = (double)(var4 - 48);
            Double.isNaN(var9);
            Double.isNaN(var5);
            var3 = (int)(var5 + var7 * var9);
        }

        return var3;
    }

    public static final String byte2hex(byte[] var0) {
        if (var0 != null) {
            int var1 = 0;

            String var2;
            for(var2 = ""; var1 < var0.length; ++var1) {
                String var3 = Integer.toHexString(var0[var1] & 255);
                StringBuilder var4;
                if (var3.length() == 1) {
                    var4 = new StringBuilder();
                    var4.append(var2);
                    var4.append("0");
                    var4.append(var3);
                    var2 = var4.toString();
                } else {
                    var4 = new StringBuilder();
                    var4.append(var2);
                    var4.append(var3);
                    var2 = var4.toString();
                }
            }

            return var2.toUpperCase();
        } else {
            IllegalArgumentException var5 = new IllegalArgumentException("Argument b ( byte array ) is null! ");
            throw var5;
        }
    }

    public static String bytesToHexString(byte[] var0) {
        StringBuilder var1 = new StringBuilder("");
        if (var0 != null && var0.length > 0) {
            for(int var2 = 0; var2 < var0.length; ++var2) {
                String var3 = Integer.toHexString(var0[var2] & 255);
                if (var3.length() < 2) {
                    var1.append(0);
                }

                var1.append(var3);
            }

            return var1.toString();
        } else {
            return null;
        }
    }

    public static String bytetoString(byte[] var0) {
        int var1 = var0.length;
        String var2 = "";

        for(int var3 = 0; var3 < var1; ++var3) {
            char var4 = (char)var0[var3];
            StringBuilder var5 = new StringBuilder();
            var5.append(var2);
            var5.append(var4);
            var2 = var5.toString();
        }

        return var2;
    }

    public static byte charToByte(char var0) {
        return (byte)"0123456789ABCDEF".indexOf(var0);
    }

    public static byte[] copyByte(List<byte[]> var0) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();

        for(int var2 = 0; var2 < var0.size(); ++var2) {
            try {
                var1.write((byte[])var0.get(var2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return var1.toByteArray();
    }

    public static byte[] encrypt(byte[] var0, byte[] var1) {
        try {
            SecureRandom var2 = new SecureRandom();
            DESKeySpec var3 = new DESKeySpec(var1);
            SecretKey var5 = SecretKeyFactory.getInstance("DES").generateSecret(var3);
            Cipher var6 = Cipher.getInstance("DES");
            var6.init(1, var5, var2);
            var0 = var6.doFinal(var0);
            return var0;
        } catch (Throwable var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String getTimeID() {
        Date var0 = new Date(System.currentTimeMillis());

        String var1;
        try {
            SimpleDateFormat var3 = new SimpleDateFormat("yyMMddHHmmss");
            var1 = var3.format(var0);
        } catch (Exception var2) {
            var2.printStackTrace();
            var1 = "";
        }

        return var1;
    }

    public static final byte[] hex2byte(String var0) {
        if (var0.length() % 2 != 0) {
            IllegalArgumentException var8 = new IllegalArgumentException();
            throw var8;
        } else {
            char[] var1 = var0.toCharArray();
            byte[] var2 = new byte[var0.length() / 2];
            int var3 = var0.length();
            int var4 = 0;

            for(int var5 = 0; var4 < var3; ++var5) {
                StringBuilder var7 = new StringBuilder();
                var7.append("");
                int var6 = var4 + 1;
                var7.append(var1[var4]);
                var7.append(var1[var6]);
                var2[var5] = (new Integer(Integer.parseInt(var7.toString(), 16) & 255)).byteValue();
                var4 = var6 + 1;
            }

            return var2;
        }
    }

    public static int hexStringToAlgorism(String var0) {
        var0 = var0.toUpperCase();
        int var1 = var0.length();
        int var2 = var1;

        int var3;
        for(var3 = 0; var2 > 0; --var2) {
            char var4 = var0.charAt(var2 - 1);
            int var11;
            if (var4 >= '0' && var4 <= '9') {
                var11 = var4 - 48;
            } else {
                var11 = var4 - 55;
            }

            double var5 = (double)var3;
            double var7 = Math.pow(16.0D, (double)(var1 - var2));
            double var9 = (double)var11;
            Double.isNaN(var9);
            Double.isNaN(var5);
            var3 = (int)(var5 + var7 * var9);
        }

        return var3;
    }

    public static String hexStringToBinary(String var0) {
        String var1 = var0.toUpperCase();
        int var2 = var1.length();
        var0 = "";

        for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1.charAt(var3);
            StringBuilder var5;
            switch(var4) {
                case '0':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0000");
                    var0 = var5.toString();
                    break;
                case '1':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0001");
                    var0 = var5.toString();
                    break;
                case '2':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0010");
                    var0 = var5.toString();
                    break;
                case '3':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0011");
                    var0 = var5.toString();
                    break;
                case '4':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0100");
                    var0 = var5.toString();
                    break;
                case '5':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0101");
                    var0 = var5.toString();
                    break;
                case '6':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0110");
                    var0 = var5.toString();
                    break;
                case '7':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("0111");
                    var0 = var5.toString();
                    break;
                case '8':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("1000");
                    var0 = var5.toString();
                    break;
                case '9':
                    var5 = new StringBuilder();
                    var5.append(var0);
                    var5.append("1001");
                    var0 = var5.toString();
                    break;
                default:
                    switch(var4) {
                        case 'A':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1010");
                            var0 = var5.toString();
                            break;
                        case 'B':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1011");
                            var0 = var5.toString();
                            break;
                        case 'C':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1100");
                            var0 = var5.toString();
                            break;
                        case 'D':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1101");
                            var0 = var5.toString();
                            break;
                        case 'E':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1110");
                            var0 = var5.toString();
                            break;
                        case 'F':
                            var5 = new StringBuilder();
                            var5.append(var0);
                            var5.append("1111");
                            var0 = var5.toString();
                    }
            }
        }

        return var0;
    }

    public static byte[] hexStringToByte(String var0) {
        int var1 = var0.length() / 2;
        byte[] var2 = new byte[var1];
        var0 = hexStringToBinary(var0);

        int var5;
        for(int var3 = 0; var3 < var1; var3 = var5) {
            int var4 = var3 * 8;
            var5 = var3 + 1;
            var2[var3] = (byte)((byte)binaryToAlgorism(var0.substring(var4 + 1, var5 * 8)));
            if (var0.charAt(var4) == '1') {
                var2[var3] = (byte)((byte)(0 - var2[var3]));
            }
        }

        return var2;
    }

    public static byte[] hexStringToBytes(String var0) {
        if (var0 != null && !var0.equals("")) {
            var0 = var0.toUpperCase();
            int var1 = var0.length() / 2;
            char[] var2 = var0.toCharArray();
            byte[] var6 = new byte[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
                int var4 = var3 * 2;
                byte var5 = charToByte(var2[var4]);
                var6[var3] = (byte)((byte)(charToByte(var2[var4 + 1]) | var5 << 4));
            }

            return var6;
        } else {
            return null;
        }
    }

    public static String hexStringToString(String var0, int var1) {
        int var2 = var0.length() / var1;
        String var3 = "";

        int var5;
        for(int var4 = 0; var4 < var2; var4 = var5) {
            var5 = var4 + 1;
            char var6 = (char)hexStringToAlgorism(var0.substring(var4 * var1, var5 * var1));
            StringBuilder var7 = new StringBuilder();
            var7.append(var3);
            var7.append(var6);
            var3 = var7.toString();
        }

        return var3;
    }

    public static String longalgorismToHEXString(long var0, int var2) {
        String var3 = Long.toHexString(var0);
        String var4 = var3;
        if (var3.length() % 2 == 1) {
            StringBuilder var5 = new StringBuilder();
            var5.append("0");
            var5.append(var3);
            var4 = var5.toString();
        }

        return patchHexString(var4.toUpperCase(), var2);
    }

    public static int parseToInt(String var0, int var1) {
        int var2;
        try {
            var2 = Integer.parseInt(var0);
        } catch (NumberFormatException var3) {
            return var1;
        }

        var1 = var2;
        return var1;
    }

    public static int parseToInt(String var0, int var1, int var2) {
        try {
            var2 = Integer.parseInt(var0, var2);
        } catch (NumberFormatException var3) {
            return var1;
        }

        var1 = var2;
        return var1;
    }

    public static String patchHexString(String var0, int var1) {
        String var2 = "";

        StringBuilder var4;
        for(int var3 = 0; var3 < var1 - var0.length(); ++var3) {
            var4 = new StringBuilder();
            var4.append("0");
            var4.append(var2);
            var2 = var4.toString();
        }

        var4 = new StringBuilder();
        var4.append(var2);
        var4.append(var0);
        return var4.toString().substring(0, var1);
    }
}
