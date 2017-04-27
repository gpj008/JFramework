package guanpj.me.com.jdownloader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Jie on 2017/4/23.
 */

public class DataChanger extends Observable {

    private static DataChanger mInstance;
    private LinkedHashMap<String, DownloadEntry> mDownloadEntries;

    private DataChanger() {
        mDownloadEntries = new LinkedHashMap<>();
    }

    public synchronized static DataChanger getInstance() {
        if(mInstance == null) {
            mInstance = new DataChanger();
        }
        return mInstance;
    }

    public void postStatus(DownloadEntry entry) {
        mDownloadEntries.put(entry.id, entry);
        setChanged();
        notifyObservers(entry);
    }

    public ArrayList<DownloadEntry> getRecoverableEntries() {
        ArrayList<DownloadEntry> recoverabableEntries = null;
        for(Map.Entry<String, DownloadEntry> entry : mDownloadEntries.entrySet()) {
            if(entry.getValue().status == DownloadEntry.DownloadStatus.OnPause) {
                if(recoverabableEntries == null) {
                    recoverabableEntries = new ArrayList<>();
                }
                recoverabableEntries.add(entry.getValue());
            }
        }
        return recoverabableEntries;
    }
}
