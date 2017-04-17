package com.me.guanpj.jhttp.callback;

import android.util.Log;

import com.me.guanpj.jhttp.error.AppException;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class StringCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String result) throws AppException {
        Log.e("gpj", "bindData rerutn : " + result);
        return result;
    }
}
