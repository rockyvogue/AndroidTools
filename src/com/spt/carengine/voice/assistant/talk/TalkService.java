/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : TalkService.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.talk
 * @Author : Dancindream
 * @CreateDate : 2012-5-22
 */
package com.spt.carengine.voice.assistant.talk;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import cn.yunzhisheng.common.util.ErrorUtil;
import cn.yunzhisheng.tts.offline.TTSPlayerListener;
import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.common.USCError;
import cn.yunzhisheng.vui.assistant.IDataControl;
import cn.yunzhisheng.vui.assistant.IVoiceAssistantListener;
import cn.yunzhisheng.vui.assistant.VoiceAssistant;
import cn.yunzhisheng.vui.custom.ILocationGPSListener;
import cn.yunzhisheng.vui.custom.ILocationSearchByKeyListener;
import cn.yunzhisheng.vui.custom.IPoiSearchByGPSListener;
import cn.yunzhisheng.vui.custom.IPoiSearchByKeyListener;
import cn.yunzhisheng.vui.interfaces.ILocationListener;
import cn.yunzhisheng.vui.interfaces.IPoiListener;
import cn.yunzhisheng.vui.modes.LocationInfo;
import cn.yunzhisheng.vui.wakeup.IWakeupListener;
import cn.yunzhisheng.vui.wakeup.IWakeupOperate;

import com.spt.carengine.R;
import com.spt.carengine.activity.MainActivity;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.AssistantPreference;
import com.spt.carengine.voice.ITalkService;
import com.spt.carengine.voice.assistant.preference.UserPreference;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("HandlerLeak")
public class TalkService extends Service {
	public static final String TAG = "TalkService";

	private final static String ACTION_INIT = "cn.yunzhisheng.intent.action.INIT";
	public final static String TALK_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.talk.onInitDone";
	public final static String TALK_EVENT_ON_RECORDING_START = "cn.yunzhisheng.intent.talk.onRecordingStart";
	public final static String TALK_EVENT_ON_RECORDING_STOP = "cn.yunzhisheng.intent.talk.onRecordingStop";
	public final static String TALK_EVENT_ON_START = "cn.yunzhisheng.intent.talk.onTalkStart";
	public final static String TALK_EVENT_ON_STOP = "cn.yunzhisheng.intent.talk.onTalkStop";
	public final static String TALK_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.talk.onTalkCancel";
	public final static String TALK_EVENT_ON_DATA_DONE = "cn.yunzhisheng.intent.talk.onDataDone";
	public final static String TALK_EVENT_RESULT = "cn.yunzhisheng.intent.talk.onResult";
	public final static String TALK_EVENT_ON_SESSION_PROTOCAL = "cn.yunzhisheng.intent.talk.onSessionProtocal";
	// private final static String ACTION_INTENT_FIRST=
	// "cn.yunzhisheng.vui.assistant.FirstActiviy";
	// private final static String ACTION_INTENT_TRY=
	// "cn.yunzhisheng.vui.assistant.FirstActiviy.try";

	public final static String WAKEUP_EVENT_START = "cn.yunzhisheng.intent.wakeup.start";
	public final static String WAKEUP_EVENT_STOP = "cn.yunzhisheng.intent.wakeup.stop";
	public final static String WAKEUP_EVENT_CANCEL = "cn.yunzhisheng.intent.wakeup.cancel";
	public final static String SESSION_EVENT_REALEASE = "cn.yunzhisheng.intent.session.release";
	
	public final static String SET_RECOGNIZERTYPE = "cn.yunzhisheng.intent.talk.settype";
	
	public final static String WAKEUP_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.wakeup.onInitDone";
	public final static String WAKEUP_EVENT_ON_START = "cn.yunzhisheng.intent.wakeup.onStart";
	public final static String WAKEUP_EVENT_ON_STOP = "cn.yunzhisheng.intent.wakeup.onStop";
	public final static String WAKEUP_EVENT_ON_SUCCESS = "cn.yunzhisheng.intent.wakeup.onSuccess";
	public final static String WAKEUP_EVENT_ON_ERROR = "cn.yunzhisheng.intent.wakeup.onError";
	
