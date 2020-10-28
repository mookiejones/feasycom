package com.magna.beaconlibrary.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public ToastUtil() {
    }

    public static void show(Context context, String message) {
        Toast.makeText(context.getApplicationContext(), message, 1).show();
    }
}
