
package com.spt.carengine.voice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.spt.carengine.view.PercentRelativeLayout;

/**
 * <功能描述> 语音模块的根视图
 * 
 * @author Administrator
 */
public class VoiceSessionRelativeLayout extends PercentRelativeLayout {
    public static final String TAG = "SessionLinearLayout";

    private DispatchKeyEventListener mDispatchKeyEventListener;

    private OnTouchEventListener mOnTouchEventListener;

    public VoiceSessionRelativeLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public VoiceSessionRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceSessionRelativeLayout(Context context) {
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

    public void setDispatchKeyEventListener(
            DispatchKeyEventListener mDispatchKeyEventListener) {
        this.mDispatchKeyEventListener = mDispatchKeyEventListener;
    }

    /**
     * <功能描述> 按键分派事件的监听器
     * 
     * @author Administrator
     */
    public static interface DispatchKeyEventListener {
        boolean dispatchKeyEvent(KeyEvent event);
    }

    public OnTouchEventListener getOnTouchEventListener() {
        return mOnTouchEventListener;
    }

    public void setOnTouchEventListener(
            OnTouchEventListener onTouchEventListener) {
        this.mOnTouchEventListener = onTouchEventListener;
    }

    /**
     * <功能描述>触摸事件的监听器
     * 
     * @author Administrator
     */
    public static interface OnTouchEventListener {
        boolean onTouchEvent(MotionEvent event);
    }
}