	public final static String TTS_EVENT_ON_PLAY_BEGIN = "cn.yunzhisheng.intent.tts.onPlayBegin";
	public final static String TTS_EVENT_ON_PLAY_END = "cn.yunzhisheng.intent.tts.onPlayEnd";
	public final static String TTS_EVENT_ON_ERROR = "cn.yunzhisheng.intent.tts.onError";
	public final static String TTS_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.tts.onCancel";
	public final static String TTS_EVENT_ON_BUFFER = "cn.yunzhisheng.intent.tts.onBuffer";

	public final static String ACTIVE_EVENT_STATUS_CHANGED_MESSAGE = "cn.yunzhisheng.intent.active.data.status_changed";

	public final static String TALK_DATA_PROTOCAL = "cn.yunzhisheng.intent.talk.data.protocal";
	public final static String TALK_DATA_RESULT = "cn.yunzhisheng.intent.talk.data.result";
	public final static String TALK_DATA_ERROR_CODE = "cn.yunzhisheng.intent.talk.data.error_code";
	public final static String TALK_DATA_ERROR_MESSAGE = "cn.yunzhisheng.intent.talk.data.error_message";

	public final static String ACTIVE_DATA_STATUS = "cn.yunzhisheng.intent.active.data.status";
	public final static String ACTIVE_STATUS = "cn.yunzhisheng.intent.active.data.status.isactive";
	public final static String TTS_EVENT_ON_VOCIE_SPEED = "cn.yunzhisheng.intent.tts.setVoiceSpeed";
	
	public final static String ACTIVE_EVENT_ISACTIVE_MESSAGE = "cn.yunzhisheng.intent.active.data.isactive";

	public static final String TTS_VOICE_SPEED = "TTS_VOICE_SPEED";
	
	public final static String TTS_TEXT_ON_VOCIE_PLAY = "cn.yunzhisheng.intent.tts.text";
	
	private VoiceAssistant mVoiceAssistant = null;
	private IWakeupOperate mWakeupOperate = null;

	private static IDataControl mDataControl = null;
	private static ArrayList<String> mOnlineSupportList = null;
	private static ArrayList<String> mOfflineSupportList = null;

	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;

	private String mPackageName;
	private NotificationManager mNotificationManager;
	private ITTSControl mTTSControl;
	private UserPreference mUserPreference;
	private float TTS_SPEED_SLOW = 0;
	private float TTS_SPEED_STANDARD = 13;
	private float TTS_SPEED_FAST = 20;
	// private AudioManager mAudioManager;

	// 20150316 add by Dancindream Fix FAILED BINDER TRANSACTION &
	// android.os.TransactionTooLargeException
	private static HashMap<String, String> mSessionProtocalHashMap = new HashMap<String, String>();
	
    public static boolean TALK_INITDONE = false;
    public static boolean WAKEUP_INITDONE = false;

	public static String getSessionProtocal(String id) {
		if (mSessionProtocalHashMap.containsKey(id)) {
			String protocal = mSessionProtocalHashMap.get(id);
			mSessionProtocalHashMap.remove(id);
			return protocal;
		} else {
			return "";
		}
	}

