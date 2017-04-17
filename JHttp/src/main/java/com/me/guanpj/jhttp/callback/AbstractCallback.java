package com.me.guanpj.jhttp.callback;

import com.me.guanpj.jhttp.error.AppException;
import com.me.guanpj.jhttp.listener.OnProgressUpdateListener;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jie on 2017/4/9.
 */

public abstract class AbstractCallback<T> implements ICallback<T> {

    private String path;
    public volatile boolean isCancelled;

    @Override
    public T parse(HttpURLConnection connection, OnProgressUpdateListener listener) throws AppException {
        try {
            checkIfCancelled();
            InputStream is = null;
            int status = connection.getResponseCode();
            if(status == HttpsURLConnection.HTTP_OK){

                String encoding = connection.getContentEncoding();
                if (encoding != null && "gzip".equalsIgnoreCase(encoding)) {
                    is = new GZIPInputStream(connection.getInputStream());
                }
                else if (encoding != null && "deflate".equalsIgnoreCase(encoding)) {
                    is = new InflaterInputStream(connection.getInputStream());
                }
                else {
                    is = connection.getInputStream();
                }

                if(path == null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer  = new byte[2048];
                    int len;
                    while ((len = is.read(buffer)) != -1){
                        checkIfCancelled();
                        baos.write(buffer, 0, len);
                    }
                    is.close();
                    baos.flush();
                    baos.close();
                    String result = new String(baos.toByteArray());
                    T t = bindData(result);
                    return postRequest(t);
                }else {
                    FileOutputStream fos = new FileOutputStream(path);
                    int totalLen = connection.getContentLength();
                    int currentLen = 0;

                    byte[] buffer  = new byte[2048];
                    int len;
                    while ((len = is.read(buffer)) != -1){
                        checkIfCancelled();
                        fos.write(buffer, 0, len);
                        currentLen += len;
                        if(null != listener) {
                            listener.onProgressUpdate(currentLen, totalLen);
                        }
                    }
                    is.close();
                    fos.flush();
                    fos.close();
                    T t = bindData(path);
                    return postRequest(t);
                }
            }else {
                throw new AppException(status, connection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.SERVER, e.getMessage());
        }
    }

    @Override
    public T parse(HttpURLConnection connection) throws AppException {
        return parse(connection, null);
    }

    @Override
    public T preRequest() {
        return null;
    }

    @Override
    public T postRequest(T t) {
        return t;
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    public void checkIfCancelled() throws AppException {
        if (isCancelled){
            throw new AppException(AppException.ErrorType.CANCEL, "the request has been cancelled");
        }
    }

    @Override
    public void onProgressUpdate(int state, int currentLen, int totalLen) {

    }

    protected abstract T bindData(String result) throws AppException;

    public ICallback setCachePath(String path) {
        this.path = path;
        return this;
    }
}
