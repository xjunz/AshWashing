package com.ashwashing.pro.impl.bluetooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashwashing.pro.api.bean.DeviceInfo;


public class WaterDevice {
    public final static String TAG = WaterDevice.class.getSimpleName();
    private String mRawName;
    private String mAddress;
    private BluetoothDevice mDevice;
    private String mAlias;
    private DeviceInfo mInfo;


    public DeviceInfo getInfo() {
        return mInfo;
    }

    public void setInfo(DeviceInfo info) {
        mInfo = info;
    }

    private WaterDevice() {
    }

    ;

    WaterDevice(@NonNull BluetoothDevice device) {
        mDevice = device;
        mAddress = device.getAddress();
        mRawName = device.getName();
    }


    static WaterDevice mockDevice(String address) {
        WaterDevice mock = new WaterDevice();
        mock.mAddress = address;
        return mock;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mRawName;
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public String getAlias() {
        return mAlias;
    }


    public BluetoothDevice getBluetoothDevice() {
        return this.mDevice;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof WaterDevice) {
            return getAddress().equals(((WaterDevice) obj).getAddress());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }


}



