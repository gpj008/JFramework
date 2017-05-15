package guanpj.me.com.jdownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jie on 2017/5/11.
 */

public class ConnectThread implements Runnable {

    private final String url;
    private final ConnectListener listener;
    private volatile boolean isRunning = false;

    public ConnectThread(String url, ConnectListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {
        isRunning = true;
        HttpURLConnection conn = null;
        boolean isSupportRange = false;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(Constant.READ_TIMEOUT);
            conn.setConnectTimeout(Constant.CONNECT_TIMEOUT);
            int responseCode = conn.getResponseCode();
            int contentLength = conn.getContentLength();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                if("bytes".equals(conn.getHeaderField("Accept-Ranges"))) {
                    isSupportRange = true;
                }
                listener.onConnect(isSupportRange, contentLength);
            } else {
                listener.onError("server error:" + responseCode);
            }
            isRunning = false;
        } catch (Exception e) {
            isRunning = false;
            listener.onError(e.getMessage());
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    public interface ConnectListener{
        void onConnect(boolean isSupportRange, int totalLength);
        void onError(String errorMessage);
    }
}
