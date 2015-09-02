
package com.spt.carengine.view.explore.navigate;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.spt.carengine.R;
import com.spt.carengine.view.explore.data.AbstractFile;
import com.spt.carengine.view.explore.data.FileListDataSourceHandle;
import com.spt.carengine.view.swipelistview.BaseSwipeListViewListener;
import com.spt.carengine.view.swipelistview.SwipeListView;

/**
 * @author Rocky
 * @Time 2015年8月5日 下午3:20:12
 * @description show content
 */
public class BaseFileListShowContentView {

    /** 打开文件节点的处理 */
    public static final int HANDLER_MESSAGE_FILESHOW_OPEN_FILENODE = 11;
    /** 选中文件 **/
    public static final int HANDLER_MESSAGE_FILE_LIST_SELECT_FILE = 32;
    /** 打开文件处理 **/
    public static final int HANDLER_MESSAGE_FILE_LIST_OPEN_FILE = 31;

    protected RelativeLayout mParentView;

    protected FileListDataSourceHandle fileSourceHandler;

    private Handler mHandler;

    private SwipeListView mSwipeListView;

    private View emptyFolderView;
    private View loadingView;

    private View contentView;

    private ExploreVideoAdapter exploreVideoAdapter;

    public BaseFileListShowContentView(RelativeLayout parentView,
            FileListDataSourceHandle fSourceHandle) {
        this.mParentView = parentView;
        this.fileSourceHandler = fSourceHandle;

        initInflaterLayout(parentView);

        initUIControl();

        showLoadingView(true);
        
        initListViewAdapter();

        bindListener();

        reloadSwipeListViewProperty();
        
       
    }

    /*************************************** 初始化处理 *****************************************************************/
    /**
     * inflater layout file
     * 
     * @param parentView
     */
    private void initInflaterLayout(RelativeLayout parentView) {
        LayoutInflater mInflater = (LayoutInflater) mParentView.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = mInflater.inflate(R.layout.explore_whole_content,
                parentView, false);
        mParentView.addView(contentView);
    }

    /**
     * init ui widget value
     */
    private void initUIControl() {
        mSwipeListView = (SwipeListView) contentView.findViewById(R.id.explore_listview);
        emptyFolderView = contentView.findViewById(R.id.empty_folder_id);
        loadingView = contentView.findViewById(R.id.loading_folder_id);
    }

    /**
     * init object's data
     */
    private void initListViewAdapter() {
        exploreVideoAdapter = new ExploreVideoAdapter(mSwipeListView,
                fileSourceHandler.getArryListIFiles(), mParentView.getContext());
        mSwipeListView.setAdapter(exploreVideoAdapter);
    }

    private void bindListener() {
        mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                AbstractFile abstractFile = (AbstractFile) arg0
                        .getItemAtPosition(position);
                Message message = mHandler.obtainMessage(
                        HANDLER_MESSAGE_FILESHOW_OPEN_FILENODE, position, -1,
                        abstractFile);
                mHandler.sendMessage(message);
            }
        });
        mSwipeListView
                .setSwipeListViewListener(new TestBaseSwipeListViewListener());

    }

    class TestBaseSwipeListViewListener extends BaseSwipeListViewListener {

        @Override
        public void onClickFrontView(int position) {
            super.onClickFrontView(position);
            AbstractFile abstractFile = fileSourceHandler.getArryListIFiles()
                    .get(position);
            Message message = mHandler.obtainMessage(
                    HANDLER_MESSAGE_FILESHOW_OPEN_FILENODE, position, -1,
                    abstractFile);
            mHandler.sendMessage(message);
        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                AbstractFile abstractFile = fileSourceHandler
                        .getArryListIFiles().get(position);
                abstractFile.delete();
                fileSourceHandler.getArryListIFiles().remove(position);
            }
            exploreVideoAdapter.notifyDataSetChanged();
            if (fileSourceHandler.getArryListIFiles().size() == 0) {
                showEmptyView(true);

            } else {
                showEmptyView(false);
            }
        }
    }

    private void showEmptyView(boolean isVisible) {
        emptyFolderView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mSwipeListView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }
    
    private void showLoadingView(boolean isVisible) {
        loadingView.setVisibility(isVisible ?  View.VISIBLE : View.GONE);
        emptyFolderView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        mSwipeListView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        
    }

    private void reloadSwipeListViewProperty() {
        mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        mSwipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        mSwipeListView.setOffsetLeft(convertDpToPixel(385));
    }
    
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = mParentView.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    
    protected Handler updateUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 伍文
        };
    };

    /**
     * 更新数据
     */
    public void updateAllDataInfo() {
        exploreVideoAdapter.notifyDataSetChanged();
        if (fileSourceHandler.getArryListIFiles().size() == 0) {
            showEmptyView(true);

        } else {
            showEmptyView(false);
        }
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setViewLayoutParams(LayoutParams showViewLayoutParams) {
        contentView.setLayoutParams(showViewLayoutParams);
    }

    /**
     * @return the mSwipeListView
     */
    public ListView getListView() {
        return mSwipeListView;
    }

}
