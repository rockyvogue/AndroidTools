
package com.spt.carengine.view.explore.navigate;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.spt.carengine.R;
import com.spt.carengine.activity.MainActivity;
import com.spt.carengine.fragment.MainContentFragment;
import com.spt.carengine.utils.PixelUtils;
import com.spt.carengine.view.ReturnBarView;

import java.util.ArrayList;

/**
 * @author Rocky
 * @Time 2015.8.5 10:49:54
 * @description Manager navigatebar's view and display file list view
 */
public class NavigateView {

    /**
     * Back button message id
     */
    public static final int HANDLER_MESSAGE_NAVBAR_BACK_COMMAND = 11;
    /**
     * push view message id
     */
    public static final int HANDLER_MESSAGE_NAVBAR_PUSH_NEWVIEW = 21;
    /**
     * pop view message id
     */
    public static final int HANDLER_MESSAGE_NAVBAR_POP_ALLVIEW = 31;

    public static final int ID_NAVIGATE_BAR_VIEW = 1;
    public static final int ID_SHOW_CONTENT_VIEW = 2;

    /**
     * Root view parameter, parent view;
     */
    protected RelativeLayout mParentView;
    /**
     * Navigate bar view
     */
    protected NavigateBarView mNavigateBarView = null;

    /**
     * Show Content View
     */
    protected NaviShowView mNaviShowView = null;

    /**
     * The handler of sending message
     */
    protected Handler mHandler = null;

    /**
     * Save NaviShowView view list
     */
    protected ArrayList<NaviShowView> mShowViews = new ArrayList<NaviShowView>();

    /*
     * 浏览文件的类型
     */
    public static enum ExploreType {
        Photo, Video, TraceRecord
    }

    private static ExploreType exploreType = ExploreType.TraceRecord;

    /**
     * Construct method
     * 
     * @param parentView root view
     * @param navBarView show navigate bar view
     * @param rootShowView show content view
     */
    public NavigateView(RelativeLayout parentView, NavigateBarView navBarView,
            NaviShowView rootShowView, ExploreType exploreType) {
        mParentView = parentView;
        this.exploreType = exploreType;

        // initialization Handler deal;
        initCommandHanlder();

        // initialization view xml value
        initChildContentAndLayoutViewInfo(navBarView);

        // add root showview
        initRootViewInfo(rootShowView);
    }

    /**
     * init interface view
     * 
     * @param naviBarView
     */
    protected void initChildContentAndLayoutViewInfo(NavigateBarView naviBarView) {
        // init all widget to null
        initAllViewDefaultValueInfo();

        // init view widget
        initChildViewContentInfo(naviBarView);

        // init view layout
        initChildViewLayoutInfo();
    }

    /**
     * Initialization all view default value to null
     */
    protected void initAllViewDefaultValueInfo() {
        // navigate bar view
        this.mNavigateBarView = null;
        // current show view
        this.mNaviShowView = null;
    }

    /**
     * init interface view widget
     * 
     * @param naviBarView
     */
    protected void initChildViewContentInfo(NavigateBarView naviBarView) {
        // init navigate bar view
        initNavigateBarContentInfo(naviBarView);

        // init show view
        initShowContentContentInfo();

    }

    /**
     * init navigate bar view
     * 
     * @param naviBarView
     */
    protected void initNavigateBarContentInfo(NavigateBarView naviBarView) {
        mNavigateBarView = naviBarView;
        mNavigateBarView.setId(ID_NAVIGATE_BAR_VIEW);
        mNavigateBarView.setGravity(Gravity.CENTER_VERTICAL);
        mParentView.addView(mNavigateBarView);
        // set handler deal
        mNavigateBarView.setCommandHandler(mHandler);
    }

    /**
     * initialization show view content
     */
    protected void initShowContentContentInfo() {
        // show center's view
        mNaviShowView = new NaviShowView(mParentView.getContext());
        mNaviShowView.setId(ID_SHOW_CONTENT_VIEW);
        mParentView.addView(mNaviShowView);
    }

    /**
     * init view layout
     */
    protected void initChildViewLayoutInfo() {
        // init current view layout
        initCurrentLayoutInfo();
    }

    /**
     * init current view layout
     */
    protected void initCurrentLayoutInfo() {
        // init navigatebar view layout
        RelativeLayout.LayoutParams navBarLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        navBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        WindowManager windowManager = (WindowManager) mParentView.getContext().getSystemService(mParentView.getContext().WINDOW_SERVICE);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        navBarLayoutParams.height = (int) (screenHeight*0.1638);
//        navBarLayoutParams.height = PixelUtils.dip2px(mParentView.getContext(),
//                70);
        mNavigateBarView.setLayoutParams(navBarLayoutParams);

        initNewViewLayoutInfo(mNaviShowView);
    }

    /**
     * init root view
     * 
     * @param rootShowView
     */
    protected void initRootViewInfo(NaviShowView rootShowView) {
        // add root show view to view list
        pushToNavigateView(rootShowView);
    }

    /****************************************** 消息事件处理 ********************************************/

