package com.magna.beaconlibrary.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.magna.beaconlibrary.controller.FscSppApiImp;
import com.magna.beaconlibrary.controller.FscSppCallbacks;
import com.magna.beaconlibrary.util.FileUtil;
import com.magna.beaconlibrary.util.LogUtil;

import java.io.IOException;
import java.io.OutputStream;

public class OTASPPService extends Service {
    private static final byte d = 127;
    private static final byte e = 1;
    private static final byte f = 2;
    private static final byte g = 4;
    private static final byte h = 6;
    private static final byte i = 21;
    private static final byte j = 24;
    private static final byte k = 26;
    private static final int l = 25;
    private final String b = "otaService";
    private final Object r = new Object();
    public byte[] a;
    private com.magna.beaconlibrary.util.b c = new com.magna.beaconlibrary.util.b();
    private byte[] m = new byte[1];
    private byte n;
    private int o;
    private int p;
    private int q;
    private FscSppCallbacks s;
    private Boolean t = false;
    private Boolean u = false;
    private boolean v;
    private byte[] w = new byte[5];
    private byte x;
    private Handler y;
    private Boolean z;
    private FscSppApiImp A;
    private OTASPPService.OTAThread B;
    private Thread C;
    private Thread D;
    private byte[] E;
    private byte[] F;
    private byte[] G;
    private byte[] H;
    private int I;
    private int J = 0;
    private OutputStream K;
    private IBinder L = new OTASPPService.LocalBinder();
    private int M = 0;

    public OTASPPService() {
    }

    public IBinder onBind(Intent intent) {
        LogUtil.i("otaService", "onBind");
        this.v = true;
        this.a = intent.getByteArrayExtra("fileByte");
        this.E = intent.getByteArrayExtra("fileByteNoChack");
        this.z = true;
        this.B = new OTASPPService.OTAThread();
        this.A = FscSppApiImp.getInstance();
        this.K = this.A.c();
        this.s = this.A.d();
        this.y = new Handler();
        this.D = new Thread(new Runnable() {
            public void run() {
                boolean var1 = false;

                while (OTASPPService.this.z) {
                    int var6 = 0;
                    LogUtil.i("otaService", "ota receive run");

                    try {
                        if (null != OTASPPService.this.A.b()) {
                            LogUtil.i("otaService", "ota read begin");
                            var6 = OTASPPService.this.A.b().read(OTASPPService.this.w);
                            LogUtil.i("otaService", "ota read end");
                            LogUtil.i("otaService", "len " + var6);
                        }
                    } catch (Exception var5) {
                        OTASPPService.this.z = false;
                    }

                    if (var6 > 0) {
                        synchronized (OTASPPService.this.r) {
                            OTASPPService.this.x = OTASPPService.this.w[0];
                            LogUtil.i("otaService", "ota status " + OTASPPService.this.x);
                            OTASPPService.this.r.notifyAll();
                        }
                    }
                }

            }
        });
        return this.L;
    }

    public boolean onUnbind(Intent intent) {
        LogUtil.i("otaService", "onUnbind");
        this.z = false;
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        this.v = false;
        LogUtil.i("otaService", "onDestroy");
        super.onDestroy();
    }

