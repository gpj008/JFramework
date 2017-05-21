package com.me.guanpj.jdownloader.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.me.guanpj.jdownloader.DownloadManager;
import com.me.guanpj.jdownloader.core.DownloadEntry;
import com.me.guanpj.jdownloader.notify.DataWatcher;
import com.me.guanpj.jdownloader.utility.Constant;
import com.me.guanpj.jdownloader.utility.Trace;

public class AppDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadManager mDownloadManager;
    private TextView mTxtDesc;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnCancel;
    private DownloadEntry downloadEntry;
    private AppEntry appEntry;
    private DataWatcher mWacther = new DataWatcher() {
        @Override
        protected void notifyUpdate(DownloadEntry entry) {
            if(entry.id.equals(downloadEntry.id)) {
                downloadEntry = entry;
                initData();
                Trace.e(entry.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        mTxtDesc = (TextView) findViewById(R.id.txt_desc);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnPause.setOnClickListener(this);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mDownloadManager = DownloadManager.getInstance(this);

        appEntry = (AppEntry) getIntent().getSerializableExtra(Constant.KEY_APP_ENTRY);
        downloadEntry = mDownloadManager.containsDownloadEntry(appEntry.url)
                ? mDownloadManager.getDownloadEntry(appEntry.url) : appEntry.generateDownloadEntry();

        initData();
    }

    private void initData() {
        mTxtDesc.setText(appEntry.name + "  " + appEntry.size + "\n" + appEntry.desc + "\n" + downloadEntry.status + "\n"
                + Formatter.formatShortFileSize(getApplicationContext(), downloadEntry.currentLength)
                + "/" + Formatter.formatShortFileSize(getApplicationContext(), downloadEntry.totalLength));
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
        switch (v.getId()) {
            case R.id.btn_start:
                mDownloadManager.add(downloadEntry);
                break;
            case R.id.btn_pause:
                if(downloadEntry.status == DownloadEntry.DownloadStatus.OnDownload) {
                    mDownloadManager.pause(downloadEntry);
                } else if(downloadEntry.status == DownloadEntry.DownloadStatus.OnPause) {
                    mDownloadManager.resume(downloadEntry);
                }
                break;
            case R.id.btn_cancel:
                mDownloadManager.cancel(downloadEntry);
                break;
        }
    }
}
