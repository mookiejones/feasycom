package com.magna.beaconlibrary.bean;

public class Monitor extends FeasyBeacon {
    private String temperature;
    private String humidity;

    public Monitor() {
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
