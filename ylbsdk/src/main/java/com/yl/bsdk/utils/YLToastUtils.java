package com.yl.bsdk.utils;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yl.bsdk.R;

public class YLToastUtils {

    private Toast toast;

    private static volatile YLToastUtils mToastUtils;

    private YLToastUtils(Context context) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.layout_ylb_toast, null);
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
    }

    public static YLToastUtils getInstance(Context context) {
        if (null == mToastUtils) {
            synchronized (YLToastUtils.class) {
                if (null == mToastUtils) {
                    mToastUtils = new YLToastUtils(context);
                }
            }
        }
        return mToastUtils;
    }

    public void showMsg(String toastMsg, int type, int duration) {
        if (TextUtils.isEmpty(toastMsg)) return;
        if (duration == 0) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(duration);
        }
        TextView tv = toast.getView().findViewById(R.id.tv_msg);
        ImageView iv = toast.getView().findViewById(R.id.ic_toast);
        tv.setText(toastMsg);
        switch (type) {
            case 1:
                iv.setImageResource(R.drawable.ic_ylb_toast_success);
                break;
            case 2:
                iv.setImageResource(R.drawable.ic_ylb_toast_fail);
                break;
            case 3:
                iv.setImageResource(R.drawable.ic_ylb_toast_warn);
                break;
            default:
                iv.setVisibility(View.GONE);
                break;
        }
        toast.show();
    }

    public void cancel() {
        if (null != toast) {
            toast.cancel();
            toast = null;
        }
        mToastUtils = null;
    }

}
