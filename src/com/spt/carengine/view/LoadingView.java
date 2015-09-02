/*
 * 文 件 名:  LoadingView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月12日
 */

package com.spt.carengine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.carengine.R;

/**
 * 公用loading控件
 * 
 * @author Heaven
 */
public class LoadingView extends PercentRelativeLayout {
    private ImageView mLoading;
    private TextView mPrompt;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLoading = (ImageView) findViewById(R.id.loading);
        mPrompt = (TextView) findViewById(R.id.loading_prompt);
        if (mPrompt == null || mLoading == null) {
            throw new InflateException("Miss a child?");
        }

    }

    public void setTextShowMsg(String text) {
        if ("".equals(text) || null == text) {
            return;
        }
        mPrompt.setText(text);

    }

    private void startRotate() {
        Animation anim = AnimationUtils.loadAnimation(getContext(),
                R.anim.loading);
        LinearInterpolator lir = new LinearInterpolator();
        anim.setInterpolator(lir);
        mLoading.startAnimation(anim);
    }

    private void stopRotate() {
        mLoading.clearAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRotate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }
}
