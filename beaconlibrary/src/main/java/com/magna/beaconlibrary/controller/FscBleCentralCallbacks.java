package com.magna.beaconlibrary.controller;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;

import java.util.ArrayList;

public interface FscBleCentralCallbacks extends FscCallbacks {
    void blePeripheralFound(BluetoothDeviceWrapper var1, int var2, byte[] var3);

    void blePeripheralConnected(BluetoothGatt var1, BluetoothDevice var2);

    void blePeripheralDisonnected(BluetoothGatt var1, BluetoothDevice var2);

    void atCommandCallBack(String var1, String var2, String var3);

    void servicesFound(BluetoothGatt var1, BluetoothDevice var2, ArrayList<BluetoothGattService> var3);

    void characteristicForService(BluetoothGatt var1, BluetoothDevice var2, BluetoothGattService var3, BluetoothGattCharacteristic var4);

    void packetReceived(BluetoothGatt var1, BluetoothDevice var2, BluetoothGattService var3, BluetoothGattCharacteristic var4, String var5, String var6, byte[] var7, String var8);

    void readResponse(BluetoothGatt var1, BluetoothDevice var2, BluetoothGattService var3, BluetoothGattCharacteristic var4, String var5, String var6, byte[] var7, String var8);

    void sendPacketProgress(BluetoothGatt var1, BluetoothDevice var2, BluetoothGattCharacteristic var3, int var4, byte[] var5);

    void otaProgressUpdate(int var1, int var2);
}
