package com.magna.beaconlibrary.controller;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.magna.beaconlibrary.bean.AltBeacon;
import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.EddystoneBeacon;
import com.magna.beaconlibrary.bean.EncryptInfo;
import com.magna.beaconlibrary.bean.FeasyBeacon;
import com.magna.beaconlibrary.bean.Ibeacon;
import com.magna.beaconlibrary.bean.Monitor;
import com.magna.beaconlibrary.bean.QuickConnectionParam;
import com.magna.beaconlibrary.service.AtCommandService;
import com.magna.beaconlibrary.service.OTASPPService;
import com.magna.beaconlibrary.util.FeasycomUtil;
import com.magna.beaconlibrary.util.FileUtil;
import com.magna.beaconlibrary.util.LogUtil;
import com.magna.beaconlibrary.util.TeaCode;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class FscBleCentralApiImp implements FscBleCentralApi {
    private static final FscBleCentralCallbacks C = new FscBleCentralCallbacksImp();
    public static boolean EN_AUTO_INQUERY = true;
    public static boolean EN_AUTO_VERIFY = true;
    private static Set<String> b;
    private static WeakReference<AtCommandService> c;
    private static Context s = null;
    private static FscBleCentralApiImp I;
    private static ArrayList<String> L = new ArrayList<String>() {
        {
            this.add("1800");
            this.add("1801");
            this.add("2a00");
            this.add("2a01");
            this.add("2a05");
            this.add("2a29");
            this.add("2a24");
            this.add("2a25");
            this.add("2a27");
            this.add("2a26");
            this.add("2a28");
            this.add("2a23");
            this.add("2a2a");
        }
    };
    private static ServiceConnection P = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FscBleCentralApiImp.b("onServiceConnected   parameterModifyServiceConnection");
            Log.e("FscBLE", "onServiceConnected: 绑定服务成功");
            AtCommandService.b = true;
            FscBleCentralApiImp.c = new WeakReference(((OTASPPService.LocalBinder) service).a());
            ((AtCommandService) FscBleCentralApiImp.c.get()).a(FscBleCentralApiImp.b, true, FscBleCentralApiImp.EN_AUTO_INQUERY, FscBleCentralApiImp.EN_AUTO_VERIFY);
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.e("FscBLE", "onServiceConnected: 绑定服务失败");
        }
    };

    private final BluetoothGattCallback N;

    private
    Runnable mOnConnectTimeoutCallback;

    private
    Runnable mOnScanTimeoutCallback;

    private
    Runnable mOnSmartScanTimeoutCallback;

    private String d = null;

    private ArrayList<Byte> beaconParameterStringBuffer;

    private EncryptInfo mEncryptInfo;
    private int e;

    private boolean mAuthOK = false;
    private int k = 20;
    private BluetoothGattCharacteristic l = null;

    private Handler sHandler = new Handler(Looper.getMainLooper());
    private boolean m = true;
    private Thread n;
    private boolean o = true;
    private BluetoothGattService p;
    private boolean q = true;
    private int r = 81920;
    private boolean t = false;
    private String u = "";
    private BluetoothManager v = null;
    private BluetoothAdapter w = null;

    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt bluetoothGatt = null;
    private ArrayList<BluetoothGattService> bluetoothGattServices = new ArrayList();
    private FscBleCentralCallbacks fscBleCentralCallbacks = null;

    private FscBleCentralCallbacks mUiCallback;
    private boolean D;
    private ScanCallback scanCallback;
    private int F;
    private int G;
    private boolean J;
    private boolean K;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private byte[] O;

    private FscBleCentralApiImp() {
        this.mUiCallback = C;
        this.D = false;
        this.F = 0;
        this.G = 70;

        this.J = false;
        this.K = false;
        this.mOnConnectTimeoutCallback = new Runnable() {
            public void run() {
                FscBleCentralApiImp.b("conn  check");
                FscBleCentralApiImp.b("auth_suc " + mAuthOK);
                FscBleCentralApiImp.b("time check");
                if (d == null && !mAuthOK) {
                    onBeaconAuthFailed(3, "conn timeout");
                }

            }
        };
        this.mOnScanTimeoutCallback = new Runnable() {
            public void run() {
                if (!K) {
                    stopScan();
                }

            }
        };
        this.mOnSmartScanTimeoutCallback = new Runnable() {
            public void run() {
                c();
            }
        };

        this.leScanCallback = new BluetoothAdapter.LeScanCallback() {
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                FscBleCentralApiImp.b("device found call back 1");
                BluetoothDeviceWrapper var4 = new BluetoothDeviceWrapper(device, rssi, BluetoothDeviceWrapper.BLE_MODE);
                if (fscBleCentralCallbacks != null) {
                    FscBleCentralApiImp.b("device found call back 2");
                    fscBleCentralCallbacks.blePeripheralFound(var4, rssi, scanRecord);
                } else {
                    var4.setAdvData(FileUtil.bytesToHex(scanRecord, scanRecord.length));
                    FeasycomUtil.a(var4, scanRecord);
                    Monitor var5 = com.magna.beaconlibrary.util.e.a(device, rssi, scanRecord);
                    var4.setMonitor(var5);
                    Ibeacon var6 = com.magna.beaconlibrary.util.d.a(device, rssi, scanRecord);
                    var4.setiBeacon(var6);
                    EddystoneBeacon var7 = com.magna.beaconlibrary.util.c.a(device, rssi, scanRecord);
                    var4.setgBeacon(var7);
                    AltBeacon var8 = com.magna.beaconlibrary.util.a.a(device, rssi, scanRecord);
                    var4.setAltBeacon(var8);
                    FeasyBeacon var9 = FeasycomUtil.a(device, rssi, scanRecord);
                    var4.setFeasyBeacon(var9);
                    mUiCallback.blePeripheralFound(var4, rssi, scanRecord);
                }
            }
        };
        this.N = new BluetoothGattCallback() {
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                if (mtu > k + 3) {
                    if (mtu > 185) {
                        mtu = 185;
                    }

                    k = mtu - 3;
                    FscBleCentralApiImp.b("mtu " + (k + 3));
                }

                if (mEncryptInfo != null && mEncryptInfo.isEncryptAlgorithmUniversal()) {
                    o();
                }

            }

            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                FscBleCentralApiImp.b("newState   " + newState);
                FscBleCentralApiImp.b("status   " + status);
                if (newState == 2) {
                    FscBleCentralApiImp.b("BluetoothProfile.STATE_CONNECTED");
                    t = true;
                    k();
                } else if (newState == 0) {
                    if (status != 0) {
                        sHandler.post(new Runnable() {
                            public void run() {
                                d();
                                if (e <= 10) {
                                    e++;
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        bluetoothGatt = mBluetoothDevice.connectGatt(FscBleCentralApiImp.s, false, N, 2);
                                    } else {
                                        bluetoothGatt = mBluetoothDevice.connectGatt(FscBleCentralApiImp.s, false, N);
                                    }
                                } else {
                                    t = false;
                                    mUiCallback.blePeripheralDisonnected(bluetoothGatt, mBluetoothDevice);
                                    a(120);
                                    disconnect();
                                }

                            }
                        });
                    } else if (t) {
                        sHandler.post(new Runnable() {
                            public void run() {
                                if (null != bluetoothGatt) {
                                    if (e <= 10) {
                                        e++;
                                        bluetoothGatt.connect();
                                    } else {
                                        d = null;
                                        t = false;
                                        mUiCallback.blePeripheralDisonnected(bluetoothGatt, mBluetoothDevice);
                                        a(120);
                                    }
                                }

                            }
                        });
                    }
                }

            }

            public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                FscBleCentralApiImp.b("onPhyUpdate");
            }

            public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyRead(gatt, txPhy, rxPhy, status);
                FscBleCentralApiImp.b("onPhyRead");
            }

            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == 0) {
                    l();
                    if (mEncryptInfo != null && mEncryptInfo.isEncryptAlgorithmBeacon()) {
                        o();
                    }

                    m();
                    Log.e("FscBLE", "onServicesDiscovered: ******************************************");
                    a(110);
                }

                if (null != l && l.getWriteType() != 1 && (l.getProperties() & 4) != 0) {
                    l.setWriteType(1);
                }

            }

            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == 0) {
                    FscBleCentralApiImp.b("read success");
                    FscBleCentralApiImp.b("ch" + characteristic.getUuid().toString());
                    FscBleCentralApiImp.b("value" + new String(characteristic.getValue()));
                    c(characteristic);
                } else {
                    FscBleCentralApiImp.b("read failed");
                    FscBleCentralApiImp.b("ch" + characteristic.getUuid().toString());
                }

            }

            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                b(characteristic);
            }

            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == 0) {
                }

            }

            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                if (status == 0) {
                }

            }
        };
    }

    private static void b(String var0) {
        LogUtil.i("FscBLE", var0);
    }

    public static FscBleCentralApiImp getInstance() {
        if (I == null) {
            I = new FscBleCentralApiImp();
        }

        return I;
    }

    public static FscBleCentralApiImp getInstance(Context context) {
        s = context;
        if (I == null) {
            I = new FscBleCentralApiImp();
        }

        return I;
    }

    public static FscBleCentralApiImp getInstance(Activity activity) {
        s = activity.getApplicationContext();
        if (I == null) {
            I = new FscBleCentralApiImp();
        }

        return I;
    }

    private static void b(BluetoothGatt var0) {
        try {
            Method var1 = var0.getClass().getMethod("refresh");
            if (null != var1) {
                boolean var2 = (Boolean) var1.invoke(var0);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private static String a(byte[] var0) {
        StringBuilder var1 = new StringBuilder(var0.length);
        byte[] var2 = var0;
        int var3 = var0.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            var1.append(String.format("%02x ", var5).toUpperCase());
        }

        return var1.toString();
    }

    private static String a(String var0, String[] var1) {
        String[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            var0 = var0.replace(var5, "");
        }

        return var0;
    }

    public void a(FscBleCentralCallbacks var1) {
        this.fscBleCentralCallbacks = var1;
    }

    public void setCallbacks(FscBleCentralCallbacks callback) {
        this.mUiCallback = callback;
        if (this.mUiCallback == null) {
            this.mUiCallback = C;
        }

    }

    public FscBleCentralCallbacks a() {
        return this.mUiCallback;
    }

    public BluetoothDevice b() {
        return this.mBluetoothDevice;
    }

    public boolean isConnected() {
        return this.t;
    }

    public void sendATCommand(Set<String> command) {
        Log.e("FscBLE", "sendATCommand: " + command.size());
        b = command;
        if (!AtCommandService.b) {
            Intent var2 = new Intent();
            var2.setClass(s, AtCommandService.class);
            s.bindService(var2, P, Context.BIND_AUTO_CREATE);
        } else {
            ((AtCommandService) c.get()).a(b, true, EN_AUTO_INQUERY, EN_AUTO_VERIFY);
        }

    }

    private boolean a(BluetoothGattCharacteristic var1, boolean var2, byte[] var3) {
        boolean var4 = this.bluetoothGatt.setCharacteristicNotification(var1, var2);
        if (!var4) {
            return false;
        } else {
            List var5 = var1.getDescriptors();
            if (var5 != null && var5.size() > 0) {
                Iterator var6 = var5.iterator();

                while (var6.hasNext()) {
                    BluetoothGattDescriptor var7 = (BluetoothGattDescriptor) var6.next();
                    var7.setValue(var3);
                    this.bluetoothGatt.writeDescriptor(var7);
                }

                return true;
            } else {
                return true;
            }
        }
    }

    public boolean setCharacteristic(BluetoothGattCharacteristic ch, int operation) {
        if (operation == 5) {
            b("operation FscBleCentralApi.ENABLE_CHARACTERISTIC_INDICATE");
            return (ch.getProperties() & 32) == 0 ? false : this.a(ch, true, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        } else if (operation == 6) {
            b("operation FscBleCentralApi.DISABLE_CHARACTERISTIC_INDICATE");
            return this.a(ch, false, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        } else if (operation == 3) {
            b("operation FscBleCentralApi.ENABLE_CHARACTERISTIC_NOTIFICATION");
            return (ch.getProperties() & 16) == 0 ? false : this.a(ch, true, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else if (operation == 4) {
            b("operation FscBleCentralApi.DISABLE_CHARACTERISTIC_NOTIFICATION");
            return (ch.getProperties() & 16) == 0 ? false : this.a(ch, false, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        } else if (operation == 2) {
            if ((ch.getProperties() & 8) != 0) {
                ch.setWriteType(2);
                this.l = ch;
                return true;
            } else {
                return false;
            }
        } else if (operation == 1) {
            if ((ch.getProperties() & 4) != 0) {
                ch.setWriteType(1);
                this.l = ch;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean checkBleHardwareAvailable() {
        BluetoothManager var1 = (BluetoothManager) s.getSystemService(Context.BLUETOOTH_SERVICE);
        if (var1 == null) {
            return false;
        } else {
            BluetoothAdapter var2 = var1.getAdapter();
            if (var2 == null) {
                return false;
            } else {
                boolean var3 = s.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
                return var3;
            }
        }
    }

    public boolean isBtEnabled() {
        BluetoothManager var1 = (BluetoothManager) s.getSystemService(Context.BLUETOOTH_SERVICE);
        if (var1 == null) {
            return false;
        } else {
            BluetoothAdapter var2 = var1.getAdapter();
            return var2 == null ? false : var2.isEnabled();
        }
    }

    public boolean startScan() {
        b("ble startScan");
        if (Build.VERSION.SDK_INT >= 24) {
            if (!this.q) {
                return false;
            }

            this.q = false;
        }

        if (this.w.isDiscovering()) {
            this.w.cancelDiscovery();
        }

        this.mUiCallback.startScan();
        this.sHandler.removeCallbacks(this.mOnScanTimeoutCallback);
        this.sHandler.postDelayed(this.mOnScanTimeoutCallback, 15000L);
        this.sHandler.postDelayed(new Runnable() {
            public void run() {
                q = true;
            }
        }, 6000L);
        (new Thread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= 21 && D) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        w.getBluetoothLeScanner().startScan(scanCallback);
                    }
                } else {
                    w.startLeScan(leScanCallback);
                }

            }
        })).start();
        return true;
    }

    public boolean startScan(int time) {
        b("startScan");
        if (time == 0) {
            this.K = true;
        } else {
            this.K = false;
        }

        if (Build.VERSION.SDK_INT >= 24) {
            if (!this.q) {
                return false;
            }

            this.q = false;
        } else if (Build.VERSION.SDK_INT >= 21 && this.scanCallback != null && this.D) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.w.getBluetoothLeScanner().stopScan(this.scanCallback);
            }
        } else {
            this.w.stopLeScan(this.leScanCallback);
        }

        if (this.w.isDiscovering()) {
            this.w.cancelDiscovery();
        }

        this.mUiCallback.startScan();
        this.sHandler.removeCallbacks(this.mOnScanTimeoutCallback);
        this.sHandler.postDelayed(this.mOnScanTimeoutCallback, (long) time);
        this.sHandler.postDelayed(new Runnable() {
            public void run() {
                q = true;
            }
        }, 6000L);
        (new Thread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= 21 && D) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        w.getBluetoothLeScanner().startScan(scanCallback);
                    }
                } else {
                    w.startLeScan(leScanCallback);
                }

            }
        })).start();
        return true;
    }

    public boolean a(int var1, final QuickConnectionParam var2) {
        b("startScan");
        if (Build.VERSION.SDK_INT >= 24) {
            if (!this.q) {
                return false;
            }

            this.q = false;
        }

        this.mUiCallback.startScan();
        this.sHandler.removeCallbacks(this.mOnScanTimeoutCallback);
        this.sHandler.removeCallbacks(this.mOnSmartScanTimeoutCallback);
        this.sHandler.postDelayed(this.mOnSmartScanTimeoutCallback, (long) var1);
        this.sHandler.postDelayed(new Runnable() {
            public void run() {
                q = true;
            }
        }, 6000L);
        (new Thread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= 21) {
                    ScanSettings var1 = null;
                    var1 = (new ScanSettings.Builder()).setScanMode(2).build();
                    ArrayList var2x = new ArrayList();
                    if (var2.getName() != null) {
                        var2x.add((new android.bluetooth.le.ScanFilter.Builder()).setDeviceName(var2.getName()).build());
                    }

                    if (var2.getMac() != null) {
                        var2x.add((new android.bluetooth.le.ScanFilter.Builder()).setDeviceAddress(var2.getMac()).build());
                    }

                    w.getBluetoothLeScanner().startScan(var2x, var1, scanCallback);
                } else {
                    w.startLeScan(leScanCallback);
                }

            }
        })).start();
        return true;
    }

    public void stopScan() {
        b("stopScan");
        this.mUiCallback.stopScan();
        if (Build.VERSION.SDK_INT >= 21 && this.scanCallback != null && this.D) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.w.getBluetoothLeScanner().stopScan(this.scanCallback);
            }
        } else {
            this.w.stopLeScan(this.leScanCallback);
        }

    }

    public void c() {
        b("stopScan");
        this.mUiCallback.stopScan();
        if (Build.VERSION.SDK_INT >= 21 && this.scanCallback != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.w.getBluetoothLeScanner().stopScan(this.scanCallback);
            }
        } else {
            this.w.stopLeScan(this.leScanCallback);
        }

        this.a((FscBleCentralCallbacks) null);
    }

    public boolean initialize() {
        if (this.v == null) {
            this.v = (BluetoothManager) s.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.v == null) {
                return false;
            }
        }

        if (this.w == null) {
            this.w = this.v.getAdapter();
        }

        if (this.w == null) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                this.scanCallback = new ScanCallback() {
                    @RequiresApi(
                            api = 21
                    )
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        Log.e("FscBLE", "onScanResult: 5.0以下调用");
                        FscBleCentralApiImp.b("5.0 device found call back 1");
                        BluetoothDevice var3 = result.getDevice();
                        int var4 = result.getRssi();
                        byte[] var5 = result.getScanRecord().getBytes();

                        try {
                            Log.e("FscBLE", "onScanResult: " + new String(var5, "utf-8"));
                        } catch (UnsupportedEncodingException var11) {
                            var11.printStackTrace();
                            Log.e("FscBLE", "onScanResult: " + var11);
                        }

                        BluetoothDeviceWrapper var6 = new BluetoothDeviceWrapper(var3, var4, BluetoothDeviceWrapper.BLE_MODE);
                        if (fscBleCentralCallbacks != null) {
                            FscBleCentralApiImp.b("5.0 device found call back 2");
                            fscBleCentralCallbacks.blePeripheralFound(var6, var4, var5);
                        } else {
                            var6.setName(result.getScanRecord().getDeviceName());
                            var6.setAdvData(FileUtil.bytesToHex(var5, var5.length));
                            FeasycomUtil.a(var6, var5);
                            Ibeacon var7 = com.magna.beaconlibrary.util.d.a(var3, var4, var5);
                            var6.setiBeacon(var7);
                            EddystoneBeacon var8 = com.magna.beaconlibrary.util.c.a(var3, var4, var5);
                            var6.setgBeacon(var8);
                            AltBeacon var9 = com.magna.beaconlibrary.util.a.a(var3, var4, var5);
                            var6.setAltBeacon(var9);
                            FeasyBeacon var10 = FeasycomUtil.a(var3, var4, var5);
                            var6.setFeasyBeacon(var10);
                            mUiCallback.blePeripheralFound(var6, var4, var5);
                        }
                    }

                    public void onBatchScanResults(List<ScanResult> results) {
                        super.onBatchScanResults(results);
                        FscBleCentralApiImp.b("5.0 onBatchScanResults");
                    }

                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        FscBleCentralApiImp.b("5.0 onScanFailed");
                    }
                };
            }

            LogUtil.initialize(s);
            return true;
        }
    }

    private boolean i() {
        b("connect  enter");
        this.e = 0;
        this.k = 20;
        if (this.w != null && this.u != null) {
            this.mBluetoothDevice = this.w.getRemoteDevice(this.u);
            if (this.mBluetoothDevice == null) {
                return false;
            } else {
                try {
                    this.sHandler.removeCallbacks(this.mOnConnectTimeoutCallback);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

                b("connect  post");
                this.sHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        j();
                    }
                });
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean a(String var1, EncryptInfo var2) {
        b("connect  enter");
        this.mEncryptInfo = var2;
        this.F = this.G;
        this.e = 0;
        this.k = 20;
        this.l = null;
        if (this.w != null && var1 != null) {
            this.u = var1;
            this.mBluetoothDevice = this.w.getRemoteDevice(this.u);
            if (this.mBluetoothDevice == null) {
                return false;
            } else {
                try {
                    this.sHandler.removeCallbacks(this.mOnConnectTimeoutCallback);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

                if (!" ".equals(this.mEncryptInfo.getPassword()) && this.mEncryptInfo.getPassword() != null) {
                    this.mAuthOK = false;
                    this.sHandler.postDelayed(this.mOnConnectTimeoutCallback, 12000L);
                } else {
                    this.beaconParameterStringBuffer = FeasycomUtil.byteFifo1;
                }

                b("connect  post");
                this.sHandler.post(new Runnable() {
                    public void run() {
                        j();
                    }
                });
                return true;
            }
        } else {
            return false;
        }
    }

    private void j() {
        b("connect gatt");
        if (Build.VERSION.SDK_INT < 21) {
            this.r = 4096;
        }

        FeasycomUtil.a = new LinkedBlockingQueue(this.r);
        if (this.bluetoothGatt != null && this.bluetoothGatt.getDevice().getAddress().equals(this.u)) {
            b("gatt.connect()");
            boolean var1 = this.bluetoothGatt.connect();
            b("gatt.connect() => " + var1);
            if (var1) {
            }
        } else {
            b("dev.connectGatt()");
            if (Build.VERSION.SDK_INT >= 21) {
                this.bluetoothGatt = this.mBluetoothDevice.connectGatt(s, false, this.N, 2);
            } else {
                this.bluetoothGatt = this.mBluetoothDevice.connectGatt(s, false, this.N);
            }
        }

    }

    public boolean connect(String deviceAddress) {
        b("connect  enter");
        this.F = this.G;
        this.mEncryptInfo = EncryptInfo.createRandom("Universal");
        this.e = 0;
        this.k = 20;
        this.l = null;
        if (this.w != null && deviceAddress != null) {
            this.u = deviceAddress;
            this.mBluetoothDevice = this.w.getRemoteDevice(this.u);
            if (this.mBluetoothDevice == null) {
                return false;
            } else {
                try {
                    this.sHandler.removeCallbacks(this.mOnConnectTimeoutCallback);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

                this.mAuthOK = false;
                this.sHandler.postDelayed(this.mOnConnectTimeoutCallback, 12000L);
                b("connect  post");
                this.sHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        j();
                    }
                });
                return true;
            }
        } else {
            return false;
        }
    }

    public void disconnect() {
        b("disconnect");
        this.stopSend();

        try {
            this.a((FscBleCentralCallbacks) null);
            s.unbindService(P);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        this.d = null;
        this.mEncryptInfo = null;
        this.beaconParameterStringBuffer = null;
        this.e = 12;
        this.l = null;
        this.t = false;
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.disconnect();
        }

        this.sHandler.post(new Runnable() {
            public void run() {
                if (null != bluetoothGatt) {
                    FscBleCentralApiImp.b(bluetoothGatt);
                     d();
                    mUiCallback.blePeripheralDisonnected(bluetoothGatt, mBluetoothDevice);
                }

            }
        });
    }

    public void d() {
        if (null != this.bluetoothGatt) {
            b("gatt close enter");
            this.bluetoothGatt.close();
        }

        this.bluetoothGatt = null;
    }

    private void k() {
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.discoverServices();
        }

    }

    private void l() {
        if (this.bluetoothGattServices != null && this.bluetoothGattServices.size() > 0) {
            this.bluetoothGattServices.clear();
        }

        if (this.bluetoothGatt != null) {
            this.bluetoothGattServices.addAll(this.bluetoothGatt.getServices());
        }

        Iterator var1 = this.bluetoothGattServices.iterator();

        BluetoothGattService var2;
        while (var1.hasNext()) {
            var2 = (BluetoothGattService) var1.next();
            String var3 = var2.getUuid().toString().substring(4, 8);
            if (this.c(var3)) {
                var1.remove();
            } else if (var3.toLowerCase().equals("fff0")) {
                this.a(var2);
            } else if (var3.toLowerCase().equals("180a")) {
                this.p = var2;
            }
        }

        var1 = this.bluetoothGattServices.iterator();

        label69:
        while (true) {
            do {
                if (!var1.hasNext()) {
                    this.sHandler.postDelayed(new Runnable() {
                        public void run() {
                            Log.e("FscBLE", "run: " + Looper.getMainLooper());
                            Log.e("FscBLE", "run: " + Looper.myLooper());
                            Log.e("FscBLE", "run: sendAuthInfo");
//                             sendAuthInfo();
                        }
                    }, 500L);
                    b("connected");
                    this.mUiCallback.blePeripheralConnected(this.bluetoothGatt, this.mBluetoothDevice);
                    this.e = 12;
                    this.mUiCallback.servicesFound(this.bluetoothGatt, this.mBluetoothDevice, this.bluetoothGattServices);
                    this.t = true;
                    if (null != this.l) {
                        this.a(this.l);
                    }

                    return;
                }

                var2 = (BluetoothGattService) var1.next();
            } while (this.c(var2.getUuid().toString().substring(4, 8)));

            Iterator var6 = var2.getCharacteristics().iterator();

            while (true) {
                BluetoothGattCharacteristic var4;
                int var5;
                do {
                    if (!var6.hasNext()) {
                        continue label69;
                    }

                    var4 = (BluetoothGattCharacteristic) var6.next();
                    var5 = var4.getProperties();
                    if ((var5 & 2) != 0) {
                    }

                    if ((var5 & 16) != 0) {
                        this.a(var4, true, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                } while ((var5 & 4) == 0 && (var5 & 8) == 0);

                if (this.l == null) {
                    this.l = var4;
                }
            }
        }
    }

    private void a(int var1) {
        Log.e("FscBLE", "notifyOtaState: " + var1);
        if (this.J) {
            this.J = false;
            this.mUiCallback.otaProgressUpdate(0, var1);
        }

    }

    private void m() {
    }

    private native void sendAuthInfo();

    private native void beaconAuthInfoRunnableRun();

    private native void universalAuthInfoRunnableRun();



    private void a(BluetoothGattService var1) {
        Iterator var2 = var1.getCharacteristics().iterator();

        while (var2.hasNext()) {
            BluetoothGattCharacteristic var3 = (BluetoothGattCharacteristic) var2.next();
            int var4 = var3.getProperties();
            if ((var4 & 16) != 0) {
                this.a(var3, true, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }

            if ((var4 & 8) != 0 && this.l == null) {
                this.l = var3;
            }
        }

    }

    private void n() {
        if (this.bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= 21 && this.k < 185 && !this.bluetoothGatt.requestMtu(185)) {
            }

        }
    }

    private boolean c(String var1) {
        return L.contains(var1.toLowerCase());
    }

    private void a(BluetoothGattCharacteristic var1) {
        if (var1 != null) {
            this.mUiCallback.characteristicForService(this.bluetoothGatt, this.mBluetoothDevice, var1.getService(), var1);
        }
    }

    public void read(BluetoothGattCharacteristic ch) {
        if (this.w != null && this.bluetoothGatt != null) {
            this.bluetoothGatt.readCharacteristic(ch);
        }
    }

    public boolean startOTA(byte[] dfuFile, boolean reset) {
        String var3 = "0";
        if (this.isConnected() && null != dfuFile) {
            if (reset) {
                var3 = "1";
            }

            TeaCode teaCode = new TeaCode();
            byte[] var5 = teaCode.feasycom_decryption(dfuFile);
            if (var5 == null) {
                return false;
            } else {
                byte[] var6 = new byte[var5.length - 1024];
                System.arraycopy(var5, 1024, var6, 0, var5.length - 1024);
                this.sendCommand(("EnterDFU" + var3).getBytes());

                try {
                    Thread.sleep(500L);
                    this.disconnect();
                    Thread.sleep(1500L);
                    this.i();
                    this.J = true;
                } catch (Exception var8) {
                    var8.printStackTrace();
                }

                return true;
            }
        } else {
            return false;
        }
    }


  
    private native void onBeaconAuthFailed(int var1, String var2);

    
    private native boolean onResponseAuth(String var1, String var2);

    private boolean b(BluetoothGattCharacteristic var1) {
        if (this.w != null && this.bluetoothGatt != null && var1 != null) {
            byte[] var2 = var1.getValue();
            if (var2.length <= 0) {
                return true;
            } else {
                String var3 = null;

                try {
                    var3 = new String(var2, "UTF-8");
                    b("rec " + var3);
                } catch (UnsupportedEncodingException var7) {
                    var7.printStackTrace();
                }

                String var4 = a(var2);
                String var5 = LogUtil.currentTimeStamp();
                if (this.fscBleCentralCallbacks == null) {
                    b("york null");
                    if (var3.contains("AUTH") && this.d == null) {
                        String var6 = var4.replace(" ", "").toUpperCase();
                        if (this.onResponseAuth(var3, var6)) {
                            return true;
                        }

                        Log.i("FscBLE", "onResponseAuth false");
                    }
                } else {
                    if (null != this.beaconParameterStringBuffer) {
                        this.b(var2);
                    }

                    this.fscBleCentralCallbacks.packetReceived(this.bluetoothGatt, this.mBluetoothDevice, var1.getService(), var1, var3, var4, var2, var5);
                }

                if (null == this.d && var3.length() >= 4 && var3.substring(0, 4).equals("AUTH")) {
                    this.d = var3;
                } else {
                    this.mUiCallback.packetReceived(this.bluetoothGatt, this.mBluetoothDevice, var1.getService(), var1, var3, var4, var2, var5);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private void b(byte[] var1) {
        Log.e("FscBLE", "handleBeaconAtCommands: **********************************");
        FeasycomUtil.a(FeasycomUtil.byteFifo1, var1);
        byte[] var2 = FeasycomUtil.a(FeasycomUtil.byteFifo1);
        String var3 = new String(var2);
        b("beacon receive  " + var3);
        if (var3.contains("OK\r\n") || var3.contains("ERROR\r\n") || var3.contains("Opened")) {
            FscBeaconCallbacks var4 = (FscBeaconCallbacks) this.mUiCallback;
            if (var3.contains("Opened")) {
                var4.deviceInfo("Opened", (Object) null);
                var4.deviceInfo("MODEL", FscBeaconApiImp.moduleString);
                var4.deviceInfo("VERSION", FscBeaconApiImp.versionString);
            } else if (var3.contains("NAME")) {
                String var5 = a(var3, new String[]{"+NAME=", "\r", "\n", "OK", "+LENAME="});
                if (!var3.contains(var5)) {
                    var4.deviceInfo("NAME", a(var3, new String[]{"\n+NAME=", "\r", "\n\nOK\n", "\n+LENAME="}));
                } else {
                    var4.deviceInfo("NAME", var5);
                }
            } else if (var3.contains("BWMODE")) {
                var4.deviceInfo("BWMODE", var3.contains("+BWMODE=1") ? "1" : "0");
            } else if (var3.contains("PIN")) {
                var4.deviceInfo("PIN", a(var3, new String[]{"+PIN=", "\r", "\n", "OK"}));
            } else if (var3.contains("ADVIN")) {
                var4.deviceInfo("ADVIN", a(var3, new String[]{"+ADVIN=", "\r", "\n", "OK"}));
            } else if (var3.contains("BADVDATA")) {
                if (null != FscBeaconApiImp.beacons) {
                    FeasycomUtil.a(var3.toLowerCase(), FscBeaconApiImp.beacons);
                }

                var4.deviceInfo("BADVDATA", FscBeaconApiImp.beacons);
                if (!"BP103".equals(FscBeaconApiImp.moduleString) && !"BP104".equals(FscBeaconApiImp.moduleString)) {
                    var4.deviceInfo("END", (Object) null);
                }
            } else {
                String var6;
                if (var3.contains("TXPOWER")) {
                    var6 = a(var3, new String[]{"+TXPOWER=", "\r", "\n", "OK"});
                    if ("BP671".equals(FscBeaconApiImp.moduleString)) {
                        var4.deviceInfo("TXPOWER", var6);
                    } else {
                        int var7 = FileUtil.formattingOneHexToInt(var6);
                        var4.deviceInfo("TXPOWER", Integer.valueOf(var7).toString());
                    }
                } else if (var3.contains("EXTEND")) {
                    var6 = var3.substring(var3.indexOf(",") + 1).replace("\r\n\r\nOK\r\n", "");
                    b("EXTEND  " + var6);
                    var4.deviceInfo("EXTEND", var6);
                } else if (var3.contains("RAP")) {
                    var4.deviceInfo("RAP", a(var3, new String[]{"+RAP=", "\r", "\n", "OK"}));
                } else {
                    String[] var8;
                    String[] var9;
                    String[] var10;
                    if (var3.contains("KEYCFG")) {
                        var9 = var3.split("=");
                        var10 = var9[1].split(",");
                        var8 = new String[20];
                        var8[0] = var10[0];
                        var8[1] = var10[1].replace("\r\n\r\nOK\r\n", "");
                        var4.deviceInfo("KEYCFG", var8);
                    } else if (var3.contains("GSCFG")) {
                        var9 = var3.split("=");
                        var10 = var9[1].split(",");
                        var8 = new String[20];
                        var8[0] = var10[0];
                        var8[1] = var10[1].replace("\r\n\r\nOK\r\n", "");
                        var4.deviceInfo("GSCFG", var8);
                    } else if (var3.contains("BUZ")) {
                        var6 = var3.split("=")[1].replace("\r\n\r\nOK\r\n", "");
                        var4.deviceInfo("BUZ", var6);
                    } else if (var3.contains("LED")) {
                        var6 = var3.split("=")[1].replace("\r\n\r\nOK\r\n", "");
                        var4.deviceInfo("LED", var6);
                    }
                }
            }

            FeasycomUtil.b(FeasycomUtil.byteFifo1);
        }
    }

    private void c(BluetoothGattCharacteristic var1) {
        if (this.w != null && this.bluetoothGatt != null && var1 != null) {
            byte[] var3 = var1.getValue();
            if (var3.length > 0) {
                String var4 = null;

                try {
                    var4 = new String(var3, "UTF-8");
                } catch (UnsupportedEncodingException var6) {
                    var6.printStackTrace();
                    return;
                }

                String var2 = a(var3);
                this.mUiCallback.readResponse(this.bluetoothGatt, this.mBluetoothDevice, var1.getService(), var1, var4, var2, var3, LogUtil.currentTimeStamp());
            }
        }
    }

    private boolean a(BluetoothGattCharacteristic var1, byte[] var2) {
        if (this.w != null && this.bluetoothGatt != null && var1 != null) {
            Log.e("text", "writeDataToCharacteristic: " + new String(var2));
            b("writech" + var1.getUuid().toString());
            b("write" + new String(var2));
            if (var1.getWriteType() != 1 && var1.getWriteType() == 2) {
            }

            var1.setValue(var2);
            boolean var3 = this.bluetoothGatt.writeCharacteristic(var1);
            b("flag " + var3);
            return var3;
        } else {
            if (null == this.w) {
            }

            if (null == this.bluetoothGatt) {
            }

            if (null == var1) {
            }

            return false;
        }
    }

    private void c(byte[] var1) {
        Log.e("FscBLE", "writeData: " + new String(var1));

        while (!this.a(this.e(), var1) && this.t) {
        }

    }

    private void a(byte[] var1, int var2) {
        while (!this.a(this.e(), var1) && this.t) {
            if (Build.VERSION.SDK_INT < 21) {
                try {
                    Thread.sleep((long) var2);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
            }
        }

    }

    public BluetoothGattCharacteristic e() {
        return this.l;
    }

    private void a(int var1, byte[] var2) {
        this.mUiCallback.sendPacketProgress(this.bluetoothGatt, this.mBluetoothDevice, this.l, var1, var2);
    }


    public boolean sendCommand(final byte[] data) {
        this.O = data;
        b("sendCommand enter");
        if (!this.m) {
            return false;
        } else {
            this.o = true;
            this.sHandler.postDelayed(new Runnable() {
                public void run() {
                    int var1 = 0;
                    int var2 = 0;
                    byte[] var3 = new byte[k];

                    while (data.length - var1 > k) {
                        if (!o) {
                            a(var1 * 100 / data.length, (byte[]) null);
                            return;
                        }

                        System.arraycopy(data, var1, var3, 0, k);
                        c(var3);
                        var1 += k;
                        a(var1 * 100 / data.length, var3);
                        ++var2;

                        try {
                            Thread.sleep(70L);
                        } catch (InterruptedException var5) {
                            var5.printStackTrace();
                        }
                    }

                    int var4 = data.length - var1;
                    if (var4 > 0) {
                        var3 = new byte[var4];
                        System.arraycopy(data, var1, var3, 0, var4);
                        c(var3);
                        m = true;
                        a(100, (byte[]) var3);
                        FscBleCentralApiImp.b("send" + new String(data));
                        FscBleCentralApiImp.b("send" + FileUtil.bytesToHex(data, data.length));
                    }
                }
            }, 100L);
            return true;
        }
    }

    public boolean send(final byte[] data) {
        b("send " + new String(data) + " length:" + data.length);
        if (this.e() == null) {
            b("getWritecharacteristic() == null");
            return false;
        } else if (!this.m) {
            b("isFinishSendPackge == false");
            return false;
        } else {
            this.m = false;
            if (null == data) {
                b("sendPacket == null");
                return false;
            } else {
                this.o = true;
                this.n = new Thread(new Runnable() {
                    public void run() {
                        int var1 = 0;
                        int var2 = 0;

                        byte[] var3;
                        for (var3 = new byte[k]; data.length - var1 > k; ++var2) {
                            if (!o) {
                                a(var1 * 100 / data.length, (byte[]) null);
                                return;
                            }

                            System.arraycopy(data, var1, var3, 0, k);
                            FeasycomUtil.a(var3);
                            var1 += k;
                            a(var1 * 100 / data.length, var3);
                        }

                        int var4 = data.length - var1;
                        if (var4 > 0) {
                            var3 = new byte[var4];
                            System.arraycopy(data, var1, var3, 0, var4);
                            FeasycomUtil.a(var3);
                            m = true;
                            a(100, (byte[]) var3);
                        }
                    }
                });
                this.n.start();
                return true;
            }
        }
    }

    public boolean setSendInterval(int ms) {
        if (ms >= 0 && ms <= 1000) {
            this.F = ms;
            b("send interval " + this.F);
            return true;
        } else {
            return false;
        }
    }

    public boolean cancleSendInterval() {
        this.F = this.G;
        return true;
    }

    public void stopSend() {
        this.o = false;
        this.m = true;
    }

    private void o() {
        (new Thread(new Runnable() {
            public void run() {
                FscBleCentralApiImp.b("send interval " + F);

                while (t) {
                    if (FeasycomUtil.a.size() == 0 && m) {
                        a(101, (byte[]) null);
                    }

                    try {
                        if (FeasycomUtil.a.size() < k) {
                            Thread.sleep(100L);
                        }

                        byte[] var1 = FeasycomUtil.a(k);
                        if (var1 != null) {
                            a((byte[]) var1, 15);
                            if (F > 0 && F <= 1000) {
                                Thread.sleep((long) F);
                            }
                        }
                    } catch (InterruptedException var2) {
                        var2.printStackTrace();
                    } catch (NullPointerException var3) {
                        var3.printStackTrace();
                    }
                }

            }
        })).start();
    }
}
