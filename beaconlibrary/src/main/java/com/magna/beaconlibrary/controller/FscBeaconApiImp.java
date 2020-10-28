package com.magna.beaconlibrary.controller;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.magna.beaconlibrary.bean.BeaconBean;
import com.magna.beaconlibrary.bean.BluetoothDeviceWrapper;
import com.magna.beaconlibrary.bean.CommandBean;
import com.magna.beaconlibrary.bean.EncryptInfo;
import com.magna.beaconlibrary.bean.FeasyBeacon;
import com.magna.beaconlibrary.util.FeasycomUtil;

import java.util.ArrayList;
import java.util.HashSet;

public class FscBeaconApiImp implements FscBeaconApi {
    public static String versionString;
    public static String moduleString;
    public static String nameString;
    public static String encryptWay;
    public static ArrayList<BeaconBean> beacons;
    public static CommandBean commandBean;
    public static FeasyBeacon feasyBeacon;
    private static Context d;
    private static FscBeaconApiImp e;
    private static FscBleCentralApiImp f = null;
    private final String c = "FscBeacon";
    String a;
    String b;

    private FscBeaconApiImp() {
    }

    public static FscBeaconApiImp getInstance(Activity activity) {
        d = activity.getApplicationContext();
        if (null == e) {
            e = new FscBeaconApiImp();
        }

        f = FscBleCentralApiImp.getInstance(activity);
        return e;
    }

    public static FscBeaconApiImp getInstance() {
        if (null == e) {
            e = new FscBeaconApiImp();
        }

        return e;
    }

    public boolean isBtEnabled() {
        return f.isBtEnabled();
    }

    public boolean checkBleHardwareAvailable() {
        return f.checkBleHardwareAvailable();
    }

    public void initialize() {
        f.initialize();
    }

    public void setCallbacks(FscBeaconCallbacks callback) {
        f.setCallbacks(callback);
    }

    public boolean connect(BluetoothDeviceWrapper device, String pin) {
        if (null == d) {
            return false;
        } else {
            if (pin != null) {
                try {
                    Integer.valueOf(pin);
                } catch (Exception var4) {
                    var4.printStackTrace();
                    return false;
                }

                if (pin.length() == 0 || pin.length() > 6) {
                    return false;
                }
            }

            if (null == device.getFeasyBeacon()) {
                return false;
            } else {
                beacons = new ArrayList();

                for (int var3 = 0; var3 < 10; ++var3) {
                    beacons.add(new BeaconBean(Integer.valueOf(var3 + 1).toString(), ""));
                }

                commandBean = new CommandBean();
                nameString = device.getName();
                moduleString = FeasycomUtil.a(device.getFeasyBeacon().getModule());
                encryptWay = device.getFeasyBeacon().getEncryptionWay();
                versionString = FeasycomUtil.e(device.getFeasyBeacon().getVersion());
                EncryptInfo encryptInfo = EncryptInfo.create(device.getAddress(), pin, "Beacon");
                f.a(device.getAddress(), encryptInfo);
                return true;
            }
        }
    }

    public void disconnect() {
        nameString = null;
        beacons = null;
        commandBean = null;
        moduleString = null;
        encryptWay = null;
        versionString = null;
        f.disconnect();
    }

    public void startScan(int time) {
        f.startScan(time);
    }

    public void stopScan() {
        f.stopScan();
    }

    public boolean isConnected() {
        return f != null ? f.isConnected() : false;
    }

    public void startGetDeviceInfo() {
        FscBleCentralApiImp.EN_AUTO_INQUERY = false;
        FscBleCentralApiImp.EN_AUTO_VERIFY = false;
        HashSet var1 = new HashSet();
        var1.add("AT+RAP");
        var1.add("AT+MAC");
        f.sendATCommand(var1);
    }

    public void startGetDeviceInfo(String moduleString) {
        FscBleCentralApiImp.EN_AUTO_INQUERY = false;
        FscBleCentralApiImp.EN_AUTO_VERIFY = false;
        HashSet var2 = new HashSet();
        var2.add("AT+BWMODE");
        var2.add("AT+NAME");
        var2.add("AT+PIN");
        var2.add("AT+ADVIN");
        var2.add("AT+EXTEND");
        var2.add("AT+BADVDATA");
        if (moduleString != null && !"".equals(moduleString) && ("26".equals(moduleString) || "27".equals(moduleString) || "28".equals(moduleString) || "29".equals(moduleString) || "30".equals(moduleString) || "31".equals(moduleString))) {
            var2.add("AT+TXPOWER");
        }

        f.sendATCommand(var2);
    }

