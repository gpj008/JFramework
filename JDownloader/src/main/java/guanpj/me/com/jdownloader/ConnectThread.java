package guanpj.me.com.jdownloader;

/**
 * Created by Jie on 2017/5/11.
 */

public class ConnectThread implements Runnable {

    private final String url;
    private final ConnectListener listener;

    public ConnectThread(String url, ConnectListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {

    }

    public interface ConnectListener{
        void onConnect(boolean isSupportRange, int totalLength);
        void onError(String errorMessage);
    }
}
