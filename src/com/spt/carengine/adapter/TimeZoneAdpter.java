
package com.spt.carengine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.spt.carengine.R;
import com.spt.carengine.db.bean.TimeZoneCity;
import com.spt.carengine.utils.ScreenUtils;

import java.util.List;

public class TimeZoneAdpter extends BaseAdapter {
    private LayoutInflater mInflater;
    private static final int ALBUM_ITEM_WIDTH = 140;
    private static final int ALBUM_ITEM_HEIGHT = 60;
    private Context mContext;
    private List<TimeZoneCity> mList;
    private String mCityName;

    public TimeZoneAdpter(Context context, List<TimeZoneCity> list) {
        mInflater = LayoutInflater.from(context);
        this.mList = list;
        this.mContext = context;
        // this.mCityName = name;
    }

    public void setTimeZoneCity(String name) {
        this.mCityName = name;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_timezone,
                    parent, false);
            holder = new Holder();
            holder.tv_timedate = (TextView) convertView
                    .findViewById(R.id.tv_timedate);

            int itemWidth = ScreenUtils.getRealWidthValue(mContext,
                    ALBUM_ITEM_WIDTH);
            int itemHeight = ScreenUtils.getRealHeightValue(mContext,
                    ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }
        TimeZoneCity city = mList.get(position);
        String name = city.mCityName;
        if (null != name && !"".equals(name)) {
            holder.tv_timedate.setText(name);
            if (mCityName.equals(name)) {
                holder.tv_timedate
                        .setBackgroundResource(R.drawable.time_but_selected);
            } else {
                holder.tv_timedate
                        .setBackgroundResource(R.drawable.select_btn_date_save);
            }
        }
        return convertView;
    }

    private static class Holder {
        TextView tv_timedate;
    }
}
