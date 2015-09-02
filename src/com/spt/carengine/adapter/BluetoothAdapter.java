
package com.spt.carengine.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.utils.ScreenUtils;

import java.util.List;

@SuppressLint("InflateParams")
public class BluetoothAdapter extends BaseAdapter {
    private static final int ALBUM_ITEM_HEIGHT = 80;
    private List<String[]> mListBlue;
    private LayoutInflater mInflater;
    private Context mContext;
    public OnListViewOnItem mViewOnItem;
    private boolean mFlag;

    public interface OnListViewOnItem {
        void setListViewOnItemLisenter(int position);
    }

    public BluetoothAdapter(Context context, List<String[]> mListBlue,
            boolean flag) {
        this.mContext = context;
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context);

        }

        this.mListBlue = mListBlue;
        this.mFlag = flag;

    }

    public void setOnListViewOnItem(OnListViewOnItem o) {

        this.mViewOnItem = o;

    }

    @Override
    public int getCount() {
        if (mListBlue == null) {
            return 0;
        }

        return mListBlue.size();
    }

    @Override
    public Object getItem(int position) {

        return mListBlue.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_bluetooth, null);
            holder.mImg_blconnect = (ImageView) convertView
                    .findViewById(R.id.img_blconnect);
            holder.mImg_connect = (ImageView) convertView
                    .findViewById(R.id.img_connect);
            holder.mTv_phone = (TextView) convertView
                    .findViewById(R.id.tv_phone);
            int itemHeight = ScreenUtils.getRealHeightValue(mContext,
                    ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] strBlue = mListBlue.get(position);
        String devname = strBlue[2];
        if (null != devname && !"".equals(devname)) {
            holder.mTv_phone.setText(devname);
        }
        if (position == 0 && mFlag) {
            holder.mImg_blconnect.setVisibility(View.VISIBLE);
            holder.mImg_connect.setVisibility(View.VISIBLE);
        } else {
            holder.mImg_blconnect.setVisibility(View.GONE);
            holder.mImg_connect.setVisibility(View.GONE);
        }

        final int p = position;
        holder.mImg_blconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mViewOnItem != null) {
                    mViewOnItem.setListViewOnItemLisenter(p);
                }

            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView mImg_blconnect, mImg_connect;
        TextView mTv_phone;
    }

}
