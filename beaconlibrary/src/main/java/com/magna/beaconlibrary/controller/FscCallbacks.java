package com.magna.beaconlibrary.controller;

import android.bluetooth.BluetoothDevice;

public interface FscCallbacks {
    void startScan();

    void stopScan();

    void connectProgressUpdate(BluetoothDevice var1, int var2);
}
