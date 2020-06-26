package com.ashwashing.pro.impl.cmd;

public interface Listener {

    /*public void afterCollectDeviceDatum(boolean success, final String ConsumeDT, final int PrjID, final int DeviceID, final int AccID, String GroupID, final int UseCount, String YKMoney, String UsedMoney, String Rate, String MacAddress) {
}*/

    void afterCollectDeviceDatum(boolean success, String consumeDT, int productID, int deviceID, int accountID, String groupID, int useCount, String perMoney, String usedMoney, String rate, String mac, byte[] raw);

    void afterAwakenDevice(boolean success, int deviceStatus, int deviceID, int productID, int accountID, byte[] mac, byte[] tac, int i5, int i6, int chargeMethod, int i8, String str);

    void afterStoreSpray(boolean success);

    void afterEndSpray(boolean success, int i);

    void startDeal(boolean success, byte[] bArr);

    void stopDeal(boolean success);

    void afterDispatchFeeRate(boolean success);
}
