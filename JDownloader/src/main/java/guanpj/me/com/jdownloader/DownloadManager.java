package guanpj.me.com.jdownloader;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadManager {

    private static DownloadManager mInstance;
    private final Context mContext;

    private DownloadManager(Context context) {
        mContext = context;
    }

    public synchronized static DownloadManager getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new DownloadManager(context);
        }
        return mInstance;
    }

    public void add(DownloadEntry entry) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_ADD);
        mContext.startService(intent);
    }

    public void pause(DownloadEntry entry) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_PAUSE);
        mContext.startService(intent);
    }

    public void resume(DownloadEntry entry) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_RESUME);
        mContext.startService(intent);
    }

    public void cancel(DownloadEntry entry) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_CANCEL);
        mContext.startService(intent);
    }

    public void pauseAll() {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_PAUSE_ALL);
        mContext.startService(intent);
    }

    public void recoverAll() {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constant.KEY_DOWNLOAD_ACTION, Constant.KEY_DOWNLOAD_ACTION_RECOVER_ALL);
        mContext.startService(intent);
    }

    public void addObserver(DataWatcher wacther) {
        DataChanger.getInstance().addObserver(wacther);
    }

    public void deleteObserver(DataWatcher wacther) {
        DataChanger.getInstance().deleteObserver(wacther);
    }
}
