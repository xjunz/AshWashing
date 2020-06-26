//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ashwashing.pro.impl.analyze;

import com.ashwashing.pro.impl.bluetooth.BluetoothService;
import com.ashwashing.pro.impl.cmd.Processor;

import java.util.ArrayList;
import java.util.List;

public class CMDUtils {
    public CMDUtils() {
    }

    public static String caijishuju(BluetoothService var0, boolean var1) {
        byte[] var2 = new byte[1];
        String var4 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)5, DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString(0L, 2))));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var4));
        var3.append("0a");
        String var5 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var5));
        }

        return var1 ? var4 : null;
    }

    public static String chaxueshebei(BluetoothService var0, boolean var1) {
        byte[] var2 = new byte[1];
        String var4 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)4, DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString(0L, 2))));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var4));
        var3.append("0a");
        String var5 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var5));
        }

        return var1 ? var4 : null;
    }

    public static String dealFinish(BluetoothService var0, int var1, int var2, boolean var3) {
        byte[] var4 = new byte[1];
        var4 = DigitalTrans.hexStringToByte(DigitalTrans.algorismToHEXString(0L, 2));
        byte[] var5 = new byte[1];
        var5 = DigitalTrans.hexStringToByte(DigitalTrans.algorismToHEXString((long)var1, 2));
        byte[] var6 = new byte[1];
        byte[] var7 = DigitalTrans.hexStringToByte(DigitalTrans.algorismToHEXString((long)var2, 2));
        ArrayList var11 = new ArrayList();
        var11.add(var4);
        var11.add(var5);
        var11.add(var7);
        String var8 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)9, DigitalTrans.copyByte(var11)));
        StringBuilder var9 = new StringBuilder();
        var9.append("23");
        var9.append(DigitalTrans.StringToAsciiString(var8));
        var9.append("0a");
        String var10 = var9.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var10));
        }

        return var3 ? var8 : null;
    }

    public static String dealStart(BluetoothService var0, String var1) {
        String var2 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)8, DigitalTrans.hexStringToBytes(var1)));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var2));
        var3.append("0a");
        var1 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var1));
        }

        return null;
    }

    public static String downFateToDev(BluetoothService var0, String var1) {
        String var2 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)2, DigitalTrans.hexStringToBytes(var1)));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var2));
        var3.append("0a");
        var1 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var1));
        }

        return null;
    }

    public static void dispatchFeeRate(BluetoothService bluetoothService, byte[] mac, byte[] tac, List<byte[]> bytesList) {
        byte[] encrypt = DigitalTrans.encrypt(new byte[]{tac[0], tac[1], (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8}, new byte[]{mac[0], mac[1], mac[2], mac[3], mac[4], mac[5], (byte) 85, (byte) -86});
        byte[] obj = new byte[]{encrypt[0], encrypt[1], encrypt[2], encrypt[3]};
        bytesList.add(obj);
        String bytesToHexString = DigitalTrans.bytesToHexString(sendCommendBuffer((byte) 2, DigitalTrans.copyByte(bytesList)));
        String stringBuilder2 = "23" + DigitalTrans.StringToAsciiString(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(DigitalTrans.hexStringToByte(stringBuilder2));
        }

    }

    public static String fanhuicunchu(BluetoothService var0, boolean var1, String var2, int var3, int var4, int var5, int var6) {
        byte[] var7 = new byte[6];
        byte[] var12 = DigitalTrans.hexStringToBytes(var2);
        var7 = new byte[4];
        var7 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var3, 8));
        byte[] var8 = new byte[4];
        var8 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var4, 8));
        byte[] var9 = new byte[4];
        var9 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var5, 8));
        byte[] var10 = new byte[4];
        byte[] var11 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var6, 8));
        ArrayList var15 = new ArrayList();
        var15.add(var12);
        var15.add(var7);
        var15.add(var8);
        var15.add(var9);
        var15.add(var11);
        var2 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)6, DigitalTrans.copyByte(var15)));
        StringBuilder var13 = new StringBuilder();
        var13.append("23");
        var13.append(DigitalTrans.StringToAsciiString(var2));
        var13.append("0a");
        String var14 = var13.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var14));
        }

        return var1 ? var2 : null;
    }

    public static String jieshufeilv(BluetoothService var0, boolean var1) {
        byte[] var2 = new byte[1];
        String var4 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)3, DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString(0L, 2))));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var4));
        var3.append("0a");
        String var5 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var5));
        }

        return var1 ? var4 : null;
    }

    public static String qingchushebei(BluetoothService var0, boolean var1) {
        byte[] var2 = new byte[1];
        String var4 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)0, DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString(0L, 2))));
        StringBuilder var3 = new StringBuilder();
        var3.append("23");
        var3.append(DigitalTrans.StringToAsciiString(var4));
        var3.append("0a");
        String var5 = var3.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var5));
        }

        return var1 ? var4 : null;
    }

    public static final byte[] sendCommendBuffer(byte var0, byte[] var1) {
        byte var2 = 0;
        byte var3;
        short var6;
        switch(var0) {
            case 0:
                var3 = 1;
                var6 = 25;
                break;
            case 1:
                var3 = 8;
                var6 = 32;
                break;
            case 2:
                var3 = 48;
                var6 = 33;
                break;
            case 3:
                var3 = 1;
                var6 = 34;
                break;
            case 4:
                var3 = 1;
                var6 = 35;
                break;
            case 5:
                var3 = 1;
                var6 = 133;
                break;
            case 6:
                var3 = 22;
                var6 = 134;
                break;
            case 7:
                var3 = 3;
                var6 = 24;
                break;
            case 8:
                var3 = 64;
                var6 = 49;
                break;
            case 9:
                var3 = 3;
                var6 = 50;
                break;
            default:
                var3 = 0;
                var6 = 0;
        }

        int var4 = var3 + 8;
        byte[] var5 = new byte[var4];
        var5[0] = (byte)96;
        var5[1] = (byte)0;
        var5[2] = (byte)((byte)(var3 + 3));
        var5[3] = (byte)-128;
        var5[4] = (byte)((byte)var6);
        var5[5] = (byte)0;
        int var7 = 0;

        for(int var8 = var2; var8 < var1.length; ++var8) {
            var5[var8 + 6] = (byte)var1[var8];
            var7 += var1[var8];
        }

        var5[var4 - 2] = (byte)((byte)(var7 + var5[3] + var5[4] + var5[5]));
        var5[var4 - 1] = (byte)22;
        return var5;
    }

    public static String settingDecive(BluetoothService var0, int var1, int var2, int var3, boolean var4) {
        byte[] var5 = new byte[1];
        var5 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var1, 2));
        byte[] var6 = new byte[1];
        var6 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var2, 2));
        byte[] var7 = new byte[1];
        var7 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var3, 2));
        ArrayList var8 = new ArrayList();
        var8.add(var5);
        var8.add(var6);
        var8.add(var7);
        String var9 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)7, DigitalTrans.copyByte(var8)));
        StringBuilder var10 = new StringBuilder();
        var10.append("23");
        var10.append(DigitalTrans.StringToAsciiString(var9));
        var10.append("0a");
        String var11 = var10.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var11));
        }

        return var4 ? var9 : null;
    }

    public static String xiafaxiangmu(BluetoothService var0, int var1, int var2, boolean var3) {
        byte[] var4 = new byte[4];
        var4 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var1, 8));
        byte[] var5 = new byte[4];
        byte[] var6 = DigitalTrans.hexStringToBytes(DigitalTrans.algorismToHEXString((long)var2, 8));
        ArrayList var8 = new ArrayList();
        var8.add(var4);
        var8.add(var6);
        String var7 = DigitalTrans.bytesToHexString(sendCommendBuffer((byte)1, DigitalTrans.copyByte(var8)));
        StringBuilder var9 = new StringBuilder();
        var9.append("23");
        var9.append(DigitalTrans.StringToAsciiString(var7));
        var9.append("0a");
        String var10 = var9.toString();
        if (var0 != null && var0.hasConnection()) {
            var0.write(DigitalTrans.hexStringToByte(var10));
        }

        return var3 ? var7 : null;
    }

    public class CmdType {
        public static final byte CMD_TYPE_CAIJIXIAOFEI = 5;
        public static final byte CMD_TYPE_CHAXUESHEBEI = 4;
        public static final byte CMD_TYPE_DEALEND = 9;
        public static final byte CMD_TYPE_DEALSTART = 8;
        public static final byte CMD_TYPE_FANHUIJILU = 6;
        public static final byte CMD_TYPE_JIESHUXIAOFEI = 3;
        public static final byte CMD_TYPE_QINGCHU = 0;
        public static final byte CMD_TYPE_SETTINGDECIVE = 7;
        public static final byte CMD_TYPE_XIAFAFEILV = 2;
        public static final byte CMD_TYPE_XIAFAXIANGMU = 1;

        public CmdType() {
        }
    }
}