    @SuppressLint("HandlerLeak")
    protected void initCommandHanlder() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                handlerMessageCommandHandler(msg);
                super.handleMessage(msg);
            }
        };
    }

    protected void handlerMessageCommandHandler(Message msg) {
        switch (msg.what) {
            case HANDLER_MESSAGE_NAVBAR_BACK_COMMAND:
                // Back button command
                popFromNavigateView();
                break;

            case HANDLER_MESSAGE_NAVBAR_PUSH_NEWVIEW:
                // Push a new view to NavigateView
                pushToNavigateView((NaviShowView) msg.obj);
                break;

            case HANDLER_MESSAGE_NAVBAR_POP_ALLVIEW:
                // remove all view
                popAllNavigateView();
                break;

            default:
                break;
        }
    }

    /**
     * pop view from navigateview
     */
    protected void popFromNavigateView() {
        // remove view
        int viewSize = mShowViews.size() - 1;

        if (viewSize > 0) {
            // Get removed view
            NaviShowView curShowView = mShowViews.remove(viewSize);
            clearViewAtMemory(curShowView);
            curShowView = null;

        } else {
            handlerMsgReturnHomeCommand();
            return;
        }
        // remove current view
        mShowViews.remove(mNaviShowView);
        // get last view
        mNaviShowView = getLastView();
        // add last view
        mParentView.addView(mNaviShowView);
        // request parentView focus
        mParentView.requestFocus();
    }

    /**
     * Get the last view of the navigateView
     * 
     * @return NaviShowView
     */
    private NaviShowView getLastView() {
        // show view
        NaviShowView naviShowView = null;

        int nViewSize = mShowViews.size();

        if (nViewSize > 0) {
            naviShowView = mShowViews.get(nViewSize - 1);

        } else {
            naviShowView = new NaviShowView(mParentView.getContext());
            naviShowView.setId(ID_SHOW_CONTENT_VIEW);
        }
        setNavigateViewLabel(naviShowView);
        return naviShowView;
    }

    private void setNavigateViewLabel(NaviShowView showView) {
        if (mShowViews != null && mNavigateBarView != null) {
            BaseFileListShowView fileListShowView = ((BaseFileListShowView) showView);
            if (mShowViews.size() == 1) {
                if (exploreType == ExploreType.Photo) {
                    mNavigateBarView.getmBackButtonLabelTv().setText(
                            R.string.home_photo);

                } else if (exploreType == ExploreType.Video) {
                    mNavigateBarView.getmBackButtonLabelTv().setText(
                            R.string.home_video);

                } else if (exploreType == ExploreType.TraceRecord) {
                    mNavigateBarView.getmBackButtonLabelTv().setText(
                            R.string.home_car_record);
                }

            } else {
                String currentFolderName = fileListShowView
                        .getFileListDataSourceHandle().getCurrentFolderName();
                mNavigateBarView.getmBackButtonLabelTv().setText(
                        currentFolderName);
            }
        }
    }

    /**
     * push view to navigateView
     * 
     * @param newView a new view
     */
    protected void pushToNavigateView(NaviShowView newView) {
        // remove view
        mParentView.removeView(mNaviShowView);
        // get focus
        mNaviShowView.requestFocus();
        // add a new view
        mNaviShowView = addNewView(newView);

        // display new view
        mParentView.addView(mNaviShowView);

        // request focus
        mParentView.requestFocus();
    }

    private NaviShowView addNewView(NaviShowView newView) {
        NaviShowView showView = null;
        // not exist , and insert newView to last's view
        if (!isExistView(newView)) {
            newView.setNavigateViewHandler(mHandler);

            // insert new show view
            initNewViewLayoutInfo(newView);
            mShowViews.add(newView);
        }

        showView = getLastView();

        setNavigateViewLabel(showView);

        return showView;
    }

    /**
     * Initialization layout information of new add view
     * 
     * @param newView
     */
    protected void initNewViewLayoutInfo(NaviShowView newView) {
        // init show content view layout
        RelativeLayout.LayoutParams showLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        showLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        showLayoutParams.addRule(RelativeLayout.ABOVE, ID_NAVIGATE_BAR_VIEW);
        newView.setLayoutParams(showLayoutParams);
    }

    /**
     * this view is exist?
     */
    private boolean isExistView(NaviShowView newView) {
        boolean isExist = false;
        // loop query whether newView's object is exist or not
        int size = mShowViews.size();
        if (size == 0)
            return isExist;
        if (newView == null)
            return isExist;

        for (int index = 0; index < size; index++) {
            if (newView == mShowViews.get(index)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * remove all navigateView
     */
    public void popAllNavigateView() {
        // loop remove view
        while ((mShowViews.size() - 1) > 0) {
            mShowViews.remove(mShowViews.size() - 1);
        }

        mParentView.removeView(mNaviShowView);
        // Get last show view
        mNaviShowView = getLastView();
        // the last add view
        mParentView.addView(mNaviShowView);
        // request focus
        mParentView.requestFocus();
    }

    /**
     * return Main Home View
     */
    protected void handlerMsgReturnHomeCommand() {
        MainContentFragment mainContent = new MainContentFragment();
        FragmentTransaction transaction = ((MainActivity) mParentView
                .getContext()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainContent);
        transaction.commit();
    }

    /*************************************** 清除内存方法 *****************************/

    public void clearViewAtMemory(NaviShowView showView) {
        showView.clearPropertyInfo();
    }

    /**
     * Clear property value
     */
    public void clearPropertyInfo() {
        if (mShowViews != null) {
            mShowViews.clear();
            mShowViews = null;
        }
//         if(mNavigateBar != null) {
//         mNavigateBar.clearPropertyInfo();
//         mNavigateBar = null;
//         }

    }

    /*************************************** Get / Set 方法 *****************************/

    public NaviShowView getmNaviShowView() {
        return mNaviShowView;
    }

    public void setmNaviShowView(NaviShowView mNaviShowView) {
        this.mNaviShowView = mNaviShowView;
    }

    public ArrayList<NaviShowView> getmShowViews() {
        return mShowViews;
    }

    public void setmShowViewArray(ArrayList<NaviShowView> mShowViews) {
        this.mShowViews = mShowViews;
    }

    public static ExploreType getExploreType() {
        return exploreType;
    }
}
