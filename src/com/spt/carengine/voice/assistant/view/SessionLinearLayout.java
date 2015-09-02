/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : SessionLinearLayout.java
 * @ProjectName : CarPlayNew
 * @PakageName : cn.yunzhisheng.carplay.view
 * @Author : Brant
 * @CreateDate : 2014-9-30
 */
package com.spt.carengine.voice.assistant.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 
 * <功能描述>会话窗口的根视图View
 * @author  Administrator
 */
public class SessionLinearLayout extends LinearLayout {
    
	public static final String TAG = "SessionLinearLayout";
	private DispatchKeyEventListener mDispatchKeyEventListener;
	private OnTouchEventListener mOnTouchEventListener;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SessionLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SessionLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SessionLinearLayout(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mDispatchKeyEventListener != null) {
			return mDispatchKeyEventListener.dispatchKeyEvent(event);
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mOnTouchEventListener != null) {
			return mOnTouchEventListener.onTouchEvent(event);
		}
		
		return super.onTouchEvent(event);
	}

	public DispatchKeyEventListener getDispatchKeyEventListener() {
		return mDispatchKeyEventListener;
	}

	public void setDispatchKeyEventListener(DispatchKeyEventListener mDispatchKeyEventListener) {
		this.mDispatchKeyEventListener = mDispatchKeyEventListener;
	}

	/**
	 * 
	 * <功能描述> 按键分派事件的监听器
	 * @author  Administrator
	 */
	public static interface DispatchKeyEventListener {
		boolean dispatchKeyEvent(KeyEvent event);
	}
	
	public OnTouchEventListener getOnTouchEventListener() {
		return mOnTouchEventListener;
	}

	public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener) {
		this.mOnTouchEventListener = onTouchEventListener;
	}
	/**
	 * 
	 * <功能描述>触摸事件的监听器
	 * @author  Administrator
	 */
	public static interface OnTouchEventListener {
		boolean onTouchEvent(MotionEvent event);
	}
}
