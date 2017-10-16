package com.yl.bsdk.jsbridge;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.yl.bsdk.models.ToastModel;
import com.yl.bsdk.utils.YLToastUtils;

public class ToastHandler implements BridgeHandler {

    private Context mContext;

    public ToastHandler(Context context) {
        mContext = context;
    }

    @Override
    public void handler(String data, final CallBackFunction function) {

        Gson gson = new Gson();
        ToastModel model = gson.fromJson(data, ToastModel.class);
        YLToastUtils.getInstance(mContext).showMsg(model.getMsg(), model.getType(), model.getDuration());
        if(function != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            if (model.getDuration() == 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        function.onCallBack("toast dismiss");
                    }
                }, 2000);
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        function.onCallBack("toast dismiss");
                    }
                }, model.getDuration());
            }
        }
    }
}
