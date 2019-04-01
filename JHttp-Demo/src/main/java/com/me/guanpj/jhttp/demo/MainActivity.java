package com.me.guanpj.jhttp.demo;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.me.guanpj.jhttp.Request;
import com.me.guanpj.jhttp.RequestManager;
import com.me.guanpj.jhttp.callback.FileCallback;
import com.me.guanpj.jhttp.callback.JsonArrayReaderCallback;
import com.me.guanpj.jhttp.callback.JsonCallback;
import com.me.guanpj.jhttp.callback.JsonReaderCallback;
import com.me.guanpj.jhttp.callback.StringCallback;
import com.me.guanpj.jhttp.entity.FileEntity;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //testHttpGetOnSubThread();
        //testHttpPostOnSubThread();
        //testHttpPostOnSubThreadForGeneric();
        //testHttpPostOnSubThreadForDownload();
        //testHttpPostOnSubThreadForDownloadWithProgress();
        //testHttpPostOnSubThreadForGenericLoadMore();
        //testHttpForJsonReaderArray();
        //testUploadProgressUpdated();
        testHttpPostOnSubThreadForOKHTTP();
    }

    public void testHttpGetOnSubThread(){
        String url = "http://api.stay4it.com";
        //String url = "https://www.baidu.com";
        Request request = new Request(url);
        request.setCallback(new StringCallback(){
            @Override
            public void onSuccesus(String result) {
                Log.e("gpj", "testHttpGet return:" + result);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        });
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpPostOnSubThread(){
        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        Request request = new Request(url, Request.RequestMethod.POST);
        request.content = content;
        request.setCallback(new StringCallback() {
            @Override
            public String preRequest() {
                return super.preRequest();
            }

            @Override
            public void onSuccesus(String result) {
                Log.e("gpj", "testHttpPost return:" + result);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        });
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpPostOnSubThreadForGeneric(){
        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        String path = Environment.getExternalStorageDirectory() + File.separator + "json.tmp";
        Request request = new Request(url, Request.RequestMethod.POST);
        request.content = content;
        request.setCallback(new JsonReaderCallback<User>() {
            @Override
            public void onSuccesus(User result) {
                Log.e("gpj", "testHttpPost return:" + result.toString());
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        }.setCachePath(path));
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpPostOnSubThreadForDownload(){
        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        Request request = new Request(url, Request.RequestMethod.POST);
        request.content = content;
        String path = Environment.getExternalStorageDirectory() + File.separator + "json.tmp";
        request.setCallback(new FileCallback() {
            @Override
            public void onSuccesus(String result) {
                Log.e("gpj", "testHttpPost return:" + result);
            }

            @Override
            public void onFailure(Exception ex) {

            }
        }.setCachePath(path));
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpPostOnSubThreadForDownloadWithProgress(){
        String url = "http://api.stay4it.com/uploads/test.jpg";
        final Request request = new Request(url, Request.RequestMethod.GET);
        String path = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
        request.setCallback(new FileCallback() {
            @Override
            public void onProgressUpdate(int state, int currentLen, int totalLen) {
                Log.e("gpj", "download:" + currentLen + "/" + totalLen);
            }

            @Override
            public void onSuccesus(String result) {
                Log.e("gpj", "testHttpPost return:" + result);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        }.setCachePath(path));
        request.setEnableProgressUpdate(true);
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpPostOnSubThreadForGenericLoadMore(){
        String url = "http://api.stay4it.com/v1/public/core/?service=user.getAll";
        url += "&timestamp=" + System.currentTimeMillis() + "&count=30";
        Request request = new Request(url, Request.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<Module>>() {
            @Override
            public ArrayList<Module> preRequest(){
                return null;
            }
            @Override
            public ArrayList<Module> postRequest(ArrayList<Module> modules) {
                return modules;
            }

            @Override
            public void onSuccesus(ArrayList<Module> result) {

            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        });
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    public void testHttpForJsonReaderArray() {
        String url = "http://api.stay4it.com/v1/public/core/?service=user.getAll";
        url += "&timestamp=" + System.currentTimeMillis() + "&count=30";
        Request request = new Request(url, Request.RequestMethod.GET);
        String path = Environment.getExternalStorageDirectory() + File.separator + "jsonarray.tmp";
        request.setCallback(new JsonArrayReaderCallback<Module>() {
            @Override
            public void onSuccesus(ArrayList<Module> result) {
                Log.e("gpj", "result : " + result.size());
            }

            @Override
            public void onFailure(Exception ex) {

            }
        }.setCachePath(path));
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    private void testUploadProgressUpdated(){
        String url = "http://api.stay4it.com/v1/public/core/?service=user.updateAvatar";
        Request request = new Request(url, Request.RequestMethod.POST);

        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Charset", "UTF-8");
        request.addHeader("Content-Type", "multipart/form-data;boundary=7d4a6d158c9");

        String path = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";

        ArrayList<FileEntity> entities = new ArrayList<FileEntity>();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(path.substring(path.lastIndexOf("/") + 1));
        fileEntity.setFilePath(path);
        fileEntity.setFileType("image/png");
        entities.add(fileEntity);

        request.content = "stay4it";
        request.filePath = path;

        request.setCallback(new FileCallback() {
            @Override
            public void onProgressUpdate(int state, int currentLen, int totalLen) {
                super.onProgressUpdate(state, currentLen, totalLen);
                Log.e("gpj", "state=" + state + " onprogressUpdated:" + currentLen + "/" + totalLen);
            }

            @Override
            public void onSuccesus(String result) {
                Log.e("gpj", "result : " + result);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
            }
        });
        request.setEnableProgressUpdate(true);
        request.setGlobalExceptionListener(this);
        request.setTag(toString());
        RequestManager.getInstance().performRequest(request);
    }

    private void testHttpPostOnSubThreadForOKHTTP() {
        String url = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";
        Request request = new Request(url, Request.RequestMethod.POST, Request.RequestTool.OKHTTPURLCONNECTION);
        String path = Environment.getExternalStorageDirectory() + File.separator + "json.tmp";
        request.setCallback(new JsonReaderCallback<User>() {
            @Override
            public void onSuccesus(User result) {
                Log.e("gpj", "testHttpGet return:" + result.toString());
            }

            @Override
            public void onFailure(Exception ex) {

            }
        }.setCachePath(path));
        request.content = content;
        request.maxRetryCount = 0;
        RequestManager.getInstance().performRequest(request);
    }
}
