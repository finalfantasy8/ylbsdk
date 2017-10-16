package com.yl.bsdk.models;

import java.io.Serializable;

/*
 * H5 传入 sdk
 */
public class H5ToYLBSdkMsg<T> implements Serializable {

    private String appid;  // 接入的H5app编号
    private String appname;  // 接入的H5app名称
    private T params;   // js 传入 java 的参数
    private String callback; // js端 回调函数

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
