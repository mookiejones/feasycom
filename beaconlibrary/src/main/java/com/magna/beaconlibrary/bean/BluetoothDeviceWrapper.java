package com.magna.beaconlibrary.bean;


import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class BluetoothDeviceWrapper implements Serializable {
    public static final int FLAG = 1;
    public static final int INCOMPLETE_SERVICE_UUIDS_16BIT = 2;
    public static final int INCOMPLETE_SERVICE_UUIDS_128BIT = 6;
    public static final int COMPLETE_LOCAL_NAME = 9;
    public static final int TX_POWER_LEVEL = 10;
    public static final int SERVICE_DATA = 22;
    public static final int MANUFACTURER_SPECIFIC_DATA = 255;
    public static String SPP_MODE = "SPP";
    public static String BLE_MODE = "BLE";
    private String address;
    private String name;
    private Integer rssi;
    private String model;
    private String advData;
    private int bondState;
    private String flag;
    private String incompleteServiceUUIDs_16bit;
    private String incompleteServiceUUIDs_128bit;
    private String completeLocalName;
    private String serviceData;
    private String txPowerLevel;
    private String manufacturerSpecificData;
    private Ibeacon iBeacon = null;
    private EddystoneBeacon gBeacon = null;
    private FeasyBeacon feasyBeacon = null;
    private AltBeacon altBeacon = null;
    private Monitor monitor = null;

    public BluetoothDeviceWrapper(String address) {
        this.address = address;
    }

    public BluetoothDeviceWrapper(BluetoothDevice device, int rssi, String model) {
        this.address = device.getAddress();
        this.name = device.getName();
        this.rssi = rssi;
        this.model = model;
        this.bondState = device.getBondState();
    }

    public Monitor getMonitor() {
        return this.monitor;
    }

    public void setMonitor(Monitor monitor) {
        if (monitor != null) {
            this.monitor = monitor;
        }

    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRssi() {
        return this.rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Ibeacon getiBeacon() {
        return this.iBeacon;
    }

    public void setiBeacon(Ibeacon iBeacon) {
        if (null != iBeacon) {
            this.iBeacon = iBeacon;
            this.gBeacon = null;
            this.altBeacon = null;
        }

    }

    public EddystoneBeacon getgBeacon() {
        return this.gBeacon;
    }

    public void setgBeacon(EddystoneBeacon gBeacon) {
        if (null != gBeacon) {
            this.gBeacon = gBeacon;
            this.iBeacon = null;
            this.altBeacon = null;
        }

    }

    public FeasyBeacon getFeasyBeacon() {
        return this.feasyBeacon;
    }

    public void setFeasyBeacon(FeasyBeacon feasyBeacon) {
        this.feasyBeacon = feasyBeacon;
    }

    public AltBeacon getAltBeacon() {
        return this.altBeacon;
    }

    public void setAltBeacon(AltBeacon altBeacon) {
        if (null != altBeacon) {
            this.altBeacon = altBeacon;
            this.iBeacon = null;
            this.gBeacon = null;
        }

    }

    public String getAdvData() {
        return this.advData;
    }

    public void setAdvData(String advData) {
        this.advData = advData;
    }

    public String getFlag() {
        return this.flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIncompleteServiceUUIDs_16bit() {
        return this.incompleteServiceUUIDs_16bit;
    }

    public void setIncompleteServiceUUIDs_16bit(String incompleteServiceUUIDs_16bit) {
        this.incompleteServiceUUIDs_16bit = incompleteServiceUUIDs_16bit;
    }

    public String getIncompleteServiceUUIDs_128bit() {
        return this.incompleteServiceUUIDs_128bit;
    }

    public void setIncompleteServiceUUIDs_128bit(String incompleteServiceUUIDs_128bit) {
        this.incompleteServiceUUIDs_128bit = incompleteServiceUUIDs_128bit;
    }

    public String getCompleteLocalName() {
        return this.completeLocalName;
    }

    public void setCompleteLocalName(String completeLocalName) {
        this.completeLocalName = completeLocalName;
    }

    public String getTxPowerLevel() {
        return this.txPowerLevel;
    }

    public void setTxPowerLevel(String txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
    }

    public String getManufacturerSpecificData() {
        return this.manufacturerSpecificData;
    }

    public void setManufacturerSpecificData(String manufacturerSpecificData) {
        this.manufacturerSpecificData = manufacturerSpecificData;
    }

    public String getServiceData() {
        return this.serviceData;
    }

    public void setServiceData(String serviceData) {
        this.serviceData = serviceData;
    }

    public int getBondState() {
        return this.bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }
}
