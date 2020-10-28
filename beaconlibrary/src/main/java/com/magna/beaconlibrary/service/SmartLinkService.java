package com.magna.beaconlibrary.service;


import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.QuickConnectionParam;
import com.magna.beaconlibrary.controller.FscBleCentralApiImp;
import com.magna.beaconlibrary.controller.FscBleCentralCallbacks;
import com.magna.beaconlibrary.controller.FscBleCentralCallbacksImp;
import com.magna.beaconlibrary.controller.FscSppApiImp;
import com.magna.beaconlibrary.controller.FscSppCallbacks;
import com.magna.beaconlibrary.controller.FscSppCallbacksImp;
import com.magna.beaconlibrary.util.LogUtil;

public class SmartLinkService extends Service {
    private final String a = "smartLinkService";
    private IBinder b = new SmartLinkService.LocalBinder();
    private FscBleCentralApiImp c;
    private FscSppApiImp d;
    private QuickConnectionParam e;
    private long f;
    private long g;
    private boolean h = true;

    public SmartLinkService() {
    }

    public IBinder onBind(Intent intent) {
        LogUtil.i("smartLinkService", "onBind");
        return this.b;
    }

    public boolean onUnbind(Intent intent) {
        LogUtil.i("smartLinkService", "onUnbind");
        this.d.a((FscSppCallbacks) null);
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        LogUtil.i("smartLinkService", "onDestroy");
        if (this.d != null) {
            this.d.a((FscSppCallbacks) null);
        }

        if (this.c != null) {
            this.c.a((FscBleCentralCallbacks) null);
        }

        super.onDestroy();
    }

    public void a(final QuickConnectionParam var1) {
        this.e = var1;
        this.c = FscBleCentralApiImp.getInstance(var1.getActivity());
        this.c.a(new FscBleCentralCallbacksImp() {
            public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {
                LogUtil.i("smartLinkService", "device name  " + device.getName());
                LogUtil.i("smartLinkService", "param name  " + var1.getName());
                LogUtil.i("smartLinkService", "param name  " + (var1.getName() == null));
                LogUtil.i("smartLinkService", "device mac  " + device.getAddress());
                LogUtil.i("smartLinkService", "param mac  " + var1.getMac());
                if (device.getName() == null || var1.getName() == null || "".equals(var1.getName()) || device.getName().equals(var1.getName())) {
                    if (!SmartLinkService.this.h) {
                        SmartLinkService.this.h = true;
                        SmartLinkService.this.g = System.currentTimeMillis();
                        LogUtil.i("smartLinkService", "smartLinkTime scan--found " + (SmartLinkService.this.g - SmartLinkService.this.f) + " ms");
                        if (Build.VERSION.SDK_INT < 24) {
                            SmartLinkService.this.c.c();
                        }

                        if (device.getAddress() != null && var1.getMac() != null && !"".equals(var1.getMac()) && !device.getAddress().equals(var1.getMac())) {
                            SmartLinkService.this.d.a(var1.getMac());
                        } else {
                            SmartLinkService.this.d.a(device.getAddress());
                        }

                    }
                }
            }
        });
        this.d = FscSppApiImp.getInstance(var1.getActivity());
        this.d.a(new FscSppCallbacksImp() {
            public void sppConnected(BluetoothDevice device) {
                SmartLinkService.this.d.sendCommand(var1.getData().getBytes());
            }
        });
        this.f = System.currentTimeMillis();

        try {
            this.c.a(8000, var1);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public SmartLinkService a() {
            return SmartLinkService.this;
        }
    }
}