	/**
	 * @Description : TODO 用于唤醒模块的调用【若没有唤醒功能，该部分开忽略】
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WAKEUP_EVENT_START.equals(action)) {
				startWakeup();
			} else if (WAKEUP_EVENT_STOP.equals(action)) {
				stopWakeup();
			} else if (WAKEUP_EVENT_CANCEL.equals(action)) {
				cancelWakeup();
			} else if (SESSION_EVENT_REALEASE.equals(action)) {
				releaseSession();
			} else if (SET_RECOGNIZERTYPE.equals(action)) {
				String recognizerType = intent.getStringExtra("recognizerType");
				if(recognizerType != null && recognizerType.length() > 0 ){
					setRecognizerTalkType(recognizerType);
				}
			} else if ("cn.yunzhisheng.intent.tts.onPlayEnd_starttalk".equals(action)) {
				LOG.writeMsg(this, LOG.MODE_VOICE, "------cn.yunzhisheng.intent.tts.onPlayEnd_starttalk");
				showMessage(TTS_EVENT_ON_PLAY_END);
			} else if (TTS_EVENT_ON_VOCIE_SPEED.equals(action)) {
				String speed = intent.getStringExtra(TTS_VOICE_SPEED/*wuwen SettingFragment.TTS_VOICE_SPEED*/);
				setTTSSpeed(speed);
			}else if(TTS_TEXT_ON_VOCIE_PLAY.equals(action)){
				String text = intent.getStringExtra("text");
				playTTS(text);
			}
		}
	};

	private TTSPlayerListener mTTSPlayerListener = new TTSPlayerListener() {

		@Override
		public void onTtsData(byte[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPlayEnd() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mTTSPlayerListener, onPlayEnd");
			showMessage(TTS_EVENT_ON_PLAY_END);
			// unmuteMusicStream();
		}

		@Override
		public void onPlayBegin() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mTTSPlayerListener, onPlayBegin");
			showMessage(TTS_EVENT_ON_PLAY_BEGIN);
			// muteMusicStream();
		}

		@Override
		public void onInitFinish() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(USCError arg0) {
			showMessage(TTS_EVENT_ON_ERROR);
		}

		@Override
		public void onCancel() {
			showMessage(TTS_EVENT_ON_CANCEL);
		}

		@Override
		public void onBuffer() {
			showMessage(TTS_EVENT_ON_BUFFER);
		}
	};

	private final ITalkService.Stub mBinder = new ITalkService.Stub() {

		@Override
		public void startTalk() throws RemoteException {
			TalkService.this.startTalk();
		}

		@Override
		public void stopTalk() throws RemoteException {
			TalkService.this.stopTalk();
		}

		@Override
		public void cancelTalk(boolean callback) throws RemoteException {
			TalkService.this.cancelTalk(callback);
		}

		@Override
		public void putCustomText(String text) throws RemoteException {
			TalkService.this.putCustomText(text);
		}

		@Override
		public void setProtocal(String protocal) throws RemoteException {
			TalkService.this.setProtocal(protocal);
		}

		@Override
		public void onStart() throws RemoteException {
			if (mVoiceAssistant != null) {
				mVoiceAssistant.onStart();
			}
		}

		@Override
		public void onStop() throws RemoteException {
			if (mVoiceAssistant != null) {
				mVoiceAssistant.onStop();
			}
		}

		@Override
		public void playTTS(String text) throws RemoteException {
			TalkService.this.playTTS(text);
		}

		@Override
		public void cancelTTS() throws RemoteException {
			TalkService.this.cancelTTS();
		}

		@Override
		public String getContactName(String number) throws RemoteException {
			return mVoiceAssistant.getPhonesContact(number);
		}

		@Override
		public void setRecognizerTalkType(String type) throws RemoteException {
			TalkService.this.setRecognizerTalkType(type);
		}

	};

	/**
	 * @Description : TODO 语音魔方的回调
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private IVoiceAssistantListener mVoiceAssistantListener = new IVoiceAssistantListener() {

		/**
		 * @Description : TODO 录音的音量回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onVolumeUpdate(int)
		 */
		@Override
		public void onVolumeUpdate(int volume) {
			AssistantPreference.mRecordingVoiceVolume = (float) volume;
		}

		/**
		 * @Description : TODO 当调用cancel方法之后的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onCancel()
		 */
		@Override
		public void onCancel() {
			TalkService.this.onTalkCancel();
		}

		/**
		 * @Description : TODO 语音助手初始化结束的回调【注：需要在调用init前将回调设置到模块中】
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onInitDone()
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void onInitDone() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService, callback onInitDone");
			if (mVoiceAssistant != null) {
				// TODO 获取数据操作句柄【若没有支持“提醒”领域，该部分可以忽略】
				mDataControl = mVoiceAssistant.getDataControl();
				// TODO 获取有网络所支持的领域列表
				mOnlineSupportList = mVoiceAssistant.getSupportList(true);
				// TODO 获取没有网络所支持的领域列表
				mOfflineSupportList = mVoiceAssistant.getSupportList(false);
			}
			TALK_INITDONE = true;
			showMessage(TALK_EVENT_ON_INITDONE);
		}
		
		/**
		 * @Description : 语音助手得到的原始语义协议【可忽略】
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onProtocal(java.lang.String)
		 */
		@Override
		public void onProtocal(String protocol) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onProtocal: protocol " + protocol);
		}

		/**
		 * @Description : TODO 返回的用于界面显示的语音魔方协议【重要】
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onSessionProtocal(java.lang.String)
		 */
		@Override
		public void onSessionProtocal(String protocol) {
			TalkService.this.onSessionProtocal(protocol);
		}

		/**
		 * @Description : TODO 当调用start方法后的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onStart()
		 */
		@Override
		public void onStart() {
			TalkService.this.onTalkStart();
		}

		/**
		 * @Description : TODO 当调用stop方法之后的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onStop()
		 */
		@Override
		public void onStop() {
			TalkService.this.onTalkStop();
		}

		/**
		 * @Description : TODO
		 * 当录音正式开始时的回调【与onTalkStart之间的时间差=硬件麦克风开启时间+麦克风全零数据时间】
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onRecordingStart()
		 */
		@Override
		public void onRecordingStart() {
			TalkService.this.onRecordingStart();
		}

		@Override
		public void onRecordingStop() {
			TalkService.this.onRecordingStop();
		}

		/**
		 * @Description : TODO 当助手将所有数据初始化好时的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onDataDone()
		 */
		@Override
		public void onDataDone() {
			TalkService.this.onDataDone();
		}

		/**
		 * @Description : 返回语音识别的结果【可忽略】
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-23
		 * @see cn.yunzhisheng.vui.assistant.IVoiceAssistantListener#onResult(java.lang.String)
		 */
		public void onResult(String result) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "onInitDone()-->> onResult(), result: " + result);
			TalkService.this.onTalkResult(result);
		}

		@Override
		public void onActiveStatusChanged(int flag) {
			TalkService.this.onActiveStatusChanged(flag);
		}

		@Override
		public void isActive(boolean status) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "talk service listener is active: " + status + "");
			// TODO Auto-generated method stub
			TalkService.this.isActive(status);
		}
	};

	/**
	 * @Description : TODO 唤醒插件的回调【若没有唤醒功能，该部分可忽略】
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private IWakeupListener mWakeupListener = new IWakeupListener() {

		/**
		 * @Description : TODO 唤醒成功，返回相应的唤醒词
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.wakeup.IWakeupListener#onSuccess(java.lang.String)
		 */
		@Override
		public void onSuccess(String result) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener, onSuccess(): result " + result);
			TalkService.this.onWakeupSuccess(result);
		}

		/**
		 * @Description : TODO 当唤醒服务停止时的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.wakeup.IWakeupListener#onStop()
		 */
		@Override
		public void onStop() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener, onStop()");
			TalkService.this.onWakeupStop();
		}

		/**
		 * @Description : TODO 当唤醒服务开启时的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.wakeup.IWakeupListener#onStart()
		 */
		@Override
		public void onStart() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener, onStart()");
			TalkService.this.onWakeupStart();
		}

		/**
		 * @Description : TODO 唤醒服务初始化完成的回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.wakeup.IWakeupListener#onInitDone()
		 */
		@Override
		public void onInitDone() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener, onInitDone()");
			TalkService.this.onWakeupInitDone();
		}

		/**
		 * @Description : TODO 唤醒服务的异常回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.vui.wakeup.IWakeupListener#onError(cn.yunzhisheng.common.util.ErrorUtil)
		 */
		@Override
		public void onError(ErrorUtil error) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener, onError(): error " + error);
			TalkService.this.onWakeupError(error);
		}
	};

	private OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onSharedPreferenceChanged:key " + key);
			if (UserPreference.KEY_MAP.equals(key)) {
				updateMapConfig();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService onCreate()");
		mUserPreference = new UserPreference(this);
		mUserPreference.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mAudioManager = (AudioManager)
		// getSystemService(Context.AUDIO_SERVICE);
		getPackageInfo();
		registReceiver();

		mTTSControl = TTSFactory.createTTSControl(this, "");
		mTTSControl.setStreamType(AudioManager.STREAM_MUSIC);
		mTTSControl.initTTSEngine(this);
		mTTSControl.setTTSListener(mTTSPlayerListener);
		String ttsSpeed = mUserPreference.getString(UserPreference.KEY_TTS_SPEED, UserPreference.TTS_SPEED_FAST);
		
		Log.d(TAG, "speed in sp  : " + ttsSpeed);
		//由于TTS_SPEED_DEFAULT与TTS_SPEED_STANDARD都指代默认速度，需要更改
		String ttsSpeed_value = null;
		if("TTS_SPEED_DEFAULT".equals(ttsSpeed)){
			ttsSpeed_value = "standard";
		}else if("TTS_SPEED_SLOW".equals(ttsSpeed)){
			ttsSpeed_value = "slow";
		}else if("TTS_SPEED_FAST".equals(ttsSpeed)){
			ttsSpeed_value = "fast";
		}
		Log.d(TAG, "The init speed : " + ttsSpeed_value);
		if(ttsSpeed_value != null){
			setTTSSpeed(ttsSpeed_value);
		}
		
		
		// mTTSControl.setVoiceVolume(arg0);
		// ttsControl.initTTSEngine(this,
		// PrivatePreference.FOLDER_PACKAGE_FILES);

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		sendMessage(ACTION_INIT);
	}

	private void init() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService  init");
		mVoiceAssistant = new VoiceAssistant(TalkService.this);
		// TODO 设置语音魔方的回调
		mVoiceAssistant.setListener(mVoiceAssistantListener);

		// 自定义数据处理(注:处理过程放在异步中)
		/* TODO 增加此方法，用于处理每次获取到的GPS */
		// mVoiceAssistant.setLocationGPSListener(mLocationGPSListener);
		/* TODO 增加此方法，可以定制查询某个地址的GPS方法，若不增加，则默认使用百度寻址定位 */
		// mVoiceAssistant.setLocationSearchByKeyListener(mILocationSearchByKeyListener);
		/* TODO 增加此方法，可以定制通过GPS搜索附近商户的功能，若不增加，则默认使用大众点评数据 */
		// mVoiceAssistant.setPoiSearchByGPSListener(mPoiSearchByGPSListener);
		/* TODO 增加此方法，可以定制通过关键字搜索附近商户的功能，若不增加，则默认使用大众点评数据 */
		// mVoiceAssistant.setPoiSearchByKeyListener(mPoiSearchByKeyListener);

		updateMapConfig();

		mVoiceAssistant.setVadEnable(true);//VAD
