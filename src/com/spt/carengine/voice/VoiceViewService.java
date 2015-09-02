/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WindowService.java
 * @ProjectName : Voizard
 * @PakageName : cn.yunzhisheng.voizard.service
 * @Author : Brant
 * @CreateDate : 2013-5-27
 */

package com.spt.carengine.voice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import cn.yunzhisheng.common.util.LogUtil;

import com.spt.carengine.R;
import com.spt.carengine.fragment.VoiceFragment;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.MessageReceiver;
import com.spt.carengine.voice.assistant.preference.UserPreference;
import com.spt.carengine.voice.assistant.session.SessionManager;
import com.spt.carengine.voice.assistant.util.PackageUtil;
import com.spt.carengine.voice.phone.PhoneStateReceiver;
import com.spt.carengine.voice.view.VoiceBottomMicroControlBar;
import com.spt.carengine.voice.view.VoiceSessionContainer;
import com.spt.carengine.voice.view.VoiceSessionRelativeLayout;
import com.spt.carengine.voice.view.VoiceSessionRelativeLayout.DispatchKeyEventListener;
import com.spt.carengine.voice.view.VoiceSessionRelativeLayout.OnTouchEventListener;

import java.util.List;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-5-27
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-5-27
 * @Modified: 2013-5-27: 实现基本功能
 */
@SuppressLint("InlinedApi")
public class VoiceViewService extends Service {

    public static final String ACTION_START_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.START_HOME_TIMER";
    public static final String ACTION_STOP_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.STOP_HOME_TIMER";

    public static final String ACTION_START_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.START_WAKEUP";
    public static final String ACTION_STOP_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.STOP_WAKEUP";
    
    public static final String EXTRA_KEY_DISMISS_FLOAT_WINDOW = "DISMISS_FLOAT_WINDOW";
    public static final String EXTRA_KEY_START_TALK_FROM = "START_TALK_FROM";
    public static final String START_TALK_FROM_MAIN_ACTIVITY = "START_FROM_MAIN_ACTIVITY";
    public static final String START_TALK_FROM_FLOAT_MIC = "START_FROM_FLOAT_MIC";
    public static final String START_TALK_FROM_SHAKE = "START_FROM_SHAKE";
    public static final String START_TALK_FROM_SIMULATE = "START_FROM_SIMULATE";
    public static final String START_TALK_FROM_WIDGET = "START_FROM_WIDGET";
    public static final String START_TALK_FROM_WAKEUP = "START_FROM_WAKEUP";
    public static final String START_TALK_FROM_HARDWARE = "START_FROM_HARDWARE";
    public static final String START_TALK_FROM_OTHER = "START_FROM_OTHER";

    public static final String CANCEL_TALK_FROM_SCREEN_OFF = "CANCEL_FROM_SCREEN_OFF";
    public static final String CANCEL_TALK_FROM_BACK_KEY = "CANCEL_FROM_BACK_KEY";
    public static final String CANCEL_TALK_FROM_MANUAL = "CANCEL_FROM_MANUAL";
    
    public static final String ACTION_CANCEL_SESSION_MANAGER = "cancel.session.manager";
    public static final String ACTION_DESTORY_SESSION_MANAGER = "destory.talk.server.";

    private List<String> mLauncherPackage;
    private VoiceSessionRelativeLayout mViewRoot;
    private VoiceSessionContainer mSessionContainer;
    private VoiceBottomMicroControlBar mMicrophoneControl;
    private SessionManager mSessionManager = null;
    
    private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
    
    private Point mWindowSize = new Point();
    private WindowManager mWindowManager;
    
    private UserPreference mPreference;
    private boolean mEnableWakeup;
    private boolean mStartTalking = false;
    private Handler mHandler = new Handler();
    
    private Runnable mStartTalkRunnable = new Runnable() {
        @Override
        public void run() {
            mStartTalking = false;
        }
    };
    
    private void resetWindowParamsFlags() {
        mWindowParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    private void initWindowParams() {
        mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWindowParams.format = PixelFormat.RGBA_8888;
        resetWindowParamsFlags();
        mWindowParams.flags =
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        // |
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mWindowParams.gravity = Gravity.CENTER;
        // mWindowParams.windowAnimations = R.anim.slide_right_in;
        Resources res = getResources();
        int width = res.getDimensionPixelSize(R.dimen.window_width);
        int height = res.getDimensionPixelSize(R.dimen.window_height);
        mWindowParams.width = width;
        mWindowParams.height = height;

        updateDisplaySize();
    }
    
    private void updateDisplaySize() {
        Display display = mWindowManager.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            mWindowSize.y = display.getHeight();
            mWindowSize.x = display.getWidth();
        } else {
            display.getSize(mWindowSize);
        }
    }

