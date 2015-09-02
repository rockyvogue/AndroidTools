/*
 * 文 件 名:  PhotoAdapter.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月17日
 */

package com.spt.carengine.album.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.photoview.PhotoView;

import java.util.List;

/**
 * <功能描述>
 * 
 * @author Administrator
 */
public class PhotoAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private List<PhotoListInfo> mList;

    public PhotoAdapter(Context context, List<PhotoListInfo> datas) {
        mInflater = LayoutInflater.from(context);
        mList = datas;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mInflater.inflate(R.layout.viewpager_item_photo, null);
        PhotoView view = (PhotoView) v.findViewById(R.id.photo);
        ImageLoader.getInstance().displayImage(
                "file://" + mList.get(position).path, view);
        container.addView(v, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        return v;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