    public int a(byte[] var1, int var2, byte[] var3, int var4) throws IOException {
        this.J = 0;
        this.t = false;
        this.u = true;
        this.G = var1;
        this.I = var2;
        this.F = new byte[1029];
        this.o = 0;
        this.n = 1;
        this.p = 0;
        if (!this.v) {
            return -1;
        } else {
            int var5 = 0;

            while (var5 < 16) {
                if (!this.z) {
                    return 0;
                }

                this.a(800);
                switch (this.x) {
                    case 21:
                        this.o = 0;
                        return this.e();
                    case 24:
                        if (this.a(3000) == 24) {
                            this.m[0] = 24;
                            this.b(this.m);
                            LogUtil.i("otaService", "disconnect 3");
                            this.A.disconnect();
                            this.s.otaProgressUpdate(this.J, 120);
                            LogUtil.i("otaService", "receive can command");
                            this.f();
                            return -1;
                        }
                    default:
                        ++var5;
                        break;
                    case 67:
                        this.G = var3;
                        this.I = var4;
                        this.o = 1;
                        return this.e();
                    case 83:
                        this.o = 1;
                        return this.e();
                }
            }

            this.m[0] = 24;
            this.b(this.m);

            try {
                Thread.sleep(200L);
                this.b(this.m);
                Thread.sleep(200L);
                this.b(this.m);
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            LogUtil.i("otaService", "disconnect 4");
            this.A.disconnect();
            this.s.otaProgressUpdate(this.J, 120);
            LogUtil.i("otaService", "send can command 1");
            this.f();
            return -2;
        }
    }

    private int e() throws IOException {
        while (this.v) {
            this.F[0] = 2;
            this.F[1] = this.n;
            this.F[2] = (byte) (~this.n);
            this.q = this.I - this.p;
            LogUtil.i("otaService", "begin " + this.p);
            LogUtil.i("otaService", "fileByteLen " + this.I);
            LogUtil.i("otaService", "byteCount " + this.q);
            this.J = this.p * 100 / this.I;
            if (this.J >= 100) {
                this.J = 100;
                this.y.postDelayed(new Runnable() {
                    public void run() {
                        OTASPPService.this.s.otaProgressUpdate(OTASPPService.this.J, 10086);
                    }
                }, 1000L);
                this.z = false;
            }

            (new Thread(new Runnable() {
                public void run() {
                    OTASPPService.this.s.otaProgressUpdate(OTASPPService.this.J, 121);
                }
            })).start();
            if (this.q > 1024) {
                this.q = 1024;
            }

            int var1;
            if (this.q > 0) {
                for (var1 = 3; var1 < 1027; ++var1) {
                    this.F[var1] = 0;
                }

                if (this.q == 0) {
                    this.F[3] = 26;
                } else {
                    for (var1 = 0; var1 < this.q; ++var1) {
                        this.F[var1 + 3] = this.G[this.p + var1];
                    }

                    if (this.q < 1024) {
                        this.F[3 + this.q] = 26;
                    }
                }

                int var2;
                if (this.o > 0) {
                    byte[] var9 = new byte[1024];

                    for (var2 = 0; var2 < 1024; ++var2) {
                        var9[var2] = this.F[var2 + 3];
                    }

                    short var8 = this.c.a(var9, 1024);
                    int var3 = var8 & '\uffff';
                    this.F[1027] = (byte) (var8 >> 8 & 255);
                    this.F[1028] = (byte) (var8 & 255);
                } else {
                    byte var10 = 0;

                    for (var2 = 3; var2 < 1027; ++var2) {
                        var10 += this.F[var2];
                    }

                    this.F[1027] = var10;
                }

                boolean var11 = true;

                for (this.M = 0; this.M < 25; ++this.M) {
                    if (!this.z) {
                        LogUtil.i("otaService", "disconnect 5");
                        this.A.disconnect();
                        this.s.otaProgressUpdate(this.J, 120);
                        return 0;
                    }

                    LogUtil.i("otaService", "retry  " + this.M);
                    if (!this.v || this.M >= 24) {
                        LogUtil.i("otaService", "disconnect 6");
                        this.A.disconnect();
                        this.s.otaProgressUpdate(this.J, 120);
                        LogUtil.i("otaService", "thread control " + this.v);
                        LogUtil.i("otaService", "retry " + this.M);
                        LogUtil.i("otaService", "retry > MAXRETRANS");
                        return -1;
                    }

                    this.q = 1028 + (this.o > 0 ? 1 : 0);
                    (new Thread(new Runnable() {
                        public void run() {
                            try {
                                OTASPPService.this.K.write(OTASPPService.this.F, 0, OTASPPService.this.q);
                            } catch (IOException var2) {
                                OTASPPService.this.M = 25;
                                var2.printStackTrace();
                            }

                        }
                    })).start();
                    synchronized (this.r) {
                        try {
                            LogUtil.i("otaService", "wait start");
                            this.r.wait(1000L);
                            LogUtil.i("otaService", "wait end");
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        if (this.x >= 0) {
                            switch (this.x) {
                                case 6:
                                    ++this.n;
                                    this.p += 1024;
                                    this.M = 25;
                                    this.x = 127;
                                    var11 = false;
                                case 21:
                                    break;
                                case 24:
                                    if (!this.v) {
                                        return -1;
                                    }

                                    if (this.a(3000) == 24) {
                                        this.m[0] = 24;
                                        this.b(this.m);
                                        LogUtil.i("otaService", "send can");
                                        this.f();
                                        return -1;
                                    }
                                case 127:
                            }
                        }
                    }
                }

                if (!var11) {
                    continue;
                }

                this.m[0] = 24;

                try {
                    this.b(this.m);
                    Thread.sleep(200L);
                    this.b(this.m);
                    Thread.sleep(200L);
                    this.b(this.m);
                } catch (Exception var5) {
                    var5.printStackTrace();
                }

                LogUtil.i("otaService", "disconnect 1");
                this.A.disconnect();
                this.s.otaProgressUpdate(this.J, 120);
                LogUtil.i("otaService", "send can command 2");
                this.f();
                return -4;
            }

            LogUtil.i("otaService", "begin " + this.p);
            LogUtil.i("otaService", "fileByteLen " + this.I);
            LogUtil.i("otaService", "byteCount " + this.q);

            for (var1 = 0; var1 < 3; ++var1) {
                if (!this.v) {
                    return -1;
                }

                this.m[0] = 4;
                this.b(this.m);
                LogUtil.i("otaService", "send EOT");
                if (this.a(1000) == 6) {
                    break;
                }
            }

            LogUtil.i("otaService", "send can 1");
            this.f();
            this.t = true;
            return this.x == 6 ? this.p : -5;
        }

        return -1;
    }

    private byte a(int var1) {
        try {
            Thread.sleep((long) var1);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        return this.x;
    }

    public Boolean a() {
        return this.t;
    }

    public Boolean b() {
        return this.u;
    }

    public void a(Boolean var1) {
        this.u = var1;
    }

    public void b(Boolean var1) {
        this.t = var1;
    }

    public byte[] c() {
        return this.E;
    }

    public void a(byte[] var1) {
        this.E = var1;
    }

    private void f() {
        LogUtil.i("otaService", "disconnect 2");
        this.A.disconnect();
        this.z = false;
    }

    public OTASPPService.OTAThread d() {
        return this.B;
    }

    private void b(byte[] var1) {
        this.H = var1;
        (new Thread(new Runnable() {
            public void run() {
                try {
                    LogUtil.i("otaService", "ota send");
                    LogUtil.i("otaService", FileUtil.bytesToHex(OTASPPService.this.H, OTASPPService.this.H.length));
                    OTASPPService.this.K.write(OTASPPService.this.H);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        })).start();
    }

    private void a(final byte[] var1, final int var2, final int var3) {
        this.H = var1;
        (new Thread(new Runnable() {
            public void run() {
                try {
                    LogUtil.i("otaService", "ota send");
                    LogUtil.i("otaService", FileUtil.bytesToHex(OTASPPService.this.H, OTASPPService.this.H.length));
                    OTASPPService.this.K.write(var1, var2, var3);
                } catch (Exception var2x) {
                    var2x.printStackTrace();
                }

            }
        })).start();
    }

    public class OTAThread extends Thread {
        public OTAThread() {
        }

        public void run() {
            super.run();

            try {
                OTASPPService.this.D.start();
                OTASPPService.this.a(OTASPPService.this.a, OTASPPService.this.a.length, OTASPPService.this.E, OTASPPService.this.E.length);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public OTASPPService a() {
            return OTASPPService.this;
        }
    }
}
