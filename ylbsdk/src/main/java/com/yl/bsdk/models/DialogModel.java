package com.yl.bsdk.models;


import java.io.Serializable;

public class DialogModel implements Serializable {

    private String title; // 标题

    private String msg; // 信息

    private String confirm; // 确定按钮文案，默认"确定"，回调函数结果传"1"

    private String cancel; // 取消按钮文案，默认"取消"，回调函数结果传"0"

    private int type; // 1：只有一个按钮confirm； 2：两个按钮

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
