package guanpj.me.com.jdownloader;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadTask implements Runnable {

    private final DownloadEntry entry;
    private Handler handler;
    private boolean isPaused = false;
    private boolean isCanceled = false;

    public DownloadTask(DownloadEntry entry, Handler handler) {
        this.entry = entry;
        this.handler = handler;
    }

    @Override
    public void run() {
        start();
    }

    public void start() {
        entry.status = DownloadEntry.DownloadStatus.OnDownload;
        //DataChanger.getInstance().postStatus(entry);
        if(handler != null) {
            Message msg = handler.obtainMessage();
            msg.obj = entry;
            handler.sendMessage(msg);
        }

        entry.totalLength = 1024 * 15;
        for (int i = entry.currentLength; i < entry.totalLength; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isPaused || isCanceled) {
                entry.status = isPaused ? DownloadEntry.DownloadStatus.OnPause : DownloadEntry.DownloadStatus.OnCancel;
                //DataChanger.getInstance().postStatus(entry);
                if(handler != null) {
                    Message msg = handler.obtainMessage();
                    msg.obj = entry;
                    handler.sendMessage(msg);
                }
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
        //DataChanger.getInstance().postStatus(entry);
        if(handler != null) {
            Message msg = handler.obtainMessage();
            msg.obj = entry;
            handler.sendMessage(msg);
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {

    }

    public void cancel() {
        isCanceled = true;
    }
}
