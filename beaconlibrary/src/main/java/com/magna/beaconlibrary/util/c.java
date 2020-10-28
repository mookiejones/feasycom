package com.magna.beaconlibrary.util;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.magna.beaconlibrary.bean.EddystoneBeacon;

public class c {
    public static final String a = "0201060303AAFE";
    public static final String b = "16AAFE";
    public static final String c = "http://www.";
    public static final String d = "https://www.";
    public static final String e = "http";
    public static final String f = "http://";
    public static final String g = "https://";
    public static final String h = "www.";
    public static final String i = ".com";
    public static final String j = ".org";
    public static final String k = ".edu";
    public static final String l = ".net";
    public static final String m = ".info";
    public static final String n = ".biz";
    public static final String o = ".gov";
    public static final String p = ".com/";
    public static final String q = ".org/";
    public static final String r = ".edu/";
    public static final String s = ".net/";
    public static final String t = ".info/";
    public static final String u = ".biz/";
    public static final String v = ".gov/";
    public static final byte w = 0;
    public static final byte x = 1;
    public static final byte y = 2;
    public static final byte z = 3;
    public static final byte A = 0;
    public static final byte B = 1;
    public static final byte C = 2;
    public static final byte D = 3;
    public static final byte E = 4;
    public static final byte F = 5;
    public static final byte G = 6;
    public static final byte H = 7;
    public static final byte I = 8;
    public static final byte J = 9;
    public static final byte K = 10;
    public static final byte L = 11;
    public static final byte M = 12;
    public static final byte N = 13;
    private static final String O = "EddystoneBeaconUtil";

    public c() {
    }

    public static EddystoneBeacon a(BluetoothDevice bluetoothDevice, int var1, byte[] var2) {
        int var3 = 0;
        byte var4 = 0;

        while (var3 < var2.length) {
            if ((var2[var3] & 255) + var3 + 1 > var2.length) {
                return null;
            }

            if (var2[var3] < 7) {
                var3 += (var2[var3] & 255) + 1;
            } else {
                if ((var2[var3 + 2] & 255) == 170 && (var2[var3 + 3] & 255) == 254) {
                    byte[] var5 = new byte[var2[var3]];
                    System.arraycopy(var2, var3 + 1, var5, 0, var5.length);
                    EddystoneBeacon eddystoneBeacon = new EddystoneBeacon();

                    eddystoneBeacon.setDeviceName(bluetoothDevice.getName());

                    eddystoneBeacon.setFrameTypeHex(Integer.toHexString(var2[var3 + 4]));
                    eddystoneBeacon.setFrameTypeString(a(var2[var3 + 4]));
                    eddystoneBeacon.setEddystoneRssi(var2[var3 + 5]);
                    if ("URL".equals(eddystoneBeacon.getFrameTypeString())) {
                        String var7 = a(var5, 5, var5.length - 5);
                        eddystoneBeacon.setDataValue(var7);
                        eddystoneBeacon.setUrl(var7);
                    } else {
                        byte[] var11 = new byte[var5.length - 5];
                        System.arraycopy(var5, 5, var11, 0, var11.length);
                        String var8 = a(var11).toLowerCase();
                        eddystoneBeacon.setDataValue(var8);

                        try {
                            eddystoneBeacon.setNameSpace(var8.substring(0, 20));
                            eddystoneBeacon.setInstance(var8.substring(20, 32));
                            eddystoneBeacon.setReserved(var8.substring(32, 36));
                        } catch (Exception var10) {
                            var10.printStackTrace();
                            return null;
                        }
                    }

                    FeasycomUtil.a(eddystoneBeacon, var2, var4, var2.length - 5);
                    return eddystoneBeacon;
                }

                var3 += (var2[var3] & 255) + 1;
            }
        }

        return null;
    }

    public static String a(byte var0) {
        switch (var0 & 255) {
            case 0:
                return "UID";
            case 16:
                return "URL";
            case 32:
                return "TLM";
            case 48:
                return "EID";
            case 64:
                return "RESERVED";
            default:
                return "Unknow";
        }
    }

