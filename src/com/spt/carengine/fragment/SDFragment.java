
package com.spt.carengine.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.recordvideo.FileUtils;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.utils.UtilTools;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SDFragment extends Fragment implements OnClickListener {
    private ArrayList<List<String>> mListGB;
    private ReturnBarView mReturnBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListGB = new ArrayList<List<String>>();
        if (UtilTools.hasSDCard()) {// 判断是否有SD卡存在
            String[] str = getAllScardPath();
            for (int i = 0; i < str.length; i++) {
                List<String> l = readSDCard(str[i]);
                if (null != l && l.size() > 0) {
                    mListGB.add(l);
                }
            }
        } else {
            File root = Environment.getRootDirectory();
            String path = root.getPath();
            if (null != path) {
                List<String> listSys = readSDCard(path);
                if (null != listSys && listSys.size() > 0) {
                    mListGB.add(listSys);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sd, container, false);
        initShowReturnBarView(view);
        ListView sd_listview = (ListView) view.findViewById(R.id.sd_listview);
        SDAdpter adpter = new SDAdpter(mListGB, getActivity());
        sd_listview.setDivider(null);
        sd_listview.setAdapter(adpter);
        return view;
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TPYE_SDCARD);
        mReturnBarView.setBackListener(this);
    }

    private String[] getAllScardPath() {
        String[] str = new String[2];
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (!columns[1].contains("mnt")) {// media_rw
                            if (columns[1].contains("sdcard0")) {
                                str[0] = columns[1];
                            } else {
                                str[1] = columns[1];
                            }
                        }
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (!columns[1].contains("mnt")) {// media_rw
                            if (columns[1].contains("sdcard0")) {
                                str[0] = columns[1];
                            } else {
                                str[1] = columns[1];
                            }
                        }
                    }
                }
            }
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /***
     * 计算存储空间大小，和可使用大小
     */
    private List<String> readSDCard(String path) {
        List<String> list = new ArrayList<String>();
        try {
            StatFs sf = new StatFs(path);
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            list.add(UtilTools.FormetFileSize("" + blockSize * blockCount));// /总容量
            list.add(UtilTools.FormetFileSize("" + availCount * blockSize));// 剩余容量
            list.add(UtilTools.FormetFileSize(""
                    + (blockSize * blockCount - availCount * blockSize)));// 已使用容量
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EventBus.getDefault()
                        .post(Constant.MODULE_TYPE_SYSTEM_SETTINGS);
                break;
            default:
                break;
        }
    }

    class SDAdpter extends BaseAdapter {
        private static final int ALBUM_ITEM_HEIGHT = 160;
        private ArrayList<List<String>> mList;
        private Context mContext;

        public SDAdpter(ArrayList<List<String>> list, Context context) {
            this.mList = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int p) {
            return mList.get(p);
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        @Override
        public View getView(int p, View view, ViewGroup parent) {
            Holder holder = null;
            if (view == null) {
                holder = new Holder();
                view = LayoutInflater.from(mContext).inflate(R.layout.list_sd,
                        null);
                holder.mTv_all_gb = (TextView) view
                        .findViewById(R.id.tv_all_gb);
                holder.mTv_used_gb = (TextView) view
                        .findViewById(R.id.tv_used_gb);
                holder.mTv_surplus_gb = (TextView) view
                        .findViewById(R.id.tv_surplus_gb);
                int itemHeight = ScreenUtils.getRealHeightValue(mContext,
                        ALBUM_ITEM_HEIGHT);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, itemHeight);
                view.setLayoutParams(param);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            List<String> list = mList.get(p);
            String str2 = mContext.getString(R.string.mTv_surplus_gb)
                    + list.get(1);
            String str1 = mContext.getString(R.string.mTv_used_gb)
                    + list.get(2);
            if (p == 0) {
                holder.mTv_all_gb.setText(mContext
                        .getString(R.string.mTv_all_gb) + list.get(0));
            } else {
                holder.mTv_all_gb.setText(mContext
                        .getString(R.string.mTv_allsd_gb) + list.get(0));
            }
            holder.mTv_used_gb.setText(str1);
            holder.mTv_surplus_gb.setText(str2);
            return view;
        }

        class Holder {
            TextView mTv_all_gb, mTv_used_gb, mTv_surplus_gb;
        }
    }
}
