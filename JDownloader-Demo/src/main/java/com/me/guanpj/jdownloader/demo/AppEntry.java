package com.me.guanpj.jdownloader.demo;

import com.me.guanpj.jdownloader.core.DownloadEntry;

import java.io.Serializable;

/**
 * Created by Jie on 2017/5/21.
 */

public class AppEntry implements Serializable{
    public String name;
    public String icon;
    public String size;
    public String desc;
    public String url;

    @Override
    public String toString() {
        return name + "-----" + desc + "-----" + url;
    }

    public DownloadEntry generateDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.id = url;
        entry.name = name;
        entry.url = url;
        return entry;
    }
}
