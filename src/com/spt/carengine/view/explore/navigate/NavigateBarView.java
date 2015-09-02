
package com.spt.carengine.view.explore.navigate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.utils.UtilTools;
import com.spt.carengine.view.PercentFrameLayout;
import com.spt.carengine.view.ReturnBarView;

/**
 * @author Rocky
 * @Time 2015.8.5 11:30:23
 * @description The navigatebar view of the bottom
 */
public class NavigateBarView extends RelativeLayout implements OnClickListener {

    public NavigateBarView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    protected Activity mContext;

    protected Handler mCommandHandler;

    protected RelativeLayout mLeftLayout;
    protected PercentFrameLayout mVedioLayout;
    private ReturnBarView mReturnBarView;
    protected ImageButton mLeftLayoutBack;
    /**
     * Back button labe textview , In back button right side
     */
    protected TextView mBackButtonLabelTv;

    public NavigateBarView(Activity context) {
        super(context);
        this.mContext = context;
        initChildViewContentInfo();

        bindOnClickListener();
    }

    /**
     * Initializtion view widget
     */
    protected void initChildViewContentInfo() {
        initLeftContextInfo();
    }

    /**
     * Initialization left side back button
     */
    protected void initLeftContextInfo() {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLeftLayout = (RelativeLayout) mLayoutInflater.inflate(
                R.layout.mybackbtn_layout, null);
        mReturnBarView = (ReturnBarView) mLeftLayout
                .findViewById(R.id.vedio_return_bar);
        mReturnBarView.init(ReturnBarView.TYPE_VEDIO);
        mBackButtonLabelTv = (TextView) mReturnBarView.findViewById(R.id.title);
        mLeftLayoutBack = (ImageButton) mLeftLayout.findViewById(R.id.back);

        this.addView(mLeftLayout);
        this.setBackgroundResource(R.color.return_bar_bg);
    }

    /**
     * Bind Listener event
     */
    protected void bindOnClickListener() {
        // mLeftLayout.setOnClickListener(this);
        mLeftLayoutBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                mCommandHandler
                        .sendEmptyMessage(NavigateView.HANDLER_MESSAGE_NAVBAR_BACK_COMMAND);
                break;

            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        UtilTools.hideSoftKeyboard(getContext(), getWindowToken());
        return super.onTouchEvent(arg0);
    }

    /*************************************** 清除内存方法 *****************************/
    /**
     * 清空属性值
     */

    protected void clearPropertyInfo() {
        mContext = null;
        mCommandHandler = null;
        // mLeftLayout = null;
        mReturnBarView = null;
    }

    /*************************************** Set 与 Get 方法 *****************************/

    public void setCommandHandler(Handler handler) {
        this.mCommandHandler = handler;
    }

    /**
     * Back button layout view
     * 
     * @return
     */
    public RelativeLayout getmLeftLayout() {
        return mLeftLayout;
        // return mReturnBarView ;
    }

    public TextView getmBackButtonLabelTv() {
        return mBackButtonLabelTv;
    }
}