//		mVoiceAssistant.setVadTimeout(1000);//VAD超时
//		mVoiceAssistant.setNetMode("DEFAULT");//网络
		
		// TODO 语音魔方初始化
		mVoiceAssistant.init();

		// TODO 用于独立场景
		// mVoiceAssistant.setIndependentSession(true);
		// mVoiceAssistant.setCurrentSessionValue("cn.yunzhisheng.call");

		int screenWidth = AssistantPreference.SCREEN_WIDTH;
		int screenHeight = AssistantPreference.SCREEN_HEIGHT;
		double screenSize = AssistantPreference.SCREEN_SIZE;
		if (screenWidth != -1 && screenHeight != -1 && screenSize != -1) {
			mVoiceAssistant.setPhoneInfo(screenWidth + "x" + screenHeight, String.valueOf(screenSize));
		}

		// TODO 获取唤醒插件句柄【若没有唤醒功能，此处得到的是null】
		Object o = mVoiceAssistant.getOperate("OPERATE_WAKEUP");
		LOG.writeMsg(this, LOG.MODE_VOICE, "唤醒语音, o : " + o);
		
		if (o != null && o instanceof IWakeupOperate) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "唤醒语音,进入初始化模块, o : " + o);
			mWakeupOperate = (IWakeupOperate) o;
			mWakeupOperate.setWakeupListener(mWakeupListener);
			mWakeupOperate.init();
		}
	}
	
	private ILocationGPSListener mLocationGPSListener = new ILocationGPSListener() {
        
        @Override
        public void onLocationGPSResult(LocationInfo arg0, ILocationListener arg1) {
            LOG.writeMsg(this, LOG.MODE_VOICE, "gps," + arg0.getAddress() + "," + arg0.getCity());
        }
    };
    
    private ILocationSearchByKeyListener mILocationSearchByKeyListener = new ILocationSearchByKeyListener() {
        
        @Override
        public void onSearchResultBy(String arg0, String arg1,
                ILocationListener arg2) {
            LOG.writeMsg(this, LOG.MODE_VOICE, "LocationSearch," + arg0 + "," + arg1);
        }
    };
    
    private IPoiSearchByGPSListener mPoiSearchByGPSListener = new IPoiSearchByGPSListener() {
        
        @Override
        public void searchPoiByGps(double arg0, double arg1, String arg2,
                String arg3, int arg4, int arg5, IPoiListener arg6) {
            LOG.writeMsg(this, LOG.MODE_VOICE, "LocationSearch," + arg0 + "," + arg1);
        }
    };
    
    private IPoiSearchByKeyListener mPoiSearchByKeyListener = new IPoiSearchByKeyListener() {
        
        @Override
        public void searchPoiByKey(String arg0, String arg1, String arg2,
                String arg3, int arg4, int arg5, IPoiListener arg6) {
            LOG.writeMsg(this, LOG.MODE_VOICE, "LocationSearch," + arg0 + "," + arg1);
        }
    };
	
	private void setTTSSpeed(String speed){		
		if(speed!=null&&speed.length()==0){
			LOG.writeMsg(this, LOG.MODE_VOICE, "TTS Speed value is empty");
			return;
		}
		if (speed.equals(UserPreference.SLOW_VOICE_SPEED)) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_SLOW);
			LOG.writeMsg(this, LOG.MODE_VOICE, "Slow speed : " + TTS_SPEED_SLOW);
		} else if (speed.equals(UserPreference.DEFAULT_VOICE_SPEED)) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_STANDARD);
			LOG.writeMsg(this, LOG.MODE_VOICE, "Default speed : " + TTS_SPEED_STANDARD);
		} else if (speed.equals(UserPreference.FAST_VOICE_SPEED)) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_FAST);
			LOG.writeMsg(this, LOG.MODE_VOICE, "Fast speed : " + TTS_SPEED_FAST);
		}
	}

	private void sendMessage(String action) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "sendMessage: action " + action);
		sendMessage(new Intent(action));
	}

	private void sendMessage(Intent intent) {
		if (intent != null) {
			Message msg = mServiceHandler.obtainMessage();
			msg.obj = intent;
			mServiceHandler.sendMessage(msg);
		}
	}
	
