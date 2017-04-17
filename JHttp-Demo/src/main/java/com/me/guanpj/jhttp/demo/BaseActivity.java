package com.me.guanpj.jhttp.demo;

import android.support.v7.app.AppCompatActivity;

import com.me.guanpj.jhttp.RequestManager;
import com.me.guanpj.jhttp.error.AppException;
import com.me.guanpj.jhttp.listener.OnGlobalExceptionListener;

/**
 * Created by Jie on 2017/4/9.
 */

public class BaseActivity extends AppCompatActivity implements OnGlobalExceptionListener {
    @Override
    public boolean handlerException(AppException e) {
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestManager.getInstance().cancelRequest(toString(), true);
    }
}
