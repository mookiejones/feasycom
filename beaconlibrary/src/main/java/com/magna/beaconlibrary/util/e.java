package com.magna.beaconlibrary.util;


import android.bluetooth.BluetoothDevice;

import com.magna.beaconlibrary.bean.Monitor;

public class e {
    private static final String a = "MonitorUtil";

    public e() {
    }

    public static Monitor a(BluetoothDevice var0, int var1, byte[] var2) {
        int var3 = 0;

        while (var3 < var2.length - 1) {
            if ((var2[var3] & 255) == 0) {
                return null;
            }

            if ((var2[var3 + 1] & 255) != 255) {
                var3 += (var2[var3] & 255) + 1;
                if (var3 >= var2.length - 1) {
                    return null;
                }
            } else {
                if ((var2[var3 + 2] & 255) == 255 && (var2[var3 + 3] & 255) == 240) {
                    byte[] var4 = new byte[4];
                    System.arraycopy(var2, var3 + 7, var4, 0, (var2[var3] & 255) - 6);
                    Monitor var5 = new Monitor();
                    var5.setTemperature(Integer.toHexString(var4[0] & 255) + "." + Integer.toHexString(var4[1] & 255));
                    var5.setHumidity(Integer.toHexString(var4[2] & 255) + "." + Integer.toHexString(var4[3] & 255));
                    return var5;
                }

                var3 += (var2[var3] & 255) + 1;
                if (var3 >= var2.length - 1) {
                    return null;
                }
            }
        }

        return null;
    }
}
