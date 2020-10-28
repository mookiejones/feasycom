package com.magna.beaconlibrary.controller;


import com.magna.beaconlibrary.bean.BeaconBean;
import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.FeasyBeacon;

public interface FscBeaconApi {
    int BEACON_AMOUNT = 10;

    boolean isBtEnabled();

    boolean checkBleHardwareAvailable();

    void initialize();

    void setCallbacks(FscBeaconCallbacks var1);

    boolean connect(BluetoothDeviceWrapper var1, String var2);

    void disconnect();

    void startScan(int var1);

    void stopScan();

    boolean isConnected();

    void startGetDeviceInfo(String var1);

    void startGetDeviceInfo(String var1, FeasyBeacon var2);

    void startGetDeviceInfo();

    void setDeviceName(String var1);

    void setFscPin(String var1);

    void setBroadcastInterval(String var1);

    void setTxPower(String var1);

    void setExtend(String var1);

    void setConnectable(boolean var1);

    void setGscfg(String var1, String var2);

    void setKeycfg(String var1, String var2);

    void setBuzzer(boolean var1);

    void setLed(String var1);

    boolean isBeaconInfoFull();

    boolean addBeaconInfo(BeaconBean var1);

    boolean deleteBeaconInfo(String var1);

    BeaconBean getBeaconInfo(String var1);

    boolean updateBeaconInfo(BeaconBean var1, String var2);

    void saveIp(String var1);

    void saveBeaconInfo();

    void setFeasyBeacon(FeasyBeacon var1);

    void setRemoteAp(String var1, String var2);

    void getMac();
}
