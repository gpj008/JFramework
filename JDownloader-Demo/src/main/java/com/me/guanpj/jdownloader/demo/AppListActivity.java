package com.me.guanpj.jdownloader.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.me.guanpj.jdownloader.DownloadManager;
import com.me.guanpj.jdownloader.core.DownloadEntry;
import com.me.guanpj.jdownloader.notify.DataWatcher;
import com.me.guanpj.jdownloader.utility.Constant;
import com.me.guanpj.jdownloader.utility.Trace;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jie on 2017/4/24.
 */

public class AppListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DownloadManager mDownloadManager;
    private ListView mAppList;
    private MyAdapter mAdapter;
    private List<AppEntry> mData;
    private RequestQueue mQueue;
    private DataWatcher mWatcher = new DataWatcher() {
        @Override
        protected void notifyUpdate(DownloadEntry object) {
            mAdapter.notifyDataSetChanged();
            Trace.e(object.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mDownloadManager = DownloadManager.getInstance(this);
        mQueue = Volley.newRequestQueue(this);
        mAppList = (ListView) findViewById(R.id.app_list);
        mAppList.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mAppList.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        Type type = new TypeToken<ArrayList<AppEntry>>() {
        }.getType();
        String url = "http://api.stay4it.com/v1/public/core/?service=downloader.applist";
        GsonRequest request = new GsonRequest<ArrayList<AppEntry>>(Request.Method.GET, url,
                type, new Response.Listener<ArrayList<AppEntry>>() {

            @Override
            public void onResponse(ArrayList<AppEntry> response) {
                for (AppEntry appEntry : response) {
                    Trace.e(appEntry.toString());
                }
                mData = response;
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadManager.addObserver(mWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDownloadManager.deleteObserver(mWatcher);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppEntry entry = mData.get(position);
        Intent intent = new Intent(this, AppDetailActivity.class);
        intent.putExtra(Constant.KEY_APP_ENTRY, entry);
        startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(AppListActivity.this).inflate(R.layout.activity_applist_item, null);
                holder = new ViewHolder();
                holder.mTitleView = (TextView) convertView.findViewById(R.id.txt_title);
                holder.mStatusView = (TextView) convertView.findViewById(R.id.txt_status);
                holder.mDownloadBtn = (Button) convertView.findViewById(R.id.btn_start);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppEntry appEntry = mData.get(position);
            final DownloadEntry entry = mDownloadManager.containsDownloadEntry(appEntry.url)
                    ? mDownloadManager.getDownloadEntry(appEntry.url) : appEntry.generateDownloadEntry();
            holder.mTitleView.setText(appEntry.name + "  " + appEntry.size + "\n" + appEntry.desc);
            holder.mStatusView.setTag(entry.id);
            holder.mStatusView.setText(entry.status + "\n"
                    + Formatter.formatShortFileSize(getApplicationContext(), entry.currentLength)
                    + "/" + Formatter.formatShortFileSize(getApplicationContext(), entry.totalLength));
            holder.mDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(entry.status == DownloadEntry.DownloadStatus.OnIdle || entry.status == DownloadEntry.DownloadStatus.OnCancel) {
                        mDownloadManager.add(entry);
                    } else if(entry.status == DownloadEntry.DownloadStatus.OnWait || entry.status == DownloadEntry.DownloadStatus.OnDownload) {
                        mDownloadManager.pause(entry);
                    } else if(entry.status == DownloadEntry.DownloadStatus.OnPause) {
                        mDownloadManager.resume(entry);
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            if(mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public AppEntry getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private static class ViewHolder {
        TextView mTitleView;
        TextView mStatusView;
        Button mDownloadBtn;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if (item.getTitle().equals("Pause All")) {
                item.setTitle(R.string.action_recover_all);
                mDownloadManager.pauseAll();
            } else {
                item.setTitle(R.string.action_pause_all);
                mDownloadManager.recoverAll();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
