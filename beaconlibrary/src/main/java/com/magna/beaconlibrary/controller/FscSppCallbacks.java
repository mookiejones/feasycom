package com.magna.beaconlibrary.controller;

import android.bluetooth.BluetoothDevice;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;

public interface FscSppCallbacks extends FscCallbacks {
    void sppDeviceFound(BluetoothDeviceWrapper var1, int var2);

    void sppConnected(BluetoothDevice var1);

    void sppDisconnected(BluetoothDevice var1);

    void connectProgressUpdate(BluetoothDevice var1, int var2);

    void sendPacketProgress(BluetoothDevice var1, int var2, byte[] var3);

    void packetReceived(byte[] var1, String var2, String var3);

    void otaProgressUpdate(int var1, int var2);

    void atCommandCallBack(String var1, String var2, String var3);
}
