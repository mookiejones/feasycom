package com.magna.beaconlibrary.bean;


public class EddystoneBeacon extends FeasyBeacon {
    private final String TAG = "FscEddystone";
    private String frameTypeString;
    private String frameTypeHex;
    private int eddystoneRssi;
    private String dataValue;
    private String url;
    private String nameSpace;
    private String instance;
    private String reserved;

    public EddystoneBeacon() {
    }

    public int getEddystoneRssi() {
        return this.eddystoneRssi;
    }

    public void setEddystoneRssi(int eddystoneRssi) {
        this.eddystoneRssi = eddystoneRssi;
    }

    public String getFrameTypeString() {
        return this.frameTypeString;
    }

    public void setFrameTypeString(String frameTypeString) {
        this.frameTypeString = frameTypeString;
    }

    public String getFrameTypeHex() {
        return this.frameTypeHex;
    }

    public void setFrameTypeHex(String frameTypeHex) {
        if (frameTypeHex.length() == 1) {
            frameTypeHex = "0" + frameTypeHex;
        }

        this.frameTypeHex = frameTypeHex;
    }

    public String getDataValue() {
        return this.dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
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
}
