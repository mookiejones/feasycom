package com.magna.beaconlibrary.util;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.magna.beaconlibrary.bean.DfuFileInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class FileUtil {
    static final String[] a = new String[]{"BT401", "BT405", "BT426N", "BT501", "BT502", "BT522", "BT616", "BT625", "BT626", "BT803", "BT813D", "BT816S", "BT821", "BT822", "BT826", "BT826N", "BT836", "BT836N", "BT906", "BT909", "BP102", "BT816S3", "BT926", "BT901", "BP109", "BP103", "BP104", "BP201", "BP106", "BP101", "BP671", "BT826H", "BT826NH", "BT826E", "BT826EH"};
    static final String[] b = new String[]{"BT901", "BT906", "BT909", "BT826", "BT926"};
    private static final String c = "0123456789ABCDEF";

    public FileUtil() {
    }

    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context != null && fileUri != null) {
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, fileUri)) {
                String var2;
                String[] var3;
                String var4;
                if (isExternalStorageDocument(fileUri)) {
                    var2 = DocumentsContract.getDocumentId(fileUri);
                    var3 = var2.split(":");
                    var4 = var3[0];
                    if ("primary".equalsIgnoreCase(var4)) {
                        return Environment.getExternalStorageDirectory() + "/" + var3[1];
                    }
                } else {
                    if (isDownloadsDocument(fileUri)) {
                        var2 = DocumentsContract.getDocumentId(fileUri);
                        Uri var8 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(var2));
                        return getDataColumn(context, var8, (String) null, (String[]) null);
                    }

                    if (isMediaDocument(fileUri)) {
                        var2 = DocumentsContract.getDocumentId(fileUri);
                        var3 = var2.split(":");
                        var4 = var3[0];
                        Uri var5 = null;
                        if ("image".equals(var4)) {
                            var5 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(var4)) {
                            var5 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(var4)) {
                            var5 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        String var6 = "_id=?";
                        String[] var7 = new String[]{var3[1]};
                        return getDataColumn(context, var5, var6, var7);
                    }
                }
            } else {
                if ("content".equalsIgnoreCase(fileUri.getScheme())) {
                    if (isGooglePhotosUri(fileUri)) {
                        return fileUri.getLastPathSegment();
                    }

                    return getDataColumn(context, fileUri, (String) null, (String[]) null);
                }

                if ("file".equalsIgnoreCase(fileUri.getScheme())) {
                    return fileUri.getPath();
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static String getModelName(String fileName) {
        return fileName != null && !"".equals(fileName) && fileName.split("_").length == 8 ? fileName.split("_")[0] : null;
    }

    public static String getAppVersion(String fileName) {
        return fileName != null && !"".equals(fileName) && fileName.split("_").length == 8 ? fileName.split("_")[6] : null;
    }

    public static String getBootLoaderVersion(String fileName) {
        return fileName != null && !"".equals(fileName) && fileName.split("_").length == 8 ? fileName.split("_")[4] : null;
    }

    public static byte[] readFile(String filePath) throws IOException {
        if (filePath != null && filePath.length() >= 1) {
            String var1 = URLDecoder.decode(filePath, "UTF-8");
            var1 = var1.replace("file:", "");
            String var2 = "";
            File var3 = new File(var1, "");
            FileInputStream var4 = new FileInputStream(var3);

            try {
                byte[] var5 = new byte[(int) var3.length()];
                var4.read(var5);
                return var5;
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor var4 = null;
        String[] var5 = new String[]{"_data"};

        try {
            var4 = context.getContentResolver().query(uri, var5, selection, selectionArgs, (String) null);
            if (var4 != null && var4.moveToFirst()) {
                int var6 = var4.getColumnIndexOrThrow("_data");
                String var7 = var4.getString(var6);
                return var7;
            }
        } finally {
            if (var4 != null) {
                var4.close();
            }

        }

        return "";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String bytesToHex(byte[] buf, int length) {
        if (length < 1) {
            return "";
        } else {
            int var2 = 0;
            StringBuffer var3 = new StringBuffer();

            while (var2 < length) {
                var3.append(String.format("%02X ", buf[var2++]));
            }

            return var3.toString();
        }
    }

    public static byte[] hexToByte(String string) {
        if (string.length() <= 0) {
            return new byte[0];
        } else {
            String var1 = string.replace(" ", "");
            if (var1.length() % 2 == 1) {
                StringBuffer var2 = new StringBuffer(var1);
                var2.insert(var1.length() - 1, '0');
                var1 = var2.toString();
            }

            byte[] var5 = new byte[var1.length() / 2];
            String var3 = "";

            for (int var4 = 0; var4 < var1.length(); var4 += 2) {
                var3 = var1.substring(var4, var4 + 2);
                if (var4 == 0) {
                    var5[var4] = (byte) Integer.parseInt(var3, 16);
                } else {
                    var5[var4 / 2] = (byte) Integer.parseInt(var3, 16);
                }
            }

            return var5;
        }
    }

    public static String formattingOneIntToStrings(int a) {
        int var1 = a & 15;
        return "0123456789ABCDEF".substring(var1, var1 + 1);
    }

    public static int formattingOneHexToInt(String a) {
        return a != null && a.length() == 1 ? "0123456789ABCDEF".indexOf(a.toUpperCase()) : 0;
    }

    public static int formattingHexToInt(String a) {
        String var1 = a.substring(0, 1);
        String var2 = a.substring(1, 2);
        return formattingOneHexToInt(var1) * 16 + formattingOneHexToInt(var2);
    }

    public static int add(int a, int b) {
        int var2 = a & b;

        int var3;
        int var4;
        for (var3 = a ^ b; var2 != 0; var3 ^= var4) {
            var4 = var2 << 1;
            var2 = var3 & var4;
        }

        return var3;
    }

    public static int minus(int a, int b) {
        return add(a, add(~b, 1));
    }

    private static long a(byte var0) {
        long var1 = (long) var0;
        if (var1 < 0L) {
            var1 += 256L;
        }

        return var1;
    }

    public static int byteToInt_2(byte a) {
        return a & 255;
    }

    public static int byteToInt_2(byte a, byte b) {
        int var2 = b & 255;
        int var3 = a & 255;
        return var3 + (var2 << 8);
    }

    public static int byteToInt_2(byte a, byte b, byte c, byte d) {
        int var4 = d & 255;
        int var5 = c & 255;
        int var6 = b & 255;
        int var7 = a & 255;
        return var7 + (var6 << 8) + (var5 << 16) + (var4 << 24);
    }

    public static int stringToInt(String string) {
        String var1 = string.substring(0, 2);
        String var2 = string.substring(2, 4);
        int var3 = formattingHexToInt(var1);
        int var4 = formattingHexToInt(var2);
        byte var5 = (byte) var3;
        byte var6 = (byte) var4;
        int var7 = byteToInt_2(var5, var6);
        return var7;
    }

    public static int stringToInt1(String string) {
        string = string.replace(" ", "");
        String var1 = string.substring(0, 2);
        String var2 = string.substring(2, 4);
        int var3 = formattingHexToInt(var1);
        int var4 = formattingHexToInt(var2);
        byte var5 = (byte) var3;
        byte var6 = (byte) var4;
        int var7 = byteToInt_2(var6, var5);
        return var7;
    }

    public static short byteToShort_2(byte a, byte b) {
        return (short) add(a & 255, b << 8);
    }

    public static int[] byteToInt(byte[] content) {
        int[] var1 = new int[content.length / 4];
        int var2 = 0;

        for (int var3 = 0; var3 < content.length; var3 += 4) {
            var1[var2] = (int) (a(content[var3]) | a(content[var3 + 1]) << 8 | a(content[var3 + 2]) << 16 | a(content[var3 + 3]) << 24);
            ++var2;
        }

        return var1;
    }

    public static byte[] intToByte(int[] content) {
        byte[] var1 = new byte[content.length * 4];
        int var2 = 0;

        for (int var3 = 0; var3 < var1.length; var3 += 4) {
            var1[var3] = (byte) (content[var2] & 255);
            var1[var3 + 1] = (byte) (content[var2] >> 8 & 255);
            var1[var3 + 2] = (byte) (content[var2] >> 16 & 255);
            var1[var3 + 3] = (byte) (content[var2] >> 24 & 255);
            ++var2;
        }

        return var1;
    }

    public static long intToLong(int i) {
        long var1 = (long) i & 4294967295L;
        return var1;
    }

    @NonNull
    public static String getFileName(String pathandname) {
        int var1 = pathandname.lastIndexOf("/");
        int var2 = pathandname.lastIndexOf(".");
        return var1 != -1 && var2 != -1 ? pathandname.substring(var1 + 1, var2) : "";
    }

    public static long readFileSize(String filePath) throws IOException {
        if (filePath != null && filePath.length() >= 1) {
            String var1 = URLDecoder.decode(filePath, "UTF-8");
            var1 = var1.replace("file:", "");
            File var2 = new File(var1, "");
            return var2.length();
        } else {
            return 0L;
        }
    }

    public static byte[] readFileToByte(String filePath) throws IOException {
        if (filePath != null && filePath.length() >= 1) {
            String var1 = URLDecoder.decode(filePath, "UTF-8");
            var1 = var1.replace("file:", "");
            File var2 = new File(var1, "");
            FileInputStream var3 = new FileInputStream(var1);
            long var4 = var2.length();
            byte[] var6 = new byte[(int) var4];
            var3.read(var6);
            return var6;
        } else {
            return null;
        }
    }

    public static InputStream getFileStream(String filePath) throws Exception {
        if (filePath != null && filePath.length() >= 1) {
            String var1 = null;
            var1 = URLDecoder.decode(filePath, "UTF-8");
            var1 = var1.replace("file:", "");
            FileInputStream var2 = null;
            var2 = new FileInputStream(var1);
            return var2;
        } else {
            return null;
        }
    }

    public static String getFileByInputString(InputStream inputStream) {
        InputStreamReader var1 = null;
        var1 = new InputStreamReader(inputStream);
        BufferedReader var2 = new BufferedReader(var1);
        StringBuffer var3 = new StringBuffer("");

        String var4;
        try {
            while ((var4 = var2.readLine()) != null) {
                var3.append(var4);
                var3.append("\n");
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return new String(var3);
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static DfuFileInfo getDfuFileInformation(byte[] data) {
        return (new TeaCode()).getDfuFileInformation(data);
    }

    public static boolean isReconnect(String moduleInfo) {
        String[] var1 = b;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            if (var4.equals(moduleInfo)) {
                return false;
            }
        }

        return true;
    }

    public static String getModelName(int modleNumber) {
        return modleNumber > a.length ? "Unknown" : a[modleNumber - 1];
    }

    public static boolean needsReconnect(byte[] dfuData) {
        DfuFileInfo var1 = getDfuFileInformation(dfuData);
        String var2 = getModelName(var1.type_model);
        return isReconnect(var2);
    }
}
