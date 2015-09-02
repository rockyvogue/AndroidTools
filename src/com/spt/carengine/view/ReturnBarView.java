/*
 * 文 件 名:  ReturnBarView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月17日
 */

package com.spt.carengine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.carengine.R;

/**
 * 返回条
 * 
 * @author Heaven
 */
public class ReturnBarView extends PercentRelativeLayout {
    public static final int TYPE_ALBUM = 1;
    public static final int TYPE_PHOTOS = 2;
    public static final int TYPE_PHOTO = 3;
    public static final int TYPE_CLOUDS = 4;
    public static final int TYPE_BLUETOOTH = 5;
    public static final int TYPE_USER = 6;
    public static final int TYPE_FM = 7;
    public static final int TYPE_SYSTEM_SETTINGS = 8;
    public static final int TYPE_WIFI = 9;
    public static final int TPYE_SDCARD = 10;
    public static final int TYPE_TRAFFIC = 11;
    public static final int TYPE_VEDIO = 12;
    public static final int TYPE_DATE = 13;
    public static final int TYPE_TIMEDATE = 14;
    public static final int TYPE_WIFI_CONNECT = 20;
    public static final int TPYE_ABOUT = 15;
    private ImageButton mBack;
    private View mBackLayout;
    private ImageButton mDelete;
    private TextView mTitle;
    private TextView mEdit;
    private CheckBox mCkeckBoxSwitch;
    private int mType;
    private Button mBtUpdate;
    private ImageView wifi_more;
    private ImageView wifi_refresh;

    public ReturnBarView(Context context) {
        super(context);
    }

    public ReturnBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReturnBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBack = (ImageButton) findViewById(R.id.back);
        mEdit = (TextView) findViewById(R.id.edit);
        mBackLayout = (View) findViewById(R.id.back_layout);
        mDelete = (ImageButton) findViewById(R.id.delete_photo);
        mTitle = (TextView) findViewById(R.id.title);
        mCkeckBoxSwitch = (CheckBox) findViewById(R.id.ck_clouds_dog_switch);
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        wifi_more = (ImageView) findViewById(R.id.wifi_more);
        wifi_refresh = (ImageView) findViewById(R.id.wifi_refresh);

        if (mBack == null || mTitle == null || mEdit == null || mDelete == null
                || mBackLayout == null || mCkeckBoxSwitch == null
                || mBtUpdate == null || wifi_more == null
                || wifi_refresh == null) {
            throw new InflateException("Miss a child?");
        }
    }

    public void init(int type) {
        mType = type;
        switch (type) {
            case TYPE_ALBUM:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mTitle.setText(R.string.album_title);
                break;
            case TYPE_PHOTOS:
                showEdit(true);
                break;
            case TYPE_PHOTO:
                mEdit.setVisibility(View.GONE);
                mTitle.setText(R.string.photo_list);
                break;
            case TYPE_CLOUDS:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.user_list_item_cloud_dog);
                break;
            case TYPE_BLUETOOTH:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.user_list_item_bluetooth);
                break;
            case TYPE_USER:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.home_user);
                break;
            case TYPE_FM:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.user_list_item_fm);
                break;
            case TYPE_SYSTEM_SETTINGS:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.user_list_item_system_settings);
                break;
            case TYPE_WIFI:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                wifi_more.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.VISIBLE);
                wifi_refresh.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.user_system_settings_wifi);
                break;
            case TPYE_SDCARD:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.mTv_sdcard);
                break;
            case TYPE_TRAFFIC:
                mEdit.setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.traffic_title);
                mEdit.setText(R.string.traffic_right_text);
                break;
            case TYPE_VEDIO:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.home_video);
                break;
            case TYPE_DATE:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mBtUpdate.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.tv_datetitle);
                break;
            case TYPE_TIMEDATE:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mBtUpdate.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.tv_time_date);
                break;
            case TYPE_WIFI_CONNECT:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                wifi_more.setVisibility(View.VISIBLE);
                mCkeckBoxSwitch.setVisibility(View.VISIBLE);
                wifi_refresh.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.wifi_connect_wlan);
                break;
            case TPYE_ABOUT:
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
                mCkeckBoxSwitch.setVisibility(View.GONE);
                mTitle.setText(R.string.tv_about);
                break;
        }
    }

    public void showEdit(boolean show) {
        if (show) {
            mEdit.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.GONE);
        } else {
            mEdit.setVisibility(View.GONE);
            mDelete.setVisibility(View.VISIBLE);
        }
    }

    public void setBackListener(final OnClickListener l) {
        mBack.setOnClickListener(l);
        mBackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mBack.performClick();
            }
        });

        mBackLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case KeyEvent.ACTION_DOWN:
                        mBack.setImageResource(R.drawable.back_pressed);
                        break;
                    case KeyEvent.ACTION_UP:
                        mBack.setImageResource(R.drawable.back);
                        break;
                }
                return false;
            }
        });
    }

    public void setcheckBoxState(boolean bcheck) {
        mCkeckBoxSwitch.setChecked(bcheck);
    }

    public void setOncheckBoxListener(
            CompoundButton.OnCheckedChangeListener cl, final OnClickListener l) {
        mCkeckBoxSwitch.setOnClickListener(l);
        mCkeckBoxSwitch.setOnCheckedChangeListener(cl);
    }

    public void setListener(OnClickListener l) {
        switch (mType) {
            case TYPE_PHOTOS:
                mEdit.setOnClickListener(l);
                mDelete.setOnClickListener(l);
                break;
            case TYPE_PHOTO:
                mDelete.setOnClickListener(l);
                break;
            case TYPE_TRAFFIC:
                mEdit.setOnClickListener(l);
            case TYPE_DATE:
            case TYPE_TIMEDATE:
                mBtUpdate.setOnClickListener(l);
                break;
            case TYPE_WIFI:
                wifi_more.setOnClickListener(l);
                wifi_refresh.setOnClickListener(l);
                break;

        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
