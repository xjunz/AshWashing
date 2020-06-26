package com.ashwashing.pro.api.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    /**
     * DevName : 热水表留园E1-4层-411房
     * PrjName : 无锡职业技术学院
     * QUName : 留园
     * FJName : 411房
     * DevTypeName : 热水表
     * PrjID : 72
     * DevTypeID : 1
     * DevStatusID : 0
     */
    public String DevName;
    public String DevMac;
    public String PrjId;
    private int PrjRate;

    public boolean hasPrjRate() {
        return PrjRate == 1;
    }

    public static DeviceInfo mock(String mac) {
        DeviceInfo info = new DeviceInfo();
        info.DevMac = mac;
        return info;
    }

    public void setHasPrjRate(boolean has) {
        PrjRate = has ? 1 : 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DeviceInfo) {
            return DevMac.equals(((DeviceInfo) obj).DevMac);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return DevMac.hashCode();
    }

}
