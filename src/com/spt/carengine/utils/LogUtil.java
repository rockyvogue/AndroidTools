/*
 * 文 件 名:  LogUtil.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月12日
 */

package com.spt.carengine.utils;

import android.util.Log;

/**
 * <功能描述>
 * 
 * @author Heaven
 */
public class LogUtil {
    private static final String TAG = "car_engine";
    private static final boolean LOG_ON_OFF = true;

    public static void i(String tag, String msg) {
        if (LOG_ON_OFF) {
            Log.i(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LOG_ON_OFF) {
            Log.v(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_ON_OFF) {
            Log.e(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_ON_OFF) {
            Log.w(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_ON_OFF) {
            Log.d(TAG, "[" + tag + "] " + msg);
        }
    }
}
