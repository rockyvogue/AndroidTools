
package com.spt.carengine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.utils.ScreenUtils;

public class WifiPopupWindowAdapter extends BaseAdapter {
    private static final int ALBUM_ITEM_WIDTH = 154;
    private static final int ALBUM_ITEM_HEIGHT = 60;
    private int[] mTitle;
    private LayoutInflater mInflater;

    public WifiPopupWindowAdapter(Context context, int[] mTitle) {
        super();
        this.mTitle = mTitle;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return mTitle.length;
    }

    @Override
    public Object getItem(int arg0) {

        return mTitle[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.wifi_popupwindow_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.titleName = (TextView) convertView
                    .findViewById(R.id.wifi_popup_name);

            viewHolder.view = convertView
                    .findViewById(R.id.wifi_underline_view);
            int itemWidth = ScreenUtils.getRealWidthValue(parent.getContext(),
                    ALBUM_ITEM_WIDTH);
            int itemHeight = ScreenUtils.getRealHeightValue(
                    parent.getContext(), ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.titleName.setText(mTitle[position]);

        return convertView;

    }

    private static class ViewHolder {
        TextView titleName;
        View view;
    }
}
