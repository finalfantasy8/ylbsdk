package com.example.bdemo.ylbsdkdemo;

import android.app.Application;

import com.yl.bsdk.YLBSdkManager;


public class APPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        YLBSdkManager.setDebugMode(true); // 默认为false
        YLBSdkManager.init(this);
        YLBSdkManager.getYlbSdkManager().setLoginActivity("com.example.bdemo.ylbsdkdemo.LoginActivity");
        YLBSdkManager.getYlbSdkManager().setPayActivity("com.example.bdemo.ylbsdkdemo.PayActivity");
    }
}
