package com.magna.beaconlibrary.controller;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;

import java.util.ArrayList;

public class FscBeaconCallbacksImp implements FscBeaconCallbacks {
    public FscBeaconCallbacksImp() {
    }

    public void startScan() {
    }

    public void stopScan() {
    }

    public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {
    }

    public void blePeripheralConnected(BluetoothGatt gatt, BluetoothDevice device) {
    }

    public void blePeripheralDisonnected(BluetoothGatt gatt, BluetoothDevice device) {
    }

    public void atCommandCallBack(String command, String param, String status) {
    }

    public void servicesFound(BluetoothGatt gatt, BluetoothDevice device, ArrayList<BluetoothGattService> services) {
    }

    public void characteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
    }

    public void packetReceived(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, String hexString, byte[] rawValue, String timestamp) {
    }

    public void readResponse(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, String hexString, byte[] rawValue, String timestamp) {
    }

    public void sendPacketProgress(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattCharacteristic ch, int percentage, byte[] sendByte) {
    }

    public void otaProgressUpdate(int percentage, int status) {
    }

    public void deviceInfo(String parameterName, Object parameter) {
    }

    public void connectProgressUpdate(BluetoothDevice device, int status) {
    }
}
