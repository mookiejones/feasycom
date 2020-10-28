package com.magna.beaconlibrary.bean;


public class EncryptAlgorithm {
    public EncryptAlgorithm() {
    }

    public static class Beacon {
        public static final String a = "Beacon";

        public Beacon() {
        }

        public static native String parseRandomNumber(String var0);

        public static native boolean randomNumberMatches(EncryptInfo var0, String var1);
    }

    public static class Universal {
        public static final String a = "Universal";

        public Universal() {
        }

        public static native String parseRandomNumber(String var0);

        public static native boolean randomNumberMatches(EncryptInfo var0, String var1);
    }
}
