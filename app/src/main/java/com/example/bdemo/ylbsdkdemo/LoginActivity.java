package com.example.bdemo.ylbsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.bsdk.constants.YLBSdkConstants;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity implements OnClickListener{

    private EditText name;
    private EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView back = findViewById(R.id.iv_title_back);
        TextView msg = findViewById(R.id.h5_message);
        name = findViewById(R.id.et_username);
        pwd = findViewById(R.id.et_password);
        Button login = findViewById(R.id.btn_login);

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
            try {
                JSONObject object = new JSONObject();
                object.put("username", name.getText().toString().trim());
                object.put("token", pwd.getText().toString().trim());
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
