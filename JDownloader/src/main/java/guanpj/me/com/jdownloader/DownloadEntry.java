package guanpj.me.com.jdownloader;

import java.io.Serializable;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadEntry implements Serializable {

    public enum DownloadStatus{OnWait, OnDownload, OnPause, OnResume, OnComplete, OnCancel}

    public String id;
    public String name;
    public String url;

    public DownloadStatus status;
    public int currentLength;
    public int totalLength;

    @Override
    public String toString() {
        return name + " is " + status.name() + " with " + currentLength + "/" + totalLength;
    }
}