    public void startGetDeviceInfo(String moduleString, FeasyBeacon fb) {
        FscBleCentralApiImp.EN_AUTO_INQUERY = false;
        FscBleCentralApiImp.EN_AUTO_VERIFY = false;
        HashSet var3 = new HashSet();
        var3.add("AT+BWMODE");
        var3.add("AT+NAME");
        var3.add("AT+PIN");
        var3.add("AT+ADVIN");
        var3.add("AT+EXTEND");
        var3.add("AT+BADVDATA");
        if (fb.getKeycfg()) {
            var3.add("AT+KEYCFG");
        }

        if (fb.getGsensor()) {
            var3.add("AT+GSCFG");
        }

        if (fb.getBuzzer()) {
            var3.add("AT+BUZ=1,100");
        }

        if (fb.getLed()) {
            var3.add("AT+LED");
        }

        if (moduleString != null && !"".equals(moduleString) && ("26".equals(moduleString) || "27".equals(moduleString) || "28".equals(moduleString) || "29".equals(moduleString) || "30".equals(moduleString) || "31".equals(moduleString))) {
            var3.add("AT+TXPOWER");
        }

        f.sendATCommand(var3);
    }

    public void setBuzzer(boolean flag) {
        if (commandBean != null && beacons != null) {
            FscBleCentralApiImp.EN_AUTO_INQUERY = false;
            FscBleCentralApiImp.EN_AUTO_VERIFY = false;
            if (flag) {
                commandBean.setCommand("AT+BUZ=1,100");
            } else {
                commandBean.setCommand("AT+BUZ=0");
            }

            f.sendATCommand(commandBean.getCommand());
        }
    }

    public void setDeviceName(String deviceName) {
        if (commandBean != null) {
            if (f.isConnected()) {
                if (null != deviceName && !"".equals(deviceName)) {
                    commandBean.setCommand("AT+NAME=" + deviceName);
                    commandBean.setCommand("AT+LENAME=" + deviceName);
                } else {
                    commandBean.setCommand("AT+NAME=Feasycom");
                    commandBean.setCommand("AT+LENAME=Feasycom");
                }

            }
        }
    }

    public void setFscPin(String pin) {
        if (commandBean != null) {
            if (f.isConnected()) {
                try {
                    Integer.valueOf(pin);
                } catch (Exception var3) {
                    var3.printStackTrace();
                    pin = "0000";
                }

                if (pin.length() == 0 || pin.length() > 6) {
                    pin = "0000";
                }

                commandBean.setCommand("AT+PIN=" + pin);
            }
        }
    }

    public void setBroadcastInterval(String intervalTime) {
        if (commandBean != null) {
            if (f.isConnected()) {
                commandBean.setCommand("AT+ADVIN=" + intervalTime);
            }
        }
    }

    public void setTxPower(String txPower) {
        if (commandBean != null) {
            if (f.isConnected()) {
                if (!moduleString.equals("BP671") && txPower.length() != 1) {
                    txPower = "7";
                }

                commandBean.setCommand("AT+TXPOWER=" + txPower);
            }
        }
    }

    public void setExtend(String extendString) {
        if (commandBean != null) {
            if (f.isConnected()) {
                if (extendString != null) {
                    if (extendString.length() != 0) {
                        commandBean.setCommand("AT+EXTEND=" + extendString.getBytes().length + "," + extendString);
                    }
                }
            }
        }
    }

    public void setConnectable(boolean connectable) {
        if (commandBean != null) {
            if (connectable) {
                commandBean.setCommand("AT+BWMODE=0");
            } else {
                commandBean.setCommand("AT+BWMODE=1");
            }

        }
    }

    public void setGscfg(String advin, String duration) {
        if (commandBean != null) {
            if (f.isConnected()) {
                commandBean.setCommand("AT+GSCFG=" + advin + "," + duration);
            }
        }
    }

