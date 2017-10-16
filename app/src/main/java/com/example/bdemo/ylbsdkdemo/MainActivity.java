package com.example.bdemo.ylbsdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yl.bsdk.YLBSdkManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                YLBSdkManager.jumpToH5App(MainActivity.this, "file:///android_asset/index.html");
                YLBSdkManager.jumpToH5AppWithInitParams(MainActivity.this, "file:///android_asset/index.html", "{\"token\": \"127hfahkhgbagygalbhjlagyfglbvlaughr\"}");
            }
        });
    }
}
