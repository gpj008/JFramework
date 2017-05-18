package com.me.guanpj.jdownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadThread implements Runnable {

    private int index;
    private File desFile;
    private String url;
    private int startPos;
    private int endPos;
    private DownloadListener listener;

    public DownloadThread(int index, File desFile, String url, int startPos, int endPos, DownloadListener listener) {
        this.index = index;
        this.desFile = desFile;
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.listener = listener;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            connection.setReadTimeout(Constant.READ_TIMEOUT);
            connection.setConnectTimeout(Constant.CONNECT_TIMEOUT);
            int responseCode = connection.getResponseCode();
            int contentLength = connection.getContentLength();
            RandomAccessFile raf = null;
            InputStream is = null;
            if(responseCode == HttpURLConnection.HTTP_PARTIAL) {
                raf = new RandomAccessFile(desFile, "rw");
                raf.seek(startPos);
                is = connection.getInputStream();
                int len = -1;
                byte[] buffer = new byte[2048];
                while ((len = is.read(buffer)) != -1) {
                    raf.write(buffer, 0, len);
                    listener.onDownloadProgressChange(index, len);
                }
                raf.close();
                is.close();
                listener.onDownloadComplete(index);
            }
        } catch (Exception e) {
            listener.onDownloadError(index, e.getMessage());
        } finally {
            if(null != connection) {
                connection.disconnect();
            }
        }
    }

    interface DownloadListener {
        void onDownloadProgressChange(int index, int progress);
        void onDownloadPause(int index);
        void onDownloadComplete(int index);
        void onDownloadCancel(int index);
        void onDownloadError(int index, String message);
    }
}
