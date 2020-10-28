package com.magna.beaconlibrary.util;


public class FeasyBeaconUtil {
    public FeasyBeaconUtil() {
    }

    public static String moduleAdapter(String modle) {
        String[] var1 = f.a;
        String[] var2 = f.b;
        if (!"".equals(modle) && null != modle) {
            if (var1.length != var2.length) {
                return "unknow";
            } else {
                for (int var3 = 0; var3 < var1.length; ++var3) {
                    if (modle.equals(var1[var3])) {
                        if (!"".equals(var2[var3]) && null != var2[var3]) {
                            return getModelByFileName(var2[var3]);
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

    public static String getSPPAddrByBLEAddr(String addrBLE) {
        String var2 = addrBLE.substring(1, 2);
        int var1 = FileUtil.formattingOneHexToInt(var2);
        var1 ^= 1;
        var2 = FileUtil.formattingOneIntToStrings(var1);
        addrBLE = addrBLE.substring(0, 1) + var2 + addrBLE.substring(2);
        return addrBLE;
    }

    public static String getVersionByFileName(String fileName) {
        String[] var1 = fileName.split("_");
        return var1[6];
    }

    public static String getModelByFileName(String fileName) {
        String[] var1 = fileName.split("_");
        return var1[0];
    }

    public static boolean updateDetermine(String version, String typeNumberTemp) {
        String[] var2 = f.a;
        String[] var3 = f.b;
        if (null == typeNumberTemp) {
            return true;
        } else if (var2.length != var3.length) {
            return true;
        } else {
            for (int var4 = 0; var4 < var2.length; ++var4) {
                if (var2[var4].equals(typeNumberTemp)) {
                    try {
                        if (Integer.parseInt(version) < Integer.parseInt(FeasycomUtil.c(var3[var4]))) {
                            return true;
                        }

                        return false;
                    } catch (Exception var6) {
                        var6.printStackTrace();
                        return true;
                    }
                }
            }

            return true;
        }
    }
}
