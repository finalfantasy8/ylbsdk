package com.yl.bsdk.models;

import java.io.Serializable;

/*
 * sdk 传入 接入的app
 */
public class YLBSdkToAppMsg implements Serializable {

    private String h5AppId; // 调用的H5app编号

    private String h5AppName; // 调用的H5app名称

    private String params; // 传入参数

    public YLBSdkToAppMsg(String h5AppId, String h5AppName, String params) {
        this.h5AppId = h5AppId;
        this.h5AppName = h5AppName;
        this.params = params;
    }

    public String getH5AppId() {
        return h5AppId;
    }

    public void setH5AppId(String h5AppId) {
        this.h5AppId = h5AppId;
    }

    public String getH5AppName() {
        return h5AppName;
    }

    public void setH5AppName(String h5AppName) {
        this.h5AppName = h5AppName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
