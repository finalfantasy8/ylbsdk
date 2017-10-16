Android SDK 集成指南

使用提示

本文是YLB SDK标准的集成指南文档。用以指导SDk的使用方法，默认读者已经熟悉Android Studio的基本使用方法，以及具有一定的 Android 编程知识基础。

本篇指南匹配的YLB SDK版本为：v1.0.0 及以后版本。

产品功能说明

本YLB SDK方便开发者基于腾讯浏览服务x5内核来快捷的Android App增加H5Web浏览功能，并提供了一些通用的Js与Native交互的Api。

SDK集成步骤

1、在工程libs文件下引入SDK的.aar文件包、tbs_sdk.jar、gson.jar。（将demoapp app/libs/路径下的文件拷贝到工程libs文件下）


2、在 app module 的 gradle 中添加依赖：

android {
    ...

}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile(name:'ylb_sdk_v1.0.0_20170928_102147', ext:'aar') // 添加此行，name必须与集成.aar包名一致
    ...
}

repositories {   // 添加此项
    flatDir{
        dirs 'libs'
    }
}

3、在AndroidManifest中添加权限：

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="您应用的包名">

    <!-- Required -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />

    <application
        android:name="Your Application Name"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        ...


    </application>

</manifest>

必须权限说明

权限	                     用途
INTERNET	             允许应用可以访问网络。
READ_PHONE_STATE	     允许应用访问手机状态。
WRITE_EXTERNAL_STORAGE	 允许应用写入外部存储。
ACCESS_NETWORK_STATE	 允许应用获取网络信息状态，如当前的网络连接是否有效。
ACCESS_WIFI_STATE        允许应用获取当前WiFi接入的状态以及WLAN热点的信息。

4、添加代码

YLB SDK 提供的API接口，都主要集中在com.yl.bsdk.YLBSdkManager 类里。

基础API

init 初始化SDK
public static void init(Context context)

setDebugMode 设置调试模式
注：该接口需在init接口之前调用，避免出现部分日志没打印的情况。多进程情况下建议在自定义的Application中onCreate中调用。
public static void setDebugMode(boolean debugEnalbed)

调用示例代码

init 只需要在应用程序启动时调用一次该API即可。
以下代码定制一个本应用程序 Application 类。需要在AndoridManifest.xml里配置。请参考上面AndroidManifest.xml片断。
public class ExampleApplication extends Application {
@Override
    public void onCreate() {
        super.onCreate();

        YLBSdkManager.setDebugMode(true); // 默认为false
        YLBSdkManager.init(this);
        YLBSdkManager.getYlbSdkManager().setLoginActivity("com.example.ylbsdkdemo.LoginActivity"); // 支持H5授权登录，可在此设置
        YLBSdkManager.getYlbSdkManager().setPayActivity("com.example.ylbsdkdemo.PayActivity"); // 支持H5调用应用内支付，可在此设置
    }
}

API - jumpToH5App

调用了此 API 后，将跳转到H5Web页面。

接口定义
public static void jumpToH5App(Context context, String url);

参数说明
context
url H5应用链接地址

YLBSdkManager.jumpToH5App(MainActivity.this, "https://wap.example.com/");


API - jumpToH5AppWithInitParams

调用了此 API 后，将跳转到H5Web页面。

接口定义
public static void jumpToH5AppWithInitParams(Context context, String url, String initParams);

参数说明
context
url H5应用链接地址
initParams 传递给H5应用的参数, 与h5App协定

YLBSdkManager.jumpToH5AppWithInitParams(MainActivity.this, "https://wap.example.com/", "initParams");

支持授权登录

在SDK初始化时设置 调用接口 YLBSdkManager.getYlbSdkManager().setLoginActivity("com.example.ylbsdkdemo.LoginActivity");

1）调起登录页面时，SDK向登录Activity传入一个YLBSdkToAppMsg对象的JSONString，通过getIntent().getStringExtra(YLBSdkConstants.EXTRA_YLBSDK_MSG); 获取

public class YLBSdkToAppMsg {
    private String h5AppId;   // 调用的H5app编号，可用于来源统计
    private String h5AppName; // 调用的H5app名称，可用于来源统计
    private String params;    // 预留参数，应用可自定义，H5App按自定义规则传参
}

2）登录操作。。。

3）登录操作完成后，通过一下代码向SDK返回参数，由SDK通知H5App执行结果

Intent intent = new Intent();
intent.putExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT, params);   // params 为AppToH5Msg对象的JSONString
setResult(RESULT_OK, i);
finish();


public class AppToH5Msg {
    private String resultCode; // 返回状态  00：未处理， 01：成功， 02：失败， 03：处理中， 04：取消。 可与H5App协定
    private String resultMsg;  // 应用执行后的返回信息，与H5App协定，可以为JSONString
}

