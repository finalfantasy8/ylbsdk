package com.yl.bsdk.utils;


import android.util.Log;

import com.yl.bsdk.YLBSdkManager;

public class YLLogUtils {

    public static void d(String tag, String msg) {
        if (YLBSdkManager.isDebugMode()) Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (YLBSdkManager.isDebugMode()) Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (YLBSdkManager.isDebugMode()) Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (YLBSdkManager.isDebugMode()) Log.e(tag, msg);
    }
}
