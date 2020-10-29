package com.magna.moldingtools.Bean;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CommandBean {
    public static final String COMMAND_AT_TEST = "AT";
    public static final String COMMAND_HEADER = "AT+";
    public static final String COMMAND_BEGIN = "Opened";
    public static final String COMMAND_MODEL = "MODEL";
    public static final String COMMAND_VERSION = "VERSION";
    public static final String COMMAND_NAME = "NAME";
    public static final String COMMAND_TNAME = "TNAME";
    public static final String COMMAND_LENAME = "LENAME";
    public static final String COMMAND_ADVIN = "ADVIN";
    public static final String COMMAND_GSCFG = "GSCFG";
    public static final String COMMAND_KEYCFG = "KEYCFG";
    public static final String COMMAND_LED = "LED";
    public static final String COMMAND_BUZ = "BUZ";
    public static final String COMMAND_OTA = "OTA";
    public static final String COMMAND_MAC = "MAC";
    public static final String COMMAND_BWMODE = "BWMODE";
    public static final String COMMAND_BWMODE_OPEN = "0";
    public static final String COMMAND_BWMODE_CLOSE = "1";
    public static final String COMMAND_PIN = "PIN";
    public static final String COMMAND_TX_POWER = "TXPOWER";
    public static final String COMMAND_EXTEND = "EXTEND";
    public static final String COMMAND_BADVDATA = "BADVDATA";
    public static final String COMMAND_END = "END";
    public static final String COMMAND_TOPIC = "PUBTPC";
    public static final String COMMAND_RAP = "RAP";
    public static final String COMMAND_BROKER = "BROKER";
    public static final String COMMAND_MQTT_CLIENT = "MQCLI";
    public static final String COMMAND_QOS = "QOS";
    public static final String DEFALUT_DEVICE_NAME = "Feasycom";
    public static final String DEFALUT_PIN = "0000";
    public static final String DEFALUT_ADVIN = "152";
    public static final String DEFALUT_TXPOWER = "7";
    public static final String COMMAND_FINISH = "2";
    public static final String COMMAND_SUCCESSFUL = "1";
    public static final String COMMAND_FAILED = "0";
    public static final String COMMAND_NO_NEED = "3";
    public static final String COMMAND_TIME_OUT = "4";
    public static final int PASSWORD_CHECK = 0;
    public static final int PASSWORD_SUCCESSFULE = 1;
    public static final int PASSWORD_FAILED = 2;
    public static final int PASSWORD_TIME_OUT = 3;
    private Set<String> command = new HashSet();

    public CommandBean() {
    }

    public Set<String> getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        boolean var3 = false;
        String var4 = "";
        int var7;
        if (command.contains("BADVDATA") && command.contains("=")) {
            var7 = command.indexOf("=") + 1;
            var4 = command.substring(0, var7);
        } else if (command.contains("=")) {
            var7 = command.indexOf("=");
            var4 = command.substring(0, var7);
        }

        Iterator var5 = this.command.iterator();

        while (var5.hasNext()) {
            String var6 = (String) var5.next();
            if (var6.contains("=")) {
                int var2 = var6.indexOf("=");
                if (var6.contains("BADVDATA")) {
                    if (var6.substring(0, var2 + 2).equals(var4)) {
                        var5.remove();
                    }
                } else if (var6.substring(0, var2).equals(var4)) {
                    var5.remove();
                }
            } else if (var6 != null && !"".equals(var6) && var6.equals(command)) {
                var5.remove();
            }
        }

        this.command.add(command);
    }
}
