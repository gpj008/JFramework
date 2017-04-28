package guanpj.me.com.jdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import guanpj.me.com.jdownloader.db.DBControler;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadService extends Service {

    public static final int NOTIFY_CONNECTING = 0;
    public static final int NOTIFY_DOWNLOADING = 1;
    public static final int NOTIFY_UPDATING = 2;
    public static final int NOTIFY_PAUSED_OR_CANCELLED = 3;
    public static final int NOTIFY_COMPLETED = 4;

    private Map<String, DownloadTask> mDownloadingTasks;
    private ExecutorService mExecutors;
    private LinkedBlockingDeque<DownloadEntry> mWaitingQueue;
    private DataChanger mDataChanger;
    private DBControler mDBControler;
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadEntry entry = (DownloadEntry) msg.obj;
            switch (msg.what) {
                case NOTIFY_PAUSED_OR_CANCELLED:
                case NOTIFY_COMPLETED:
                    checkAndDoNext(entry);
                    break;
            }
            DataChanger.getInstance(getApplicationContext()).postStatus(entry);
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
        mWaitingQueue = new LinkedBlockingDeque<>();
        mDataChanger = DataChanger.getInstance(getApplicationContext());
        mDBControler = DBControler.getInstance(getApplicationContext());

        ArrayList<DownloadEntry> downloadEntries = mDBControler.queryAll();
        if(downloadEntries != null && downloadEntries.size() > 0) {
            for (DownloadEntry downloadEntry : downloadEntries) {
                if(downloadEntry.status == DownloadEntry.DownloadStatus.OnDownload ||
                        downloadEntry.status == DownloadEntry.DownloadStatus.OnIdle) {
                    downloadEntry.status = DownloadEntry.DownloadStatus.OnPause;
                    addDownload(downloadEntry);
                }
                mDataChanger.addDownloadEntry(downloadEntry.id, downloadEntry);
            }
        }
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
            case Constant.KEY_DOWNLOAD_ACTION_PAUSE_ALL:
                pauseAll();
                break;
            case Constant.KEY_DOWNLOAD_ACTION_RECOVER_ALL:
                recoverAll();
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
            mDataChanger.postStatus(entry);
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
            mDataChanger.postStatus(entry);
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
            mDataChanger.postStatus(entry);
        }
    }

    private void pauseAll() {
        while (mWaitingQueue.iterator().hasNext()) {
            DownloadEntry entry = mWaitingQueue.poll();
            entry.status = DownloadEntry.DownloadStatus.OnPause;
            mDataChanger.postStatus(entry);
        }
        for(Map.Entry<String, DownloadTask> entry : mDownloadingTasks.entrySet()) {
            entry.getValue().pause();
        }
        mDownloadingTasks.clear();
    }

    private void recoverAll() {
        ArrayList<DownloadEntry> recoverableEntries = DataChanger.getInstance(getApplicationContext()).getRecoverableDownloadEntries();
        if(recoverableEntries != null && recoverableEntries.size() > 0) {
            for (DownloadEntry recoverableEntry : recoverableEntries) {
                addDownload(recoverableEntry);
            }
        }
    }
}