//	private String getMapValue() {
//		String map = mUserPreference.getString(UserPreference.KEY_MAP, "");
//		if (TextUtils.isEmpty(map)) {
//			String locationVendor = cn.yunzhisheng.preference.PrivatePreference.getValue(
//				"location_vendor",
//				UserPreference.MAP_VALUE_AMAP);
//			String poiVendor = cn.yunzhisheng.preference.PrivatePreference.getValue(
//				"poi_vendor",
//				UserPreference.MAP_VALUE_AMAP);
//			if (locationVendor != null && !locationVendor.equals(poiVendor)) {
//				LOG.writeMsg(this, LOG.MODE_VOICE, "location_vendor != poi_vendor");
//			}
//			map = locationVendor;
//		}
//		return map;
//	}

	private void updateMapConfig() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "updateMapConfig");

		String map = mUserPreference.getString(UserPreference.KEY_MAP, "");
		if (TextUtils.isEmpty(map)) {
			String locationVendor = cn.yunzhisheng.preference.PrivatePreference.getValue("location_vendor",UserPreference.MAP_VALUE_BAIDU);
			String poiVendor = cn.yunzhisheng.preference.PrivatePreference.getValue("poi_vendor",UserPreference.MAP_VALUE_BAIDU);
			if (locationVendor != null && !locationVendor.equals(poiVendor)) {
				LOG.writeMsg(this, LOG.MODE_VOICE, "location_vendor != poi_vendor");
			}
			map = locationVendor;
			mUserPreference.putString(UserPreference.KEY_MAP, map);
		}
