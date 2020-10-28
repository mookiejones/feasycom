package com.magna.beaconlibrary.util;


import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.Keep;

import com.magna.beaconlibrary.bean.BeaconBean;
import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.FeasyBeacon;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class FeasycomUtil {
    private static final String c = "FeasycomUtil";
    public static LinkedBlockingQueue<Byte> a = new LinkedBlockingQueue(81920);
    @Keep
    public static ArrayList<Byte> byteFifo1 = new ArrayList();
    public static ArrayList<Byte> b = new ArrayList();



    public static byte[] a(int var0) {
        int var2 = a.size() > var0 ? var0 : a.size();
        if (var2 <= 0) {
            return null;
        } else {
            byte[] var1 = new byte[var2];

            for (int var3 = 0; var3 < var1.length; ++var3) {
                try {
                    var1[var3] = a.take();
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }
            }

            return var1;
        }
    }

    public static boolean a(byte[] var0) {
        if (var0 == null) {
            return false;
        } else {
            for (int var1 = 0; var1 < var0.length; ++var1) {
                try {
                    a.put(var0[var1]);
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }
            }

            return true;
        }
    }

    public static void a(ArrayList<Byte> var0, byte[] var1) {
        if (var1 != null) {
            if (var1.length != 0) {
                if (var0 != null) {
                    for (int var2 = 0; var2 < var1.length; ++var2) {
                        var0.add(var1[var2]);
                    }

                }
            }
        }
    }

    public static byte[] a(ArrayList<Byte> var0) {
        if (var0 == null) {
            return null;
        } else if (var0.size() == 0) {
            return null;
        } else {
            byte[] var1 = new byte[var0.size()];

            for (int var2 = 0; var2 < var0.size(); ++var2) {
                var1[var2] = var0.get(var2);
            }

            return var1;
        }
    }

    public static void b(ArrayList<Byte> var0) {
        var0.clear();
    }

    public static synchronized void a(BluetoothDeviceWrapper bluetoothDeviceWrapper, byte[] var1) {
        boolean var4 = false;

        for (int var6 = 0; var6 < var1.length; ++var6) {
            int var3 = FileUtil.byteToInt_2(var1[var6]);
            if (var3 == 0) {
                return;
            }

            if (var3 > var1.length - var6) {
                return;
            }

            int var2 = FileUtil.byteToInt_2(var1[var6 + 1]);

            byte[] var5;
            try {
                var5 = new byte[var3 - 1];
                System.arraycopy(var1, var6 + 1 + 1, var5, 0, var3 - 1);
            } catch (Exception var9) {
                var9.printStackTrace();
                return;
            }

            if (var2 == 1) {
                bluetoothDeviceWrapper.setFlag(FileUtil.bytesToHex(var5, var5.length));
            } else if (var2 == 9) {
                bluetoothDeviceWrapper.setCompleteLocalName(new String(var5));
            } else if (var2 == 2) {
                byte var7 = var5[0];
                var5[0] = var5[1];
                var5[1] = var7;
                bluetoothDeviceWrapper.setIncompleteServiceUUIDs_16bit(FileUtil.bytesToHex(var5, var5.length));
            } else if (var2 == 6) {
                byte[] var10 = new byte[var5.length];

                for (int var8 = 0; var8 < var5.length; ++var8) {
                    var10[var5.length - 1 - var8] = var5[var8];
                }

                bluetoothDeviceWrapper.setIncompleteServiceUUIDs_128bit(FileUtil.bytesToHex(var10, var10.length));
            } else if (var2 == 22) {
                bluetoothDeviceWrapper.setServiceData(FileUtil.bytesToHex(var5, var5.length));
            } else if (var2 == 255) {
                bluetoothDeviceWrapper.setManufacturerSpecificData(FileUtil.bytesToHex(var5, var5.length));
            } else if (var2 == 10) {
                bluetoothDeviceWrapper.setTxPowerLevel(FileUtil.bytesToHex(var5, var5.length));
            }

            var6 += var3;
        }

    }

    public static synchronized boolean a(FeasyBeacon var0, byte[] var1, int var2, int var3) {
        while (var2 < var3 && ((var1[var2] & 255) != 22 || (var1[var2 + 1] & 255) != 240 || (var1[var2 + 2] & 255) != 255)) {
            ++var2;
        }

        if (var2 == var3) {
            return false;
        } else {
            byte[] var4 = new byte[]{var1[var2 + 3]};
            String var5 = FileUtil.bytesToHex(var4, var4.length);
            var0.setmodule(FileUtil.stringToInt1("00 " + var5) + "");
            byte[] var6 = new byte[]{var1[var2 + 4], var1[var2 + 5]};
            String var7 = FileUtil.bytesToHex(var6, var6.length);
            var0.setVersion(FileUtil.stringToInt1(var7) + "");
            byte[] var8 = new byte[]{var1[var2 + 6]};
            String var9 = FileUtil.bytesToHex(var8, var8.length).replace(" ", "");
            var0.setEncryptionWay(var9);
            if (var9.contains("00")) {
                var0.setConnectable(false);
            } else if (var9.contains("01")) {
                var0.setConnectable(true);
            }

            byte[] var10 = new byte[]{var1[var2 + 13]};
            String var11 = Integer.valueOf(var10[0]).toString();
            var0.setBattery(var11);
            return true;
        }
    }

    public static synchronized FeasyBeacon a(BluetoothDevice var0, int var1, byte[] var2) {
        FeasyBeacon var3 = new FeasyBeacon();
        return a(var3, var2, 0, var2.length - 5) ? var3 : null;
    }

    public static String a(String var0) {
        String[] var1 = f.a;
        String[] var2 = f.b;
        if (!"".equals(var0) && null != var0) {
            if (var1.length != var2.length) {
                return "unknow";
            } else {
                for (int var3 = 0; var3 < var1.length; ++var3) {
                    if (var0.equals(var1[var3])) {
                        if (!"".equals(var2[var3]) && null != var2[var3]) {
                            return d(var2[var3]);
                        }

                        return "unknow";
                    }
                }

                return "unknow";
            }
        } else {
            return "unknow";
        }
    }

    public static String b(String var0) {
        String var2 = var0.substring(1, 2);
        int var1 = FileUtil.formattingOneHexToInt(var2);
        var1 ^= 1;
        var2 = FileUtil.formattingOneIntToStrings(var1);
        var0 = var0.substring(0, 1) + var2 + var0.substring(2);
        return var0;
    }

    public static String c(String var0) {
        String[] var1 = var0.split("_");
        return var1[6];
    }

    public static String d(String var0) {
        String[] var1 = var0.split("_");
        return var1[0];
    }

    public static String e(String var0) {
        StringBuffer var1 = new StringBuffer();
        var1.append("V");
        if (var0.length() != 3) {
            var1.append("-unknow");
            return new String(var1);
        } else {
            for (int var2 = 0; var2 < var0.length(); ++var2) {
                var1.append(var0.substring(var2, var2 + 1));
                if (var2 < var0.length() - 1) {
                    var1.append(".");
                }
            }

            return new String(var1);
        }
    }


    public static native String checkLength(String var0, int var1, boolean var2);

    public static boolean a(String var0, ArrayList<BeaconBean> var1) {
        String[] var11 = var0.split("\r\n\r\n");
        int var12 = 0;

        for (int var13 = 0; var12 < 10; ++var13) {
            String var2 = "";
            BeaconBean var14 = var1.get(var13);

            try {
                String[] var15 = var11[var12].split(",");
                if ("1".equals(var15[3])) {
                    var14.setEnable(true);
                } else {
                    var14.setEnable(false);
                }

                if (var15[2].length() == 2) {
                    --var13;
                } else {
                    String var3;
                    int var4;
                    String var5;
                    String var6;
                    String var7;
                    if ("ff4c000215".equals(var15[2].substring(8, 18).toLowerCase())) {
                        var2 = "iBeacon";
                        var7 = var15[2].substring(18, 50).toLowerCase();
                        var5 = var15[2].substring(50, 54);
                        var6 = var15[2].substring(54, 58);
                        var3 = var15[2].substring(58, 60);
                        var14.setBeaconType(var2);
                        var14.setUuid(var7);
                        var14.setMajor(Integer.valueOf(FileUtil.stringToInt1(var5)).toString());
                        var14.setMinor(Integer.valueOf(FileUtil.stringToInt1(var6)).toString());
                        var4 = FileUtil.formattingHexToInt(var3);
                        if (var4 > 128) {
                            var4 = (short) (Integer.valueOf("ff" + var3, 16) & '\uffff');
                        }

                        var14.setPower(var4 + "");
                    } else if ("1bff".equals(var15[2].substring(6, 10).toLowerCase()) && "beac".equals(var15[2].substring(14, 18).toLowerCase())) {
                        var2 = "AltBeacon";
                        String var8 = var15[2].substring(10, 14);
                        var7 = var15[2].substring(18, 50).toLowerCase();
                        var5 = var15[2].substring(50, 54).toLowerCase();
                        var6 = var15[2].substring(54, 58).toLowerCase();
                        var3 = var15[2].substring(58, 60);
                        String var9 = var15[2].substring(60, 62).toLowerCase();
                        var14.setBeaconType(var2);
                        var14.setManufacturerId(Integer.valueOf(FileUtil.stringToInt1(var8)).toString());
                        var14.setId1(var7);
                        var14.setId2(var5);
                        var14.setId3(var6);
                        var4 = FileUtil.formattingHexToInt(var3);
                        if (var4 > 128) {
                            var4 = (short) (Integer.valueOf("ff" + var3, 16) & '\uffff');
                        }

                        var14.setPower(var4 + "");
                        var14.setManufacturerReserved(var9);
                    } else if (var15[2].contains("16AAFE".toUpperCase()) || var15[2].contains("16AAFE".toLowerCase())) {
                        if (var15[2].substring(22, 24).equals("20")) {
                            String var16 = var15[2].substring(24, 26);
                            var14.setVersion(var16);
                        } else {
                            var3 = var15[2].substring(24, 26);
                            var4 = FileUtil.formattingHexToInt(var3);
                            if (var4 > 128) {
                                var4 = (short) (Integer.valueOf("ff" + var3, 16) & '\uffff');
                            }

                            var14.setPower(var4 + "");
                        }

                        String var10;
                        if (var15[2].substring(22, 24).equals("00")) {
                            var2 = "UID";
                            var10 = var15[2].substring(26, var15[2].length());
                            var14.setBeaconType(var2);
                            var14.setNameSpace(var10.substring(0, 20).toLowerCase());
                            var14.setInstance(var10.substring(20, 32).toLowerCase());
                            var14.setReserved(var10.substring(32, 36).toLowerCase());
                        } else if (var15[2].substring(22, 24).equals("10")) {
                            var2 = "URL";
                            byte[] var20 = FileUtil.hexToByte(var15[2].substring(26, var15[2].length()));
                            var10 = com.magna.beaconlibrary.util.c.b(var20[0]);

                            for (int var17 = 1; var17 < var20.length; ++var17) {
                                Log.e("FeasycomUtil", "setBroadcastInfo: " + String.valueOf(var20[var17]));
                                var10 = var10 + com.magna.beaconlibrary.util.c.c(var20[var17]);
                            }

                            var14.setBeaconType(var2);
                            Log.e("FeasycomUtil", "setBroadcastInfo: " + var10);
                            var14.setUrl(var10);
                        } else if (!var15[2].substring(22, 24).equals("20") && var15[2].substring(22, 24).equals("30")) {
                        }
                    }
                }
            } catch (Exception var18) {
                var14.setBeaconType("");
            }

            ++var12;
        }

        for (var12 = 0; var12 < 10; ++var12) {
            BeaconBean var19 = var1.get(var12);
            if ("".equals(var19.getBeaconType()) || null == var19.getBeaconType()) {
                return true;
            }
        }

        return false;
    }

    public static String a(int var0, ArrayList<BeaconBean> var1) throws Exception {
        StringBuffer var14 = new StringBuffer();
        BeaconBean var15 = var1.get(var0);
        if (null == var15.getPower() || "".equals(var15.getPower())) {
            var15.setPower("0");
        }

        if (Integer.valueOf(var15.getPower()) >= -128 && Integer.valueOf(var15.getPower()) <= 127) {
            String var6 = Integer.toHexString(Integer.valueOf(var15.getPower()));
            var6 = var6.replace(" ", "");
            if (var6.length() == 1) {
                var6 = "0" + var6;
            }

            if (var6.length() == 8) {
                var6 = var6.substring(var6.length() - 2, var6.length());
            }

            if ("".equals(var15.getBeaconType())) {
                return "00";
            } else {
                String var2;
                if ("iBeacon".equals(var15.getBeaconType())) {
                    String var3 = var15.getUuid();
                    if ("".equals(var3) || null == var3) {
                        var3 = "00000000000000000000000000000000";
                    }

                    if (var3.length() != 32) {
                        return "00";
                    } else {
                        if ("".equals(var15.getMajor()) || null == var15.getMajor()) {
                            var15.setMajor("12345");
                        }

                        if ("".equals(var15.getMinor()) || null == var15.getMinor()) {
                            var15.setMinor("12345");
                        }

                        String var4 = Integer.toHexString(Integer.valueOf(var15.getMajor())).replace(" ", "");
                        var4 = checkLength(var4, 4, true);
                        String var5 = Integer.toHexString(Integer.valueOf(var15.getMinor())).replace(" ", "");
                        var5 = checkLength(var5, 4, true);
                        var2 = Integer.toHexString((var3.length() + var4.length() + var5.length() + var6.length() + 10) / 2);
                        var14.append("020106");
                        var14.append(var2);
                        var14.append("FF4C000215");
                        var14.append(var3);
                        var14.append(var4);
                        var14.append(var5);
                        var14.append(var6);
                        return new String(var14);
                    }
                } else {
                    if ("AltBeacon".equals(var15.getBeaconType())) {
                        var14.append("020106");
                        var14.append("1BFF");
                        if ("".equals(var15.getManufacturerId()) || null == var15.getManufacturerId()) {
                            var15.setManufacturerId("0");
                        }

                        String var7 = Integer.toHexString(Integer.valueOf(var15.getManufacturerId())).replace(" ", "");
                        var7 = checkLength(var7, 4, true);
                        var14.append(var7);
                        var14.append("BEAC");
                        if ("".equals(var15.getId1()) || null == var15.getId1()) {
                            var15.setId1("00000000000000000000000000000000");
                        }

                        var14.append(var15.getId1());
                        if ("".equals(var15.getId2()) || null == var15.getId2()) {
                            var15.setId2("0000");
                        }

                        var14.append(var15.getId2());
                        if ("".equals(var15.getId3()) || null == var15.getId3()) {
                            var15.setId3("0000");
                        }

                        var14.append(var15.getId3());
                        var14.append(var6);
                        if ("".equals(var15.getManufacturerReserved()) || null == var15.getManufacturerReserved()) {
                            var15.setManufacturerReserved("00");
                        }

                        var14.append(var15.getManufacturerReserved());
                    } else {
                        var14.append("0201060303AAFE");
                        String var8;
                        String var9;
                        if ("UID".equals(var15.getBeaconType())) {
                            var8 = "00";
                            if (null == var15.getNameSpace() || "".equals(var15.getNameSpace())) {
                                var15.setNameSpace("00000000000000000000");
                            }

                            if (null == var15.getInstance() || "".equals(var15.getInstance())) {
                                var15.setInstance("000000000000");
                            }

                            if (null == var15.getReserved() || "".equals(var15.getReserved())) {
                                var15.setReserved("0000");
                            }

                            var9 = var15.getNameSpace() + var15.getInstance() + var15.getReserved();
                            if (var9.length() != 36) {
                                return "00";
                            }

                            var2 = Integer.toHexString(var9.length() / 2 + 1 + 3 + var6.length() / 2);
                            var2 = checkLength(var2, 2, true);
                            var14.append(var2);
                            var14.append("16AAFE");
                            var14.append(var8);
                            var14.append(var6);
                            var14.append(var9);
                        } else {
                            if ("URL".equals(var15.getBeaconType())) {
                                var8 = "10";
                                if (null == var15.getUrl() || "".equals(var15.getUrl())) {
                                    var15.setUrl("http://www.feasycom.com");
                                }

                                var9 = var15.getUrl();
                                String var10 = com.magna.beaconlibrary.util.c.a(var9);
                                Log.e("FeasycomUtil", "header_url: " + var9);
                                String var11 = com.magna.beaconlibrary.util.c.b(var10);
                                Log.e("FeasycomUtil", "header_url_foot: " + var11);
                                if (var10.equals(var11)) {
                                    byte[] var12 = var11.substring(2, var11.length()).getBytes();
                                    String var13 = FileUtil.bytesToHex(var12, var12.length).replace(" ", "");
                                    var2 = Integer.toHexString((var13.length() + 2 + 6 + 2 + var6.length()) / 2);
                                    var2 = checkLength(var2, 2, true);
                                    var14.append(var2);
                                    var14.append("16AAFE");
                                    var14.append(var8);
                                    var14.append(var6);
                                    var14.append(var11.substring(0, 2));
                                    var14.append(var13);
                                } else {
                                    int var16 = 0;
                                    Log.e("FeasycomUtil", "header_url_foot: " + var11);
                                    var11 = var11.replace("00", "0e").replace(" ", "");
                                    Log.e("FeasycomUtil", "header_url_foot: " + var11);
                                    String[] var17 = var11.split("0");
                                    Log.e("FeasycomUtil", "header_url_foot: " + String.valueOf(var17));

                                    int var18;
                                    for (var18 = 1; var18 < var17.length; ++var18) {
                                        var17[var18] = "0" + var17[var18];
                                    }

                                    for (var18 = 1; var18 < var17.length; ++var18) {
                                        var16 = var16 + var17[var18].length() - 2;
                                    }

                                    var16 = var16 + var17.length - 1 + 3 + 1 + 1;
                                    var2 = Integer.toHexString(var16);
                                    var2 = checkLength(var2, 2, true);
                                    if (var2.equals("0e")) {
                                        var14.append("{");
                                    } else {
                                        var14.append(var2);
                                    }

                                    var14.append("16AAFE");
                                    Log.e("FeasycomUtil", "getBroadcastHex: " + new String(var14));
                                    var14.append(var8);
                                    Log.e("FeasycomUtil", "getBroadcastHex: " + new String(var14));
                                    var14.append(var6);
                                    Log.e("FeasycomUtil", "getBroadcastHex: " + new String(var14));

                                    for (var18 = 1; var18 < var17.length; ++var18) {
                                        if (var17[var18].length() > 2) {
                                            byte[] var19 = var17[var18].substring(2, var17[var18].length()).getBytes();
                                            var14.append(var17[var18].substring(0, 2));
                                            var14.append(FileUtil.bytesToHex(var19, var19.length).replace(" ", ""));
                                        } else {
                                            var14.append(var17[var18]);
                                        }
                                    }
                                }

                                return (new String(var14)).replace("0e", "00").replace("0E", "00").replace("{", "0E");
                            }

                            if (!"TLM".equals(var15.getBeaconType()) && "EID".equals(var15.getBeaconType())) {
                            }
                        }
                    }

                    return new String(var14);
                }
            }
        } else {
            return "00";
        }
    }

    public static String b(int var0, ArrayList<BeaconBean> var1) {
        BeaconBean var2 = var1.get(var0);
        return var2.isEnable() ? "1" : "0";
    }
}
