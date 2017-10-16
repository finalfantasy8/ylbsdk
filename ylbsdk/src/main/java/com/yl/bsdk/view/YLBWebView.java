package com.yl.bsdk.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yl.bsdk.constants.YLBSdkConstants;
import com.yl.bsdk.R;
import com.yl.bsdk.YLBSdkManager;
import com.yl.bsdk.jsbridge.BridgeHandler;
import com.yl.bsdk.jsbridge.BridgeUtil;
import com.yl.bsdk.jsbridge.CallBackFunction;
import com.yl.bsdk.jsbridge.DefaultHandler;
import com.yl.bsdk.jsbridge.WebViewJavascriptBridge;
import com.yl.bsdk.models.AppToH5Msg;
import com.yl.bsdk.models.DialogModel;
import com.yl.bsdk.models.SmkOrder;
import com.yl.bsdk.models.H5ToYLBSdkMsg;
import com.yl.bsdk.models.ToastModel;
import com.yl.bsdk.models.YLBSdkToAppMsg;
import com.yl.bsdk.utils.YLCommonUtils;
import com.yl.bsdk.utils.YLDialogUtils;
import com.yl.bsdk.utils.YLJumpUtils;
import com.yl.bsdk.utils.YLLogUtils;
import com.yl.bsdk.utils.YLToastUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * x5 内核的webview
 */
public class YLBWebView extends WebView implements WebViewJavascriptBridge {

    private boolean isSmallWebViewDisplayed = false;
    private boolean isClampedY = false; // 是否下拉状态
    ProgressBar progressBar;
    private Context mContext;
    public ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    public ValueCallback<Uri[]> mUploadCallbackAboveL;
    public final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调
    public Uri imageUri;
    private String mOrderString;
    private String jsCallback = "";

    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<com.yl.bsdk.jsbridge.Message> startupMessage = new ArrayList<com.yl.bsdk.jsbridge.Message>();

