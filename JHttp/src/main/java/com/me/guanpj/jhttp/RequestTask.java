package com.me.guanpj.jhttp;

import android.os.AsyncTask;

import com.me.guanpj.jhttp.error.AppException;
import com.me.guanpj.jhttp.listener.OnProgressUpdateListener;

import java.net.HttpURLConnection;

/**
 * Created by Jie on 2017/4/8.
 */

public class RequestTask extends AsyncTask {

    private final Request request;

    public RequestTask(Request request){
        this.request = request;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if(request.callBack != null){
            Object o = request.callBack.preRequest();
            if (o != null) {
                return o;
            }
        }
        return doRequest(0);
    }

    private Object doRequest(int currentCount) {
        try {
            HttpURLConnection connection = null;
            if (request.tool == Request.RequestTool.URLCONNECTION) {
                connection = HttpUrlConnectionUtil.execute(request, !request.enableProgressUpdate ? null : new OnProgressUpdateListener() {
                    @Override
                    public void onProgressUpdate(int currentLen, int totalLen) {
                        publishProgress(Request.STATE_UPLOAD, currentLen, totalLen);
                    }
                });
            } else if(request.tool == Request.RequestTool.OKHTTPURLCONNECTION){
                connection = OKHttpUrlConnectionUtil.execute(request, !request.enableProgressUpdate ? null : new OnProgressUpdateListener() {
                    @Override
                    public void onProgressUpdate(int curLen, int totalLen) {
                        publishProgress(Request.STATE_UPLOAD, curLen, totalLen);
                    }
                });
            }
            if(request.enableProgressUpdate){
                return request.callBack.parse(connection, new OnProgressUpdateListener() {
                    @Override
                    public void onProgressUpdate(int currentLen, int totalLen) {
                        publishProgress(Request.STATE_DOWNLOAD, currentLen, totalLen);
                    }
                });
            }else {
                return request.callBack.parse(connection);
            }
        } catch (AppException e) {
            if(e.type == AppException.ErrorType.TIMEOUT){
                if(currentCount < request.maxRetryCount){
                    return doRequest(++currentCount);
                }
            }
            return e;
        }
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        request.callBack.onProgressUpdate((Integer) values[0], (Integer) values[1], (Integer) values[2]);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(o instanceof AppException){
            if(request.onGlobalExceptionListener != null){
                if(!request.onGlobalExceptionListener.handlerException((AppException) o)){
                    request.callBack.onFailure((AppException) o);
                }
            }
            else {
                request.callBack.onFailure((AppException) o);
            }
        }else {
            request.callBack.onSuccesus(o);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
