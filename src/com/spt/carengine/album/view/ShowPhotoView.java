/*
 * 文 件 名:  ShowPhotoView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月14日
 */

package com.spt.carengine.album.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

import com.spt.carengine.R;
import com.spt.carengine.album.adapter.PhotoAdapter;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 照片展示，可以放大
 * 
 * @author Heaven
 */
public class ShowPhotoView extends PercentRelativeLayout {
    private static final String TAG = "ShowPhotoView";
    private ReturnBarView mReturnBarView;
    private ViewPager mViewPager;
    private PhotoAdapter mAdapter;
    private List<PhotoListInfo> mList;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.delete_photo:
                    int pos = mViewPager.getCurrentItem();
                    if (mList != null && mList.size() > pos) {
                        PhotoListInfo info = mList.get(pos);
                        File file = new File(info.path);
                        if (file.exists()) {
                            file.delete();
                        }
                        mList.remove(pos);
                    }
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    LogUtil.d(TAG, "delete photo success");
                    break;
            }

        }
    };

    public ShowPhotoView(Context context) {
        super(context);
    }

    public ShowPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        mViewPager = (ViewPager) findViewById(R.id.photo_viewpager);
        if (mReturnBarView == null || mViewPager == null) {
            throw new InflateException("Miss a child?");
        }
        mReturnBarView.init(ReturnBarView.TYPE_PHOTO);
    }

    public void setListener(OnClickListener l) {
        mReturnBarView.setBackListener(l);
        mReturnBarView.setListener(mOnClickListener);
    }

    public void showPhoto(PhotoListInfo info, int pos, List<PhotoListInfo> all) {
        mList = new ArrayList<PhotoListInfo>(all);
        setTag(info);
        mAdapter = new PhotoAdapter(getContext(), mList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(pos);
    }

    public void reset() {
        mList.clear();
    }
}