//		String poiVendor = cn.yunzhisheng.preference.PrivatePreference.getValue("poi_vendor",UserPreference.MAP_VALUE_AMAP);
		
		if (!UserPreference.MAP_VALUE_BAIDU.equals(map) && !UserPreference.MAP_VALUE_BAIDU.equals(map)) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "Map config error! User 'AMAP' as default!");
			map = UserPreference.MAP_VALUE_BAIDU;
		}
		cn.yunzhisheng.preference.PrivatePreference.setValue("location_vendor", map);
		cn.yunzhisheng.preference.PrivatePreference.setValue("poi_vendor", map);
	}

	private void registReceiver() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "registReceiver:" + mPackageName);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WAKEUP_EVENT_START);
		filter.addAction(WAKEUP_EVENT_STOP);
		filter.addAction(WAKEUP_EVENT_CANCEL);
		filter.addAction(SESSION_EVENT_REALEASE);
		filter.addAction(SET_RECOGNIZERTYPE);
		filter.addAction("cn.yunzhisheng.intent.tts.onPlayEnd_starttalk");
		filter.addAction(TTS_TEXT_ON_VOCIE_PLAY);
		filter.addAction(TTS_EVENT_ON_VOCIE_SPEED);
		registerReceiver(mServiceReceiver, filter);
	}

	private void unregistReceiver() {
		try {
			unregisterReceiver(mServiceReceiver);
		} catch (Exception e) {
			e.printStackTrace();;
		}
	}

	private void startTalk() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "------startTalk -------");
		// TODO 开始语音识别
		mVoiceAssistant.start();
	}

	private void stopTalk() {
		// TODO 结束录音
		mVoiceAssistant.stop();
	}

	private void cancelTalk(boolean callback) {
		// TODO 取消识别流程
		mVoiceAssistant.cancel(callback);
	}

	private void startWakeup() {
		if (mWakeupOperate != null) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "Wakeup, startWakeup");
			// TODO 开启唤醒服务
			mWakeupOperate.startWakeup();
		}
	}

	private void stopWakeup() {
		if (mWakeupOperate != null) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "Wakeup, stopWakeup");
			// TODO 关闭唤醒服务
			mWakeupOperate.stopWakeup();
		}
	}
	
	private void cancelWakeup() {
		if (mWakeupOperate != null) {
			// TODO 关闭唤醒服务
		    LOG.writeMsg(this, LOG.MODE_VOICE, "Wakeup, cancelWakeup");
			mWakeupOperate.cancelWakeup();
		}
	}

	private void releaseSession() {
		if (mVoiceAssistant != null) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "Wakeup, releaseSession");
			mVoiceAssistant.releaseSession();
		}
	}
	
	private void setRecognizerTalkType(String type){
		LOG.writeMsg(this, LOG.MODE_VOICE, "setRecognizerTalkType type: " + type);
		if (mVoiceAssistant != null) {
			mVoiceAssistant.setRecognizerTalkType(type);
		}
	}

	private void putCustomText(String text) {
		// TODO 文本替代语音输入，跳过语音识别获取语义数据
		mVoiceAssistant.putCustomText(text);
	}

	private void setProtocal(String protocal) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "setProtocal protocal: " + protocal);
		// TODO 设置魔方协议，用于跳过一些业务流程
		mVoiceAssistant.setProtocal(protocal);
	}

	private void onTalkStart() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkStart");
		showMessage(TALK_EVENT_ON_START);
	}

	private void onTalkStop() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkStop");
		showMessage(TALK_EVENT_ON_STOP);
	}

	private void onRecordingStart() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onRecordingStart");
		showMessage(TALK_EVENT_ON_RECORDING_START);
		// muteMusicStream();
	}

	private void onRecordingStop() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onRecordingStop");
		showMessage(TALK_EVENT_ON_RECORDING_STOP);
		// unmuteMusicStream();
	}

	private void onTalkCancel() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkCancel");
		showMessage(TALK_EVENT_ON_CANCEL);
		// unmuteMusicStream();
	}

	private void onDataDone() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onDataDone");
		showMessage(TALK_EVENT_ON_DATA_DONE);
	}

	private void onTalkResult(String result) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkResult: result " + result);
		Bundle bundle = new Bundle();
		bundle.putString(TALK_DATA_RESULT, result);
		showMessage(TALK_EVENT_RESULT, bundle);
	}

	private void onSessionProtocal(String protocal) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onSessionProtocal: " + "length " + (protocal == null ? 0 : protocal.length()) 
		        + "----" + protocal);
		Bundle extras = new Bundle();
		String time = String.valueOf(System.currentTimeMillis());
		extras.putString(TALK_DATA_PROTOCAL, time);
		mSessionProtocalHashMap.put(time, protocal);
		showMessage(TALK_EVENT_ON_SESSION_PROTOCAL, extras);
	}

	private void onActiveStatusChanged(int flag) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onActiveStatusChanged: flag " + flag);
		Bundle extras = new Bundle();
		extras.putInt(ACTIVE_DATA_STATUS, flag);
		showMessage(ACTIVE_EVENT_STATUS_CHANGED_MESSAGE, extras);
	}
	
	private void onWakeupInitDone() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onWakeupInitDone");
		WAKEUP_INITDONE = true;
		showMessage(WAKEUP_EVENT_ON_INITDONE);
	}

	private void onWakeupStart() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onWakeupStart");
		showMessage(WAKEUP_EVENT_ON_START);
		notifyWakeupRunning();
	}

	private void onWakeupStop() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onWakeupStop");
		showMessage(WAKEUP_EVENT_ON_STOP);
		cancelWakeupNotification();
	}

	private void onWakeupSuccess(String command) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onWakeupSuccess");
		showMessage(WAKEUP_EVENT_ON_SUCCESS);
