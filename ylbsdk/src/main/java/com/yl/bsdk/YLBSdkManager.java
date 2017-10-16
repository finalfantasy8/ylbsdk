package com.yl.bsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.smtt.sdk.QbSdk;
import com.yl.bsdk.constants.YLBSdkConstants;
import com.yl.bsdk.utils.YLLogUtils;
import com.yl.bsdk.view.YLBWebViewActivity;

public class YLBSdkManager {

    private static YLBSdkManager ylbSdkManager;

    private static boolean isDebugMode = false;

    private String payActivity = "";

    private String loginActivity = "";


    private YLBSdkManager () {

    }

    public static void init(Context context) {
        ylbSdkManager = new YLBSdkManager();
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                YLLogUtils.d("QbSdk", " onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                YLLogUtils.d("QbSdk", " onCoreInitFinished");
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context.getApplicationContext(), cb);
    }

    public static YLBSdkManager getYlbSdkManager() {
        if (ylbSdkManager == null) {
            synchronized (YLBSdkManager.class) {
                if (ylbSdkManager == null) {
                    ylbSdkManager = new YLBSdkManager();
                }
            }
        }
        return ylbSdkManager;
    }

    /**
     * 跳转h5页面
     * @param context Context
     * @param url h5地址
     */
    public static void jumpToH5App(Context context, String url) {
        Intent intent = new Intent(context, YLBWebViewActivity.class);
        intent.putExtra(YLBSdkConstants.EXTRA_H5_URL, url);
        if (!(context instanceof Activity)) intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转h5页面
     * @param context Context
     * @param url h5地址
     * @param initParams h5所需参数
     */
    public static void jumpToH5AppWithInitParams(Context context, String url, String initParams) {
        Intent intent = new Intent(context, YLBWebViewActivity.class);
        intent.putExtra(YLBSdkConstants.EXTRA_H5_URL, url);
        intent.putExtra(YLBSdkConstants.EXTRA_INIT_PARAMS, initParams);
        if (!(context instanceof Activity)) intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }

    public static void setDebugMode(boolean debugEnabled) {
        isDebugMode = debugEnabled;
    }

    public String getPayActivity() {
        return payActivity;
    }

    public String getLoginActivity() {
        return loginActivity;
    }

    public void setPayActivity(String payActivity) {
        this.payActivity = payActivity;
    }

    public void setLoginActivity(String loginActivity) {
        this.loginActivity = loginActivity;
    }
}
