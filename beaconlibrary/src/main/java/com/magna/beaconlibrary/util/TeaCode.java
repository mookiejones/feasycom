package com.magna.beaconlibrary.util;

import com.magna.beaconlibrary.bean.DfuFileInfo;

public class TeaCode {
    static {
        System.loadLibrary("feasycom");
    }

    public TeaCode() {
    }

    public native byte[] encrypt_bitstream(byte[] var1);

    public native byte[] decrypt_bitstream(byte[] var1);

    public native byte[] feasycom_decryption(byte[] var1);

    public native DfuFileInfo getDfuFileInformation(byte[] var1);
}