    public void setKeycfg(String advin, String duration) {
        if (commandBean != null) {
            if (f.isConnected()) {
                commandBean.setCommand("AT+KEYCFG=" + advin + "," + duration);
            }
        }
    }

    public void setLed(String ledTime) {
        commandBean.setCommand("AT+LED=" + ledTime);
    }

    public boolean isBeaconInfoFull() {
        for (int var2 = 0; var2 < beacons.size(); ++var2) {
            BeaconBean var1 = (BeaconBean) beacons.get(var2);
            if ("".equals(var1.getBeaconType()) || null == var1.getBeaconType()) {
                return false;
            }
        }

        return true;
    }

    public boolean addBeaconInfo(BeaconBean beacon) {
        boolean var2 = false;
        int var4 = 1;
        int var5 = 10;

        BeaconBean var3;
        int var6;
        for (var6 = 0; var6 < beacons.size(); ++var6) {
            var3 = (BeaconBean) beacons.get(var6);
            if ("".equals(var3.getBeaconType())) {
                if ("URL".equals(beacon.getBeaconType())) {
                    var2 = true;
                    var3.setBeaconType(beacon.getBeaconType());
                    var3.setUrl(beacon.getUrl());
                    var3.setPower(beacon.getPower());
                    var3.setEnable(beacon.isEnable());
                } else if ("UID".equals(beacon.getBeaconType())) {
                    var2 = true;
                    var3.setBeaconType(beacon.getBeaconType());
                    var3.setNameSpace(beacon.getNameSpace());
                    var3.setInstance(beacon.getInstance());
                    var3.setReserved(beacon.getReserved());
                    var3.setPower(beacon.getPower());
                    var3.setEnable(beacon.isEnable());
                } else if ("iBeacon".equals(beacon.getBeaconType())) {
                    var2 = true;
                    var3.setBeaconType(beacon.getBeaconType());
                    var3.setUuid(beacon.getUuid());
                    var3.setMajor(beacon.getMajor());
                    var3.setMinor(beacon.getMinor());
                    var3.setPower(beacon.getPower());
                    var3.setEnable(beacon.isEnable());
                } else if ("AltBeacon".equals(beacon.getBeaconType())) {
                    var2 = true;
                    var3.setBeaconType(beacon.getBeaconType());
                    var3.setId1(beacon.getId1());
                    var3.setId2(beacon.getId2());
                    var3.setId3(beacon.getId3());
                    var3.setPower(beacon.getPower());
                    var3.setEnable(beacon.isEnable());
                    var3.setManufacturerReserved(beacon.getManufacturerReserved());
                    var3.setManufacturerId(beacon.getManufacturerId());
                }
                break;
            }
        }

        for (var6 = 0; var6 < beacons.size(); ++var6) {
            var3 = (BeaconBean) beacons.get(var6);
            if (!"".equals(var3.getBeaconType()) && null != var3.getBeaconType()) {
                var3.setIndex(Integer.valueOf(var4).toString());
                ++var4;
            } else {
                var3.setIndex(Integer.valueOf(var5).toString());
                --var5;
            }
        }

        return var2;
    }

    public boolean deleteBeaconInfo(String index) {
        boolean var2 = false;

        BeaconBean var3;
        int var4;
        for (var4 = 0; var4 < beacons.size(); ++var4) {
            var3 = (BeaconBean) beacons.get(var4);
            if (index.equals(var3.getIndex())) {
                if (!"".equals(var3.getBeaconType()) && null != var3.getBeaconType()) {
                    var2 = true;
                    beacons.remove(var4);
                    beacons.add(new BeaconBean(Integer.valueOf(var4 + 1).toString(), ""));
                    break;
                }

                var2 = false;
                return var2;
            }
        }

        for (var4 = 0; var4 < beacons.size(); ++var4) {
            var3 = (BeaconBean) beacons.get(var4);
            var3.setIndex(Integer.valueOf(var4 + 1).toString());
        }

        return var2;
    }

    public BeaconBean getBeaconInfo(String index) {
        BeaconBean var2 = null;

        try {
            var2 = (BeaconBean) beacons.get(Integer.valueOf(index) - 1);
        } catch (Exception var4) {
            var4.printStackTrace();
            var2 = null;
        }

        return var2;
    }

