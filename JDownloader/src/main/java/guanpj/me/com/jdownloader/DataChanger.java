package guanpj.me.com.jdownloader;

import java.util.Observable;

/**
 * Created by Jie on 2017/4/23.
 */

public class DataChanger extends Observable {

    private static DataChanger mInstance;

    private DataChanger() {
    }

    public synchronized static DataChanger getInstance() {
        if(mInstance == null) {
            mInstance = new DataChanger();
        }
        return mInstance;
    }

    public void postStatus(DownloadEntry entry) {
        setChanged();
        notifyObservers(entry);
    }
}
