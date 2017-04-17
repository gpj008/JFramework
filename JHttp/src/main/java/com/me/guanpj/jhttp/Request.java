package com.me.guanpj.jhttp;

import android.os.Build;

import com.me.guanpj.jhttp.callback.ICallback;
import com.me.guanpj.jhttp.entity.FileEntity;
import com.me.guanpj.jhttp.error.AppException;
import com.me.guanpj.jhttp.listener.OnGlobalExceptionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Jie on 2017/4/8.
 */

public class Request {

    public RequestTask task;
    public ICallback callBack;
    public boolean enableProgressUpdate = false;
    public OnGlobalExceptionListener onGlobalExceptionListener;

    public int maxRetryCount = 3;
    public volatile boolean isCancelled;
    public String tag;
    public ArrayList<FileEntity> fileEntities;

    public static final int STATE_UPLOAD = 1;
    public static final int STATE_DOWNLOAD = 2;
    public enum RequestMethod{GET, POST, PUT, DELETE}
    public enum RequestTool{URLCONNECTION, OKHTTPURLCONNECTION}

    public String url;
    public String content;
    public String filePath;
    public Map<String, String> headers;
    public RequestMethod method;
    public RequestTool tool;

    public Request(String url){
        this(url, RequestMethod.GET);
    }

    public Request(String url, RequestTool tool){
        this(url, RequestMethod.GET, tool);
    }

    public Request(String url, RequestMethod method){
        this(url, method, RequestTool.URLCONNECTION);
    }

    public Request(String url, RequestMethod method, RequestTool tool){
        this.url = url;
        this.method = method;
        this.tool = tool;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void excete(Executor executor) {
        task = new RequestTask(this);
        if(Build.VERSION.SDK_INT > 11){
            task.execute();
        }else {
            task.execute(executor);
        }
    }

    public void setEnableProgressUpdate(boolean enable) {
        this.enableProgressUpdate = enable;
    }

    public void setGlobalExceptionListener(OnGlobalExceptionListener onGlobalExceptionListener) {
        this.onGlobalExceptionListener = onGlobalExceptionListener;
    }

    public void setMaxRetryCount(int maxRetryCount){
        this.maxRetryCount = maxRetryCount;
    }

    public void setCallback(ICallback iCallback) {
        this.callBack = iCallback;
    }

    public void cancel(boolean force){
        isCancelled = true;
        callBack.cancel();
        task.cancel(force);
    }

    public void checkIfCancelled() throws AppException {
        if (isCancelled){
            throw new AppException(AppException.ErrorType.CANCEL, "the request has been cancelled");
        }
    }

    public void addHeader(String key, String value) {
        if (headers == null){
            headers = new HashMap<String,String>();
        }
        headers.put(key,value);
    }
}
