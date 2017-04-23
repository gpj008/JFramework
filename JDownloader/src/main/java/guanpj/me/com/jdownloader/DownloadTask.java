package guanpj.me.com.jdownloader;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadTask implements Runnable {
    private final DownloadEntry entry;
    private boolean isPaused = false;
    private boolean isCanceld = false;

    public DownloadTask(DownloadEntry entry) {
        this.entry = entry;
    }

    @Override
    public void run() {
        start();
    }

    public void start() {
        entry.status = DownloadEntry.DownloadStatus.OnDownload;
        DataChanger.getInstance().postStatus(entry);

        entry.totalLength = 1024 * 100;
        for (int i = 0; i < entry.totalLength; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isPaused || isCanceld) {
                entry.status = isPaused ? DownloadEntry.DownloadStatus.OnPause : DownloadEntry.DownloadStatus.OnCancel;
                DataChanger.getInstance().postStatus(entry);
                return;
            }
            i += 1024;
            entry.currentLength += 1024;
            DataChanger.getInstance().postStatus(entry);
        }

        entry.status = DownloadEntry.DownloadStatus.OnComplete;
        DataChanger.getInstance().postStatus(entry);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {

    }

    public void cancel() {
        isCanceld = true;
    }
}
