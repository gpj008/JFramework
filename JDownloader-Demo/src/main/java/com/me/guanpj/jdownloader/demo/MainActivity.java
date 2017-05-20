package com.me.guanpj.jdownloader.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.me.guanpj.jdownloader.notify.DataWatcher;
import com.me.guanpj.jdownloader.core.DownloadEntry;
import com.me.guanpj.jdownloader.DownloadManager;
import com.me.guanpj.jdownloader.utility.Trace;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadManager mDownloadManager;
    private Button mButtonStart;
    private Button mButtonPause;
    private Button mButtonCancel;
    private DownloadEntry entry;
    private DataWatcher mWacther = new DataWatcher() {
        @Override
        protected void notifyUpdate(DownloadEntry object) {
            entry = object;
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
        mButtonCancel = (Button) findViewById(R.id.btn_cancel);
        mButtonCancel.setOnClickListener(this);
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
        if(entry == null) {
            entry = new DownloadEntry();
            entry.id = "1";
            entry.name = "gpj";
            //entry.url = "http://shouji.360tpcdn.com/150706/5a9bec48b764a892df801424278a4285/com.mt.mtxx.mtxx_434.apk";
            entry.url = "http://shouji.360tpcdn.com/150813/9e775b5afb66feb960941cd8879af0b8/com.sankuai.meituan_291.apk";
            //entry.url = "http://shouji.360tpcdn.com/150810/10805820b9fbe1eeda52be289c682651/com.qihoo.vpnmaster_3019020.apk";
        }
        switch (v.getId()) {
            case R.id.btn_start:
                mDownloadManager.add(entry);
                break;
            case R.id.btn_pause:
                if(entry.status == DownloadEntry.DownloadStatus.OnDownload) {
                    mDownloadManager.pause(entry);
                } else if(entry.status == DownloadEntry.DownloadStatus.OnPause) {
                    mDownloadManager.resume(entry);
                }
                break;
            case R.id.btn_cancel:
                mDownloadManager.cancel(entry);
                break;
        }
    }
}
