package com.ifmvo.matthew.icedemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ifmvo.matthew.icedemo.R;
import com.ifmvo.matthew.icedemo.ice.IceClient;

public class MainActivity extends AppCompatActivity {

    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = (TextView) findViewById(R.id.txt);

        /**
         * 调用接口就可以这样写
         */
        IceClient.getUsers("ifmvo", "123456", new IceClient.Callback<String>() {
            @Override
            public void onStart() {
                // showLoading();

            }

            @Override
            public void onFailure(String msg) {
                //closeLoading();
                //showErrorMsg();
                tvInfo.setText("链接异常信息" + msg);
            }

            @Override
            public void onSuccess(String result) {

                //closeLoading();
                //showResult();
                tvInfo.setText("返回成功信息" + result);
            }
        });

    }
}
