package com.ashwashing.pro.impl.cmd;

import com.ashwashing.pro.impl.bluetooth.BluetoothService;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {


    public static void collectDeviceDatum(BluetoothService bluetoothService) {
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 5, Processor.hex2bytes(Processor.decimal2hex(0, 2))));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
    }

    public static void awakenDevice(BluetoothService bluetoothService) {
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 4, Processor.hex2bytes(Processor.decimal2hex(0, 2))));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
    }


    public static void dispatchFeeRate(BluetoothService bluetoothService, byte[] mac, byte[] tac, List<byte[]> bytesList) {
        byte[] encrypt = Processor.encrypt(new byte[]{tac[0], tac[1], (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8}, new byte[]{mac[0], mac[1], mac[2], mac[3], mac[4], mac[5], (byte) 85, (byte) -86});
        byte[] obj = new byte[]{encrypt[0], encrypt[1], encrypt[2], encrypt[3]};
        bytesList.add(obj);
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 2, Processor.dumpBytesArray(bytesList)));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }

    }


    public static void storeSpray(BluetoothService bluetoothService, String str, int i, int i2, int i3, int i4) {
        byte[] hexStringToBytes = Processor.hex2bytes(str);
        byte[] hexStringToBytes2 = Processor.hex2bytes(Processor.decimal2hex((long) i, 8));
        byte[] hexStringToBytes3 = Processor.hex2bytes(Processor.decimal2hex((long) i2, 8));
        byte[] hexStringToBytes4 = Processor.hex2bytes(Processor.decimal2hex((long) i3, 8));
        byte[] hexStringToBytes5 = Processor.hex2bytes(Processor.decimal2hex((long) i4, 8));
        ArrayList<byte[]> arrayList = new ArrayList<>();
        arrayList.add(hexStringToBytes);
        arrayList.add(hexStringToBytes2);
        arrayList.add(hexStringToBytes3);
        arrayList.add(hexStringToBytes4);
        arrayList.add(hexStringToBytes5);
        str = Processor.bytes2hex(sendCommendBuffer((byte) 6, Processor.dumpBytesArray(arrayList)));
        String stringBuilder2 = "23" + Processor.string2ascii(str) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
    }

    public static void endSpray(BluetoothService bluetoothService) {
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 3, Processor.hex2bytes(Processor.decimal2hex(0, 2))));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
    }

    public static String clear(BluetoothService bluetoothService, boolean z) {
        byte[] bArr = new byte[1];
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 0, Processor.hex2bytes(Processor.decimal2hex(0, 2))));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
        return z ? bytesToHexString : null;
    }

    private static byte[] sendCommendBuffer(byte b, byte[] bArr) {
        int i;
        int i2;
        int i3 = 0;
        switch (b) {
            case (byte) 0:
                i = 1;
                i2 = 25;
                break;
            case (byte) 1:
                i = 8;
                i2 = 32;
                break;
            case (byte) 2:
                i = 48;
                i2 = 33;
                break;
            case (byte) 3:
                i = 1;
                i2 = 34;
                break;
            case (byte) 4:
                i = 1;
                i2 = 35;
                break;
            case (byte) 5:
                i = 1;
                i2 = 133;
                break;
            case (byte) 6:
                i = 22;
                i2 = 134;
                break;
            case (byte) 7:
                i = 3;
                i2 = 24;
                break;
            case (byte) 8:
                i = 64;
                i2 = 49;
                break;
            case (byte) 9:
                i = 3;
                i2 = 50;
                break;
            default:
                i = 0;
                i2 = 0;
                break;
        }
        int i4 = i + 3;
        i += 8;
        byte[] bArr2 = new byte[i];
        bArr2[0] = (byte) 96;
        bArr2[1] = (byte) 0;
        bArr2[2] = (byte) i4;
        bArr2[3] = Byte.MIN_VALUE;
        bArr2[4] = (byte) i2;
        bArr2[5] = (byte) 0;
        int i5 = 0;
        while (i3 < bArr.length) {
            bArr2[i3 + 6] = bArr[i3];
            i5 += bArr[i3];
            i3++;
        }
        bArr2[i - 2] = (byte) (((i5 + bArr2[3]) + bArr2[4]) + bArr2[5]);
        bArr2[i - 1] = (byte) 22;
        return bArr2;
    }

    public static String settingDevice(BluetoothService bluetoothService, int i, int i2, int i3, boolean z) {
        byte[] bArr;
        bArr = Processor.hex2bytes(Processor.decimal2hex((long) i, 2));
        byte[] hexStringToBytes = Processor.hex2bytes(Processor.decimal2hex((long) i2, 2));
        byte[] hexStringToBytes2 = Processor.hex2bytes(Processor.decimal2hex((long) i3, 2));
        ArrayList<byte[]> arrayList = new ArrayList<>();
        arrayList.add(bArr);
        arrayList.add(hexStringToBytes);
        arrayList.add(hexStringToBytes2);
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 7, Processor.copyByte(arrayList)));
        String stringBuilder2 = "23" + Processor.string2ascii(bytesToHexString) + "0a";
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
        return z ? bytesToHexString : null;
    }

    public static String dispatchProject(BluetoothService bluetoothService, int i, int i2, boolean z) {
        byte[] bArr;
        bArr = Processor.hex2bytes(Processor.decimal2hex((long) i, 8));
        byte[] hexStringToBytes = Processor.hex2bytes(Processor.decimal2hex((long) i2, 8));
        ArrayList arrayList = new ArrayList();
        arrayList.add(bArr);
        arrayList.add(hexStringToBytes);
        String bytesToHexString = Processor.bytes2hex(sendCommendBuffer((byte) 1, Processor.copyByte(arrayList)));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("23");
        stringBuilder.append(Processor.string2ascii(bytesToHexString));
        stringBuilder.append("0a");
        String stringBuilder2 = stringBuilder.toString();
        if (bluetoothService != null && bluetoothService.hasConnection()) {
            bluetoothService.write(Processor.hex2byte(stringBuilder2));
        }
        return z ? bytesToHexString : null;
    }
}