    public List<com.yl.bsdk.jsbridge.Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<com.yl.bsdk.jsbridge.Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    public YLBWebView(Context arg0) {
        super(arg0);
        mContext = arg0;
        setBackgroundColor(Color.parseColor("#f5f5f9"));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public YLBWebView(Context arg0, AttributeSet arg1, boolean useCache, String order) {
        super(arg0, arg1);
        mContext = arg0;
        progressBar = (ProgressBar) ((Activity) getContext()).findViewById(R.id.pb_process);
        mOrderString = order;
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setDefaultTextEncodingName("utf-8");//设置字符编码
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(false);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBlockNetworkImage(false);
        // 设置Application Caches缓存
        webSetting.setAppCacheEnabled(useCache);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // 开启database storage API 功能
        webSetting.setDatabaseEnabled(true);
        // 开启DOM storage API 功能
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 加载url的缓存模式
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.getView().setClickable(true);
        this.getView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // 加载后，滑动页面到底/顶后仍能继续滑动，页面加载显示异常
//        this.setWebViewClientExtension(new X5WebViewEventHandler(this));// 配置X5webview的事件处理
        // WebClient settings
        this.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                BridgeUtil.webViewLoadLocalJs(webView, "YLJsBridge.js");
//                getOrderInfoToHtml();
                if (getStartupMessage() != null) {
                    for (com.yl.bsdk.jsbridge.Message m : getStartupMessage()) {
                        dispatchMessage(m);
                    }
                    setStartupMessage(null);
                }
            }

            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                YLLogUtils.e("webview", "url=" + url);

                try {
                    url = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (url.startsWith(BridgeUtil.YL_RETURN_DATA)) { // 如果是返回数据
                    handlerReturnData(url);
                    return true;
                } else if (url.startsWith(BridgeUtil.YL_OVERRIDE_SCHEMA)) {
                    flushMessageQueue();
                    return true;
                } else if (url != null && url.startsWith(YLBSdkConstants.SMKPAY_SCHEME_HEADER)) {//调起市民卡支付
                    String order = url.replace(YLBSdkConstants.SMKPAY_SCHEME_HEADER, "");
                    order = new String(Base64.decode(order, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg<SmkOrder> h5ToNative = gson.fromJson(order, H5ToYLBSdkMsg.class);
                    mOrderString = gson.toJson(h5ToNative.getParams());
                    jsCallback = h5ToNative.getCallback();
                    if (TextUtils.isEmpty(jsCallback)) jsCallback = "sendResultToWap";
                    if (YLCommonUtils.checkHasInstallApp(mContext, "com.smk")) {
                        YLJumpUtils.jumpToSmkPay((Activity) mContext, mOrderString);
                    } else {
                        view.loadUrl(YLBSdkManager.isDebugMode() ? YLBSdkConstants.SMKPAY_URL_DEBUG : YLBSdkConstants.SMKPAY_URL_RELEASE);
                    }
                } else if (url != null && url.startsWith("smkapp://getcardmsg")) { // 获取预付卡充值信息
                    sendCardNumberToHtml(((YLBWebViewActivity) mContext).getsInitParams());
                } else if (url != null && url.startsWith(YLBSdkConstants.YLBSDK_PAY_SCHEME_HEADER)){ //调起app内部支付
                    String params = url.replace(YLBSdkConstants.YLBSDK_PAY_SCHEME_HEADER, "");
                    params = new String(Base64.decode(params, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(params, H5ToYLBSdkMsg.class);
                    jsCallback = h5ToYLBSdkMsg.getCallback();
                    String h5param = (h5ToYLBSdkMsg.getParams() instanceof String) ? (String) h5ToYLBSdkMsg.getParams() : gson.toJson(h5ToYLBSdkMsg.getParams());
                    YLBSdkToAppMsg param = new YLBSdkToAppMsg(h5ToYLBSdkMsg.getAppid(), h5ToYLBSdkMsg.getAppname(), h5param);
                    if (!YLJumpUtils.jumpToAppPayForResult((YLBWebViewActivity) mContext, gson.toJson(param))) {
                        AppToH5Msg appToH5Msg = new AppToH5Msg("00", "app不支持支付");
                        sendResultToHtml(gson.toJson(appToH5Msg));
                    }
                } else if (url != null && url.startsWith(YLBSdkConstants.YLBSDK_LOGIN_SCHEME_HEADER)){ //调起app内部登录
                    String params = url.replace(YLBSdkConstants.YLBSDK_LOGIN_SCHEME_HEADER, "");
                    params = new String(Base64.decode(params, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(params, H5ToYLBSdkMsg.class);
                    jsCallback = h5ToYLBSdkMsg.getCallback();
                    String h5param = (h5ToYLBSdkMsg.getParams() instanceof String) ? (String) h5ToYLBSdkMsg.getParams() : gson.toJson(h5ToYLBSdkMsg.getParams());
                    YLBSdkToAppMsg param = new YLBSdkToAppMsg(h5ToYLBSdkMsg.getAppid(), h5ToYLBSdkMsg.getAppname(), h5param);
                    if (!YLJumpUtils.jumpToAppLoginForResult((YLBWebViewActivity) mContext, gson.toJson(param))) {
                        AppToH5Msg appToH5Msg = new AppToH5Msg("00", "app不支持登录操作");
                        sendResultToHtml(gson.toJson(appToH5Msg));
                    }
                } else if (url != null && url.startsWith(YLBSdkConstants.YLBSDK_TOAST_SCHEME_HEADER)){ //使用toast
                    String param = url.replace(YLBSdkConstants.YLBSDK_TOAST_SCHEME_HEADER, "");
                    param = new String(Base64.decode(param, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(param, H5ToYLBSdkMsg.class);
                    jsCallback = h5ToYLBSdkMsg.getCallback();
                    ToastModel model = gson.fromJson(gson.toJson(h5ToYLBSdkMsg.getParams()), ToastModel.class);
                    YLToastUtils.getInstance(mContext).showMsg(model.getMsg(), model.getType(), model.getDuration());
                    if (model.getDuration() == 0) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendResultToHtml("toast dismiss");
                            }
                        }, 2000);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendResultToHtml("toast dismiss");
                            }
                        }, model.getDuration());
                    }
                } else if (url != null && url.startsWith(YLBSdkConstants.YLBSDK_DIALOG_SCHEME_HEADER)){ //使用dialog
                    String param = url.replace(YLBSdkConstants.YLBSDK_DIALOG_SCHEME_HEADER, "");
                    param = new String(Base64.decode(param, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(param, H5ToYLBSdkMsg.class);
                    jsCallback = h5ToYLBSdkMsg.getCallback();
                    DialogModel model = gson.fromJson(gson.toJson(h5ToYLBSdkMsg.getParams()), DialogModel.class);
                    if (model.getType() == 1) {
                        YLDialogUtils.showDialogWithOneButton(mContext, model.getTitle(), model.getMsg(), model.getConfirm(), dialogListener);
                    } else if (model.getType() == 2) {
                        YLDialogUtils.showDialogWithTwoButton(mContext, model.getTitle(), model.getMsg(), model.getConfirm(), model.getCancel(), dialogListener);
                    }
                } else if (url != null && url.contains(YLBSdkConstants.YLBSDK_FINISH_SCHEME_HEADER)) {
                    ((YLBWebViewActivity) mContext).finish();
                } else if (url != null && url.contains(YLBSdkConstants.YLBSDK_GET_PARAM_SCHEME_HEADER)) {
                    String param = url.replace(YLBSdkConstants.YLBSDK_GET_PARAM_SCHEME_HEADER, "");
                    param = new String(Base64.decode(param, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(param, H5ToYLBSdkMsg.class);
                    jsCallback = h5ToYLBSdkMsg.getCallback();
                    sendResultToHtml(((YLBWebViewActivity) mContext).getsInitParams());
                } else if (url != null && url.contains(YLBSdkConstants.YLBSDK_SET_PARAM_SCHEME_HEADER)) {
                    String param = url.replace(YLBSdkConstants.YLBSDK_SET_PARAM_SCHEME_HEADER, "");
                    param = new String(Base64.decode(param, Base64.DEFAULT));
                    Gson gson = new Gson();
                    H5ToYLBSdkMsg h5ToYLBSdkMsg = gson.fromJson(param, H5ToYLBSdkMsg.class);
                    String h5param = (h5ToYLBSdkMsg.getParams() instanceof String) ? (String) h5ToYLBSdkMsg.getParams() : gson.toJson(h5ToYLBSdkMsg.getParams());
                    ((YLBWebViewActivity) mContext).setsInitParams(h5param);
                } else if (url != null && !url.startsWith("http")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedHttpAuthRequest(
                    WebView webview,
                    com.tencent.smtt.export.external.interfaces.HttpAuthHandler httpAuthHandlerhost,
                    String host, String realm) {
                boolean flag = httpAuthHandlerhost
                        .useHttpAuthUsernamePassword();
                YLLogUtils.i("X5WebView", "useHttpAuthUsernamePassword is" + flag);
                YLLogUtils.i("X5WebView", "HttpAuth host is" + host);
                YLLogUtils.i("X5WebView", "HttpAuth realm is" + realm);

            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                // super.onReceivedSslError(webView, sslErrorHandler, sslError);
                // Ignore SSL certificate errors
                sslErrorHandler.proceed(); // 接受证书
                // handler.cancel(); // 默认的处理方式，WebView变成空白页
                // handleMessage(Message msg); // 其他处理
            }

            @Override
            public void onDetectedBlankScreen(String arg0, int arg1) {
                super.onDetectedBlankScreen(arg0, arg1);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView arg0,
                                                              String arg1) {
                return super.shouldInterceptRequest(arg0, arg1);
            }

        });

        // webchromeclient settings
        this.setWebChromeClient(new WebChromeClient() {
            View myVideoView;
            View myNormalView;
            CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         CustomViewCallback customViewCallback) {
                FrameLayout normalView = (FrameLayout) ((Activity) getContext())
                        .findViewById(R.id.wv_x5);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public void onProgressChanged(WebView webview, int progress) {
                if (progressBar != null) {
                    setProgress(progress);
                } else {
                    super.onProgressChanged(webview, progress);
                }
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadFile,
                                        String acceptType, String captureType) {
                mUploadMessage = uploadFile;
                take();
            }

            @Override
            public void onShowCustomView(View arg0, int arg1,
                                         CustomViewCallback arg2) {
                CustomViewCallback callback = new CustomViewCallback() {

                    @Override
                    public void onCustomViewHidden() {
                        YLLogUtils.i("X5WebView", "video view hidden");
                    }
                };
                super.onShowCustomView(arg0, arg1, arg2);
            }

            /**
             * webview 的窗口转移
             */
            @Override
            public boolean onCreateWindow(WebView arg0, boolean arg1,
                                          boolean arg2, Message msg) {
                if (isSmallWebViewDisplayed == true) {

                    WebViewTransport webViewTransport = (WebViewTransport) msg.obj;
                    WebView webView = new WebView(YLBWebView.this.getContext()) {

                        protected void onDraw(Canvas canvas) {
                            super.onDraw(canvas);
                            Paint paint = new Paint();
                            paint.setColor(Color.GREEN);
                            paint.setTextSize(15);
                            canvas.drawText("新建窗口", 10, 10, paint);
                        }

                    };
                    webView.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView arg0,
                                                                String arg1) {
                            arg0.loadUrl(arg1);
                            return true;
                        }

                        ;
                    });
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(400, 600);
                    lp.gravity = Gravity.CENTER_HORIZONTAL
                            | Gravity.CENTER_VERTICAL;
                    YLBWebView.this.addView(webView, lp);

                    webViewTransport.setWebView(webView);
                    msg.sendToTarget();
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView webView, String url, String message,
                                     JsResult jsResult) {
                jsResult.confirm();
//                YLLogUtils.i("X5WebView", "setX5webview = null");
                return super.onJsAlert(webView, url, message, jsResult);

            }

            /**
             * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
             */
            @Override
            public boolean onJsPrompt(WebView webView, String s, String message, String s2, JsPromptResult jsPromptResult) {
                // 在这里可以判定js传过来的数据，用于调起android native 方法
                Uri uri = Uri.parse(message);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (mOrderString != null && uri.getScheme().equals("js")) {
                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {
                        // 执行JS所需要调用的逻辑
                        //参数result:代表消息框的返回值(输入值)
                        byte b[];
                        try {
                            b = mOrderString.getBytes("UTF-8");
                            jsPromptResult.confirm( Base64.encodeToString(b, Base64.DEFAULT) );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    return true;
                }
                return super.onJsPrompt(webView, s, message, s2, jsPromptResult);
            }

            @Override
            public void onReceivedTitle(WebView arg0, String title) {
                super.onReceivedTitle(arg0, title);
                if (!TextUtils.isEmpty(title)) {
                    if (((YLBWebViewActivity) mContext).getmTvTitle() != null) {
                        ((YLBWebViewActivity) mContext).getmTvTitle().setText(title);
                    }
                }
            }
        });
    }

    public void getOrderInfoToHtml() {
        byte b[] = mOrderString.getBytes();
        loadUrl("javascript:sendOrderToWap (" + "\'" + Base64.encodeToString(b, Base64.DEFAULT) + "\'" + ")");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }
        return;
    }

    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = ((Activity) mContext).getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        ((Activity) mContext).startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setSmallWebViewEnabled(boolean enabled) {
        isSmallWebViewDisplayed = enabled;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // YLLogUtils.i("X5WebView","webview scroll y is" + this.getView().getScrollY());
        // YLLogUtils.i("X5WebView","real webview scroll y is" + this.getScrollY());
        // YLLogUtils.i("X5WebView", "webview webscroll y is" + this.getWebScrollY());
        super.onScrollChanged(l, t, oldl, oldt);
    }

    // TBS: Do not use @Override to avoid false calls
    public boolean tbs_dispatchTouchEvent(MotionEvent ev, View view) {
        boolean r = super.super_dispatchTouchEvent(ev);
        YLLogUtils.d("X5WebView", "dispatchTouchEvent " + ev.getAction() + " "
                + r);
        return r;
    }

    // TBS: Do not use @Override to avoid false calls
    public boolean tbs_onInterceptTouchEvent(MotionEvent ev, View view) {
        boolean r = super.super_onInterceptTouchEvent(ev);
        return r;
    }

    protected void tbs_onScrollChanged(int l, int t, int oldl, int oldt,
                                       View view) {
        YLLogUtils.i("X5WebView", "tbs_onScrollChanged ");
        super_onScrollChanged(l, t, oldl, oldt);
    }

    protected void tbs_onOverScrolled(int scrollX, int scrollY,
                                      boolean clampedX, boolean clampedY, View view) {
        YLLogUtils.i("X5WebView", "scrollY is " + scrollY);

        if (isClampedY && !clampedY) {
            this.reload();
        }
        if (clampedY) {
            this.isClampedY = true;

        } else {
            this.isClampedY = false;
        }
        super_onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    protected void tbs_computeScroll(View view) {
        super_computeScroll();
    }

    protected boolean tbs_overScrollBy(int deltaX, int deltaY, int scrollX,
                                       int scrollY, int scrollRangeX, int scrollRangeY,
                                       int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent,
                                       View view) {
        YLLogUtils.i("X5WebView", "tbs_overScrollBy deltaY is" + deltaY);
        if (this.isClampedY) {
            this.layout(this.getLeft(), this.getTop() + (-deltaY) / 2,
                    this.getRight(), this.getBottom() + (-deltaY) / 2);
        }
        return super_overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

    protected boolean tbs_onTouchEvent(MotionEvent event, View view) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            this.isClampedY = false;
            this.layout(this.getLeft(), 0, this.getRight(), this.getBottom());
        }
        return super_onTouchEvent(event);
    }

    private void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
            if (progress == 100) {
                handler.postDelayed(runnable, 50);
            } else {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void sendResultToHtml(String result) {
        if (TextUtils.isEmpty(jsCallback)) return;
        YLLogUtils.i("------>jscallback", jsCallback);
        byte b[] = result.getBytes();
        YLLogUtils.i("WebView", "web_result base 64:" + android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT));
        loadUrl("javascript:"+ jsCallback + "(\'" + android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT) + "\')");
    }

    public void sendCardNumberToHtml(String result) {
        byte b[] = result.getBytes();
        YLLogUtils.i("WebView", "web_result base 64:" + android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT));
        loadUrl("javascript:sendCardMsgToWap(\'" + android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT) + "\')");
    }

    private YLDialogUtils.DialogButtonClickListener dialogListener = new YLDialogUtils.DialogButtonClickListener() {
        @Override
        public void onButtonClick(String var) {
            sendResultToHtml(var);
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };


    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        com.yl.bsdk.jsbridge.Message m = new com.yl.bsdk.jsbridge.Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(com.yl.bsdk.jsbridge.Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(com.yl.bsdk.jsbridge.Message m) {
        String messageJson = m.toJson();
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    List<com.yl.bsdk.jsbridge.Message> list = null;
                    try {
                        list = com.yl.bsdk.jsbridge.Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        com.yl.bsdk.jsbridge.Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否有 responseId，有就直接执行JAVA 回调方法
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // 是否有 callbackId，handler处理后数据后回调Js 回调方法
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        com.yl.bsdk.jsbridge.Message responseMsg = new com.yl.bsdk.jsbridge.Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null){
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

}
