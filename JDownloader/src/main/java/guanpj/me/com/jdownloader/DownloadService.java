package guanpj.me.com.jdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadService extends Service {

    private Map<String, DownloadTask> mDownloadingTasks;
    private ExecutorService mExecutors;
    private LinkedBlockingDeque<DownloadEntry> mWaitingQueue = new LinkedBlockingDeque<>();
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadEntry entry = (DownloadEntry) msg.obj;
            switch (entry.status) {
                case OnPause:
                case OnComplete:
                case OnCancel:
                    checkAndDoNext(entry);
                    break;
            }
            DataChanger.getInstance().postStatus(entry);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadingTasks = new HashMap<>();
        mExecutors = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadEntry entry = (DownloadEntry) intent.getSerializableExtra(Constant.KEY_DOWNLOAD_ENTRY);
        int action = intent.getIntExtra(Constant.KEY_DOWNLOAD_ACTION, -1);
        if(action != -1) {
            doAction(action, entry);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void doAction(int action, DownloadEntry entry) {
        switch (action) {
            case Constant.KEY_DOWNLOAD_ACTION_ADD:
                addDownload(entry);
                break;
            case Constant.KEY_DOWNLOAD_ACTION_PAUSE:
                pauseDownload(entry);
                break;
            case Constant.KEY_DOWNLOAD_ACTION_RESUME:
                resumeDownload(entry);
                break;
            case Constant.KEY_DOWNLOAD_ACTION_CANCEL:
                cancelDownload(entry);
                break;
        }
    }

    private void checkAndDoNext(DownloadEntry entry) {
        mDownloadingTasks.remove(entry);
        DownloadEntry nextEntry = mWaitingQueue.poll();
        if(nextEntry != null) {
            addDownload(nextEntry);
        }
    }

    private void addDownload(DownloadEntry entry) {
        if(mDownloadingTasks.size() >= Constant.MAX_DOWNLOAD_COUNT) {
            mWaitingQueue.offer(entry);
            entry.status = DownloadEntry.DownloadStatus.OnWait;
            DataChanger.getInstance().postStatus(entry);
        } else {
            startDownload(entry);
        }
    }

    private void startDownload(DownloadEntry entry) {
        DownloadTask task = new DownloadTask(entry, mHander);
        mDownloadingTasks.put(entry.id, task);
        mExecutors.execute(task);
    }

    private void pauseDownload(DownloadEntry entry) {
        DownloadTask task = mDownloadingTasks.remove(entry.id);
        if(task != null) {
            task.pause();
        } else {
            mWaitingQueue.remove(entry);
            entry.status = DownloadEntry.DownloadStatus.OnPause;
            DataChanger.getInstance().postStatus(entry);
        }
    }

    private void resumeDownload(DownloadEntry entry) {
        addDownload(entry);
    }

    private void cancelDownload(DownloadEntry entry) {
        DownloadTask task = mDownloadingTasks.remove(entry.id);
        if(task != null) {
            task.cancel();
        } else {
            mWaitingQueue.remove(entry);
            entry.status = DownloadEntry.DownloadStatus.OnCancel;
            DataChanger.getInstance().postStatus(entry);
        }
    }
}
