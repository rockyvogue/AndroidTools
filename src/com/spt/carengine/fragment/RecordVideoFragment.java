
package com.spt.carengine.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.layoutlib.bridge.impl.PlayAnimationThread;
import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.activity.MainActivity;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.fragment.ExploreTraceRecordFragment;
import com.spt.carengine.fragment.MainContentFragment;
import com.spt.carengine.recordvideo.RecordService;
import com.spt.carengine.recordvideo.RecordService.GameDisplay;
import com.spt.carengine.recordvideo.SwitchSVMessage;
import com.spt.carengine.recordvideo.TimeUtils;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.view.MainMenuView;

import de.greenrobot.event.EventBus;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午4:40:26
 * @description 行车记录摄像的Fragment
 */
public class RecordVideoFragment extends Fragment implements OnClickListener {

    private View mRootView = null;

    public static SurfaceView mSurfaceView;

    private ImageView mTopRightSideRedIcon;
    private TextView mRecordDateTv;
    private TextView mRecordTimeTv;

    private ImageView mBackBtnImg;
    private ImageView mRecordFolderImg;
    private ImageView mRecordLockVideoImg;
    private ImageView mRecordTimeImg;

    private int sfWidth;
    private int sfHeight;

    private Timer mTimer;
    private Activity mActivity;

    private MyHandler updateTimeHandler = new MyHandler(this);

    private static final int MSG_UPDATE_RECORD_TIME = 0;

    private static class MyHandler extends Handler {
        private WeakReference<RecordVideoFragment> record;

        public MyHandler(RecordVideoFragment rFragment) {
            this.record = new WeakReference<RecordVideoFragment>(rFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecordVideoFragment main = record.get();
            if (main == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_RECORD_TIME:
                    main.updateRecordTime();
                    break;
            }
        }
    }

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            updateTimeHandler.sendEmptyMessage(MSG_UPDATE_RECORD_TIME);
        }
    };

    private GameDisplay gameDisplay;

    private FrameLayout mDisplayContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_record_video,
                container, false);

        initUI();
        initData();

        return mRootView;
    }

    @Override
    public void onStop() {
        mTimer.cancel();// Fragment退出时cancel timer
        super.onStop();
    }

    private void initUI() {
        mDisplayContainer = (FrameLayout) mRootView
                .findViewById(R.id.record_video_display_container);
        mSurfaceView = (SurfaceView) mRootView
                .findViewById(R.id.record_video_surfaceview);

        // PlayAnimation(0);

        mTopRightSideRedIcon = (ImageView) mRootView
                .findViewById(R.id.record_video_red_icon);
        mRecordDateTv = (TextView) mRootView
                .findViewById(R.id.record_video_date);
        mRecordTimeTv = (TextView) mRootView
                .findViewById(R.id.record_video_time);

        mBackBtnImg = (ImageView) mRootView.findViewById(R.id.backbtn_img);
        mRecordLockVideoImg = (ImageView) mRootView
                .findViewById(R.id.record_video_lock_img);
        mRecordFolderImg = (ImageView) mRootView
                .findViewById(R.id.record_video_folder_img);
        mRecordTimeImg = (ImageView) mRootView
                .findViewById(R.id.record_video_time_img);

        mTopRightSideRedIcon.setOnClickListener(this);
        mBackBtnImg.setOnClickListener(this);
        mRecordLockVideoImg.setOnClickListener(this);
        mRecordFolderImg.setOnClickListener(this);
        mRecordTimeImg.setOnClickListener(this);

        setVideoTimeInveral(false);
        setVideoLockImage(false);

        // 切换SurfaceView
        SwitchSVMessage message = new SwitchSVMessage();
        message.setSurfaceView(mSurfaceView);
        message.setMessageType(Constant.MODULE_TYPE_IMAGEVIEW);
        EventBus.getDefault().post(message);// 主菜单切换图标

    }

    private void PlayAnimation(int type) {
        switch (type) {
            case 0:// 进入动画
                ScaleAnimation scaleAnimationIn = new ScaleAnimation(0, 1, 0,
                        1, 0, 0);
                scaleAnimationIn.setDuration(400);
                mSurfaceView.startAnimation(scaleAnimationIn);
                scaleAnimationIn.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation arg0) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {

                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // 显示地步菜单和文字

                    }
                });

                break;
            case 1:// 退出动画
                ScaleAnimation scaleAnimationOut = new ScaleAnimation(1, 0, 1,
                        0, 1, 1);
                scaleAnimationOut.setDuration(400);
                mSurfaceView.startAnimation(scaleAnimationOut);
                break;

            default:
                break;
        }

    }

    private void initData() {
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 500);
    }

    public void updateRecordTime() {
        mRecordDateTv.setText(TimeUtils.getCurDateSlashFormat());
        mRecordTimeTv.setText(TimeUtils.getCurTimeColonFormat());
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = ((MainActivity) getActivity())
                .getFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.record_video_red_icon:
                break;

            case R.id.backbtn_img:
                MainContentFragment mainContent = new MainContentFragment();
                transaction.replace(R.id.fragment_container, mainContent);
                transaction.commit();
                break;

            case R.id.record_video_lock_img:
                setVideoLockImage(true);
                break;

            case R.id.record_video_folder_img:
                ExploreTraceRecordFragment exFragment = new ExploreTraceRecordFragment();
                transaction.replace(R.id.fragment_container, exFragment);
                transaction.commit();
                break;

            case R.id.record_video_time_img:
                setVideoTimeInveral(true);
                break;

            default:
                break;
        }
    }

    /**
     * 设置录制的时间间隔 默认的录音的时间间隔为5s
     * 
     * @param isClick
     */
    private void setVideoTimeInveral(boolean isClick) {
        long timeInveral = SharePrefsUtils
                .getRecordVideoTimeInveral(getActivity());

        if (!isClick) {
            SharePrefsUtils.saveRecordVideoTimeInveral(getActivity(),
                    timeInveral);

        } else {
            if (timeInveral == SharePrefsUtils.DEFAULT_TIME_INVERAL_3M) {
                mRecordTimeImg.setImageResource(R.drawable.record_time_5_btn);
                SharePrefsUtils.saveRecordVideoTimeInveral(getActivity(),
                        SharePrefsUtils.DEFAULT_TIME_INVERAL_5M);

            } else if (timeInveral == SharePrefsUtils.DEFAULT_TIME_INVERAL_5M) {
                mRecordTimeImg.setImageResource(R.drawable.record_time_3_btn);
                SharePrefsUtils.saveRecordVideoTimeInveral(getActivity(),
                        SharePrefsUtils.DEFAULT_TIME_INVERAL_3M);
            }
        }
    }

    private void setVideoLockImage(boolean isClick) {
        boolean lockFlag = SharePrefsUtils.getRecordVideoLock(getActivity());
        if (!isClick) {
            mRecordLockVideoImg
                    .setImageResource(lockFlag ? R.drawable.video_icon_lock_btn
                            : R.drawable.video_icon_unlock_btn);

        } else {
            if (lockFlag) {
                mRecordLockVideoImg
                        .setImageResource(R.drawable.video_icon_unlock_btn);
                SharePrefsUtils.saveRecordVideoLock(getActivity(), false);

            } else {
                mRecordLockVideoImg
                        .setImageResource(R.drawable.video_icon_lock_btn);
                SharePrefsUtils.saveRecordVideoLock(getActivity(), true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SwitchSVMessage message = new SwitchSVMessage();
        message.setMessageType(Constant.MODULE_TYPE_SURFACEVIEW);
        EventBus.getDefault().post(message);// 主菜单切换图标

    }

}
