package com.yl.bsdk.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.yl.bsdk.constants.YLBSdkConstants;
import com.yl.bsdk.R;
import com.yl.bsdk.jsbridge.BridgeHandler;
import com.yl.bsdk.jsbridge.CallBackFunction;
import com.yl.bsdk.jsbridge.DefaultHandler;
import com.yl.bsdk.jsbridge.ToastHandler;
import com.yl.bsdk.models.AppToH5Msg;
import com.yl.bsdk.models.Confirm;
import com.yl.bsdk.utils.YLDialogUtils;
import com.yl.bsdk.utils.YLJumpUtils;
import com.yl.bsdk.utils.YLLogUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class YLBWebViewActivity extends Activity implements View.OnClickListener {

    private YLBWebView mX5WebView;
    private TextView mTvTitle;
    private String sInitParams = "";
    private LinearLayout mLlWebview;

    Map<String, CallBackFunction> callbacks = new HashMap<String, CallBackFunction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ylb_webview);


        LinearLayout ll_main = (LinearLayout) findViewById(R.id.ll_main);

        mTvTitle = (TextView) findViewById(R.id.tv_title);

        // 返回
        ImageView ivTitleBack = (ImageView) findViewById(R.id.iv_title_back);
        ivTitleBack.setOnClickListener(this);
        // 关闭
        ImageView ivTitleClose = (ImageView) findViewById(R.id.iv_title_finish);
        ivTitleClose.setOnClickListener(this);

        Intent it = this.getIntent();
        if (it != null) {
            String sURL = it.getStringExtra(YLBSdkConstants.EXTRA_H5_URL);
            String sOrder = it.getStringExtra(YLBSdkConstants.EXTRA_ORDER_STRING);
            sInitParams = it.getStringExtra(YLBSdkConstants.EXTRA_INIT_PARAMS);
            mLlWebview = (LinearLayout) findViewById(R.id.ll_webview);

            mX5WebView = new YLBWebView(this, null, false, sOrder);

            mX5WebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            // 修改ua使得web端正确判断
            mX5WebView.getSettings().setUserAgentString("appChannel/YLBSDK, appVersion/1.0.0");

            mLlWebview.addView(mX5WebView);
            mX5WebView.setDefaultHandler(new DefaultHandler());
            mX5WebView.loadUrl(sURL);
            mX5WebView.registerHandler("toast", new ToastHandler(this));
            mX5WebView.registerHandler("confirm", new BridgeHandler() {
                @Override
                public void handler(String data, final CallBackFunction function) {
                    Gson gson = new Gson();
                    Confirm model = gson.fromJson(data, Confirm.class);
                    YLDialogUtils.DialogButtonClickListener dialogListener = new YLDialogUtils.DialogButtonClickListener() {
                        @Override
                        public void onButtonClick(String var) {
                            function.onCallBack(var);
                        }
                    };
                    YLDialogUtils.showDialogWithTwoButton(YLBWebViewActivity.this, model.getTitle(), model.getMessage(), model.getOkButton(), model.getCancelButton(), dialogListener);
                }
            });
            mX5WebView.registerHandler("confirm", new BridgeHandler() {
                @Override
                public void handler(String data, final CallBackFunction function) {
                    Gson gson = new Gson();
                    Confirm model = gson.fromJson(data, Confirm.class);
                    YLDialogUtils.DialogButtonClickListener dialogListener = new YLDialogUtils.DialogButtonClickListener() {
                        @Override
                        public void onButtonClick(String var) {
                            function.onCallBack(var);
                        }
                    };
                    YLDialogUtils.showDialogWithTwoButton(YLBWebViewActivity.this, model.getTitle(), model.getMessage(), model.getOkButton(), model.getCancelButton(), dialogListener);
                }
            });
            mX5WebView.registerHandler("login", new BridgeHandler() {
                @Override
                public void handler(String data, final CallBackFunction function) {
                    if (!YLJumpUtils.jumpToAppLoginForResult(YLBWebViewActivity.this, data)) {
                        AppToH5Msg appToH5Msg = new AppToH5Msg("00", "app不支持登录操作");
                        Gson gson = new Gson();
                        function.onCallBack(gson.toJson(appToH5Msg));
                    } else {
                        callbacks.put("login", function);
                    }
                }
            });
        }
    }

    public String getsInitParams() {
        return sInitParams;
    }

    public void setsInitParams(String sInitParams) {
        this.sInitParams = sInitParams;
    }

    public TextView getmTvTitle() {
        return mTvTitle;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_title_back) {
            if (mX5WebView != null && mX5WebView.canGoBack()) {
                mX5WebView.goBack();
            } else {
                finish();
            }
        } else if (v.getId() == R.id.iv_title_finish) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mX5WebView != null && mX5WebView.canGoBack()) {
                mX5WebView.goBack();
            } else {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YLBSdkConstants.SMKPAY_REQUEST_CODE) {
            if (data != null) {
                YLLogUtils.i("YLBWebViewActivity", "smkpay回来的内容:" + data.getDataString());
                try {
                    String result = data.getDataString().replace("content://", "");
                    JSONObject object = new JSONObject();
                    object.put("result", result);
                    if (mX5WebView != null) mX5WebView.sendResultToHtml(object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == YLBSdkConstants.YLBSDK_PAY_REQUEST_CODE) {
            if (data != null && data.hasExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT)) {
                if (mX5WebView != null) mX5WebView.sendResultToHtml(data.getStringExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT));
            } else if (resultCode == RESULT_CANCELED) {
                AppToH5Msg appToH5Msg = new AppToH5Msg("04", "交易取消");
                if (mX5WebView != null) mX5WebView.sendResultToHtml(new Gson().toJson(appToH5Msg));
            } else {
                AppToH5Msg appToH5Msg = new AppToH5Msg("03", "未知参数");
                if (mX5WebView != null) mX5WebView.sendResultToHtml(new Gson().toJson(appToH5Msg));
            }
        } else if (requestCode == YLBSdkConstants.YLBSDK_LOGIN_REQUEST_CODE) {
            if (data != null && data.hasExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT)) {
                if (callbacks.get("login") != null) callbacks.get("login").onCallBack(data.getStringExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT));
            } else if (resultCode == RESULT_CANCELED) {
                AppToH5Msg appToH5Msg = new AppToH5Msg("04", "登录取消");
                if (callbacks.get("login") != null) callbacks.get("login").onCallBack(new Gson().toJson(appToH5Msg));
            } else {
                AppToH5Msg appToH5Msg = new AppToH5Msg("03", "未知参数");
                if (callbacks.get("login") != null) callbacks.get("login").onCallBack(new Gson().toJson(appToH5Msg));
            }
        } else {
            if (mX5WebView != null) mX5WebView.onActivityResultAboveL(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (mX5WebView != null) {
            mLlWebview.removeView(mX5WebView);
            mX5WebView.removeAllViews();
            mX5WebView.destroy();
        }
        super.onDestroy();
        System.gc();
    }
}

