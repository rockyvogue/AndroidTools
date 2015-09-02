/*
 * 文 件 名:  DropDownMenuView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月19日
 */

package com.spt.carengine.view;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.spt.carengine.R;
import com.spt.carengine.utils.LogUtil;

/**
 * 下拉菜单
 * 
 * @author Heaven
 */
public class DropDownMenuView extends PercentRelativeLayout {
    private static final String TAG = "DropDownMenuView";
    private static final float CONTENT_VIEW_HEIGHT_PERCENT = 0.1852f;
    private SeekBar mVolumeSeekBar;
    private AudioManager mMgr;
    private int mMaxVolume;
    private View mContentLayout;
    private OnHideListener mOnHideListener;

    public interface OnHideListener {
        void onHide();
    }

    public DropDownMenuView(Context context) {
        super(context);
    }

    public DropDownMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDownMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVolumeSeekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        mContentLayout = (View) findViewById(R.id.content_layout);
        if (mVolumeSeekBar == null || mContentLayout == null) {
            throw new InflateException("Miss a child?");
        }
        mMgr = (AudioManager) getContext().getSystemService(
                Context.AUDIO_SERVICE);
        mMaxVolume = mMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar.setMax(mMaxVolume);
        mVolumeSeekBar.setProgress(getCurrVolume());
        mVolumeSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public void showView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int height = (int) (dm.heightPixels * CONTENT_VIEW_HEIGHT_PERCENT);
        LogUtil.d(TAG, "in showView,height:" + height);
        TranslateAnimation anim = new TranslateAnimation(0, 0, -height, 0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        mContentLayout.startAnimation(anim);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    private void hideView() {
        int height = mContentLayout.getHeight();
        LogUtil.d(TAG, "in hideView,height:" + height);
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -height);
        anim.setDuration(300);
        anim.setFillAfter(true);
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimate = false;
                if (mOnHideListener != null) {
                    mOnHideListener.onHide();
                }
            }
        });
        mContentLayout.startAnimation(anim);
        mContentLayout.setVisibility(View.INVISIBLE);
    }

    private boolean isAnimate = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isHideDialog(mContentLayout, ev) && !isAnimate) {
                hideView();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isHideDialog(View v, MotionEvent event) {
        if (v != null) {
            int[] l = {
                    0, 0
            };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private int getCurrVolume() {
        return mMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setOnHideListener(OnHideListener l) {
        mOnHideListener = l;
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            mMgr.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
}
