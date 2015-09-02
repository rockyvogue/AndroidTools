/*
 * 文 件 名:  PhotoItemView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月13日
 */

package com.spt.carengine.album.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.widget.ImageView;

import com.spt.carengine.R;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.view.PercentRelativeLayout;

/**
 * <功能描述>
 * 
 * @author Heaven
 */
public class PhotoItemView extends PercentRelativeLayout {
    public static final int MODE_SELECTED = 1;
    public static final int MODE_UNSELECTED = 0;
    private ImageView mPhotoSelected;
    private ImageView mPhotoSelIcon;
    private PhotoListInfo mPhotoListInfo;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            handleEditModeItem();
        }
    };

    public PhotoItemView(Context context) {
        super(context);
    }

    public PhotoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotoSelected = (ImageView) findViewById(R.id.photo_selected);
        mPhotoSelIcon = (ImageView) findViewById(R.id.photo_selected_icon);
        if (mPhotoSelected == null || mPhotoSelIcon == null) {
            throw new InflateException("Miss a child?");
        }
        mPhotoSelIcon.setOnClickListener(mOnClickListener);
    }

    public void handleEditModeItem() {
        if (mPhotoListInfo == null) {
            return;
        }
        int mode = mPhotoListInfo.mode;
        if (mode == MODE_UNSELECTED) {
            mPhotoListInfo.mode = MODE_SELECTED;
            mPhotoSelected.setVisibility(View.VISIBLE);
            mPhotoSelIcon.setImageResource(R.drawable.photo_pic_selected_icon);
        } else {
            mPhotoListInfo.mode = MODE_UNSELECTED;
            mPhotoSelected.setVisibility(View.GONE);
            mPhotoSelIcon
                    .setImageResource(R.drawable.photo_pic_unselected_icon);
        }
    }

    public void setPhotoListInfo(PhotoListInfo info) {
        mPhotoListInfo = info;
    }
}
