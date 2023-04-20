package com.mapping.db.log;

import android.os.Build;

public class Log {
    static String TAG = "mappingDB";

    public static void e(String e) {
        android.util.Log.e(TAG, e);
    }

    public static void i(String e) {
        android.util.Log.i(TAG, e);
    }

    public static void w(String e) {
        android.util.Log.w(TAG, e);
    }

    public static void d(String e) {
        android.util.Log.d(TAG, e);
    }

    public static void local(String e) {
        android.util.Log.e(TAG + "-local", e);
    }

    public static void exceptionLog(String e) {
        android.util.Log.e(TAG, e);
    }

    public static void exceptionLog(Exception e) {
        android.util.Log.e(TAG, e.toString());
    }

}
