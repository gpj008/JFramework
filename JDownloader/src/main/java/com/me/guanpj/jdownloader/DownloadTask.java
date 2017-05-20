package com.me.guanpj.jdownloader;

import android.os.Handler;
import android.os.Message;

import com.me.guanpj.jdownloader.utility.TickTack;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadTask implements ConnectThread.ConnectListener, DownloadThread.DownloadListener {

    private final DownloadEntry mEntry;
    private final ExecutorService mExecutor;
    private Handler mHandler;
    private ConnectThread mConnectThread;
    private DownloadThread[] mDownloadThreads;
    private File mDesFile;
    private volatile boolean isPaused = false;
    private volatile boolean isCanceled = false;

    public DownloadTask(DownloadEntry entry, ExecutorService executor, Handler handler) {
        this.mEntry = entry;
        this.mExecutor = executor;
        this.mHandler = handler;
        this.mDesFile = DownloadConfig.getInstance().getDownloadFile(entry.url);
    }

    public void start() {
        if(mEntry.totalLength > 0) {
            Trace.e("not need to get if support range and total length");
            startDownload();
        } else {
            mEntry.status = DownloadEntry.DownloadStatus.OnConnect;
            notifyUpdate(mEntry, DownloadService.NOTIFY_CONNECTING);
            mConnectThread = new ConnectThread(mEntry.url, this);
            mExecutor.execute(mConnectThread);
        }
    }

    public void pause() {
        isPaused = true;
        if(mConnectThread != null && mConnectThread.isRunning()) {
            mConnectThread.cancel();
        }

        if(mDownloadThreads != null && mDownloadThreads.length > 0) {
            for (DownloadThread downloadThread : mDownloadThreads) {
                if(downloadThread != null && downloadThread.isRunning()) {
                    if(mEntry.isSupportRange) {
                        downloadThread.pause();
                    } else {
                        downloadThread.cancel();
                    }
                }
            }
        }
    }

    public void resume() {

    }

    public void cancel() {
        isCanceled = true;
        if(mConnectThread != null && mConnectThread.isRunning()) {
            mConnectThread.cancel();
        }

        if(mDownloadThreads != null && mDownloadThreads.length > 0) {
            for (DownloadThread downloadThread : mDownloadThreads) {
                if(downloadThread != null && downloadThread.isRunning()) {
                    downloadThread.cancel();
                }
            }
        }
    }

    private void startDownload() {
        mEntry.isSupportRange = false;
        mEntry.totalLength = -1;
        if(mEntry.isSupportRange) {
            startMultiThreadDownload();
        } else {
            startSingleThreadDownload();
        }
    }

    private void startMultiThreadDownload() {
        mEntry.status = DownloadEntry.DownloadStatus.OnDownload;
        notifyUpdate(mEntry, DownloadService.NOTIFY_DOWNLOADING);

        if(mEntry.ranges == null) {
            mEntry.ranges = new HashMap<>();
            for(int i = 0; i < Constant.MAX_DOWNLOAD_THREAD; i++) {
                mEntry.ranges.put(i, 0);
            }
        }

        int block = mEntry.totalLength / Constant.MAX_DOWNLOAD_THREAD;
        int startPos = 0, endPos = 0;
        mDownloadThreads = new DownloadThread[Constant.MAX_DOWNLOAD_THREAD];
        for (int i = 0; i < Constant.MAX_DOWNLOAD_THREAD; i++) {
            startPos = i * block + mEntry.ranges.get(i);
            if(i == Constant.MAX_DOWNLOAD_THREAD - 1) {
                endPos = mEntry.totalLength;
            } else {
                endPos = (i + 1) * block -1;
            }
            if(startPos < endPos) {
                mDownloadThreads[i] = new DownloadThread(i, mDesFile, mEntry.url, startPos, endPos, this);
                mExecutor.execute(mDownloadThreads[i]);
            }
        }
    }

    private void startSingleThreadDownload() {
        mEntry.status = DownloadEntry.DownloadStatus.OnDownload;
        notifyUpdate(mEntry, DownloadService.NOTIFY_DOWNLOADING);

        mDownloadThreads = new DownloadThread[1];
        mDownloadThreads[0] = new DownloadThread(0, mDesFile, mEntry.url, -1, -1, this);
        mExecutor.execute(mDownloadThreads[0]);
    }

    @Override
    public void onConnectSuccess(boolean isSupportRange, int totalLength) {
        mEntry.isSupportRange = isSupportRange;
        mEntry.totalLength = totalLength;

        startDownload();
    }

    @Override
    public void onConnectError(String errorMessage) {
        if(isPaused || isCanceled) {
            mEntry.status = isPaused ? DownloadEntry.DownloadStatus.OnPause : DownloadEntry.DownloadStatus.OnCancel;
            notifyUpdate(mEntry, DownloadService.NOTIFY_PAUSED_OR_CANCELLED);
        } else {
            mEntry.status = DownloadEntry.DownloadStatus.OnError;
            notifyUpdate(mEntry, DownloadService.NOTIFY_ERROR);
        }
    }

    @Override
    public synchronized void onDownloadProgressChange(int index, int progress) {
        if(mEntry.isSupportRange) {
            int range = mEntry.ranges.get(index) + progress;
            mEntry.ranges.put(index, range);
        }

        mEntry.currentLength += progress;

        if(mEntry.totalLength > 0) {
            int percent = (int) (mEntry.currentLength * 100l / mEntry.totalLength);
            if (percent > mEntry.downloadPercent) {
                mEntry.downloadPercent = percent;
                notifyUpdate(mEntry, DownloadService.NOTIFY_UPDATING);
            }
        } else {
            if(TickTack.getInstance().needNotify()) {
                notifyUpdate(mEntry, DownloadService.NOTIFY_UPDATING);
            }
        }

        /*if(mEntry.currentLength == mEntry.totalLength) {
            mEntry.downloadPercent = 100;
            mEntry.status = DownloadEntry.DownloadStatus.OnComplete;
            notifyUpdate(mEntry, DownloadService.NOTIFY_COMPLETED);
        } else {
            int percent = (int) (mEntry.currentLength * 100l / mEntry.totalLength);
            if (percent > mEntry.downloadPercent) {
                mEntry.downloadPercent = percent;
                notifyUpdate(mEntry, DownloadService.NOTIFY_UPDATING);
            }
        }*/
    }

    @Override
    public synchronized void onDownloadPause(int index) {
        for (DownloadThread downloadThread : mDownloadThreads) {
            if(downloadThread != null) {
                if(!downloadThread.isPaused()) {
                    return;
                }
            }
        }
        mEntry.status = DownloadEntry.DownloadStatus.OnPause;
        notifyUpdate(mEntry, DownloadService.NOTIFY_PAUSED_OR_CANCELLED);
    }

    @Override
    public synchronized void onDownloadComplete(int index) {
        for (DownloadThread downloadThread : mDownloadThreads) {
            if(downloadThread != null) {
                if(!downloadThread.isCompleted()) {
                    return;
                }
            }
        }
        if (mEntry.totalLength > 0 && mEntry.currentLength != mEntry.totalLength) {
            mEntry.status = DownloadEntry.DownloadStatus.OnError;
            mEntry.reset();
            notifyUpdate(mEntry, DownloadService.NOTIFY_ERROR);
        } else {
            mEntry.status = DownloadEntry.DownloadStatus.OnComplete;
            notifyUpdate(mEntry, DownloadService.NOTIFY_COMPLETED);
        }
    }

    @Override
    public synchronized void onDownloadCancel(int index) {
        for (DownloadThread downloadThread : mDownloadThreads) {
            if(downloadThread != null) {
                if(!downloadThread.isCancelled()) {
                    return;
                }
            }
        }
        mEntry.status = DownloadEntry.DownloadStatus.OnCancel;
        mEntry.reset();
        notifyUpdate(mEntry, DownloadService.NOTIFY_PAUSED_OR_CANCELLED);
    }

    @Override
    public synchronized void onDownloadError(int index, String message) {
        Trace.e("onDownloadError:" + message);
        boolean isAllError = true;
        for (DownloadThread downloadThread : mDownloadThreads) {
            if(downloadThread != null) {
                if(!downloadThread.isError()) {
                    downloadThread.cancelByError();
                    isAllError = false;
                }
            }
        }
        if(isAllError) {
            mEntry.status = DownloadEntry.DownloadStatus.OnError;
            notifyUpdate(mEntry, DownloadService.NOTIFY_ERROR);
        }
    }

    private void notifyUpdate(DownloadEntry entry, int msgWhat) {
        if(mHandler != null) {
            Trace.e("notifyUpdate:" + msgWhat + ":" + entry.currentLength);
            Message msg = mHandler.obtainMessage();
            msg.what = msgWhat;
            msg.obj = entry;
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
