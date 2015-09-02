/*
 * 文 件 名:  MainContentFragment.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月5日
 */

package com.spt.carengine.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;

import de.greenrobot.event.EventBus;

/**
 * 主界面
 * 
 * @author 时汉文
 */
public class MainContentFragment extends Fragment {
    private ImageButton mHomeNav;
    private ImageButton mHomePhoto;
    private ImageButton mHomeVideo;
    private ImageButton mHomeAmusement;
    private ImageButton mHomeUser;
    private ImageButton mHomeCarRecord;
    private ImageButton mHomeNumber;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_amusement:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_AMUSEMENT);
                    break;
                case R.id.home_car_record:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_CAR_RECORD);
                    break;
                case R.id.home_nav:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_NAV);
                    break;
                case R.id.home_number:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_NUMBER);
                    break;
                case R.id.home_photo:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_PHOTO);
                    break;
                case R.id.home_user:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
                    break;
                case R.id.home_video:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_VIDEO);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_home, null);
        initView(v);
        return v;
    }

    private void initView(View root) {
        mHomeNav = (ImageButton) root.findViewById(R.id.home_nav);
        mHomePhoto = (ImageButton) root.findViewById(R.id.home_photo);
        mHomeVideo = (ImageButton) root.findViewById(R.id.home_video);
        mHomeAmusement = (ImageButton) root.findViewById(R.id.home_amusement);
        mHomeUser = (ImageButton) root.findViewById(R.id.home_user);
        mHomeCarRecord = (ImageButton) root.findViewById(R.id.home_car_record);
        mHomeNumber = (ImageButton) root.findViewById(R.id.home_number);
        if (mHomeNav == null || mHomePhoto == null || mHomeVideo == null
                || mHomeAmusement == null || mHomeUser == null
                || mHomeCarRecord == null || mHomeNumber == null) {
            throw new InflateException("Miss a child?");
        }
        mHomeNav.setOnClickListener(mOnClickListener);
        mHomePhoto.setOnClickListener(mOnClickListener);
        mHomeVideo.setOnClickListener(mOnClickListener);
        mHomeAmusement.setOnClickListener(mOnClickListener);
        mHomeUser.setOnClickListener(mOnClickListener);
        mHomeCarRecord.setOnClickListener(mOnClickListener);
        mHomeNumber.setOnClickListener(mOnClickListener);
        mHomePhoto.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent("android.settings.SETTINGS");
                getActivity().startActivity(intent);
                return true;
            }
        });
    }
}
