package com.magna.beaconlibrary.util;


import android.bluetooth.BluetoothDevice;

import com.magna.beaconlibrary.bean.Ibeacon;

public class d {
    public static final String a = "020106";
    public static final String b = "FF4C000215";
    private static final String c = "IBeaconUtil";

    public d() {
    }

    public static Ibeacon a(BluetoothDevice var0, int var1, byte[] var2) {
        int var3 = 2;

        boolean var4;
        Ibeacon var5;
        for (var4 = false; var3 <= 5; ++var3) {
            if ((var2[var3 + 2] & 255) == 2 && (var2[var3 + 3] & 255) == 21) {
                var4 = true;
                break;
            }

            if ((var2[var3] & 255) == 45 && (var2[var3 + 1] & 255) == 36 && (var2[var3 + 2] & 255) == 191 && (var2[var3 + 3] & 255) == 22) {
                var5 = new Ibeacon();
                var5.setMajor(0);
                var5.setMinor(0);
                var5.setUuid("00000000-0000-0000-0000-000000000000");
                var5.setiBeaconRssi(-55);
                return var5;
            }

            if ((var2[var3] & 255) == 173 && (var2[var3 + 1] & 255) == 119 && (var2[var3 + 2] & 255) == 0 && (var2[var3 + 3] & 255) == 198) {
                var5 = new Ibeacon();
                var5.setMajor(0);
                var5.setMinor(0);
                var5.setUuid("00000000-0000-0000-0000-000000000000");
                var5.setiBeaconRssi(-55);
                return var5;
            }
        }

        if (!var4) {
            return null;
        } else {
            var5 = new Ibeacon();
            var5.setMajor((var2[var3 + 20] & 255) * 256 + (var2[var3 + 21] & 255));
            var5.setMinor((var2[var3 + 22] & 255) * 256 + (var2[var3 + 23] & 255));
            var5.setiBeaconRssi(var2[var3 + 24]);
            byte[] var6 = new byte[16];
            System.arraycopy(var2, var3 + 4, var6, 0, 16);
            String var7 = a(var6);
            StringBuilder var8 = new StringBuilder();
            var8.append(var7.substring(0, 8));
            var8.append("-");
            var8.append(var7.substring(8, 12));
            var8.append("-");
            var8.append(var7.substring(12, 16));
            var8.append("-");
            var8.append(var7.substring(16, 20));
            var8.append("-");
            var8.append(var7.substring(20, 32));
            var5.setUuid(var8.toString());
            FeasycomUtil.a(var5, var2, var3 + 20, var2.length - 5);
            if (var0 != null) {
                var5.setDeviceAddr(var0.getAddress());
                var5.setDeviceName(var0.getName());
            }

            return var5;
        }
    }

    private static String a(byte[] var0) {
        StringBuilder var1 = new StringBuilder("");
        if (var0 != null && var0.length > 0) {
            for (int var2 = 0; var2 < var0.length; ++var2) {
                int var3 = var0[var2] & 255;
                String var4 = Integer.toHexString(var3);
                if (var4.length() < 2) {
                    var1.append(0);
                }

                var1.append(var4);
            }

            return var1.toString();
        } else {
            return null;
        }
    }

    protected static double a(int var0, double var1) {
        if (var1 == 0.0D) {
            return -1.0D;
        } else {
            double var3 = var1 * 1.0D / (double) var0;
            if (var3 < 1.0D) {
                return Math.pow(var3, 10.0D);
            } else {
                double var5 = 0.89976D * Math.pow(var3, 7.7095D) + 0.111D;
                return var5;
            }
        }
    }
}
