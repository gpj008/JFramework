package guanpj.me.com.jdownloader_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import guanpj.me.com.jdownloader.DataWacther;
import guanpj.me.com.jdownloader.DownloadEntry;
import guanpj.me.com.jdownloader.DownloadManager;
import guanpj.me.com.jdownloader.Trace;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonStart;
    private Button mButtonPause;
    private DownloadManager mDownloadManager;
    private DataWacther mWacther = new DataWacther() {
        @Override
        protected void notifyUpdate(DownloadEntry object) {
            Trace.e(object.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonStart = (Button) findViewById(R.id.btn_start);
        mButtonStart.setOnClickListener(this);
        mButtonPause = (Button) findViewById(R.id.btn_pause);
        mButtonPause.setOnClickListener(this);
        mDownloadManager = DownloadManager.getInstance(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadManager.addObserver(mWacther);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDownloadManager.deleteObserver(mWacther);
    }

    @Override
    public void onClick(View v) {
        DownloadEntry entry = new DownloadEntry();
        entry.id = "1";
        entry.name = "gpj";
        entry.url = "http://api.stay4it.com/uploads/test.jpg";
        switch (v.getId()) {
            case R.id.btn_start:
                mDownloadManager.add(entry);
                break;
            case R.id.btn_pause:
                mDownloadManager.pause(entry);
                break;
        }
    }
}
