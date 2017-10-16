package com.example.bdemo.ylbsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.bsdk.constants.YLBSdkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class PayActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ImageView back = findViewById(R.id.iv_title_back);
        TextView msg = findViewById(R.id.h5_message);
        Button login = findViewById(R.id.btn_pay);

        msg.setText(getIntent().getStringExtra(YLBSdkConstants.EXTRA_YLBSDK_MSG));

        back.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_title_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            Random random = new Random();
            int code = random.nextInt(4) + 1;
            try {
                JSONObject object = new JSONObject();
                if (code == 1) {
                    object.put("resultCode", "01");
                    object.put("resultMsg", "支付成功");
                } else if (code == 2) {
                    object.put("resultCode", "02");
                    object.put("resultMsg", "支付失败");
                } else if (code == 3) {
                    object.put("resultCode", "03");
                    object.put("resultMsg", "支付处理中");
                } else if (code == 4) {
                    object.put("resultCode", "04");
                    object.put("resultMsg", "支付取消");
                }
                Intent i = new Intent();
                i.putExtra(YLBSdkConstants.EXTRA_YLBSDK_RESULT, object.toString());
                setResult(RESULT_OK, i);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
