package com.magna.beaconlibrary.bean;


public class AltBeacon extends FeasyBeacon {
    private String reservedId;
    private String manufacturerId;
    private String id;
    private int altBeaconRssi;

    public AltBeacon() {
    }

    public int getAltBeaconRssi() {
        return this.altBeaconRssi;
    }

    public void setAltBeaconRssi(int altBeaconRssi) {
        this.altBeaconRssi = altBeaconRssi;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturerId() {
        return this.manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getReservedId() {
        return this.reservedId;
    }

    public void setReservedId(String reservedId) {
        this.reservedId = reservedId;
    }
}

