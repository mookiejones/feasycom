package com.magna.beaconlibrary.controller;


import com.magna.beaconlibrary.bean.DfuFileInfo;
import com.magna.beaconlibrary.bean.QuickConnectionParam;

import java.util.Set;

public interface FscSppApi {
    int PACKGE_SEND_FINISH = 100;
    int FIFO_SEND_FINISH = 101;
    int OTA_STATU_BEGIN = 110;
    int OTA_STATU_PROCESSING = 121;
    int OTA_STATU_FINISH = 10086;
    int OTA_STATU_FAILED = 120;

    boolean isBtEnabled();

    boolean initialize();

    void setCallbacks(FscSppCallbacks var1);

    boolean connect(String var1);

    boolean connect(String var1, String var2);

    void disconnect();

    void startScan(int var1);

    void stopScan();

    boolean isConnected();

    boolean send(byte[] var1);

    boolean setSendInterval(int var1);

    boolean cancleSendInterval();

    void stopSend();

    DfuFileInfo checkDfuFile(byte[] var1);

    boolean startOTA(byte[] var1, boolean var2);

    void sendATCommand(Set<String> var1);

    boolean smartLink(QuickConnectionParam var1);
}