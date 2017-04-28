package guanpj.me.com.jdownloader;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Jie on 2017/4/23.
 */

@DatabaseTable(tableName = "downloadentry")
public class DownloadEntry implements Serializable {

    @DatabaseField(id = true)
    public String id;
    @DatabaseField
    public String name;
    @DatabaseField
    public String url;
    @DatabaseField
    public DownloadStatus status = DownloadStatus.OnIdle;
    @DatabaseField
    public int currentLength;
    @DatabaseField
    public int totalLength;

    public DownloadEntry() {

    }

    public DownloadEntry(String url) {
        this.url = url;
        this.id = url;
        this.name = url.substring(url.lastIndexOf("/") + 1);
    }

    public enum DownloadStatus{OnIdle, OnWait, OnConnect, OnDownload, OnPause, OnResume, OnComplete, OnCancel, OnError}

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
