
package com.spt.carengine.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.db.bean.VideoFileLockBean;
import com.spt.carengine.db.manager.VideoFileLockDBManager;
import com.spt.carengine.playvideo.Constant;
import com.spt.carengine.playvideo.HardVideoHandler;
import com.spt.carengine.playvideo.VideoHandler;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.utils.VideoUtils;
import com.spt.carengine.view.explore.data.AbstractFile;
import com.spt.carengine.view.explore.navigate.NavigateView.ExploreType;
import com.spt.carengine.view.explore.navigate.VideoInfoWidget;

import java.io.File;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午4:40:26
 * @description 播放录像视频的Fragment
 */
public class PlayVideoFragment extends Fragment implements OnClickListener {

    private View mRootView = null;

    private View mOverlayView = null;
    private TextView mPlayTimeTv;
    private TextView mTotalTimeTv;
    private SeekBar mSeekBar;

    private ImageView mBackBtnImg;
    private ImageView mPlayOrPauseImg;
    private ImageView mPlayLoopImg;
    private ImageView mLockVideoImg;
    private ImageView mPlayMoreImg;
    private ImageView mCenterPlayImg;

    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;

    private VideoHandler videoHandler = null;

    private String playurl = "";

    private int mCurrPlayIndex = 0;

    private RelativeLayout player_loading_overall;
    private TextView player_loading_tv;
    private List<AbstractFile> abstractFiles = null;
    private View seekbarView;
    private VideoFileLockDBManager videoFileLockDataOpt = null;
    private ExploreType exploreType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_play_video,
                container, false);
        this.abstractFiles = getArguments().getParcelableArrayList("data");
        this.mCurrPlayIndex = getArguments().getInt("index");
        this.exploreType = (ExploreType) getArguments().get("exploreType");

        videoFileLockDataOpt = new VideoFileLockDBManager(getActivity());

        initUI();
        initVideoHandler();
        return mRootView;
    }

    private void initUI() {
        seekbarView = mRootView
                .findViewById(R.id.playvideo_progress_overlay_id);
        mOverlayView = mRootView.findViewById(R.id.play_video_overlay_rl);
        mSurface = (SurfaceView) mRootView
                .findViewById(R.id.record_video_surfaceview);

        mSurfaceHolder = mSurface.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mPlayTimeTv = (TextView) mRootView
                .findViewById(R.id.playvideo_used_time);
        mTotalTimeTv = (TextView) mRootView
                .findViewById(R.id.playvideo_total_time);
        mSeekBar = (SeekBar) mRootView.findViewById(R.id.playvideo_seekbar);

        player_loading_overall = (RelativeLayout) mRootView
                .findViewById(R.id.player_loading_overall);
        player_loading_tv = (TextView) mRootView
                .findViewById(R.id.player_loading_tv);
        mCenterPlayImg = (ImageView) mRootView
                .findViewById(R.id.playvideo_play_or_pause_icon);
        mBackBtnImg = (ImageView) mRootView.findViewById(R.id.backbtn_img);
        mPlayOrPauseImg = (ImageView) mRootView
                .findViewById(R.id.playorpause_video_img);
        mPlayLoopImg = (ImageView) mRootView
                .findViewById(R.id.play_video_loop_img);
        setVideoLoopPlayImage();

        mLockVideoImg = (ImageView) mRootView
                .findViewById(R.id.play_video_lock_img);
        mPlayMoreImg = (ImageView) mRootView
                .findViewById(R.id.play_video_more_img);
        setVideoLockImage();

        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mCenterPlayImg.setOnClickListener(this);
        mBackBtnImg.setOnClickListener(this);
        mPlayOrPauseImg.setOnClickListener(this);
        mPlayLoopImg.setOnClickListener(this);
        mPlayMoreImg.setOnClickListener(this);
        mLockVideoImg.setOnClickListener(this);

        // mRootView.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View arg0) {
        // showOverlayView(mOverlayView.getVisibility() == View.VISIBLE ? false
        // : true);
        // }
        // });

        // 操作UI的Handler
        mVideoPlayerHandler = new VideoPlayerHandler(this);
    }

    @Override
    public void onPause() {
        videoHandler.stop();
        videoHandler.destory();
        super.onPause();
    }

    /**
     * attach and disattach surface to the lib surface的回调接口
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            mSurfaceHolder = holder;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (isFirst) {
                isFirst = false;
                videoHandler.openVideo(playurl);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = holder;
        }
    };

    private boolean isFirst = true;

    /**
     * 初始化解码器
     * 
     * @param savedInstanceState
     */
    private void initVideoHandler() {
        playurl = DEFAULT_URL;
        if (abstractFiles != null) {
            playurl = abstractFiles.get(mCurrPlayIndex).getFilePath();
        }
        videoHandler = new HardVideoHandler(getActivity(), mSurfaceHolder,
                mVideoPlayerHandler);
    }

    /**
     * 更新界面的handler消息机制
     */
    private Handler mVideoPlayerHandler = null;

    private class VideoPlayerHandler extends
            com.spt.carengine.playvideo.WeakHandler<PlayVideoFragment> {
        public VideoPlayerHandler(PlayVideoFragment owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayVideoFragment activity = getOwner();
            if (activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what) {
                case Constant.VIDEO_PLAY:
                    mPlayOrPauseImg
                            .setImageResource(R.drawable.video_play_button);
                    player_loading_overall.setVisibility(View.GONE);
                    mCenterPlayImg.setVisibility(View.GONE);
                    // 开始之后5秒隐藏覆盖物
                    delay5sHideOverlay();
                    break;

                case Constant.VIDEO_PAUSE:
                    mPlayOrPauseImg
                            .setImageResource(R.drawable.video_pause_button);
                    mCenterPlayImg.setVisibility(View.VISIBLE);
                    break;

                case Constant.VIDEO_BACKWARD:
                    setOverlayProgress();
                    break;

                case Constant.VIDEO_FORWARD:
                    setOverlayProgress();
                    break;

                case Constant.HANDLER_MSG_DISPLAY_CURRENT_PLAY_TIME:
                    setOverlayProgress();
                    break;

                case Constant.VIDEO_PLAY_COMPLETION:
                    playNextVideo();
                    break;

                case Constant.VIDEO_PLAY_ERROR:
                    errToastShow();
                    break;

                case Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_VISIBLE:// progressbar设置为可见
                    int percent = msg.arg1;
                    // 如果进度是0，则不然进度条显示
                    if (percent != 0) {
                        player_loading_tv.setText(msg.arg1 + "%");
                        player_loading_overall.setVisibility(View.VISIBLE);

                    } else {
                        player_loading_overall.setVisibility(View.GONE);
                    }

                    break;

                case Constant.VIDEO_PLAY_PROGRESSBAR_LOADING_GONE:// progressbar设置为不可见
                    player_loading_overall.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (abstractFiles == null)
            return;
        completebeforeInitTime();
        boolean isLoopFlag = SharePrefsUtils
                .getVideoLoopPlayFlag(getActivity());
        if (isLoopFlag) {
            mCurrPlayIndex++;
            if (mCurrPlayIndex > abstractFiles.size() - 1) {
                mCurrPlayIndex = 0;
            }
            playurl = abstractFiles.get(mCurrPlayIndex).getFilePath();
            videoHandler.openVideo(playurl);
            videoHandler.play();
        }
    }

    /*
     * update the overlay 视屏播放完成之后初始化时间
     */
    private void completebeforeInitTime() {
        mPlayTimeTv.setText(R.string.video_default_time);
        mTotalTimeTv.setText(R.string.video_default_time);
        mSeekBar.setProgress(0);
        showOverlayView(mOverlayView.getVisibility() == View.VISIBLE ? false
                : true);
    }

    // video doesn's
    private Toast mErrToast;

    private void errToastShow() {
        if (mErrToast == null) {
            mErrToast = Toast.makeText(getActivity(), "播放错误",
                    Toast.LENGTH_SHORT);

        } else {
            mErrToast.setText("播放错误");
        }
        mErrToast.show();
        getActivity().finish();
    }

    /**
     * update the overlay 更新界面进度条的进度
     */
    private int setOverlayProgress() {
        if (videoHandler == null) {
            return 0;
        }

        // Update all view elements
        int time = (int) videoHandler.getCurrentPosition();
        int length = (int) videoHandler.getDuration();

        mSeekBar.setMax(length);
        mSeekBar.setProgress(time);
        if (time >= 0)
            mPlayTimeTv.setText(VideoUtils.timeToStr(time));
        if (length >= 0)
            mTotalTimeTv.setText(VideoUtils.timeToStr(length));
        return time;
    }

    /**
     * 进度条的监听器
     */
    private final OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            videoHandler.seekTo(videoProgress);
            setOverlayProgress();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            if (fromUser) {
                videoProgress = progress;
                mPlayTimeTv.setText(VideoUtils.timeToStr(progress));
            }
        }
    };

    /** 保存拖动进度条的变量 */
    protected int videoProgress = 0;

    private String DEFAULT_URL = Environment.getExternalStorageDirectory()
            + File.separator + "testvideo.mp4";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playvideo_play_or_pause_icon:
                if (!videoHandler.canPause() && !videoHandler.isPlaying()) {
                    videoHandler.play();
                }
                break;

            case R.id.backbtn_img:
                BaseExploreFileFragment exploreFileFragment = null;
                if (exploreType == ExploreType.Video) {
                    exploreFileFragment = new ExploreVideoFragment();

                } else if (exploreType == ExploreType.TraceRecord) {
                    exploreFileFragment = new ExploreTraceRecordFragment();
                }
                FragmentManager fragmentManager = getActivity()
                        .getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, exploreFileFragment)
                        .commit();
                break;

            case R.id.playorpause_video_img:
                if (videoHandler.canPause()) {
                    videoHandler.pause();

                } else {
                    videoHandler.play();
                }
                break;

            case R.id.play_video_loop_img:
                SharePrefsUtils.saveVideoLoopPlayFlag(getActivity());
                setVideoLoopPlayImage();
                break;

            case R.id.play_video_lock_img:
                // 锁住视频，和解锁视频
                VideoFileLockBean vInfo = videoFileLockDataOpt
                        .acceptVideoInfoFromPath(abstractFiles.get(
                                mCurrPlayIndex).getFilePath());
                if (vInfo != null) {
                    unlockVideoFile();

                } else {
                    lockVideoFile();
                }
                setVideoLockImage();
                break;

            case R.id.play_video_more_img:
                VideoInfoWidget videoInfoWidget = new VideoInfoWidget(
                        getActivity());
                VideoInfoWidget.VideoInfo videoInfo = new VideoInfoWidget.VideoInfo();
                videoInfo.name = abstractFiles.get(mCurrPlayIndex)
                        .getFileName();
                videoInfo.time = videoHandler.getDuration();
                videoInfo.size = abstractFiles.get(mCurrPlayIndex)
                        .getFileSize();
                videoInfoWidget.showSharePopupWindow(seekbarView,
                        R.drawable.video_play_info_bg);
                videoInfoWidget.setInformation(videoInfo);
                break;

            default:
                break;
        }
    }

    /**
	 * 
	 */
    private void setVideoLoopPlayImage() {
        // TODO
        boolean isFlag = SharePrefsUtils.getVideoLoopPlayFlag(getActivity());

        if (isFlag) {
            mPlayLoopImg.setImageResource(R.drawable.video_icon_loop_p);

        } else {
            mPlayLoopImg.setImageResource(R.drawable.video_icon_loop_n);
        }
    }

    private void setVideoLockImage() {
        VideoFileLockBean vInfo = videoFileLockDataOpt
                .acceptVideoInfoFromPath(abstractFiles.get(mCurrPlayIndex)
                        .getFilePath());
        if (vInfo != null) {
            mLockVideoImg.setImageResource(R.drawable.video_icon_lock_btn);
        } else {
            mLockVideoImg.setImageResource(R.drawable.video_icon_unlock_btn);
        }
    }

    private void lockVideoFile() {
        VideoFileLockBean vLockInfo = new VideoFileLockBean();
        AbstractFile abstractFile = abstractFiles.get(mCurrPlayIndex);

        vLockInfo.setName(abstractFile.getFileName());
        vLockInfo.setPath(abstractFile.getFilePath());
        vLockInfo.setSize(abstractFile.getFileSize());
        vLockInfo.setCreateTime(abstractFile.getFileCreateDate());
        vLockInfo.setDate(abstractFile.getFileCreateDate().substring(0, 10));
        videoFileLockDataOpt.saveInfoToDatabase(vLockInfo);
    }

    private void unlockVideoFile() {
        AbstractFile abstractFile = abstractFiles.get(mCurrPlayIndex);
        videoFileLockDataOpt.deleteInfoRecordFromPath(abstractFile
                .getFilePath());
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // // 显示与隐藏覆盖物的View ////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** 视频控制栏的自动隐藏时间为5秒 */
    public static final int VIDEO_CONTROLBAR_HIDE_TIME = 5000;

    /**
     * 延迟5s隐藏覆盖物
     */
    private void delay5sHideOverlay() {
        hideOverlayHandler.removeMessages(0);
        hideOverlayHandler.sendEmptyMessageDelayed(0,
                VIDEO_CONTROLBAR_HIDE_TIME);
    }

    private Handler hideOverlayHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 4秒内隐藏覆盖物
            showOverlayView(false);
        };
    };

    /**
     * 显示与隐藏控制栏
     */
    private void showOverlayView(boolean isVisible) {
        // mOverlayView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

}
