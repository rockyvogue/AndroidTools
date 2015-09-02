/*
 * 文 件 名:  MainMenuView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月5日
 */

package com.spt.carengine.view;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.recordvideo.SwitchSVMessage;

import de.greenrobot.event.EventBus;

/**
 * 主界面主菜单
 * 
 * @author 时汉文
 */
public class MainMenuView extends PercentRelativeLayout {
    private static final int TYPE_HOME = 1;
    private static final int TYPE_CAMERA = 2;
    private static final int TYPE_CAR = 3;
    private static final int TYPE_VOICE = 4;
    private ImageButton mLastView;
    private ImageButton mCar;
    private ImageButton mVoiceImg;
    private ImageButton mCameraImg;
    private ImageButton mHomeImg;
    private static SurfaceView mCarRecord;
    private RelativeLayout mCarRecordParent;
    public static int sfWidth;
    public static int sfHeight;

    public boolean isShowCarPic = false;

    private static int count = 0;

    private int mCurrType = 0;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recorder_img:// 录像影像
                    handleCar();
                    break;
                case R.id.voice_img:
                    handleVoice();
                    break;
                case R.id.camera_img:
                    handleCamera();
                    break;
                case R.id.home_img:
                    handleHome();
                    break;
                case R.id.recorder_preview:// 录像的SurfaceView
                    handleCarPreview();
                    break;
            }
        }
    };

    public MainMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainMenuView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCar = (ImageButton) findViewById(R.id.recorder_img);
        mVoiceImg = (ImageButton) findViewById(R.id.voice_img);
        mCameraImg = (ImageButton) findViewById(R.id.camera_img);
        mHomeImg = (ImageButton) findViewById(R.id.home_img);
        mCarRecord = (SurfaceView) findViewById(R.id.recorder_preview);

        /*
         * mCarRecord.getViewTreeObserver().addOnGlobalLayoutListener( new
         * OnGlobalLayoutListener() {
         * @Override public void onGlobalLayout() { sfWidth =
         * mCarRecord.getWidth(); sfHeight = mCarRecord.getHeight();
         * Toast.makeText(getContext(), "W:H" + "->" + sfWidth + ":" + sfHeight,
         * 0) .show(); } });
         */

        if (mCar == null || mVoiceImg == null || mCameraImg == null
                || mHomeImg == null || mCarRecord == null) {
            throw new InflateException("Miss a child?");
        }
        mVoiceImg.setOnClickListener(mOnClickListener);
        mCar.setOnClickListener(mOnClickListener);
        mCameraImg.setOnClickListener(mOnClickListener);
        mHomeImg.setOnClickListener(mOnClickListener);
        mCarRecord.setOnClickListener(mOnClickListener);
        mHomeImg.performClick();

        mCarRecord.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        sfWidth = mCarRecord.getWidth();
                        sfHeight = mCarRecord.getHeight();
                    }
                });

        EventBus.getDefault().register(this);
    }

    public static SurfaceView getSurfaceView() {
        return mCarRecord;
    }

    public void onEventMainThread(SwitchSVMessage message) {

        SurfaceView surfaceView = message.getSurfaceView();
        int messageType = message.getMessageType();
        switch (messageType) {
            case Constant.MODULE_TYPE_IMAGEVIEW:// 显示行车记录图标

                if (message.getSurfaceView() != null) {

                    // 切换SurfaceView
                    MyApplication
                            .getInstance()
                            .getBindService()
                            .getGameDisplay()
                            .changeView(message.getSurfaceView(),
                                    Constant.VIDEO_WIDTH,
                                    Constant.VIDEO_HEIGHT, 0, 0, 493, 714);
                }

                mCarRecord.setBackgroundResource(R.drawable.record_car_sf);
                break;
            case Constant.MODULE_TYPE_SURFACEVIEW:// 显示行车记录影像

                mCarRecord.setBackgroundColor(Color.TRANSPARENT);
                // 切换SurfaceView
                MyApplication
                        .getInstance()
                        .getBindService()
                        .getGameDisplay()
                        .changeView(mCarRecord, Constant.VIDEO_WIDTH,
                                Constant.VIDEO_HEIGHT, 0, 0, 200, 200);
                break;
            case Constant.BINDSERVICE_SUCCESS:

                break;

            default:
                break;
        }

    }

    private void handleCar() {

    }

    private void handleCarPreview() {

        isShowCarPic = !isShowCarPic;

        if (isShowCarPic) {
            EventBus.getDefault().post(Constant.MODULE_TYPE_CAR_RECORD);
        } else {
            EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
        }

    }

    private void handleVoice() {
        recoverView(mCurrType);
        mVoiceImg.setImageResource(R.drawable.left_voice_pressed);
        setBackgroundResource(R.drawable.left_voice_background);
        mCurrType = TYPE_VOICE;
        mLastView = mVoiceImg;
        EventBus.getDefault().post(Constant.MODULE_TYPE_VOICE);
    }

    private void handleCamera() {
        recoverView(mCurrType);
        setBackgroundResource(R.drawable.left_background);
        mCurrType = TYPE_CAMERA;
        mLastView = mCameraImg;
        EventBus.getDefault().post(Constant.MODULE_TYPE_CAMERA);
    }

    private void handleHome() {
        recoverView(mCurrType);
        setBackgroundResource(R.drawable.left_background);
        mCurrType = TYPE_HOME;
        mLastView = mHomeImg;
        EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
    }

    private void recoverView(int type) {
        if (mCarRecord.getVisibility() != View.VISIBLE) {
            mCarRecord.setVisibility(View.VISIBLE);
            mCar.setVisibility(View.INVISIBLE);
        }
        if (mLastView == null) {
            return;
        }
        switch (type) {
            case TYPE_VOICE:
                mLastView.setImageResource(R.drawable.left_voice_normal);
                break;
            case TYPE_CAR:
                mLastView.setImageResource(R.drawable.left_car_press);
                break;

        }
    }

    public SurfaceView getmCarRecord() {
        return mCarRecord;
    }

}
