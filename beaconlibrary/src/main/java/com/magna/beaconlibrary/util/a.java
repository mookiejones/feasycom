package com.magna.beaconlibrary.util;

import android.bluetooth.BluetoothDevice;

import com.magna.beaconlibrary.bean.AltBeacon;

public class a {
    public static final String a = "020106";
    public static final String b = "BEAC";
    public static final String c = "1BFF";
    private static final String d = "AltBeaconUtil";

    public a() {
    }

    public static AltBeacon a(BluetoothDevice var0, int var1, byte[] var2) {
        int var3 = 0;

        AltBeacon var8;
        for (var8 = null; var3 < 8; ++var3) {
            if ((var2[var3] & 255) == 27 && (var2[var3 + 1] & 255) == 255 && (var2[var3 + 4] & 255) == 190 && (var2[var3 + 5] & 255) == 172) {
                var8 = new AltBeacon();
                byte[] var7 = new byte[2];
                System.arraycopy(var2, var3 + 2, var7, 0, 2);
                var8.setManufacturerId(FileUtil.bytesToHex(var7, 2).replace(" ", ""));
                var7 = new byte[20];
                System.arraycopy(var2, var3 + 6, var7, 0, 20);
                var8.setId(FileUtil.bytesToHex(var7, 20).replace(" ", ""));
                var8.setAltBeaconRssi(  var2[var3 + 26]);
                var7 = new byte[1];
                System.arraycopy(var2, var3 + 27, var7, 0, 1);
                var8.setReservedId( FileUtil.bytesToHex(var7, 1).replace(" ", ""));
            }
        }

        if (null == var8) {
            return var8;
        } else {
            for (var3 = 27; var3 < var2.length - 5; ++var3) {
                if ((var2[var3] & 255) == 22 && (var2[var3 + 1] & 255) == 240 && (var2[var3 + 2] & 255) == 255) {
                    byte[] var5 = new byte[]{var2[var3 + 3]};
                    String var9 = FileUtil.bytesToHex(var5, var5.length);
                    var8.setmodule(FileUtil.stringToInt1("00 " + var9) + "");
                    byte[] var4 = new byte[]{var2[var3 + 4], var2[var3 + 5]};
                    String var10 = FileUtil.bytesToHex(var4, var4.length);
                    var8.setVersion( FileUtil.stringToInt1(var10) + "");
                    byte[] var6 = new byte[]{var2[var3 + 6]};
                    String var11 = FileUtil.bytesToHex(var6, var6.length);
                    if ("00".equals(var11)) {
                        var8.setConnectable(false);
                    } else if ("01".equals(var11)) {
                        var8.setConnectable(true);
                    }
                    break;
                }
            }

            if (var0 != null) {
                var8.setDeviceAddr( var0.getAddress());
                var8.setDeviceName(  var0.getName());
            }

            return var8;
        }
    }
}
