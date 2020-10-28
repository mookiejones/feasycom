package com.magna.beaconlibrary.bean;


public class Ibeacon extends FeasyBeacon {
    private final String TAG = "FscIbeacon";
    private int major;
    private int minor;
    private String uuid;
    private int iBeaconRssi;

    public Ibeacon() {
    }

    public int getMajor() {
        return this.major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return this.minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getiBeaconRssi() {
        return this.iBeaconRssi;
    }

    public void setiBeaconRssi(int iBeaconRssi) {
        this.iBeaconRssi = iBeaconRssi;
    }
}
