package com.me.guanpj.jdownloader;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadTask implements ConnectThread.ConnectListener, DownloadThread.DownloadListener {

    private final DownloadEntry entry;
    private final ExecutorService mExecutor;
    private Handler handler;
    private ConnectThread mConnectThread;
    private volatile boolean  isPaused = false;
    private volatile boolean isCanceled = false;
    private File desFile;

    public DownloadTask(DownloadEntry entry, ExecutorService executor, Handler handler) {
        this.entry = entry;
        this.mExecutor = executor;
        this.handler = handler;
        this.desFile = DownloadConfig.getInstance().getDownloadFile(entry.url);
    }

    public void start() {
        notifyUpdate(entry, DownloadService.NOTIFY_CONNECTING);
        mConnectThread = new ConnectThread(entry.url, this);
        mExecutor.execute(mConnectThread);

        /*entry.status = DownloadEntry.DownloadStatus.OnDownload;
        notifyUpdate(entry, DownloadService.NOTIFY_DOWNLOADING);

        entry.totalLength = 1024 * 18;
        for (int i = entry.currentLength; i < entry.totalLength; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isPaused || isCanceled) {
                entry.status = isPaused ? DownloadEntry.DownloadStatus.OnPause : DownloadEntry.DownloadStatus.OnCancel;
                notifyUpdate(entry, DownloadService.NOTIFY_PAUSED_OR_CANCELLED);
                return;
            }
            i += 1024;
            entry.currentLength += 1024;
            //DataChanger.getInstance().postStatus(entry);
            if(handler != null) {
                Message msg = handler.obtainMessage();
                msg.obj = entry;
                handler.sendMessage(msg);
            }
        }

        entry.status = DownloadEntry.DownloadStatus.OnComplete;
        notifyUpdate(entry, DownloadService.NOTIFY_COMPLETED);*/
    }

    public void pause() {
        isPaused = true;
        isCanceled = true;
        if(mConnectThread != null && mConnectThread.isRunning()) {
            mConnectThread.cancel();
        }
    }

    public void resume() {

    }

    public void cancel() {
        isCanceled = true;
        if(mConnectThread != null && mConnectThread.isRunning()) {
            mConnectThread.cancel();
        }
    }

    private void notifyUpdate(DownloadEntry entry, int msgWhat) {
        if(handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = msgWhat;
            msg.obj = entry;
            handler.sendMessage(msg);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMultiThreadDownload() {
        entry.status = DownloadEntry.DownloadStatus.OnDownload;
        notifyUpdate(entry, DownloadService.NOTIFY_DOWNLOADING);

        if(entry.ranges == null) {
            entry.ranges = new HashMap<>();
            for(int i = 0; i < Constant.MAX_DOWNLOAD_THREAD; i++) {
                entry.ranges.put(i, 0);
            }
        }

        int block = entry.totalLength / Constant.MAX_DOWNLOAD_THREAD;
        int startPos = 0, endPos = 0;
        for (int i = 0; i < Constant.MAX_DOWNLOAD_THREAD; i++) {
            startPos = i * block + entry.ranges.get(i);
            if(i == Constant.MAX_DOWNLOAD_THREAD - 1) {
                endPos = entry.totalLength;
            } else {
                endPos = (i + 1) * block;
            }
            if(startPos < endPos) {
                DownloadThread downloadThread = new DownloadThread(i, desFile, entry.url, startPos, endPos, this);
                mExecutor.execute(downloadThread);
            }
        }
    }

    private void startSingleThreadDownload() {

    }

    @Override
    public void onConnect(boolean isSupportRange, int totalLength) {
        entry.isSupportRange = isSupportRange;
        entry.totalLength = totalLength;

        if(entry.isSupportRange) {
            startMultiThreadDownload();
        } else {
            startSingleThreadDownload();
        }
    }

    @Override
    public void onError(String errorMessage) {
        entry.status = DownloadEntry.DownloadStatus.OnError;
        notifyUpdate(entry, DownloadService.NOTIFY_ERROR);
    }

    @Override
    public void onDownloadProgressChange(int index, int progress) {
        int range = entry.ranges.get(index) + progress;
        entry.ranges.put(index, range);

        entry.currentLength += progress;
        if(entry.currentLength == entry.totalLength) {
            entry.downloadPercent = 100;
            entry.status = DownloadEntry.DownloadStatus.OnComplete;
            notifyUpdate(entry, DownloadService.NOTIFY_COMPLETED);
        } else {
            int percent = (int) (entry.currentLength * 100l / entry.totalLength);
            if (percent > entry.downloadPercent) {
                entry.downloadPercent = percent;
                notifyUpdate(entry, DownloadService.NOTIFY_UPDATING);
            }
        }
    }

    @Override
    public void onDownloadPause(int index) {

    }

    @Override
    public void onDownloadComplete(int index) {

    }

    @Override
    public void onDownloadCancel(int index) {

    }

    @Override
    public void onDownloadError(int index, String message) {
        Trace.e("onDownloadError:" + message);
        entry.status = DownloadEntry.DownloadStatus.OnError;
        notifyUpdate(entry, DownloadService.NOTIFY_ERROR);
    }
}
