package com.yl.bsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yl.bsdk.constants.YLBSdkConstants;
import com.yl.bsdk.YLBSdkManager;
import com.yl.bsdk.models.SmkOrder;
import com.yl.bsdk.view.YLBWebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class YLJumpUtils {

    /**
     * 跳转app内支付
     * @param activity YLBWebViewActivity
     * @param param 预传参数
     * @return 返回app是否支持打开支付页面
     */
    public static boolean jumpToAppPayForResult(Activity activity, String param) {
        if (TextUtils.isEmpty(YLBSdkManager.getYlbSdkManager().getPayActivity())) {
            return false;
        }
        try {
            Class c = Class.forName(YLBSdkManager.getYlbSdkManager().getPayActivity());
            Intent intent = new Intent(activity, c);
            intent.putExtra(YLBSdkConstants.EXTRA_YLBSDK_MSG, param);
            activity.startActivityForResult(intent, YLBSdkConstants.YLBSDK_PAY_REQUEST_CODE);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转app内登录
     * @param activity YLBWebViewActivity
     * @param param 预传参数
     * @return 返回app是否支持打开登录页面
     */
    public static boolean jumpToAppLoginForResult(Activity activity, String param) {
        if (TextUtils.isEmpty(YLBSdkManager.getYlbSdkManager().getLoginActivity())) {
            return false;
        }
        try {
            Class c = Class.forName(YLBSdkManager.getYlbSdkManager().getLoginActivity());
            Intent intent = new Intent(activity, c);
            intent.putExtra(YLBSdkConstants.EXTRA_YLBSDK_MSG, param);
            activity.startActivityForResult(intent, YLBSdkConstants.YLBSDK_LOGIN_REQUEST_CODE);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
