package com.yl.bsdk.models;


import java.io.Serializable;

public class AppToH5Msg implements Serializable {

    private String resultCode; // 返回状态  00：未处理， 01：成功， 02：失败， 03：处理中， 04：取消。

    private String resultMsg; // app执行后的返回信息，传给H5app

    public AppToH5Msg(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
