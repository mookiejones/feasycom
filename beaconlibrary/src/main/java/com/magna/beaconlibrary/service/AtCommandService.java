package com.magna.beaconlibrary.service;


import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.magna.beaconlibrary.controller.CommandState;
import com.magna.beaconlibrary.controller.FscBleCentralApiImp;
import com.magna.beaconlibrary.controller.FscBleCentralCallbacks;
import com.magna.beaconlibrary.controller.FscBleCentralCallbacksImp;
import com.magna.beaconlibrary.controller.FscSppApiImp;
import com.magna.beaconlibrary.controller.FscSppCallbacks;
import com.magna.beaconlibrary.controller.FscSppCallbacksImp;
import com.magna.beaconlibrary.util.FeasycomUtil;
import com.magna.beaconlibrary.util.LogUtil;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class AtCommandService extends Service implements CommandState {
    public static boolean a = false;
    public static boolean b = false;
    private final String c = "commandService";
    private final int p = 3000;
    private boolean d = true;
    private boolean e = true;
    private FscSppCallbacks f;
    private FscBleCentralCallbacks g;
    private HashMap<String, Integer> h;
    private ArrayList<String> i;
    private int j = 0;
    private Handler k = new Handler();
    private FscBleCentralApiImp l;
    private FscSppApiImp m;
    private IBinder n = new AtCommandService.LocalBinder();
    private Runnable o;
    private boolean q = true;

    public AtCommandService() {
    }

    public void a(String var1) {
        LogUtil.i("commandService", "commandBegin  " + var1);
        if ("$OpenFscAtEngine$".equals(var1)) {
            this.c(var1);
        } else if (var1.contains("=")) {
            if (this.d) {
                this.b(var1);
            } else {
                this.c(var1);
            }
        } else if (!var1.contains("=")) {
            this.b(var1);
        }

    }

    public void b(String var1) {
        LogUtil.i("commandService", "commandQuery  " + var1);
        String var2;
        if (var1.contains("=")) {
            var2 = var1.substring(0, var1.indexOf("="));
        } else {
            var2 = var1;
        }

        this.h.remove(var1);
        this.h.put(var1, 1);
        this.a((var2 + "\r\n").getBytes());
    }

    public void c(String var1) {
        LogUtil.i("commandService", "commandSet  " + var1);
        this.h.remove(var1);
        this.h.put(var1, 2);
        if ("$OpenFscAtEngine$".equals(var1)) {
            this.a(var1.getBytes());
        } else {
            this.a((var1 + "\r\n").getBytes());
        }

    }

    public void d(String var1) {
        LogUtil.i("commandService", "commandVerify  " + var1);
        this.h.remove(var1);
        this.h.put(var1, 3);

        try {
            var1 = var1.substring(0, var1.indexOf("="));
        } catch (Exception var3) {
            var3.printStackTrace();
            LogUtil.i("commandService", "commandVerify  command.contains('=') == false");
        }

        this.a((var1 + "\r\n").getBytes());
    }

    public void e(String var1) {
        LogUtil.i("commandService", "commandEnd  " + var1);
        this.h.remove(var1);
        this.h.put(var1, 4);
        ++this.j;
        if (this.j >= this.i.size()) {
            Log.e("commandService", "commandEnd: 1");
            this.a((String) null, (String) null, (String) "2");
        } else {
            this.a((String) this.i.get(this.j));
        }

    }

    public void onCreate() {
        LogUtil.i("commandService", "onCreate");
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        LogUtil.i("commandService", "onBind");
        return this.n;
    }

    public void onRebind(Intent intent) {
        LogUtil.i("commandService", "onRebind");
        super.onRebind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("commandService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.i("commandService", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() {
        LogUtil.i("commandService", "onLowMemory");
        super.onLowMemory();
    }

    public void onTrimMemory(int level) {
        LogUtil.i("commandService", "onTrimMemory");
        super.onTrimMemory(level);
    }

    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.i("commandService", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        LogUtil.i("commandService", "dump");
        super.dump(fd, writer, args);
    }

    public boolean onUnbind(Intent intent) {
        LogUtil.i("commandService", "onUnbind");
        a = false;
        b = false;
        LogUtil.i("commandService", "openFscAtEngine   " + a);
        LogUtil.i("commandService", "atCommandServiceConnected   " + b);
        if (this.l != null) {
            this.l.a((FscBleCentralCallbacks) null);
        }

        if (this.m != null) {
            this.m.a((FscSppCallbacks) null);
        }

        FscBleCentralApiImp.EN_AUTO_INQUERY = true;
        FscBleCentralApiImp.EN_AUTO_VERIFY = true;
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        LogUtil.i("commandService", "onDestroy");
        super.onDestroy();
    }

    public void a(Set<String> var1, boolean var2, boolean var3, boolean var4) {
        this.q = var2;
        this.d = var3;
        this.e = var4;
        this.j = 0;
        if (this.q) {
            this.l = FscBleCentralApiImp.getInstance();
            this.l.a(new FscBleCentralCallbacksImp() {
                public void packetReceived(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, String hexString, byte[] rawValue, String timestamp) {
                    AtCommandService.this.a(rawValue, timestamp, device);
                }
            });
            this.g = this.l.a();
        } else {
            this.m = FscSppApiImp.getInstance();
            this.m.a(new FscSppCallbacksImp() {
                public void packetReceived(byte[] dataByte, String dataString, String dataHexString) {
                    AtCommandService.this.a((byte[]) dataByte, (String) null, (BluetoothDevice) null);
                }
            });
            this.f = this.m.d();
        }

        this.i = new ArrayList();
        this.h = new LinkedHashMap();
        LogUtil.i("commandService", "openFscAtEngine  " + a);
        if (!a) {
            this.h.put("$OpenFscAtEngine$", 0);
            this.i.add("$OpenFscAtEngine$");
        }

        Iterator var5 = var1.iterator();

        while (var5.hasNext()) {
            String var6 = (String) var5.next();
            if ("AT+VER".equals(var6)) {
                this.h.put("AT+VER", 0);
                this.i.add("AT+VER");
            }
        }

        Iterator var8 = var1.iterator();

        while (var8.hasNext()) {
            String var7 = (String) var8.next();
            if (!"AT+VER".equals(var7)) {
                this.h.put(var7, 0);
                this.i.add(var7);
            }
        }

        this.o = new Runnable() {
            public void run() {
                LogUtil.i("commandService", "Runnable, command: " + (String) AtCommandService.this.i.get(AtCommandService.this.j));
                AtCommandService.this.a((String) ((String) AtCommandService.this.i.get(AtCommandService.this.j)), (String) null, (String) "4");
            }
        };
        this.a((String) this.i.get(this.j));
    }

    public void a(byte[] var1, String var2, BluetoothDevice var3) {
        FeasycomUtil.a(FeasycomUtil.b, var1);
        String var4 = new String(FeasycomUtil.a(FeasycomUtil.b));
        if (var4.contains("OK\r\n") || var4.contains("ERROR\r\n") || var4.contains("Open")) {
            LogUtil.i("commandService", "retemp   " + var4);
            this.k.removeCallbacks(this.o);
            String var5 = (String) this.i.get(this.j);
            if (!var4.contains("OK\r\n") && !var4.contains("Open")) {
                if (var4.contains("ERROR\r\n")) {
                    this.a((String) ((String) this.i.get(this.j)), (String) null, (String) "0");
                    this.e((String) this.i.get(this.j));
                }
            } else if (var4.contains("Open")) {
                a = true;
                this.a((String) "Opened", (String) null, (String) "1");
                this.e(var5);
            } else if (!var5.contains("=")) {
                var4 = this.f(var4);
                LogUtil.i("commandService", "command.contains('=') == false");
                LogUtil.i("commandService", var5);
                this.a(var5, var4, "1");
                this.e(var5);
            } else if (var5.contains("=")) {
                if ((Integer) this.h.get(var5) == 1) {
                    if (this.a(var5, var4)) {
                        var4 = this.f(var4);
                        this.a(var5, var4, "3");
                        this.e(var5);
                    } else {
                        this.c(var5);
                    }
                } else if ((Integer) this.h.get(var5) == 2) {
                    if (this.e) {
                        this.d(var5);
                    } else {
                        this.a((String) var5, (String) null, (String) "1");
                        this.e(var5);
                    }
                } else if ((Integer) this.h.get(var5) == 3) {
                    if (this.a(var5, var4)) {
                        var4 = this.f(var4);
                        LogUtil.i("commandService", "VERIFY SUCCESSFUL, command: " + var5 + " param: " + var4);
                        this.a(var5, var4, "1");
                    } else {
                        var4 = this.f(var4);
                        LogUtil.i("commandService", "VERIFY FAILED, command: " + var5 + " param: " + var4);
                        this.a(var5, var4, "0");
                    }

                    this.e(var5);
                }
            }

            FeasycomUtil.b(FeasycomUtil.b);
        }
    }

    private void a(byte[] var1) {
        if (this.q) {
            this.l.sendCommand(var1);
        } else {
            this.m.sendCommand(var1);
        }

        this.k.postDelayed(this.o, 3000L);
    }

    private void a(String var1, String var2, String var3) {
        LogUtil.i("commandService", "bleMode " + this.q);
        if (this.q) {
            this.g.atCommandCallBack(var1, var2, var3);
        } else {
            this.f.atCommandCallBack(var1, var2, var3);
        }

    }

    private boolean a(String var1, String var2) {
        if (var1 != null && var2 != null) {
            return this.f(var1).equals(this.f(var2));
        } else {
            return false;
        }
    }

    private String f(String var1) {
        LogUtil.i("commandService", "getParam " + var1);

        try {
            String var2 = var1.substring(var1.indexOf("="), var1.length()).replace("\r\n", "").replace("OK", "").replace("=", "");
            LogUtil.i("commandService", "getParam " + var2);
            LogUtil.i("commandService", "getParam " + var2.length());
            return var2;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public AtCommandService a() {
            return AtCommandService.this;
        }
    }
}