    public boolean updateBeaconInfo(BeaconBean beacon, String index) {
        boolean var3 = false;
        if (null != index && !"".equals(index)) {
            for (int var5 = 0; var5 < beacons.size(); ++var5) {
                BeaconBean var4 = (BeaconBean) beacons.get(var5);
                if (index.equals(var4.getIndex())) {
                    if ("URL".equals(beacon.getBeaconType())) {
                        var3 = true;
                        var4.setBeaconType(beacon.getBeaconType());
                        var4.setUrl(beacon.getUrl());
                        var4.setPower(beacon.getPower());
                        var4.setEnable(beacon.isEnable());
                    } else if ("UID".equals(beacon.getBeaconType())) {
                        var3 = true;
                        var4.setBeaconType(beacon.getBeaconType());
                        var4.setNameSpace(beacon.getNameSpace());
                        var4.setInstance(beacon.getInstance());
                        var4.setReserved(beacon.getReserved());
                        var4.setPower(beacon.getPower());
                        var4.setEnable(beacon.isEnable());
                    } else if ("iBeacon".equals(beacon.getBeaconType())) {
                        var3 = true;
                        var4.setBeaconType(beacon.getBeaconType());
                        var4.setUuid(beacon.getUuid());
                        var4.setMajor(beacon.getMajor());
                        var4.setMinor(beacon.getMinor());
                        var4.setPower(beacon.getPower());
                        var4.setEnable(beacon.isEnable());
                    } else if ("AltBeacon".equals(beacon.getBeaconType())) {
                        var3 = true;
                        var4.setBeaconType(beacon.getBeaconType());
                        var4.setId1(beacon.getId1());
                        var4.setId2(beacon.getId2());
                        var4.setId3(beacon.getId3());
                        var4.setPower(beacon.getPower());
                        var4.setEnable(beacon.isEnable());
                        var4.setManufacturerReserved(beacon.getManufacturerReserved());
                        var4.setManufacturerId(beacon.getManufacturerId());
                    } else {
                        var3 = false;
                    }
                    break;
                }
            }

            return var3;
        } else {
            return false;
        }
    }

    public void saveIp(String ip) {
        if (commandBean != null && beacons != null) {
            FscBleCentralApiImp.EN_AUTO_INQUERY = false;
            FscBleCentralApiImp.EN_AUTO_VERIFY = false;
            commandBean.setCommand("AT+OTA=0," + ip + "," + 8082);
            Log.e("FscBeacon", "saveIp: AT+OTA=0," + ip + "," + 8082);
            f.sendATCommand(commandBean.getCommand());
        }
    }

    public void saveBeaconInfo() {
        if (commandBean != null && beacons != null) {
            FscBleCentralApiImp.EN_AUTO_INQUERY = false;
            FscBleCentralApiImp.EN_AUTO_VERIFY = false;

            for (int var1 = 0; var1 < beacons.size(); ++var1) {
                try {
                    this.b = FeasycomUtil.a(var1, beacons);
                } catch (Exception var4) {
                    this.b = "00";
                }

                try {
                    this.a = FeasycomUtil.b(var1, beacons);
                } catch (Exception var3) {
                    this.a = "0";
                }

                commandBean.setCommand("AT+BADVDATA=" + var1 + "," + this.b.toUpperCase() + "," + this.a);
            }

            f.sendATCommand(commandBean.getCommand());
        }
    }

    public void setFeasyBeacon(FeasyBeacon feasyBeacon) {
        FscBeaconApiImp.feasyBeacon = feasyBeacon;
    }

    public void setRemoteAp(String ssid, String password) {
        if (commandBean != null && f.isConnected()) {
            if (ssid != null && ssid.length() != 0) {
                if (password != null && password.length() > 0) {
                    commandBean.setCommand("AT+RAP=" + ssid + "," + password);
                } else {
                    commandBean.setCommand("AT+RAP=" + ssid);
                }

                f.sendATCommand(commandBean.getCommand());
            }
        }
    }

    public void getMac() {
        FscBleCentralApiImp.EN_AUTO_INQUERY = false;
        FscBleCentralApiImp.EN_AUTO_VERIFY = false;
        HashSet var1 = new HashSet();
        var1.add("AT+MAC");
        f.sendATCommand(var1);
    }
}
