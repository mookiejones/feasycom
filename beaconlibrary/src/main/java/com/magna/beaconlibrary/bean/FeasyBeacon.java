package com.magna.beaconlibrary.bean;


import java.io.Serializable;

public class FeasyBeacon implements Serializable {
    public static final String BEACON_TYPE_EDDYSTONE_URL = "URL";
    public static final String BEACON_TYPE_EDDYSTONE_UID = "UID";
    public static final String BEACON_TYPE_IBEACON = "iBeacon";
    public static final String BEACON_TYPE_ALTBEACON = "AltBeacon";
    public static final String BEACON_TYPE_NULL = "";
    public static final int BEACON_COUNT = 10;
    public static final String BLE_KEY_WAY = "2";
    public static final int ENCRYPT_WAY = 2;
    public static final String ENCRYPT_BEACON = "Beacon";
    public static final String ENCRYPT_UNIVERSAL = "Universal";
    private boolean keycfg;
    private boolean gsensor;
    private boolean buzzer;
    private boolean led;
    private boolean connectable;
    private String module;
    private String version;
    private String deviceName;
    private String deviceAddr;
    private String battery = "";
    private String encryptionWay;
    private String topic;

    public FeasyBeacon() {
    }

    public String getBattery() {
        return this.battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getmodule() {
        return this.module;
    }

    public void setmodule(String module) {
        this.module = module;
    }

    public boolean isConnectable() {
        return this.connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddr() {
        return this.deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getEncryptionWay() {
        return this.encryptionWay;
    }

    public void setEncryptionWay(String encryptionWay) {
        this.encryptionWay = encryptionWay;
    }

    public boolean getKeycfg() {
        return this.keycfg;
    }

    public void setKeycfg(boolean keycfg) {
        this.keycfg = keycfg;
    }

    public boolean getGsensor() {
        return this.gsensor;
    }

    public void setGsensor(boolean gsensor) {
        this.gsensor = gsensor;
    }

    public boolean getBuzzer() {
        return this.buzzer;
    }

    public void setBuzzer(boolean buzzer) {
        this.buzzer = buzzer;
    }

    public boolean getLed() {
        return this.led;
    }

    public void setLed(boolean led) {
        this.led = led;
    }
}
