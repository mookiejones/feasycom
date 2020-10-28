package com.magna.beaconlibrary.util;


import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    public static final boolean COMPILE_LOG = true;
    private static final int d = 1;
    private static final int e = 2;
    private static final int f = 3;
    private static final int g = 4;
    private static final int h = 5;
    private static final int i = 5;
    private static boolean a = true;
    private static boolean b = false;
    private static boolean c = false;
    private static FileOutputStream j;
    private static OutputStreamWriter k;
    private static BufferedWriter l;

    public LogUtil() {
    }

    public static void setDebug(boolean debug) {
        a = debug;
    }

    public static void setWriteLog(boolean write) {
        b = write;
        if (!b) {
            if (l != null) {
                try {
                    l.close();
                    l = null;
                } catch (IOException var4) {
                    var4.printStackTrace();
                }
            }

            if (k != null) {
                try {
                    k.close();
                    k = null;
                } catch (IOException var3) {
                    var3.printStackTrace();
                }
            }

            if (j != null) {
                try {
                    j.close();
                    j = null;
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

    public static String currentTimeStamp() {
        return (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS")).format(new Date());
    }

    public static void initialize(Context context) {
        if (!c) {
            if (context != null) {
                if (b) {
                    String var1 = context.getApplicationContext().getExternalCacheDir().getAbsolutePath() + "/../log/";
                    String var2 = context.getApplicationContext().getPackageName();
                    File var3 = new File(var1);
                    boolean var4 = false;
                    if (!(var4 = var3.exists())) {
                        var4 = var3.mkdirs();
                    }

                    if (!var4) {
                        Log.i("Log", currentTimeStamp() + "  initialize false");
                        b = false;
                    } else {
                        String var5 = var1 + (new SimpleDateFormat("yyyy.MM.dd")).format(new Date()) + ".txt";
                        File var6 = new File(var5);
                        if (!(var4 = var6.exists())) {
                            try {
                                var4 = var6.createNewFile();
                            } catch (IOException var9) {
                                var9.printStackTrace();
                                return;
                            }
                        }

                        if (var4) {
                            try {
                                j = new FileOutputStream(var6, true);
                            } catch (FileNotFoundException var8) {
                                var8.printStackTrace();
                                return;
                            }

                            k = new OutputStreamWriter(j);
                            l = new BufferedWriter(k);
                        }

                        c = true;
                    }
                }
            }
        }
    }

    public static void i(String tag, String msg) {
        if (a) {
            if (b) {
                write(tag, msg, "i", (Throwable) null);
            }

            Log.i(tag, currentTimeStamp() + "  " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (a) {
            if (b) {
                write(tag, msg, "w", (Throwable) null);
            }

            Log.w(tag, currentTimeStamp() + "  " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (a) {
            if (b) {
                write(tag, msg, "e", (Throwable) null);
            }

        }
    }

    public static void d(String tag, String msg) {
        if (a) {
            if (b) {
                write(tag, msg, "d", (Throwable) null);
            }

            Log.d(tag, currentTimeStamp() + "  " + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (a) {
            if (b) {
                write(tag, msg, "v", (Throwable) null);
            }

            Log.v(tag, currentTimeStamp() + "  " + msg);
        }
    }

    public static void write(String tag, String msg, String level, Throwable throwable) {
        if (c) {
            if (l != null) {
                if (k != null) {
                    if (j != null) {
                        try {
                            l.write(level + "===" + tag + "===" + currentTimeStamp() + "===" + msg + "\r\n");
                            l.newLine();
                            l.flush();
                            k.flush();
                            j.flush();
                        } catch (IOException var5) {
                            var5.printStackTrace();
                        }

                    }
                }
            }
        }
    }
}
