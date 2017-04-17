package com.me.guanpj.jhttp.callback;

import com.me.guanpj.jhttp.error.AppException;
import com.me.guanpj.jhttp.listener.OnProgressUpdateListener;

import java.net.HttpURLConnection;

/**
 * Created by Jie on 2017/4/8.
 */

public interface ICallback<T> {

    void onSuccesus(T result);

    void onFailure(Exception ex);

    T preRequest();

    T postRequest(T t);

    T parse(HttpURLConnection connection, OnProgressUpdateListener listener) throws AppException;

    T parse(HttpURLConnection connection) throws AppException;

    void onProgressUpdate(int state, int currentLen, int totalLen);

    void cancel();
}
