package com.magna.beaconlibrary.controller;


import android.bluetooth.BluetoothDevice;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;

public class FscSppCallbacksImp implements FscSppCallbacks {
    public FscSppCallbacksImp() {
    }

    public void sppDeviceFound(BluetoothDeviceWrapper device, int rssi) {
    }

    public void startScan() {
    }

    public void stopScan() {
    }

    public void sppConnected(BluetoothDevice device) {
    }

    public void sppDisconnected(BluetoothDevice device) {
    }

    public void connectProgressUpdate(BluetoothDevice device, int status) {
    }

    public void sendPacketProgress(BluetoothDevice device, int percentage, byte[] sendByte) {
    }

    public void packetReceived(byte[] dataByte, String dataString, String dataHexString) {
    }

    public void otaProgressUpdate(int percentage, int status) {
    }

    public void atCommandCallBack(String command, String param, String status) {
    }
}