支持应用内支付

在SDK初始化时设置 调用接口 YLBSdkManager.getYlbSdkManager().setLoginActivity("com.example.ylbsdkdemo.PayActivity");

2）调起支付页面时，SDK向支付Activity传入一个YLBSdkToAppMsg对象的JSONString，通过getIntent().getStringExtra(YLBSdkConstants.EXTRA_YLBSDK_MSG); 获取

public class YLBSdkToAppMsg {
    private String h5AppId;   // 调用的H5app编号，可用于来源统计
    private String h5AppName; // 调用的H5app名称，可用于来源统计
    private String params;    // 订单信息，应用自定义，H5App按自定义规则传参
}

2）支付操作。。。

3）支付操作完成后，通过一下代码向SDK返回参数，由SDK通知H5App执行结果

Intent intent = new Intent();
intent.putExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT, params);   // params 为String类型，建议为AppToH5Msg对象的JSONString
setResult(RESULT_OK, i);
finish();

public class AppToH5Msg {
    private String resultCode; // 返回状态  00：未处理， 01：成功， 02：失败， 03：处理中， 04：取消。 可与H5App协定
    private String resultMsg;  // 应用执行后的返回信息，与H5App协定
}



------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------



H5App 接入规则

产品功能说明

YLB SDK提供一个H5容器，通过拦截UrlLoading，如果匹配到预先定义好的scheme header，就执行相应的操作。

SDK规则详解

1、现有以下scheme header：

scheme header           相应操作

"ylbsdk://pay/"         --应用内支付，需接入应用支持

"ylbsdk://login/"       --应用授权登录，需接入应用支持

"ylbsdk://toast/"       --原生toast

"ylbsdk://dialog/"      --原生dialog

"ylbsdk://finish/"      --关闭容器页面

"ylbsdk://getparam/"    --获取接入应用所传的初始化参数

"ylbsdk://setparam/"    --将数据缓存到SDK，可用"ylbsdk://getparam/"查询

2、scheme header后面拼接base64编码后的参数，示例代码如下：

var info = {   // info 字段固定
    appid: "10086",                // H5App编号，用于接入应用确认操作请求来源，以便埋点统计；请求登录和支付操作时must
    appname: "中国移动公众号",       // H5App名称
    params: params,                // 见第3项 params参数详解
    callback: "sendResultToWap",   // js回调函数名称，或者 "(" + 匿名行数 + ")" ，匿名行数每句语句结尾必须带";"
}

var param = BASE64.encoder(JSON.stringify(info));
var url = "ylbsdk://toast/" + param;
location.href = url;

3、params参数详解

1）调用pay或login：
按接入应用制定规则传参

2）调用dialog：
params: {
    title: obj.title,      // 标题
    msg: obj.msg,          // 信息
    confirm: obj.confirm,  // 确定按钮文案, 默认"确定"
    cancel: obj.cancel,    // 取消按钮文案, 默认"取消"
    type: obj.type,        // 类型int: 1，只有confirm一个按钮;  2，两个按钮
}

3）调用toast：
params: {
    msg: obj.msg,          // 信息
    type: obj.type,        // 类型int: 1，默认无图标；2，对号图标；3，X图标；4，！图标
    duration: obj.duration // 展示时间，单位毫秒，默认2000
}

4）调用finish或getparam：params可不传

5）调用setparam：params自定义，会覆盖接入应用传入SDK的初始化参数

3、callback参数详解

callback接受1个base64编码后的String参数，SDK直接调用JS代码callback(base64String)，示例如下

function callback(base64String) {
    var decoderStr = toStr(BASE64.decoder(result))    // base64解码得到参数
//    var json = JSON.parse(decoderStr);              // 若为JSON对象则解析
    ...
}

function toStr(arr) {
    var str = "";
    for(var i = 0; i < arr.length; i++) {
        var num = arr[i];
        str += String.fromCharCode(num);
    }
    return str
}

1）调用pay或login：
按接入应用制定规则解析入参，为AppToH5Msg的JSONString

public class AppToH5Msg {
    private String resultCode; // 返回状态  00：未处理， 01：成功， 02：失败， 03：处理中， 04：取消
    private String resultMsg;  // 应用执行后的返回信息，按规则解析
}

2）调用dialog：
点击确定按钮调用回调函数返回参数"1"，点击取消按钮调用回调函数返回参数"0"

3）调用toast：
toast展示结束消失后，调用回调函数返回参数"toast dismiss"

4）调用finish或setparam：
callback可不传，不执行回调函数

5）调用getparam：
直接调用回调函数返回参数为接入应用传入SDK的初始化参数，或者H5app通过调用setparam缓存的数据


——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

后续扩展