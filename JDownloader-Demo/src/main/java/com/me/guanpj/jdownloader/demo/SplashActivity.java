package com.me.guanpj.jdownloader.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.me.guanpj.jdownloader.DownloadManager;

/**
 * Created by Jie on 2017/4/29.
 */

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            jump();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadManager.getInstance(getApplicationContext());
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    private void jump() {
        startActivity(new Intent(this, ListActivity.class));
    }
}
