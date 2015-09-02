
package com.spt.carengine.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.spt.carengine.R;
import com.spt.carengine.recordvideo.FileUtils;
import com.spt.carengine.view.ReturnBarView;
import com.spt.carengine.view.explore.navigate.BaseFileListShowContentView;
import com.spt.carengine.view.explore.navigate.BaseFileListShowView;
import com.spt.carengine.view.explore.navigate.BaseFileListToolBarView;
import com.spt.carengine.view.explore.navigate.NavigateBarView;
import com.spt.carengine.view.explore.navigate.NavigateView;
import com.spt.carengine.view.explore.navigate.NavigateView.ExploreType;

/**
 * @author Rocky
 * @Time 2015.7.15 10:10:51
 * @descrition The Fragment of exploring recording trace file.
 */
public class BaseExploreFileFragment extends Fragment {

    private Activity mActivity;

    /**
     * 根目录下的View
     */
    private RelativeLayout mRootView;

    // 导航栏View
    private NavigateView navigateView;

    // show content list View
    private BaseFileListShowView baseFileListShowView;

    // 工具条bar
    private BaseFileListToolBarView baseFileListToolBarView;

    // show concrete content
    private BaseFileListShowContentView baseFileListShowContentView;

    private boolean thisFragmentIsPause = false;
    private ReturnBarView mReturnBarView ;

    /**
     * 是否已经加载过根目录的数据
     */
    private boolean isLoadingRoot = false;

    /** 预加载的标志 **/
    private boolean mHasLoading = false;

    /**
     * 设置预加载界面是否显示
     * 
     * @param isVisibleToUser
     */
    public void setHasLoading(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        if (isVisibleToUser && !mHasLoading) {
            mHasLoading = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        setHasLoading(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = (RelativeLayout) inflater.inflate(
                R.layout.fragment_explore_video, container, false);
        initShowViewContentInfo();
        addContentToView();
        this.thisFragmentIsPause = false;
        return mRootView;
    }

//    private void intiReturnBarView(View view) {
//        if(null==view){
//            return ;
//        }
//        mReturnBarView = (ReturnBarView) view.findViewById(R.id.video_return_bar);
//        mReturnBarView.init(ReturnBarView.TYPE_VEDIO);
//        mReturnBarView.setBackListener(mReturnBarBack);
//    }
//    private OnClickListener mReturnBarBack = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // 返回到上一个界面
//            mActivity.onBackPressed();
//        }
//    };
    @Override
    public void onResume() {
        super.onResume();
        this.thisFragmentIsPause = false;
        if (navigateView.getmShowViews().size() == 1 && !isLoadingRoot) {
            baseFileListShowView.getFileListDataSourceHandle()
                    .requestTrackRecordFiles(getFirstRequestPath());

        } else {

        }
    }

    @Override
    public void onPause() {
        this.thisFragmentIsPause = true;
        super.onPause();
    }

    /*************************************** 界面布局 *****************************/

    /**
     * init show view object
     */
    private void initShowViewContentInfo() {
        this.baseFileListShowView = new BaseFileListShowView(mActivity);
        this.baseFileListToolBarView = new BaseFileListToolBarView(mActivity);
        this.baseFileListShowContentView = new BaseFileListShowContentView(
                baseFileListShowView,
                this.baseFileListShowView.getFileListDataSourceHandle());
        this.baseFileListShowView.setShowViewLayout(baseFileListToolBarView,
                baseFileListShowContentView);
    }

    /**
     * The show content to load into fragments
     */
    protected void addContentToView() {
        // TODO NavigateBar = BackButton + Title
        NavigateBarView deviceNaviBarView = new NavigateBarView(mActivity);
        //intiReturnBarView(view);
        navigateView = new NavigateView(mRootView, deviceNaviBarView,
                baseFileListShowView, getExploreType());
        navigateView.popAllNavigateView();
    }

    /**
     * 子类去实现
     * 
     * @return
     */
    protected ExploreType getExploreType() {
        return ExploreType.TraceRecord;
    }

    private String getFirstRequestPath() {
        if (getExploreType() == ExploreType.Video) {
            return FileUtils.getStorageVideoDir();

        } else if (getExploreType() == ExploreType.Photo) {
            return FileUtils.getStorageTakePictureDir();

        } else if (getExploreType() == ExploreType.TraceRecord) {
            return FileUtils.getAppDir();
        }
        return "";
    }

}