    /**
     * 屏幕亮和关闭的广播
     */
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LOG.writeMsg(this, LOG.MODE_VOICE, "onReceive:intent " + intent
                    + ", action : " + action);
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
//                updateEnableFloatMic();
//                startFloatMicChecker(0);
                mSessionManager.onResume();

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//                stopFloatMicChecker();
                mSessionManager.onPause();

            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
//                stopFloatMicChecker();
                mSessionManager.onResume();
            }
        }
    };

    /**
     * 按键返回
     */
    private DispatchKeyEventListener mDispatchKeyEventListener = new DispatchKeyEventListener() {

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (cancelTalk(false, CANCEL_TALK_FROM_BACK_KEY)) {
                    mSessionManager.cancelSession();
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 点击边缘让语音识别框消失
     */
    private OnTouchEventListener mOnTouchEventListener = new OnTouchEventListener() {

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= mViewRoot.getWidth()) || (y < 0) || (y >= mViewRoot
                            .getHeight()))) {
                mSessionManager.cancelSessionWithTTS();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                mSessionManager.cancelSessionWithTTS();
                return true;
            }
            return false;
        }

    };

    /**
     * 悬浮框的点击事件
     */
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.bottomCancelBtn) {
                LOG.writeMsg(this, LOG.MODE_VOICE, "WindowService class, Cancel button onClick");
                mSessionManager.cancelSessionWithTTS();
            }
        }
    };

    private void updateEnableWakeup() {
        mEnableWakeup = mPreference.getBoolean(UserPreference.KEY_ENABLE_WAKEUP, UserPreference.DEFAULT_WAKEUP);
        LOG.writeMsg(this, LOG.MODE_VOICE, "updateEnableWakeup: mEnableWakeup " + mEnableWakeup);
    }
    
    /***************************** END **********************************************/

    @Override
    public void onCreate() {
        LOG.writeMsg(this, LOG.MODE_VOICE, "onCreate");
        super.onCreate();
        mPreference = new UserPreference(this);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mSessionManager = new SessionManager(this);
        mLauncherPackage = PackageUtil.getLauncherPackages(this);
        initWindowParams();
        initUserPreference();
        registerReceiver();
        
        if (PackageUtil.isRunningForeground(VoiceViewService.this)) {
            mSessionManager.showInitView();
        }
        SessionManager.mIsFirstInitDone = true;
    }

    public void setRelativedView(VoiceSessionRelativeLayout mViewRoot, 
            VoiceSessionContainer mSessionContainer,
            VoiceBottomMicroControlBar mMicrophoneControl) {
        this.mSessionContainer = mSessionContainer;
        this.mMicrophoneControl = mMicrophoneControl;
        this.mViewRoot = mViewRoot;
        mSessionManager.setRelativedView(this.mSessionContainer, this.mMicrophoneControl);
        // 显示数据初始化的框
//        if (PackageUtil.isRunningForeground(NewWindowService.this)) {
//            mSessionManager.showInitView();
//        }
        registerListener();
    }

    private void initUserPreference() {
        updateEnableWakeup();
    }

    /*************************** 注册和注销广播与监听器 ***********************/
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mScreenReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mScreenReceiver);
    }

    private void registerListener() {
        if(mViewRoot != null) {
            mViewRoot.setDispatchKeyEventListener(mDispatchKeyEventListener);
            mViewRoot.setOnTouchEventListener(mOnTouchEventListener);
        }
    }

    private void unregisterListener() {
        if(mViewRoot != null) {
            mViewRoot.setDispatchKeyEventListener(null);
        }
    }

    /********************************** END ********************************/


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "onStartCommand: intent " + intent);
        if (intent != null) {
            String action = intent.getAction();
            if (MessageReceiver.ACTION_START_TALK.equals(action)) {
                String from = intent.getStringExtra(EXTRA_KEY_START_TALK_FROM);
                if (from != null && from.length() > 0) {
                    if(START_TALK_FROM_MAIN_ACTIVITY.equals(from)) {
                        //VoiceFragment过来的广播。设置监听器。
                        setRelativedView(VoiceFragment.mRootView, VoiceFragment.mSessionContainer, VoiceFragment.mVControlBar);
                    }
                    startTalk(from);
                }
            } else if (MessageReceiver.ACTION_STOP_TALK.equals(action)) {
                stopTalk();

            } else if (MessageReceiver.ACTION_CANCEL_TALK.equals(action)) {
                cancelTalk(false, "FragmentExit");
                

            } else if (ACTION_START_WAKEUP.equals(action)) {
                mSessionManager.requestStartWakeup();
                
            } else if(ACTION_CANCEL_SESSION_MANAGER.equals(action)) {
//                cancelTalk(false, "FragmentExit");
                mSessionManager.cancelSession();
            } else if(ACTION_DESTORY_SESSION_MANAGER.equals(action)) {
              mSessionManager.cancelSession();
              stopSelf();
            }
        }
        return START_STICKY;
    }

    public void startTalk(String from) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "startTalk:from " + from);
        if (TextUtils.isEmpty(from)) {
            throw new RuntimeException("Start talk form can't be empty!");
        } else {
            if (mStartTalking) {
                return;
            }
            mStartTalking = true;
            mHandler.removeCallbacks(mStartTalkRunnable);
            mHandler.postDelayed(mStartTalkRunnable, 2000);
            mSessionManager.startTalk(from);
        }
    }

    private void stopTalk() {
        LOG.writeMsg(this, LOG.MODE_VOICE, "stopTalk");
        mSessionManager.stopTalk();
    }

    private void cancelTalk() {
        LOG.writeMsg(this, LOG.MODE_VOICE, "cancelTalk");
        mSessionManager.cancelTalk(true);
    }
    
    private boolean cancelTalk(boolean callback, String from) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "cancelTalk:from " + from);
        return mSessionManager.cancelTalk(callback);
    }
    
    /*************************** 与界面相关的View *****************************/

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }

        if (!(v instanceof ViewGroup)) {
            return false;
        }

        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }
        return false;
    }

    public void hideCancelBtn(boolean b) {
        if(mViewRoot == null) return;
        if (b) {
//            mViewRoot.findViewById(R.id.cancelDivider).setVisibility(View.GONE);
//            mViewRoot.findViewById(R.id.bottomCancelBtn).setVisibility(View.GONE);
            
        } else {
//            mViewRoot.findViewById(R.id.cancelDivider).setVisibility(View.VISIBLE);
//            mViewRoot.findViewById(R.id.bottomCancelBtn).setVisibility(View.VISIBLE);
        }
    }
    
    /*************************** END *****************************/
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LOG.writeMsg(this, LOG.MODE_VOICE, "onLowMemory");
    }

    @Override
    public void onDestroy() {
        LOG.writeMsg(this, LOG.MODE_VOICE, "onDestroy");
        super.onDestroy();
        unregisterReceiver();
        unregisterListener();
        mOnClickListener = null;
        mScreenReceiver = null;
        mSessionManager.cancelTalk(false);
        mSessionManager.onDestroy();
        PhoneStateReceiver.release();
        mSessionManager = null;
        mLauncherPackage.clear();
        mLauncherPackage = null;
    }

    public void dismiss() {
//        Intent intent = new Intent(VoiceFragment.ACTION_OPEN_HOME_COMMMAND);
//        sendBroadcast(intent);
    }
    
    public void dimissView(View view) {
        if (!view.isShown()) {
            return;
        }
        LOG.writeMsg(this, LOG.MODE_VOICE, "prepare view dismiss");
        mWindowManager.removeViewImmediate(view);
//        if (mPendingStartMicChecker) {
//            mPendingStartMicChecker = false;
//            updateEnableFloatMic();
//            startFloatMicChecker(0);
//        }
    }
    
    public void addPrepareView(View view) {
        if (view == null) {
            LOG.writeMsg(this, LOG.MODE_VOICE, "addSessionView: view null,return!");
            return;
        }
        dismiss();
        Resources res = getResources();
        int width = res.getDimensionPixelSize(R.dimen.prepare_windows_width);
        int height = res.getDimensionPixelSize(R.dimen.prepare_windows_height);
        WindowManager.LayoutParams WindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowParams.gravity = Gravity.CENTER;
        WindowParams.width = width;
        WindowParams.height = height;
        WindowParams.format = PixelFormat.RGBA_8888;
        if (view.isShown()) {
            mWindowManager.removeViewImmediate(view);
        }
        mWindowManager.addView(view, WindowParams);
    }
    
}
