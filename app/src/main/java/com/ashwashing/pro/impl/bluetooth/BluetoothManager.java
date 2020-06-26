package com.ashwashing.pro.impl.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BluetoothManager {
    private final static String TAG = BluetoothManager.class.getSimpleName();
    private static List<WaterDevice> mDevices = new ArrayList<>();
    private ScanListener mScanListener;
    private WeakReference<Context> mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mRegistered;
    private BluetoothDeviceFilter mFilter = new BluetoothDeviceFilter() {
        @Override
        public boolean accept(BluetoothDevice device) {
            return true;
        }
    };
    private static WaterDevice sAwaitingDevice;
    private BondListener mBondListener;

    public static WaterDevice  getAwaitingDevice() {
        return sAwaitingDevice;
    }

    public @Nullable
    WaterDevice getDeviceByAddress(String address) {
        if(address==null){
            return null;
        }
        int index = mDevices.indexOf(WaterDevice.mockDevice(address));
        if (index != -1) {
            return mDevices.get(index);
        }
        return null;
    }


    public void bondWaterDevice(@NonNull WaterDevice device) {
        sAwaitingDevice = device;
        cancelDiscovery();
        BluetoothDevice bluetoothDevice = device.getBluetoothDevice();
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            if (mBondListener != null)
                mBondListener.onBonded(device);
        } else {
            if (!bluetoothDevice.createBond()) {
                if (mBondListener != null)
                    mBondListener.onBondFailed();
            }
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    if (mScanListener != null)
                        mScanListener.onStartScan();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (mFilter.accept(device)) {
                        WaterDevice waterDevice = new WaterDevice(device);
                        if (!mDevices.contains(waterDevice)) {
                            mDevices.add(waterDevice);
                            if (mScanListener != null)
                                mScanListener.onFound(waterDevice);
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (mScanListener != null)
                        mScanListener.onStopScan();
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    BluetoothDevice deviceToBond = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        try {
                            confirmPairing(deviceToBond.getClass(), deviceToBond);
                            abortBroadcast();
                            deviceToBond.setPin("0000".getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        deviceToBond.setPin("0000".getBytes());
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    if (state == BluetoothDevice.BOND_BONDING) {
                        if (mBondListener != null)
                            mBondListener.onStartBond();
                    } else if (state == BluetoothDevice.BOND_BONDED) {
                        if (mBondListener != null)
                            mBondListener.onBonded(sAwaitingDevice);
                    } else if (state == BluetoothDevice.BOND_NONE) {
                        if (mBondListener != null)
                            mBondListener.onBondFailed();
                    }
                    break;
            }
        }
    };


    private BluetoothManager(@NonNull Context context) {
        mContext = new WeakReference<>(context);
        mDevices = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("This device does not support bluetooth feature!");
        }
    }

    public static BluetoothManager of(@NonNull Context context) {
        return new BluetoothManager(context);
    }


    public @NonNull
    List<WaterDevice> getDevices() {
        return mDevices;
    }


    public void startDiscovery() {
        registerReceiver();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mDevices.clear();
        mBluetoothAdapter.startDiscovery();
    }


    public void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    public void unregisterReceiver() {
        if (mRegistered) {
            mRegistered = false;
            mContext.get().unregisterReceiver(mReceiver);
        }
    }

    private void registerReceiver() {
        if (!mRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.get().registerReceiver(mReceiver, filter);
            mRegistered = true;
        }
    }


    public BluetoothAdapter getAdapter() {
        return mBluetoothAdapter;
    }


    public void setDeviceFilter(BluetoothDeviceFilter filter) {
        mFilter = filter;
    }


    public boolean isDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    public void setBondListener(@NonNull BondListener listener) {
        mBondListener = listener;
    }

    public void removeBondListener() {
        mBondListener = null;
    }

    public void removeScanListener() {
        mScanListener = null;
    }

    public void setScanListener(@NonNull ScanListener listener) {
        mScanListener = listener;
    }

    public interface BluetoothDeviceFilter {
        boolean accept(BluetoothDevice device);
    }


    public interface ScanListener {
        void onFound(WaterDevice waterDevice);

        void onStartScan();

        void onStopScan();
    }

    public interface BondListener {
        void onStartBond();

        void onBonded(WaterDevice device);

        void onBondFailed();
    }


    private static void confirmPairing(Class<?> cls, BluetoothDevice device) throws Exception {
        Method setPairingConfirmation = cls.getDeclaredMethod("confirmPairing", boolean.class);
        setPairingConfirmation.invoke(device, true);
    }


}
