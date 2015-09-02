
package com.spt.carengine.view.explore.navigate;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Rocky
 * @Time 2015年8月5日 上午10:52:16
 * @description 装入内容的View. 显示内容，一个方法加入一个新的View, 一个方法remove顶层的View
 */
public class NaviShowView extends RelativeLayout {

    protected Handler mNavigateViewHandler = null;

    public NaviShowView(Context context) {
        super(context);
    }

    public NaviShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * push view to navigateView class
     * 
     * @param newView
     */
    public void pushViewToNavigateView(NaviShowView newView) {
        Message message = mNavigateViewHandler.obtainMessage(
                NavigateView.HANDLER_MESSAGE_NAVBAR_PUSH_NEWVIEW, newView);
        mNavigateViewHandler.sendMessage(message);
    }

    /**
     * remove a view
     */
    public void popAllNavigateView() {
        Message message = mNavigateViewHandler
                .obtainMessage(NavigateView.HANDLER_MESSAGE_NAVBAR_POP_ALLVIEW);
        mNavigateViewHandler.sendMessage(message);
    }

    /*************************************** 清除内存方法 *****************************/
    /**
     * 清空属性值
     */
    protected void clearPropertyInfo() {
        mNavigateViewHandler = null;
    }

    /*************************************** Get / Set 方法 *****************************/
    public Handler getNavigateViewHandler() {
        return mNavigateViewHandler;
    }

    public void setNavigateViewHandler(Handler mNavigateViewHandler) {
        this.mNavigateViewHandler = mNavigateViewHandler;
    }

}
