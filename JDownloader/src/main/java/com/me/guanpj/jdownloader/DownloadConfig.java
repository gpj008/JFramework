package com.me.guanpj.jdownloader;

import android.os.Environment;

import com.me.guanpj.jdownloader.utility.FileUtility;

import java.io.File;

/**
 * Created by Jie on 2017/5/19.
 */

public class DownloadConfig {
    private static DownloadConfig mInstance = null;
    private File downFile;

    private DownloadConfig(){
        downFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public synchronized static DownloadConfig getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadConfig();
        }
        return mInstance;
    }

    public File getDownloadFile(String url) {
        return new File(downFile, FileUtility.getMd5FileName(url));
    }
}
