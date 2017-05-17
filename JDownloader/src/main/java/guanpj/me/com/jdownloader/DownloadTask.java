package guanpj.me.com.jdownloader;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutorService;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadTask implements ConnectThread.ConnectListener {

    private final DownloadEntry entry;
    private final ExecutorService mExecutor;
    private Handler handler;
    private volatile boolean  isPaused = false;
    private volatile boolean isCanceled = false;
    private ConnectThread mConnectThread;

    public DownloadTask(DownloadEntry entry, ExecutorService executor, Handler handler) {
        this.entry = entry;
        this.mExecutor = executor;
        this.handler = handler;
    }

    public void start() {
        notifyUpdate(entry, DownloadService.NOTIFY_CONNECTING);
        mConnectThread = new ConnectThread(entry.url, this);
        mExecutor.execute(mConnectThread);

        entry.status = DownloadEntry.DownloadStatus.OnDownload;
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
        notifyUpdate(entry, DownloadService.NOTIFY_COMPLETED);
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
        }
    }

    private void startMultiThreadDownload() {
        int block = entry.totalLength / Constant.MAX_DOWNLOAD_THREAD;
        int startPos = 0, endPos = 0;
        for (int i = 0; i < Constant.MAX_DOWNLOAD_THREAD; i++) {
            startPos = i * block;
            if(i == Constant.MAX_DOWNLOAD_THREAD - 1) {
                endPos = entry.totalLength;
            } else {
                endPos = (i + 1) * block;
            }
            DownloadThread downloadThread = new DownloadThread(entry.url, startPos, endPos);
            mExecutor.execute(downloadThread);
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
}
