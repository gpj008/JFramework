package guanpj.me.com.jdownloader;

import java.io.Serializable;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadEntry implements Serializable {

    public DownloadEntry() {

    }

    public DownloadEntry(String url) {
        this.url = url;
        this.id = url;
        this.name = url.substring(url.lastIndexOf("/") + 1);
    }

    public enum DownloadStatus{OnIdle, OnWait, OnConnect, OnDownload, OnPause, OnResume, OnComplete, OnCancel, OnError}

    public String id;
    public String name;
    public String url;

    public DownloadStatus status = DownloadStatus.OnIdle;
    public int currentLength;
    public int totalLength;

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name + " is " + status.name() + " with " + currentLength + "/" + totalLength;
    }
}
