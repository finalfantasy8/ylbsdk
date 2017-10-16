package com.yl.bsdk.models;


import java.io.Serializable;

public class ToastModel implements Serializable{

    private String msg; // 展示信息

    private int duration; // 展示时长，毫秒

    private int type; //  0,default; 1,success; 2,fail; 3,warn; 展示图片不同

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
