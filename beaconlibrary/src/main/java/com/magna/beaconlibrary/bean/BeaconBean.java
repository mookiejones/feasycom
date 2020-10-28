package com.magna.beaconlibrary.bean;


import java.io.Serializable;

public class BeaconBean implements Serializable {
    private String index;
    private String beaconType;
    private String uuid;
    private String major;
    private String minor;
    private String url;
    private String nameSpace;
    private String instance;
    private String reserved;
    private String id1;
    private String id2;
    private String id3;
    private String manufacturerId;
    private String manufacturerReserved;
    private String power;
    private boolean connectable;
    private boolean enable;
    private String version;

    public BeaconBean() {
    }

    public BeaconBean(String index, String beaconType) {
        this.index = index;
        this.beaconType = beaconType;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isConnectable() {
        return this.connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getBeaconType() {
        return this.beaconType;
    }

    public void setBeaconType(String beaconType) {
        this.beaconType = beaconType;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return this.major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return this.minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getInstance() {
        return this.instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getReserved() {
        return this.reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getPower() {
        return this.power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getManufacturerId() {
        return this.manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getManufacturerReserved() {
        return this.manufacturerReserved;
    }

    public void setManufacturerReserved(String manufacturerReserved) {
        this.manufacturerReserved = manufacturerReserved;
    }

    public String getId1() {
        return this.id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return this.id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getId3() {
        return this.id3;
    }

    public void setId3(String id3) {
        this.id3 = id3;
    }
}
