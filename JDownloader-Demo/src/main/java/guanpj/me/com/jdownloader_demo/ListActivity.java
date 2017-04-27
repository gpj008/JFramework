package guanpj.me.com.jdownloader_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import guanpj.me.com.jdownloader.DataWatcher;
import guanpj.me.com.jdownloader.DownloadEntry;
import guanpj.me.com.jdownloader.DownloadManager;

/**
 * Created by Jie on 2017/4/24.
 */

public class ListActivity extends AppCompatActivity {

    private DownloadManager mDownloadManager;
    private ListView mAppList;
    private MyAdapter mAdapter;
    private List<DownloadEntry> mData;
    private DataWatcher mWatcher = new DataWatcher() {
        @Override
        protected void notifyUpdate(DownloadEntry object) {
            int index = mData.indexOf(object);
            if(index != -1) {
                DownloadEntry entry = mData.remove(index);
                mData.add(index, object);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mDownloadManager = DownloadManager.getInstance(this);
        mAppList = (ListView) findViewById(R.id.app_list);
        mData = new ArrayList<>();
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150723/de6fd89a346e304f66535b6d97907563/com.sina.weibo_2057.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150706/f67f98084d6c788a0f4593f588ea9dfc/com.taobao.taobao_121.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150720/789cd3f2facef6b27004d9f813599463/com.mfw.roadbook_147.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150810/10805820b9fbe1eeda52be289c682651/com.qihoo.vpnmaster_3019020.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150730/580642ffcae5fe8ca311c53bad35bcf2/com.taobao.trip_3001032.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150807/42ac3ad85a189125701e69ccff36ad7a/com.eg.android.AlipayGphone_78.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150813/9e775b5afb66feb960941cd8879af0b8/com.sankuai.meituan_291.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150706/5a9bec48b764a892df801424278a4285/com.mt.mtxx.mtxx_434.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150707/2ef5e16e0b8b3135aa714ad9b56b9a3d/com.happyelements.AndroidAnimal_25.apk"));
        mData.add(new DownloadEntry("http://shouji.360tpcdn.com/150716/aea8ca0e6617b0989d3dcce0bb9877d5/com.cmge.xianjian.a360_30.apk"));
        mAdapter = new MyAdapter();
        mAppList.setAdapter(mAdapter);
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

    private class MyAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(ListActivity.this).inflate(R.layout.activity_list_item, null);
                holder = new ViewHolder();
                holder.mTitleView = (TextView) convertView.findViewById(R.id.txt_title);
                holder.mDownloadBtn = (Button) convertView.findViewById(R.id.btn_download);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final DownloadEntry entry = mData.get(position);
            holder.mTitleView.setText(entry.name + " is " + entry.status + " "
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
            return mData.size();
        }

        @Override
        public DownloadEntry getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private static class ViewHolder {
        TextView mTitleView;
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
