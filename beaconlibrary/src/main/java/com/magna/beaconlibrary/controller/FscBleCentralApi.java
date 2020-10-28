package com.magna.beaconlibrary.controller;


import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Set;

public interface FscBleCentralApi {
    int PACKGE_SEND_FINISH = 100;
    int FIFO_SEND_FINISH = 101;
    int CHARACTERISTIC_WRITE_NO_RESPONSE = 1;
    int CHARACTERISTIC_WRITE = 2;
    int ENABLE_CHARACTERISTIC_NOTIFICATION = 3;
    int DISABLE_CHARACTERISTIC_NOTIFICATION = 4;
    int ENABLE_CHARACTERISTIC_INDICATE = 5;
    int DISABLE_CHARACTERISTIC_INDICATE = 6;

    boolean isBtEnabled();

    boolean checkBleHardwareAvailable();

    boolean initialize();

    void setCallbacks(FscBleCentralCallbacks var1);

    boolean connect(String var1);

    void disconnect();

    boolean startScan();

    boolean startScan(int var1);

    void stopScan();

    boolean isConnected();

    boolean send(byte[] var1);

    boolean setSendInterval(int var1);

    boolean cancleSendInterval();

    void stopSend();

    void read(BluetoothGattCharacteristic var1);

    boolean startOTA(byte[] var1, boolean var2);

    void sendATCommand(Set<String> var1);

    boolean setCharacteristic(BluetoothGattCharacteristic var1, int var2);
}
