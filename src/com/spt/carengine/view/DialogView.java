/*
 * 文 件 名:  DialogView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月13日
 */

package com.spt.carengine.view;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.widget.TextView;

import com.spt.carengine.R;

/**
 * 相册删除对话框
 * 
 * @author Heaven
 */
public class DialogView extends PercentRelativeLayout {
    private TextView mMsg;
    private TextView mOk;
    private TextView mCancel;
    private OnDialogListener mOnDialogListener;

    public interface OnDialogListener {
        public void onCloseDialog();

        public void onExecutive();
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ok:
                    if (mOnDialogListener != null) {
                        mOnDialogListener.onExecutive();
                    }
                    break;
                case R.id.cancel:
                    if (mOnDialogListener != null) {
                        mOnDialogListener.onCloseDialog();
                    }
                    break;
            }
        }
    };

    public DialogView(Context context) {
        super(context);
    }

    public DialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnDialogListener(OnDialogListener l) {
        mOnDialogListener = l;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMsg = (TextView) findViewById(R.id.message);
        mOk = (TextView) findViewById(R.id.ok);
        mCancel = (TextView) findViewById(R.id.cancel);
        if (mMsg == null || mOk == null || mCancel == null) {
            throw new InflateException("Miss a child?");
        }
        mOk.setOnClickListener(mOnClickListener);
        mCancel.setOnClickListener(mOnClickListener);
    }

    public void setMessage(Spanned text) {
        mMsg.setText(text);
    }

    public void setMessage(String text) {
        mMsg.setText(text);
    }
}
