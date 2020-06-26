package com.ashwashing.pro.impl.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {

    private @NonNull
    BluetoothDevice mServerDevice;
    private static final UUID SERVER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mSocket;
    private ConnectionHandler mHandler;

    private static class ConnectionHandler extends Handler {
        private BluetoothService.Callback callback;
        static final int MSG_CONN_START = 0x1a;
        static final int MSG_CONNECTED = 0x1b;
        static final int MSG_WRITTEN = 0x1c;
        static final int MSG_READ = 0x1e;
        static final int MSG_CONN_FAIL = 0x1f;
        static final int MSG_CONN_LOST = 0x20;

        ConnectionHandler(BluetoothService.Callback callback) {
            super(Looper.getMainLooper());
            this.callback = callback;
        }

        private void removeCallback() {
            callback = null;
        }

        @Override
        public void handleMessage(Message msg) {
            if (callback == null) {
                return;
            }
            switch (msg.what) {
                case MSG_CONN_START:
                    callback.onConnectStart();
                    break;
                case MSG_CONNECTED:
                    callback.onConnected();
                    break;
                case MSG_WRITTEN:
                    callback.onWritten((byte[]) msg.obj);
                    break;
                case MSG_CONN_FAIL:
                    callback.onConnectFail((Exception) msg.obj);
                    break;
                case MSG_CONN_LOST:
                    callback.onConnectionLost((Exception) msg.obj);
                    break;
                case MSG_READ:
                    callback.onRead((byte[]) msg.obj);
            }
        }
    }

    private void notifyConnLost(Exception e) {
        mHandler.removeMessages(ConnectionHandler.MSG_CONN_LOST);
        mHandler.obtainMessage(ConnectionHandler.MSG_CONN_LOST, e).sendToTarget();
    }

    private void notifyConnFail(Exception e) {
        mHandler.removeMessages(ConnectionHandler.MSG_CONN_FAIL);
        mHandler.obtainMessage(ConnectionHandler.MSG_CONN_FAIL, e).sendToTarget();
    }


    public boolean hasConnection() {
        return mTransferThread != null && mTransferThread.isAlive();
    }


    public BluetoothService(@NonNull BluetoothDevice server, @NonNull Callback callback) {
        mServerDevice = server;
        mHandler = new ConnectionHandler(callback);
    }

    private TransferThread mTransferThread;
    private ConnectThread mConnectThread;

    private class ConnectThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(ConnectionHandler.MSG_CONN_START);
            try {
                mSocket = mServerDevice.createRfcommSocketToServiceRecord(SERVER_UUID);
            } catch (IOException e) {
                notifyConnFail(e);
            }
            try {
                mSocket.connect();
                mHandler.sendEmptyMessage(ConnectionHandler.MSG_CONNECTED);
                mTransferThread = new TransferThread();
                mTransferThread.start();
            } catch (IOException connectException) {
                notifyConnFail(connectException);
            }
        }

        void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ;

    public void write(byte[] data) {
        if (hasConnection()) {
            mTransferThread.write(data);
        }
    }

    //close connection and remove callback
    public synchronized void purge() {
        mHandler.removeCallback();
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //close connection safely (without connection check
    public synchronized void safeClose() {
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public interface Callback {
        void onConnectStart();

        void onConnectFail(Exception e);

        void onConnectionLost(Exception e);

        void onConnected();

        void onWritten(byte[] data);

        void onRead(byte[] data);
    }

    public synchronized void connect() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
        mConnectThread = new ConnectThread();
        mConnectThread.start();
    }

    private class TransferThread extends Thread {
        private InputStream in;
        private OutputStream out;

        TransferThread() {
            try {
                in = mSocket.getInputStream();
                out = mSocket.getOutputStream();
            } catch (IOException e) {
                notifyConnLost(e);
            }
        }

        @Override
        public void run() {
            byte[] var1 = new byte[1024];
            label76:
            while (true) {
                byte[] var2 = null;

                label73:
                while (true) {
                    boolean var10001;
                    int var3;
                    try {
                        var3 = this.in.read(var1);
                    } catch (IOException var13) {
                        var10001 = false;
                        BluetoothService.this.notifyConnFail(var13);
                        break;
                    }

                    byte var4 = 0;
                    byte var5 = 0;
                    int var6 = 0;
                    byte[] var7;
                    if (var3 == 80) {
                        var7 = new byte[80];

                        while (true) {
                            var2 = var7;
                            if (var6 >= 80) {
                                continue label73;
                            }

                            var7[var6] = (byte) var1[var6];
                            ++var6;
                        }
                    } else if (var2 == null) {
                        var2 = new byte[var3];

                        for (var6 = var4; var6 < var3; ++var6) {
                            var2[var6] = (byte) var1[var6];
                        }

                        BluetoothService.this.mHandler.obtainMessage(ConnectionHandler.MSG_READ, var2).sendToTarget();
                    } else {
                        var7 = new byte[var3];

                        for (var6 = var5; var6 < var3; ++var6) {
                            var7[var6] = (byte) var1[var6];
                        }

                        var2 = byteMerger(var2, var7);
                        BluetoothService.this.mHandler.obtainMessage(ConnectionHandler.MSG_READ, var2).sendToTarget();
                    }
                    continue label76;
                }


                return;
            }
        }

        /*   @Override
           public void run() {
               final byte[] arrayOfByte2 = new byte[1024];
               while (true) {
                   byte[] bytes = null;
                   try {
                       int m = this.in.read(arrayOfByte2);
                       int j = 0;
                       int k = 0;
                       int i = 0;
                       byte[] arrayOfByte1;
                       if (m == 80) {
                           arrayOfByte1 = new byte[80];
                           while (true) {
                               bytes = arrayOfByte1;
                               if (i >= 80) {
                                   break;
                               }
                               arrayOfByte1[i] = arrayOfByte2[i];
                               i++;
                           }
                       }
                       if (bytes == null) {
                           bytes = new byte[m];
                           for (i = j; i < m; i++) {
                               bytes[i] = arrayOfByte2[i];
                           }
                           mHandler.obtainMessage(ConnectionHandler.MSG_READ, bytes).sendToTarget();
                       } else {
                           arrayOfByte1 = new byte[m];
                           for (i = k; i < m; i++) {
                               arrayOfByte1[i] = arrayOfByte2[i];
                           }
                           bytes = byteMerger(bytes, arrayOfByte1);
                           mHandler.obtainMessage(ConnectionHandler.MSG_READ, bytes).sendToTarget();
                       }
                   } catch (IOException e) {
                       notifyConnFail(e);
                       break;
                   }
               }
           }
   */
        void write(byte[] data) {
            try {
                out.write(data);
                mHandler.obtainMessage(ConnectionHandler.MSG_WRITTEN, data);
            } catch (IOException e) {
                notifyConnLost(e);
            }
        }
    }


    private static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

}
