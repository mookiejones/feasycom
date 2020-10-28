package com.magna.beaconlibrary.bean;


public class EncryptInfo {
    static {
        System.loadLibrary("feasycom");
    }

    private String mPassword = "";
    private String mRandomNumber = "";
    private String mEncryptAlgorithm = "";

    private EncryptInfo(String[] info, String alg) {
        this.mPassword = info[0];
        this.mRandomNumber = info[1];
        this.mEncryptAlgorithm = alg;
    }

    private static native String[] gen(String var0, String var1) throws Exception;

    public static EncryptInfo create(String address, String pin, String alg) {
        String[] var3=new String[]{" ", " ", " "};

        EncryptInfo encryptInfo = new EncryptInfo(var3, alg);
        return encryptInfo;
    }

    public static native EncryptInfo createRandom(String var0);

    public boolean isEncryptAlgorithmUniversal() {
        return this.mEncryptAlgorithm != null && this.mEncryptAlgorithm.equals("Universal");
    }

    public boolean isEncryptAlgorithmBeacon() {
        return this.mEncryptAlgorithm != null && this.mEncryptAlgorithm.equals("Beacon");
    }

    public String getEncryptAlgorithm() {
        return this.mEncryptAlgorithm;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public String getRandomNumber() {
        return this.mRandomNumber.toUpperCase();
    }
}
