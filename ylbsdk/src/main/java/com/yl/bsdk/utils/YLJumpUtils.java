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
     * 跳转市民卡支付
     * @param activity Activity
     * @param order SmkOrder
     * @return 是否原生app支付
     */
    public static boolean jumpToSmkPay(Activity activity, SmkOrder order) {
        Gson gson = new Gson();
        String orderString = gson.toJson(order);
        return jumpToSmkPay(activity, orderString);
    }

    /**
     * 跳转市民卡支付
     * @param activity Activity
     * @param orderString 订单字符串
     * @return 是否原生app支付
     */
    public static boolean jumpToSmkPay(Activity activity, String orderString) {
        if (!YLCommonUtils.checkHasInstallApp(activity, "com.smk")) { // 没有安装杭州市民卡，跳转html
            Intent intentDetail = new Intent(activity, YLBWebViewActivity.class);
            intentDetail.putExtra(YLBSdkConstants.EXTRA_H5_URL, YLBSdkManager.isDebugMode() ? YLBSdkConstants.SMKPAY_URL_DEBUG : YLBSdkConstants.SMKPAY_URL_RELEASE);
            intentDetail.putExtra(YLBSdkConstants.EXTRA_ORDER_STRING, orderString);
            activity.startActivity(intentDetail);
            return false;
        } else { // 跳转市民卡app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("smkpay/");
            intent.putExtra("packageName", activity.getApplicationContext().getPackageName());
            intent.putExtra("order", orderString);
            activity.startActivityForResult(intent, YLBSdkConstants.SMKPAY_REQUEST_CODE);
            return true;
        }
    }

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

    // 市民卡预付卡充值
    public static void h5SkipCardRechargeWithMsg(Context context, String url, String idNo, String channel, String cardNum) {
        try {
            JSONObject object = new JSONObject();
            object.put("idNo", idNo);
            object.put("channel", channel);
            object.put("cardNum", cardNum);
            h5SkipCardRecharge(context, url, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 市民卡预付卡充值
    public static void h5SkipCardRecharge(Context context, String url, String initParams) {
        Intent intent = new Intent(context, YLBWebViewActivity.class);
        intent.putExtra(YLBSdkConstants.EXTRA_H5_URL, url);
        intent.putExtra(YLBSdkConstants.EXTRA_INIT_PARAMS, initParams);
        if (!(context instanceof Activity)) intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