//		playTTS("找我干嘛");
	}

	private void onWakeupError(ErrorUtil error) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onWakeupError");
		if (error != null) {
			Bundle extras = new Bundle();
			extras.putInt(TALK_DATA_ERROR_CODE, error.code);
			extras.putString(TALK_DATA_ERROR_MESSAGE, error.message);
			showMessage(WAKEUP_EVENT_ON_ERROR, extras);
		}
	}
	
	private void isActive(boolean status) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "on activer, status : " + status);
		Bundle extras = new Bundle();
		extras.putBoolean(ACTIVE_STATUS, status);
		showMessage(ACTIVE_EVENT_ISACTIVE_MESSAGE, extras);
	}

	public static IDataControl getDataControl() {
		return mDataControl;
	}

	public static ArrayList<String> getSupportList(boolean hasNetWork) {
		if (hasNetWork) {
			return mOnlineSupportList;
		} else {
			return mOfflineSupportList;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService onStartCommand:" + intent);
		return START_STICKY;
	}

	@SuppressWarnings("deprecation")
	private void notifyWakeupRunning() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "notifyWakeupRunning");
		String tickerText = getString(R.string.wakeup_started);
		Notification notification = new Notification(R.drawable.ic_launcher, tickerText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, tickerText, getString(R.string.wakeup_started_prompt), pi);
		mNotificationManager.notify(0, notification);
	}

	private void cancelWakeupNotification() {
		mNotificationManager.cancel(0);
	}

	// private void muteMusicStream() {
	// LOG.writeMsg(this, LOG.MODE_VOICE, "muteMusicStream");
	// mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	// }
	//
	// private void unmuteMusicStream() {
	// LOG.writeMsg(this, LOG.MODE_VOICE, "unmuteMusicStream");
	// mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	// }

	@Override
	public void onDestroy() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService onDestroy()");
		mNotificationManager.cancelAll();
		mNotificationManager = null;
		mTTSControl.setTTSListener(null);
		mTTSControl.releaseTTSEngine();
		mTTSControl = null;
		mTTSPlayerListener = null;
		mServiceLooper.quit();
		mDataControl = null;
		mVoiceAssistant.release();
		mVoiceAssistant = null;
		mUserPreference.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
		mUserPreference = null;
		mPreferenceChangeListener = null;
		unregistReceiver();
		super.onDestroy();
	}

	private void playTTS(String text) {
		if (TextUtils.isEmpty(text)) {
		    LOG.writeMsg(this, LOG.MODE_VOICE, "playTTS:text empty!");
			return;
		}
		
		if (mTTSControl == null) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "playTTS:mTTSControl null!");
			return;
		}
		LOG.writeMsg(this, LOG.MODE_VOICE, "playTTS:text " + text);
		cancelTTS();
		mTTSControl.play(text);
	}

	private void cancelTTS() {
		if (mTTSControl == null) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "cancelTTS:mTTSControl null!");
			return;
		}
		mTTSControl.cancel();
	}

	private void showMessage(String message, Bundle extras) {
		Intent intent = new Intent(message);
		intent.putExtras(extras);
		intent.addCategory(mPackageName);
		sendBroadcast(intent);
	}

	private void showMessage(String message) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "showMessage: message " + message);
		Intent intent = new Intent(message);
		intent.addCategory(mPackageName);
		sendBroadcast(intent);
	}
	

	private void getPackageInfo() {
		try {
			PackageInfo info = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			mPackageName = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mPackageName = null;
		}
		LOG.writeMsg(this, LOG.MODE_VOICE, "-getPackageName is--" + mPackageName);
	}
	

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		/**
		 * Handle incoming transaction requests.
		 * The incoming requests are initiated by the MMSC Server or by the MMS
		 * Client itself.
		 */
		@Override
		public void handleMessage(Message msg) {
			Intent intent = (Intent) msg.obj;
			if (intent != null) {
				String action = intent.getAction();
				LOG.writeMsg(this, LOG.MODE_VOICE, "handleMessage: action " + action);
				if (ACTION_INIT.equals(action)) {
					init();
				}
				// else if (ACTION_GRAMMER_UPDATE.equals(action)) {
				// compileGrammarData();
				// } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				// String packageName = intent.getStringExtra("package");
				// handleAppAdd(packageName);
				// } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				// String packageName = intent.getStringExtra("package");
				// handleAppRemove(packageName);
				// }
			}
		}
	}
}
