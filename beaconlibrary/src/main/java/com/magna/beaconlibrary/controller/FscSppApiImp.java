package com.magna.beaconlibrary.controller;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.DfuFileInfo;
import com.magna.beaconlibrary.bean.EncryptInfo;
import com.magna.beaconlibrary.bean.QuickConnectionParam;
import com.magna.beaconlibrary.service.AtCommandService;
import com.magna.beaconlibrary.service.OTASPPService;
import com.magna.beaconlibrary.service.SmartLinkService;
import com.magna.beaconlibrary.util.FeasycomUtil;
import com.magna.beaconlibrary.util.FileUtil;
import com.magna.beaconlibrary.util.LogUtil;
import com.magna.beaconlibrary.util.TeaCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class FscSppApiImp implements FscSppApi {
    private static final String a = "FscSPP";
    private static final FscSppCallbacks H = new FscSppCallbacksImp();
    private static FscSppApiImp e = null;
    private static WeakReference<AtCommandService> i;
    private static WeakReference<SmartLinkService> j;
    private static QuickConnectionParam k;
    private static Set<String> l;
    private static OTASPPService.OTAThread z;
    private static OTASPPService A;
    private static Context J = null;
    private static ServiceConnection V = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FscSppApiImp.A = ((OTASPPService.LocalBinder) service).a();
            FscSppApiImp.z = FscSppApiImp.A.d();
            if (!FscSppApiImp.A.a() && !FscSppApiImp.A.b()) {
                FscSppApiImp.z.start();
            }

        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private static ServiceConnection W = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FscSppApiImp.c("onServiceConnected  parameterModifyServiceConnection   atCommandServiceConnected");
            AtCommandService.b = true;
            FscSppApiImp.i = new WeakReference(((AtCommandService.LocalBinder) service).a());
            ((AtCommandService) FscSppApiImp.i.get()).a(FscSppApiImp.l, false, true, true);
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private static ServiceConnection X = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FscSppApiImp.j = new WeakReference(((SmartLinkService.LocalBinder) service).a());
            ((SmartLinkService) FscSppApiImp.j.get()).a(FscSppApiImp.k);
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };

    static {
        System.loadLibrary("feasycom");
    }

    @Keep
    private final boolean HAVE_AUTH = false;
    private final int b = 5;
    @Keep
    private final int disAutoConnect = 12;
    @Keep
    private final int enAutoConnect = 0;
    private final int n = 1000;
    private final int r = 0;
    private final int s = 10;
    private final int t = 11;
    private final int u = 12;
    @Keep
    private final int TIME_OUT = 5000;
    private final int E = 2000;
    @Keep
    private int connectCount = 0;
    private String c;
    @Keep
    private EncryptInfo encryptInfo;
    private String d;
    private boolean f = true;
    private Thread g;
    private int h = 204800;
    private int m = 0;
    private long o;
    private long p;
    private int q = 0;
    private boolean v = true;
    private byte[] w;
    private byte[] x;
    private byte[] y;
    private int B;
    private int C;
    private boolean D = true;
    private BluetoothAdapter F = null;
    private BluetoothManager G = null;
    @Keep
    private FscSppCallbacks mUiCallback = null;
    private FscSppCallbacks I = null;
    @Keep
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Keep
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothSocket K;
    private InputStream L;
    private OutputStream M;
    private IntentFilter N = null;
    private byte[] O;
    private byte[] P;
    private boolean Q;
    private boolean R = false;
    private BroadcastReceiver T = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String var3 = intent.getAction();
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(var3)) {
                int var4 = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 0);
                FscSppApiImp.this.q = var4;
                if (var4 == 10) {
                    FscSppApiImp.c("BOND_NONE");
                    FscSppApiImp.this.v = false;
                } else if (var4 == 11) {
                    FscSppApiImp.c("BOND_BONDING");
                } else if (var4 == 12) {
                    FscSppApiImp.c("BOND_BONDED");
                } else {
                    FscSppApiImp.c("BOND_DEFAULT");
                }
            }

        }
    };
    @Keep
    Runnable mOnConnectTimeoutCallback = new Runnable() {
        public void run() {
            FscSppApiImp.c("conn check");
            if (FscSppApiImp.this.c == null) {
                FscSppApiImp.c("conn timeout");
                FscSppApiImp.this.mUiCallback.connectProgressUpdate(FscSppApiImp.this.mBluetoothDevice, 3);
                FscSppApiImp.this.disconnect();
            }

        }
    };
    private BroadcastReceiver U = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String var3 = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(var3)) {
                BluetoothDevice var4 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (Build.VERSION.SDK_INT >= 18 && var4.getType() != 1 && var4.getType() != 3) {
                    FscSppApiImp.c("mBluetoothDevice type" + var4.getType());
                    return;
                }

                FscSppApiImp.c("mBluetoothDevice found");
                short var5 = intent.getExtras().getShort("android.bluetooth.device.extra.RSSI");
                BluetoothDeviceWrapper var6 = new BluetoothDeviceWrapper(var4, var5, BluetoothDeviceWrapper.SPP_MODE);
                FscSppApiImp.this.mUiCallback.sppDeviceFound(var6, var5);
            }

        }
    };
    private Runnable S = new Runnable() {
        public void run() {
            if (!FscSppApiImp.this.R) {
                FscSppApiImp.this.stopScan();
            }

        }
    };

    private FscSppApiImp() {
    }

    public static FscSppApiImp getInstance() {
        if (e == null) {
            e = new FscSppApiImp();
        }

        return e;
    }

    public static FscSppApiImp getInstance(Context context) {
        J = context;
        if (e == null) {
            e = new FscSppApiImp();
        }

        return e;
    }

    public static FscSppApiImp getInstance(Activity activity) {
        try {
            J.unbindService(V);
        } catch (Exception var3) {
        }

        try {
            J.unbindService(W);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        J = activity.getApplicationContext();
        if (e == null) {
            e = new FscSppApiImp();
        }

        return e;
    }

    private static void c(String var0) {
        LogUtil.i("FscSPP", var0);
    }

    private native void onResponseAuth(String var1);

    private void l() {
        try {
            int var1 = this.L.read(this.O);
            if (var1 == -1) {
                this.disconnect();
                return;
            }

            this.P = new byte[var1];
            System.arraycopy(this.O, 0, this.P, 0, var1);
            String var2 = new String(this.P);
            String var3 = FileUtil.bytesToHex(this.P, this.P.length);
            String var4 = var3.replace(" ", "").toUpperCase();
            if (var2.contains("AUTH") && this.c == null) {
                this.onResponseAuth(var4);
            }

            c("rec " + var2);
            if (null == this.c && var2.length() >= 4 && var2.substring(0, 4).equals("AUTH")) {
                this.c = var2;
            } else {
                if (null != this.I) {
                    this.I.packetReceived(this.P, var2, var3);
                } else {
                    c("spp york callback null");
                }

                this.mUiCallback.packetReceived(this.P, var2, var3);
            }
        } catch (IOException var5) {
            this.Q = false;
            var5.printStackTrace();
        } catch (Exception var6) {
            this.Q = false;
            var6.printStackTrace();
        }

    }

    private void m() {
        this.O = new byte[1024];
        this.Q = true;
        (new Thread(new Runnable() {
            public void run() {
                FscSppApiImp.c("run  receive");

                while (FscSppApiImp.this.Q) {
                    if (null == FscSppApiImp.this.L || null == FscSppApiImp.this.K || !FscSppApiImp.this.K.isConnected()) {
                        FscSppApiImp.this.Q = false;
                        break;
                    }

                    FscSppApiImp.this.l();
                }

                FscSppApiImp.this.c = null;
                FscSppApiImp.this.connectCount = 12;
                FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
            }
        })).start();
    }

    private void n() {
        FeasycomUtil.a = new LinkedBlockingQueue(this.h);
        (new Thread(new Runnable() {
            public void run() {
                FscSppApiImp.c("send interval " + FscSppApiImp.this.m);

                while (FscSppApiImp.this.Q) {
                    try {
                        if (FeasycomUtil.a.size() == 0 && FscSppApiImp.this.f) {
                            FscSppApiImp.this.mUiCallback.sendPacketProgress(FscSppApiImp.this.mBluetoothDevice, 101, (byte[]) null);
                        }

                        if (FeasycomUtil.a.size() < 2000 && FeasycomUtil.a.size() != 20) {
                            Thread.sleep(100L);
                            FscSppApiImp.this.M.write(FeasycomUtil.a(2000));
                            if (FscSppApiImp.this.m > 0 && FscSppApiImp.this.m <= 1000) {
                                Thread.sleep((long) FscSppApiImp.this.m);
                            }
                        } else {
                            FscSppApiImp.this.M.write(FeasycomUtil.a(2000));
                            if (FscSppApiImp.this.m > 0 && FscSppApiImp.this.m <= 1000) {
                                Thread.sleep((long) FscSppApiImp.this.m);
                            }
                        }
                    } catch (IOException var2) {
                        var2.printStackTrace();
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    } catch (NullPointerException var4) {
                    }
                }

            }
        })).start();
    }

    private IntentFilter o() {
        if (this.N == null) {
            this.N = new IntentFilter();
            this.N.addAction("android.bluetooth.device.action.FOUND");
            this.N.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        }

        return this.N;
    }

    public boolean initialize() {
        if (this.G == null) {
            this.G = (BluetoothManager) J.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.G == null) {
                return false;
            }
        }

        if (this.F == null) {
            this.F = this.G.getAdapter();
        }

        if (this.F == null) {
            return false;
        } else {
            LogUtil.initialize(J);
            return true;
        }
    }

    private boolean p() {
        if (this.K == null) {
            return false;
        } else {
            try {
                Thread.sleep(200L);
                c("connect 1 begin");
                this.K.connect();
                c("connect 1 end");
                if (this.K.isConnected()) {
                    return true;
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                c("connect 1 Exception");

                try {
                    c("connect 2 begin");
                    Thread.sleep(200L);
                    this.K.connect();
                    c("connect 2 end");
                    if (this.K.isConnected()) {
                        return true;
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();

                    try {
                        c("connect 3 begin");
                        Thread.sleep(200L);
                        this.K.connect();
                        c("connect 3 end");
                        if (this.K.isConnected()) {
                            return true;
                        }
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }
                }

                var6.printStackTrace();
            }

            return false;
        }
    }

    @Keep
    private void cancelConnectTimeoutCheck() {
        try {
            this.mHandler.removeCallbacks(this.mOnConnectTimeoutCallback);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public boolean connect(String addr, String pin) {
        c("connect  enter");
        this.v = true;
        this.m = 0;
        this.connectCount = 0;
        this.q();
        if (this.F == null) {
            return false;
        } else {
            if (!"DC".equals(addr.substring(0, 2)) && !"00".equals(addr.substring(0, 2))) {
                addr = FeasycomUtil.b(addr);
            }

            this.encryptInfo = EncryptInfo.create(addr, pin, "Beacon");
            if (!this.d(addr)) {
                return false;
            } else {
                this.cancelConnectTimeoutCheck();
                (new Thread(new Runnable() {
                    public void run() {
                        FscSppApiImp.c("connect  run");
                        if (null != FscSppApiImp.this.K) {
                            try {
                                FscSppApiImp.this.r();
                                if (FscSppApiImp.this.K.isConnected()) {
                                    try {
                                        FscSppApiImp.J.unregisterReceiver(FscSppApiImp.this.T);
                                    } catch (Exception var2) {
                                    }

                                    FscSppApiImp.c("connect  successful");
                                    FscSppApiImp.this.m();
                                    FscSppApiImp.this.sendBeaconAuthInfo();
                                    FscSppApiImp.this.n();
                                    FscSppApiImp.this.mUiCallback.sppConnected(FscSppApiImp.this.mBluetoothDevice);
                                } else {
                                    FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                                }
                            } catch (Exception var3) {
                                var3.printStackTrace();
                                FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                            }
                        } else {
                            FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                        }

                    }
                })).start();
                return true;
            }
        }
    }

    private native void sendBeaconAuthInfo();

    private boolean b(BluetoothDevice var1) {
        if (var1 == null) {
            return false;
        } else {
            this.mBluetoothDevice = var1;
            c("this.mBluetoothDevice " + this.mBluetoothDevice.toString());
            String var2 = "00001101-0000-1000-8000-00805F9B34FB";
            UUID var3 = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            try {
                if (Build.VERSION.SDK_INT >= 10) {
                    this.K = var1.createInsecureRfcommSocketToServiceRecord(var3);
                } else {
                    this.K = var1.createRfcommSocketToServiceRecord(var3);
                }

                this.L = this.K.getInputStream();
                this.M = this.K.getOutputStream();
                return true;
            } catch (Exception var5) {
                var5.printStackTrace();
                return false;
            }
        }
    }

    private boolean q() {
        try {
            if (this.L != null) {
                this.L.close();
                this.L = null;
            }

            if (this.M != null) {
                this.M.close();
                this.M = null;
            }

            if (this.K != null) {
                this.K.close();
                this.K = null;
            }

            return true;
        } catch (Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }

    public boolean a(String var1) {
        c("smartLink  enter");
        BluetoothDevice var2 = null;
        this.q();
        LogUtil.i("FscSPP", "cancelDiscovery " + this.F.cancelDiscovery());

        try {
            var2 = this.F.getRemoteDevice(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }

        if (!this.b(var2)) {
            return false;
        } else {
            (new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(50L);
                        FscSppApiImp.c("smartLink begin");
                        FscSppApiImp.this.o = System.currentTimeMillis();
                        FscSppApiImp.this.K.connect();
                        FscSppApiImp.c("smartLink successful");
                        FscSppApiImp.this.p = System.currentTimeMillis();
                        LogUtil.i("FscSPP", "smartLinkTime connect total time " + (FscSppApiImp.this.p - FscSppApiImp.this.o) + " ms");
                        FscSppApiImp.this.m();
                        if (FscSppApiImp.this.I != null) {
                            FscSppApiImp.this.I.sppConnected(FscSppApiImp.this.mBluetoothDevice);
                        } else {
                            FscSppApiImp.c("york call back null");
                        }

                        FscSppApiImp.this.mUiCallback.sppConnected(FscSppApiImp.this.mBluetoothDevice);
                    } catch (InterruptedException | IOException var4) {
                        try {
                            FscSppApiImp.this.K.connect();
                        } catch (Exception var3) {
                            if (FscSppApiImp.this.I != null) {
                                FscSppApiImp.this.I.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                            } else {
                                FscSppApiImp.c("york call back null");
                            }

                            FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                        }

                        var4.printStackTrace();
                    }

                }
            })).start();
            return true;
        }
    }

    public boolean connect(String mac) {
        c("connect  enter");
        this.v = true;
        this.m = 0;
        this.connectCount = 0;
        this.q();
        this.encryptInfo = EncryptInfo.createRandom("Universal");
        if (!this.d(mac)) {
            return false;
        } else {
            this.cancelConnectTimeoutCheck();
            (new Thread(new Runnable() {
                public void run() {
                    FscSppApiImp.c("connect  run");
                    if (FscSppApiImp.this.K == null) {
                        FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                    } else {
                        try {
                            FscSppApiImp.this.r();
                            if (!FscSppApiImp.this.K.isConnected()) {
                                FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                                return;
                            }

                            try {
                                FscSppApiImp.J.unregisterReceiver(FscSppApiImp.this.T);
                            } catch (Exception var2) {
                            }

                            FscSppApiImp.c("connect  successful");
                            FscSppApiImp.this.m();
                            FscSppApiImp.this.sendUniversalAuthInfo();
                            FscSppApiImp.this.n();
                            if (FscSppApiImp.this.I != null) {
                                FscSppApiImp.this.I.sppConnected(FscSppApiImp.this.mBluetoothDevice);
                            } else {
                                FscSppApiImp.c("york call back null");
                            }

                            FscSppApiImp.this.mUiCallback.sppConnected(FscSppApiImp.this.mBluetoothDevice);
                        } catch (Exception var3) {
                            var3.printStackTrace();
                            FscSppApiImp.this.mUiCallback.sppDisconnected(FscSppApiImp.this.mBluetoothDevice);
                        }

                    }
                }
            })).start();
            return true;
        }
    }

    private native void sendUniversalAuthInfo();

    private void r() {
        c("socketConnect");
        if (this.connectCount < 5 && (this.K == null || !this.K.isConnected())) {
            this.q = 0;

            try {
                if (this.K == null) {
                    c("socket == null");
                }

                Thread.sleep(200L);
                long var1 = System.currentTimeMillis();
                this.K.connect();
                long var3 = System.currentTimeMillis();
                c(var3 - var1 + " ms");
                c("conncount" + this.connectCount);
                ++this.connectCount;
            } catch (IOException var6) {
                Log.e("FscSPP", "socketConnect: *****************");
                var6.printStackTrace();
                if (this.q != 12 && this.q != 0) {
                    try {
                        Thread.sleep(2000L);
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

                if (this.v) {
                    if (!this.s() && this.v) {
                        this.s();
                    }
                } else {
                    this.q();
                }
            } catch (InterruptedException var7) {
                var7.printStackTrace();
            }

        }
    }

    private boolean s() {
        try {
            Thread.sleep(200L);
            this.K.connect();
            ++this.connectCount;
            return true;
        } catch (InterruptedException | IOException var2) {
            var2.printStackTrace();
            return false;
        }
    }

    private boolean d(String var1) {
        BluetoothDevice var2 = null;
        this.d = var1;
        if (null != this.K) {
            try {
                this.K.close();
            } catch (Exception var4) {
                var4.printStackTrace();
                return false;
            }
        }

        try {
            var2 = this.F.getRemoteDevice(var1);
            if (var2.getBondState() != 12) {
                J.registerReceiver(this.T, this.o());
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }

        return this.b(var2);
    }

    private boolean c(BluetoothDevice var1) {
        c("initSocket enter");
        c("bluetoothDevice " + var1.toString());
        this.d = var1.getAddress();
        if (!this.q()) {
            return false;
        } else {
            return this.b(var1);
        }
    }

    public void disconnect() {
        c("disconnect");
        this.stopSend();

        try {
            J.unbindService(X);
        } catch (Exception var5) {
        }

        try {
            J.unregisterReceiver(this.T);
        } catch (Exception var4) {
        }

        this.a((FscSppCallbacks) null);
        this.cancelConnectTimeoutCheck();

        try {
            J.unbindService(W);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        try {
            J.unbindService(V);
        } catch (Exception var2) {
        }

        this.connectCount = 12;
        this.Q = false;
        FeasycomUtil.a.clear();
        this.q();
    }

    @Keep
    public boolean sendCommand(final byte[] packet) {
        OutputStream var2 = this.c();
        if (var2 == null) {
            return false;
        } else {
            (new Thread(new Runnable() {
                public void run() {
                    try {
                        FscSppApiImp.this.c().write(packet);
                        FscSppApiImp.this.mUiCallback.sendPacketProgress(FscSppApiImp.this.mBluetoothDevice, 100, packet);
                        FscSppApiImp.c("send" + new String(packet));
                        FscSppApiImp.c("send" + packet.length + "");
                    } catch (Exception var2) {
                        var2.printStackTrace();
                    }

                }
            })).start();
            return true;
        }
    }

    public boolean send(byte[] packet) {
        c("send " + new String(packet));
        c("send " + packet.length);
        if (!this.f) {
            c("isFinishSendPackge " + this.f);
            return false;
        } else {
            this.f = false;
            if (null == packet) {
                c("null == packet ");
                return false;
            } else {
                this.D = true;
                this.g = new FscSppApiImp.a(packet);
                this.g.start();
                return true;
            }
        }
    }

    public boolean setSendInterval(int ms) {
        if (ms >= 0 && ms <= 1000) {
            this.m = ms;
            c("send interval " + this.m);
            return true;
        } else {
            return false;
        }
    }

    public boolean cancleSendInterval() {
        this.m = 0;
        return true;
    }

    public void stopSend() {
        this.D = false;
        this.f = true;
    }

    public DfuFileInfo checkDfuFile(byte[] dfuFile) {
        return FileUtil.getDfuFileInformation(dfuFile);
    }

    public synchronized void startScan(int time) {
        c("startScan");
        if (time == 0) {
            this.R = true;
        } else {
            this.R = false;
        }

        if (this.F != null) {
            if (this.F.isEnabled()) {
                J.registerReceiver(this.U, this.o());
                this.mUiCallback.startScan();
                Set var2 = this.F.getBondedDevices();
                Iterator var3 = var2.iterator();

                while (var3.hasNext()) {
                    BluetoothDevice var4 = (BluetoothDevice) var3.next();
                    BluetoothDeviceWrapper var5 = new BluetoothDeviceWrapper(var4, 0, BluetoothDeviceWrapper.SPP_MODE);
                    this.mUiCallback.sppDeviceFound(var5, 0);
                }

                this.F.startDiscovery();
                this.mHandler.removeCallbacks(this.S);
                this.mHandler.postDelayed(this.S, (long) time);
            }
        }
    }

    public void stopScan() {
        c("stopScan");

        try {
            J.unregisterReceiver(this.U);
        } catch (Exception var2) {
        }

        if (this.F.isEnabled()) {
            this.mUiCallback.stopScan();
            this.mHandler.removeCallbacks(this.S);
            if (this.F.isDiscovering()) {
                this.F.cancelDiscovery();
            }

        }
    }

    public boolean isConnected() {
        return null != this.K && this.K.isConnected();
    }

    public boolean isBtEnabled() {
        BluetoothManager var1 = (BluetoothManager) J.getSystemService(Context.BLUETOOTH_SERVICE);
        if (var1 == null) {
            return false;
        } else {
            BluetoothAdapter var2 = var1.getAdapter();
            return var2 == null ? false : var2.isEnabled();
        }
    }

    public void setCallbacks(FscSppCallbacks callback) {
        if (null == callback) {
            this.mUiCallback = H;
        } else {
            this.mUiCallback = callback;
        }

    }

    public boolean startOTA(byte[] dfuFile, boolean reset) {
        String var3 = "0";
        if (this.isConnected() && null != dfuFile) {
            if (reset) {
                var3 = "1";
            }

            this.w = dfuFile;
            TeaCode var4 = new TeaCode();
            this.x = var4.feasycom_decryption(dfuFile);
            if (this.x == null) {
                return false;
            } else {
                this.y = new byte[this.x.length - 1024];
                System.arraycopy(this.x, 1024, this.y, 0, this.x.length - 1024);
                this.sendCommand(("EnterDFU" + var3).getBytes());
                if (FileUtil.needsReconnect(dfuFile)) {
                    try {
                        Thread.sleep(1000L);
                        if (this.isConnected()) {
                            c("dfu disconnect");
                            this.disconnect();
                        }

                        Thread.sleep(3000L);
                        c("initSocket");
                        this.c(this.mBluetoothDevice);
                        (new Thread(new Runnable() {
                            public void run() {
                                if (FscSppApiImp.this.p()) {
                                    FscSppApiImp.c("OTA_STATU_BEGIN");
                                    FscSppApiImp.this.mUiCallback.otaProgressUpdate(0, 110);
                                    FscSppApiImp.this.t();
                                } else {
                                    FscSppApiImp.c("OTA_STATU_FAILED");
                                    FscSppApiImp.this.mUiCallback.otaProgressUpdate(0, 120);
                                }

                            }
                        })).start();
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                        return false;
                    }
                } else {
                    c("OTA_STATU_BEGIN");
                    this.mUiCallback.otaProgressUpdate(0, 110);
                    this.Q = false;
                    this.a((FscSppCallbacks) null);
                    this.t();
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private void t() {
        Intent var1 = new Intent();
        var1.putExtra("fileByte", this.x);
        var1.putExtra("fileByteNoChack", this.y);
        var1.setClass(J, OTASPPService.class);
        c("bind ota service");
        J.bindService(var1, V, Context.BIND_AUTO_CREATE);
    }

    public void sendATCommand(Set<String> command) {
        l = command;
        c("sendATCommand..atCommandServiceConnected " + AtCommandService.b);
        if (!AtCommandService.b) {
            Intent var2 = new Intent();
            var2.setClass(J, AtCommandService.class);
            J.bindService(var2, W, Context.BIND_AUTO_CREATE);
        } else {
            ((AtCommandService) i.get()).a(l, false, true, true);
        }

    }

    public boolean smartLink(QuickConnectionParam quickConnectionParam) {
        try {
            J.unbindService(X);
        } catch (Exception var3) {
        }

        if (null != quickConnectionParam.getData() && !"".equals(quickConnectionParam.getData())) {
            if (null == quickConnectionParam.getActivity()) {
                throw new NullPointerException("activity is null");
            } else if (null != quickConnectionParam.getName() && !"".equals(quickConnectionParam.getName()) || null != quickConnectionParam.getMac() && !"".equals(quickConnectionParam.getMac())) {
                if (!"".equals(quickConnectionParam.getMac()) && quickConnectionParam.getMac() != null && !BluetoothAdapter.checkBluetoothAddress(quickConnectionParam.getMac())) {
                    throw new IllegalArgumentException(quickConnectionParam.getMac() + " is not a valid Bluetooth address");
                } else if (!(quickConnectionParam.getActivity() instanceof Activity)) {
                    throw new IllegalArgumentException(quickConnectionParam.getActivity() + "is not a vaild Activity Object");
                } else {
                    k = quickConnectionParam;
                    Intent var2 = new Intent();
                    var2.setClass(J, SmartLinkService.class);
                    J.bindService(var2, X, Context.BIND_AUTO_CREATE);
                    return true;
                }
            } else {
                throw new NullPointerException("Filter parameter is null");
            }
        } else {
            throw new NullPointerException("data is null");
        }
    }

    public BluetoothDevice a() {
        return this.mBluetoothDevice;
    }

    public void a(BluetoothDevice var1) {
        this.mBluetoothDevice = var1;
    }

    public InputStream b() {
        return this.L;
    }

    public OutputStream c() {
        return this.M;
    }

    public FscSppCallbacks d() {
        return this.mUiCallback;
    }

    public void a(FscSppCallbacks var1) {
        c("set york call back");
        this.I = var1;
    }

    class a extends Thread {
        final int a;
        byte[] b;
        byte[] c = new byte[2000];

        public a(byte[] var2) {
            this.b = var2;
            this.a = var2.length;
        }

        public void run() {
            FscSppApiImp.this.B = 0;
            FscSppApiImp.this.C = 0;

            while (this.a - FscSppApiImp.this.B > 2000) {
                if (!FscSppApiImp.this.D) {
                    FscSppApiImp.this.f = true;
                    FscSppApiImp.this.C = FscSppApiImp.this.B * 100 / this.a;
                    FscSppApiImp.this.mUiCallback.sendPacketProgress(FscSppApiImp.this.mBluetoothDevice, FscSppApiImp.this.C, (byte[]) null);
                    return;
                }

                System.arraycopy(this.b, FscSppApiImp.this.B, this.c, 0, 2000);
                FeasycomUtil.a(this.c);
                FscSppApiImp.this.B = FscSppApiImp.this.B + 2000;
                FscSppApiImp.this.C = FscSppApiImp.this.B * 100 / this.a;
                FscSppApiImp.this.mUiCallback.sendPacketProgress(FscSppApiImp.this.mBluetoothDevice, FscSppApiImp.this.C, this.c);
            }

            this.c = new byte[this.a - FscSppApiImp.this.B];
            System.arraycopy(this.b, FscSppApiImp.this.B, this.c, 0, this.a - FscSppApiImp.this.B);
            FeasycomUtil.a(this.c);
            FscSppApiImp.this.B = FscSppApiImp.this.B + this.c.length;
            FscSppApiImp.this.f = true;
            FscSppApiImp.this.C = 100;
            FscSppApiImp.this.mUiCallback.sendPacketProgress(FscSppApiImp.this.mBluetoothDevice, FscSppApiImp.this.C, this.c);
        }
    }
}
