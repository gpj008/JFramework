package guanpj.me.com.jdownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jie on 2017/4/23.
 */

public class DownloadThread implements Runnable {

    private String url;
    private int startPos;
    private int endPos;

    public DownloadThread(String url, int startPos, int endPos) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            connection.setReadTimeout(Constant.READ_TIMEOUT);
            connection.setConnectTimeout(Constant.CONNECT_TIMEOUT);
            int responseCode = connection.getResponseCode();
            int contentLength = connection.getContentLength();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
