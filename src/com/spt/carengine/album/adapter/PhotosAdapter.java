/*
 * 文 件 名:  PhotosAdapter.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月13日
 */

package com.spt.carengine.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.album.view.PhotoItemView;
import com.spt.carengine.utils.ScreenUtils;

/**
 * <功能描述>
 * 
 * @author Heaven
 */
public class PhotosAdapter extends ArrayAdapter<PhotoListInfo> {
    private static final int PHOTO_ITEM_WIDTH = 234;
    private static final int PHOTO_ITEM_HEIGHT = 192;
    private LayoutInflater mInflater;
    private boolean isEditMode = false;

    public PhotosAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void changeEditMode(boolean flag) {
        isEditMode = flag;
    }

    public boolean getEditMode() {
        return isEditMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoListInfo info = getItem(position);
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_photo, parent,
                    false);
            holder = new Holder();
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.photoSelected = (ImageView) convertView
                    .findViewById(R.id.photo_selected);
            holder.photoSelectedIcon = (ImageView) convertView
                    .findViewById(R.id.photo_selected_icon);
            int itemWidth = ScreenUtils.getRealWidthValue(getContext(),
                    PHOTO_ITEM_WIDTH);
            int itemHeight = ScreenUtils.getRealHeightValue(getContext(),
                    PHOTO_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (convertView instanceof PhotoItemView) {
            ((PhotoItemView) convertView).setPhotoListInfo(info);
        }
        String tag = (String) holder.photo.getTag();
        String path = info.path;
        if (tag == null || !tag.equals(path)) {
            holder.photo.setImageResource(R.drawable.photo_temp);
            holder.photo.setTag(path);
            ImageLoader.getInstance().displayImage("file://" + path,
                    holder.photo);
        }
        if (isEditMode) {
            holder.photoSelectedIcon.setVisibility(View.VISIBLE);
            if (info.mode == PhotoItemView.MODE_SELECTED) {
                holder.photoSelectedIcon
                        .setImageResource(R.drawable.photo_pic_selected_icon);
                holder.photoSelected.setVisibility(View.VISIBLE);
            } else {
                holder.photoSelectedIcon
                        .setImageResource(R.drawable.photo_pic_unselected_icon);
                holder.photoSelected.setVisibility(View.GONE);
            }
        } else {
            holder.photoSelectedIcon
                    .setImageResource(R.drawable.photo_pic_unselected_icon);
            holder.photoSelectedIcon.setTag(PhotoItemView.MODE_UNSELECTED);
            holder.photoSelectedIcon.setVisibility(View.GONE);
            holder.photoSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class Holder {
        ImageView photo;
        ImageView photoSelected;
        ImageView photoSelectedIcon;
    }
}
