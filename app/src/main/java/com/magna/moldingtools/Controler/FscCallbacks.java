package com.magna.moldingtools.Controler;

import android.bluetooth.BluetoothDevice;

public interface FscCallbacks {
    void startScan();

    void stopScan();

    void connectProgressUpdate(BluetoothDevice var1, int var2);
}
