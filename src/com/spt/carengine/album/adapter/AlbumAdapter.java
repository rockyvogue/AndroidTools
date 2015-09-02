/*
 * 文 件 名:  AlbumAdapter.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月11日
 */

package com.spt.carengine.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.album.entity.AlbumListInfo;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.utils.ScreenUtils;

import java.util.List;

/**
 * 相册主界面adapter
 * 
 * @author Heaven
 */
public class AlbumAdapter extends ArrayAdapter<AlbumListInfo> {
    private static final String TAG = "AlbumAdapter";
    private static final int ALBUM_ITEM_WIDTH = 234;
    private static final int ALBUM_ITEM_HEIGHT = 277;
    private LayoutInflater mInflater;

    public AlbumAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumListInfo info = getItem(position);
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_album, parent,
                    false);
            holder = new Holder();
            holder.first = (ImageView) convertView
                    .findViewById(R.id.first_image);
            holder.second = (ImageView) convertView
                    .findViewById(R.id.second_image);
            holder.third = (ImageView) convertView
                    .findViewById(R.id.third_image);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            int itemWidth = ScreenUtils.getRealWidthValue(getContext(),
                    ALBUM_ITEM_WIDTH);
            int itemHeight = ScreenUtils.getRealHeightValue(getContext(),
                    ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.title.setText(info.title);
        List<PhotoListInfo> subFilePaths = info.subFilePaths;
        int size = subFilePaths.size();
        String first = null;
        String second = null;
        String third = null;
        if (size >= 3) {
            first = subFilePaths.get(0).path;
            second = subFilePaths.get(1).path;
            third = subFilePaths.get(2).path;
        } else if (size == 2) {
            first = subFilePaths.get(0).path;
            second = subFilePaths.get(1).path;
            third = subFilePaths.get(1).path;
        } else if (size == 1) {
            first = subFilePaths.get(0).path;
            second = subFilePaths.get(0).path;
            third = subFilePaths.get(0).path;
        }
        String tag = (String) holder.first.getTag();
        if ((tag == null || !tag.equals(first)) && first != null
                && second != null && third != null) {
            holder.first.setImageResource(R.drawable.photo_temp);
            holder.second.setImageResource(R.drawable.photo_temp);
            holder.third.setImageResource(R.drawable.photo_temp);
            holder.first.setTag(first);
            ImageLoader.getInstance().displayImage("file://" + first,
                    holder.first);
            ImageLoader.getInstance().displayImage("file://" + second,
                    holder.second);
            ImageLoader.getInstance().displayImage("file://" + third,
                    holder.third);
        }
        return convertView;
    }

    private static class Holder {
        ImageView first;
        ImageView second;
        ImageView third;
        TextView title;
    }
}
