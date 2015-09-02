
package com.spt.carengine.view.explore.navigate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.carengine.utils.PixelUtils;
import com.spt.carengine.utils.UtilTools;

/**
 * @author Rocky
 * @Time 2015.8.5 10:56:19
 * @description The tool bar of file list,and display file path
 */
public class BaseFileListToolBarView extends RelativeLayout {

    /**
     * Left side layout's ID
     */
    protected static final int ID_TOOL_BAR_LEFT_LAYOUT = 1;

    /**
     * Left side TextView's ID
     */
    protected static final int ID_TOOL_BAR_LEFT_PATH_TEXTVIEW = 2;

    protected Context mContext;

    protected Handler mCommandHandle;

    /**
     * Left side's RelativeLayout
     */
    protected RelativeLayout mLeftLayout;

    /**
     * Left side's show file path TextView
     */
    protected TextView mPathTextView;

    public BaseFileListToolBarView(Context context) {
        super(context);
        mContext = context;
        initLayoutViewAndChildViewContentInfo();
        this.setVisibility(View.GONE);
    }

    /**
     * initialization View value
     */
    protected void initLayoutViewAndChildViewContentInfo() {

        initAllViewDefaultValueInfo();

        initChildViewContentInfo();

        initChildViewLayoutInfo();
    }

    /**
     * Set all widget default value to null
     */
    protected void initAllViewDefaultValueInfo() {
        this.mLeftLayout = null;
        this.mPathTextView = null;
    }

    /**
     * initialization interface view widget
     */
    protected void initChildViewContentInfo() {
        initLeftChileViewContentInfo();
    }

    protected void initLeftChileViewContentInfo() {
        // left side
        mLeftLayout = new RelativeLayout(mContext);
        mLeftLayout.setId(ID_TOOL_BAR_LEFT_LAYOUT);
        mLeftLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(mLeftLayout);

        mPathTextView = new TextView(mContext);
        mPathTextView.setId(ID_TOOL_BAR_LEFT_PATH_TEXTVIEW);
        mPathTextView.setSingleLine(true);
        mPathTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mPathTextView.setText("/sdcard/Movies");
        mLeftLayout.addView(mPathTextView);
    }

    /**
     * initialization view layout
     */
    protected void initChildViewLayoutInfo() {
        initCurrentLayoutInfo();

        initLeftLayoutInfo();
    }

    /**
     * Initialization current view layout
     */
    protected void initCurrentLayoutInfo() {
        RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mLeftLayout.setLayoutParams(leftLayoutParams);
    }

    /**
     * Initialization left view layout
     */
    protected void initLeftLayoutInfo() {
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        textLayoutParams.leftMargin = PixelUtils.dip2px(mContext, 10);
        mPathTextView.setLayoutParams(textLayoutParams);
    }

    /*************************************** 清除内存方法 *****************************/
    /**
     * 清空属性值
     */
    public void clearPropertyInfo() {
        this.mContext = null;
        this.mPathTextView = null;
        this.mLeftLayout = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        UtilTools.hideSoftKeyboard(getContext(), getWindowToken());
        return super.onTouchEvent(event);
    }

    /*************************************** Get / Set 方法 *****************************/
    public TextView getPathTextView() {
        return mPathTextView;
    }

    public Handler getCommandHandle() {
        return mCommandHandle;
    }

    public void setCommandHandle(Handler mCommandHandle) {
        this.mCommandHandle = mCommandHandle;
    }
}