    public static String a(String var0) {
        if (var0.contains("https://www.")) {
            return var0.replace("https://www.", "01");
        } else if (var0.contains("http://www.")) {
            return var0.replace("http://www.", "00");
        } else if (var0.contains("https://")) {
            return var0.replace("https://", "03");
        } else {
            return var0.contains("http://") ? var0.replace("http://", "02") : var0;
        }
    }

    public static String b(String var0) {
        Log.e("EddystoneBeaconUtil", "getFootByHex1: " + var0);
        String var1 = var0.replace(".", "_");
        Log.e("EddystoneBeaconUtil", "getFootByHex: " + var1);
        String[] var2 = var0.split("_");
        String var3 = "";

        for (int var4 = 1; var4 < var2.length - 1; ++var4) {
            var3 = var3 + "." + var2[var4];
        }

        if (var3.contains(".com/")) {
            var3 = var3.replace(".com/", "00");
        }

        if (var3.contains(".org/")) {
            var3 = var3.replace(".org/", "01");
        }

        if (var3.contains(".edu/")) {
            var3 = var3.replace(".edu/", "02");
        }

        if (var3.contains(".net/")) {
            var3 = var3.replace(".net/", "03");
        }

        if (var3.contains(".info/")) {
            var3 = var3.replace(".info/", "04");
        }

        if (var3.contains(".biz/")) {
            var3 = var3.replace(".biz/", "05");
        }

        if (var3.contains(".gov/")) {
            var3 = var3.replace(".gov/", "06");
        }

        if (var3.contains(".com")) {
            var3 = var3.replace(".com", "07");
        }

        if (var3.contains(".org")) {
            var3 = var3.replace(".org", "08");
        }

        if (var3.contains(".edu")) {
            var3 = var3.replace(".edu", "09");
        }

        if (var3.contains(".net")) {
            var3 = var3.replace(".net", "0a");
        }

        if (var3.contains(".info")) {
            var3 = var3.replace(".info", "0b");
        }

        if (var3.contains(".biz")) {
            var3 = var3.replace(".biz", "0c");
        }

        if (var3.contains(".gov")) {
            var3 = var3.replace(".gov", "0d");
        }

        Log.e("EddystoneBeaconUtil", "getFootByHex3: " + var2[0] + var3);
        return var2[0] + var3;
    }

    public static String a(byte[] var0, int var1, int var2) {
        byte[] var3 = new byte[var2];
        System.arraycopy(var0, var1, var3, 0, var2);
        String var4 = a(var3);
        StringBuffer var5 = new StringBuffer();
        var5.append(b(var3[0]));
        if (var3.length > 2) {
            for (int var6 = 1; var6 < var3.length; ++var6) {
                var5.append(c(var3[var6]));
            }
        }

        return var5.toString();
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

    public static String b(byte var0) {
        switch (var0 & 255) {
            case 0:
                return "http://www.";
            case 1:
                return "https://www.";
            case 2:
                return "http://";
            case 3:
                return "https://";
            default:
                byte[] var1 = new byte[]{(byte) var0};
                return new String(var1, 0, 1);
        }
    }

    public static String c(byte var0) {
        switch (var0 & 255) {
            case 0:
                return ".com/";
            case 1:
                return ".org/";
            case 2:
                return ".edu/";
            case 3:
                return ".net/";
            case 4:
                return ".info/";
            case 5:
                return ".biz/";
            case 6:
                return ".gov/";
            case 7:
                return ".com";
            case 8:
                return ".org";
            case 9:
                return ".edu";
            case 10:
                return ".net";
            case 11:
                return ".info";
            case 12:
                return ".biz";
            case 13:
                return ".gov";
            default:
                byte[] var1 = new byte[]{(byte) var0};
                return new String(var1, 0, 1);
        }
    }
}
