
package com.spt.carengine.view.explore.navigate;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;

import com.spt.carengine.R;
import com.spt.carengine.activity.MainActivity;
import com.spt.carengine.fragment.PlayVideoFragment;
import com.spt.carengine.utils.PixelUtils;
import com.spt.carengine.view.explore.data.AbstractFile;
import com.spt.carengine.view.explore.data.FileFolder;
import com.spt.carengine.view.explore.data.FileListDataSourceHandle;
import com.spt.carengine.view.explore.data.IDataSourceHandleCallBack;
import com.spt.carengine.view.explore.data.SingleFile;

/**
 * @author Rocky
 * @Time 2015.8.5 10:53:38
 * @description Include a file operation tool bar and a show concret content
 *              view
 */
public class BaseFileListShowView extends NaviShowView {

    public static final int ID_TOOL_BAR_LAYOUT = 1;
    public static final int ID_SHOW_VIEW_LAYOU = 2;

    protected Context mContext;

    protected Handler mHandler;

    /** tool bar view layout */
    protected BaseFileListToolBarView mToolbarLayout;
    /** show view layout */
    protected BaseFileListShowContentView mShowContentViewLayout;
    /** get data object */
    protected FileListDataSourceHandle fileListDataSourceHandle;

    /** The interface of th callback data */
    protected IDataSourceHandleCallBack iDataSourceHandleCallBack;

    /*************************************** 构造函数 *****************************************************************/
    public BaseFileListShowView(Context context) {
        super(context);
        this.mContext = context;

        initRecallDataInterfaces();

        initHandlerCommand();

        initDataSourceValue();
    }

    /**
     * init request data callback interface
     */
    protected void initRecallDataInterfaces() {
        iDataSourceHandleCallBack = new IDataSourceHandleCallBack() {
            @Override
            public void finishAcceptDataHandle(int result) {
                // TODO Auto-generated method stub
                mHandler.sendEmptyMessage(result);
            }
        };
    }

    protected void initDataSourceValue() {
        fileListDataSourceHandle = new FileListDataSourceHandle(
                iDataSourceHandleCallBack);
    }

    /*************************************** Handler处理消息机制的处理 *****************************************************************/
    protected void initHandlerCommand() {

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
            case IDataSourceHandleCallBack.RESULT_SUCCESS:
                // TODO
                fileListDataSourceHandle.updateArrayListFiles();
                mShowContentViewLayout.updateAllDataInfo();
                break;

            case IDataSourceHandleCallBack.RESULT_FAIL:
                // TODO
                break;
            // 打开文件节点，即文件夹
            case BaseFileListShowContentView.HANDLER_MESSAGE_FILESHOW_OPEN_FILENODE:
                handlerMessageForOpenFileNode(msg.arg1, (AbstractFile) msg.obj);
                break;
            default:
                break;
        }
    }

    private void handlerMessageForOpenFileNode(int arg1, AbstractFile iFile) {
        if (iFile == null)
            return;
        if (iFile instanceof SingleFile) {
            openChildFileCommandHandle(arg1, iFile);

        } else if (iFile instanceof FileFolder) {
            openChildFolderCommandHandle(iFile);
        }
    }

    private void openChildFolderCommandHandle(AbstractFile iFile) {
        BaseFileListShowView fileListShowView = new BaseFileListShowView(
                mContext);
        BaseFileListToolBarView fileListToolBarView = new BaseFileListToolBarView(
                mContext);
        BaseFileListShowContentView fileListShowContentView = new BaseFileListShowContentView(
                fileListShowView,
                fileListShowView.getFileListDataSourceHandle());
        fileListShowView.getFileListDataSourceHandle().setCurrentPath(
                iFile.getFilePath());
        fileListShowView.setShowViewLayout(fileListToolBarView,
                fileListShowContentView);
        this.pushViewToNavigateView(fileListShowView);

        fileListShowView.getFileListDataSourceHandle().requestTrackRecordFiles(
                iFile.getFilePath());
    }

    private void openChildFileCommandHandle(int arg1, AbstractFile iFile) {
        // Toast.makeText(mContext, "打开" + iFile.getFileName()+ "文件",
        // Toast.LENGTH_SHORT).show();
        PlayVideoFragment playVideoFragment = new PlayVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableList("data",
                fileListDataSourceHandle.getArryListIFiles());
        bundle.putInt("index", arg1);
        bundle.putSerializable("exploreType", NavigateView.getExploreType());
        playVideoFragment.setArguments(bundle);

        FragmentTransaction transaction = ((MainActivity) mContext)
                .getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, playVideoFragment);
        transaction.commit();
    }

    /*************************************** 初始化处理 *********************************************************************************/
    /**
     * Set view to navigateview
     * 
     * @param toolbarLayout
     * @param showViewLayout
     */
    public void setShowViewLayout(BaseFileListToolBarView toolbarLayout,
            BaseFileListShowContentView showViewLayout) {
        // init child view
        initChildContentAndLayoutViewInfo(toolbarLayout, showViewLayout);

        // init current view layout
        initCurrentLayoutInfo();
    }

    protected void initChildContentAndLayoutViewInfo(
            BaseFileListToolBarView toolBarView,
            BaseFileListShowContentView showContentView) {
        initAllViewDefaultValueInfo();

        // init all widget
        initChildViewContentInfo(toolBarView, showContentView);

        // init current view layout
        initCurrentLayoutInfo();

    }

    /**
     * init all widget value
     */
    protected void initAllViewDefaultValueInfo() {
        this.mToolbarLayout = null;
        this.mShowContentViewLayout = null;
    }

    protected void initChildViewContentInfo(
            BaseFileListToolBarView toolBarView,
            BaseFileListShowContentView showContentView) {
        initToolBarContentInfo(toolBarView);

        initShowViewContentInfo(showContentView);
    }

    /**
     * init toolbar control information
     * 
     * @param toolBarView
     */
    protected void initToolBarContentInfo(BaseFileListToolBarView toolBarView) {
        mToolbarLayout = toolBarView;
        mToolbarLayout.setId(ID_TOOL_BAR_LAYOUT);
        this.addView(mToolbarLayout);
        mToolbarLayout.setCommandHandle(mHandler);
    }

    protected void initShowViewContentInfo(
            BaseFileListShowContentView showContentView) {
        mShowContentViewLayout = showContentView;
        mShowContentViewLayout.setmHandler(mHandler);
    }

    /**
     * Initialization current view layout
     */
    protected void initCurrentLayoutInfo() {
        // tool bar view layout
        RelativeLayout.LayoutParams toolLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        toolLayoutParams.addRule(ALIGN_PARENT_TOP);
        toolLayoutParams.height = PixelUtils.dip2px(mContext, 52);
        mToolbarLayout.setLayoutParams(toolLayoutParams);

        // show view layout
        RelativeLayout.LayoutParams showViewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        showViewLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        showViewLayoutParams.addRule(BELOW, mToolbarLayout.getId());
        mShowContentViewLayout.setViewLayoutParams(showViewLayoutParams);
    }

    public FileListDataSourceHandle getFileListDataSourceHandle() {
        return fileListDataSourceHandle;
    }

}
