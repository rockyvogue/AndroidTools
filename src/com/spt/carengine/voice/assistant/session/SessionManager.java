package com.spt.carengine.voice.assistant.session;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.yunzhisheng.common.DataTool;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;
import cn.yunzhisheng.tts.offline.common.USCError;
import cn.yunzhisheng.vui.assistant.VoiceAssistant;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.VoiceViewService;
import com.spt.carengine.voice.assistant.model.KnowledgeMode;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.preference.UserPreference;
import com.spt.carengine.voice.assistant.talk.ITalkServicePresentorListener;
import com.spt.carengine.voice.assistant.talk.ITalkServiceWakeupListener;
import com.spt.carengine.voice.assistant.talk.TalkService;
import com.spt.carengine.voice.assistant.talk.TalkServicePresentor;
import com.spt.carengine.voice.assistant.util.BeepPlayer;
import com.spt.carengine.voice.assistant.view.FunctionView;
import com.spt.carengine.voice.assistant.view.ReceiveCallView;
import com.spt.carengine.voice.assistant.view.SessionContainer;
import com.spt.carengine.voice.phone.PhoneStateReceiver;
import com.spt.carengine.voice.view.VoiceBottomMicroControlBar;
import com.spt.carengine.voice.view.VoiceSessionContainer;
import com.spt.carengine.voice.view.VoiceMessageItem.MsgSource;
import com.spt.carengine.voice.view.VoiceSessionContainer.ITTSStatusListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Module : Session层核心类
 * @Comments : 根据协议进行Session生成和协议分发，该类为Session层的核心枢纽
 * @Author : Dancindream
 * @CreateDate : 2014-4-1
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2014-4-1
 * @Modified: 2014-4-1: 实现基本功能
 */
/*
 * TTS完成度:接听电话,产品形态
 */
@SuppressLint("HandlerLeak")
public class SessionManager implements ITalkServicePresentorListener {
	private static final String TAG = "SessionManager";

	private static final int TALK_STATUS_IDLE = 0x01;
	private static final int TALK_STATUS_LISTENING = 0x02;
	private static final int TALK_STATUS_WAITING = 0x03;

	// public static final int TTS_STATUS_IDLE = 0x01;
	// public static final int TTS_STATUS_BUFFER = 0x02;
	// public static final int TTS_STATUS_PLAYING = 0x03;

	private static final int FIELD_TYPE_NORMAL = -1010000111;
	private static final int FIELD_TYPE_SMS = -1010000112;
	private static final int FIELD_TYPE_CALL = -1010000113;
	private int FIELD_TYPE = FIELD_TYPE_NORMAL;

	private static final int mTalkType_NORMAL = 0;
	private static final int mTalkType_SMS = 1;
	private static final int mTalkType_CALL = 2;
	private int mTalkType = mTalkType_NORMAL;

	private VoiceViewService windowService = null;
	private FunctionView mFunctionView = null;
	private ReceiveCallView mReceiveCallView = null;
	private VoiceSessionContainer mSessionViewContainer = null;
	private VoiceBottomMicroControlBar mMicrophoneControl = null;

	private int mCurrentTalkStatus = TALK_STATUS_IDLE;
	private TalkServicePresentor mTalkServicePresentor = null;
	private BaseSession mCurrentSession = null;
	private BeepPlayer mBeepPlayer = null;

	private UserPreference mPreference;

	public static boolean mIsFirstInitDone = false;
	private boolean mWakeupRecordingState;
	private boolean mWakeupResult;
	private boolean mRecognitionIintDone;
	private boolean mWakeUpInitDone;
	private boolean mRecognitionDataDone;
	private boolean mRecognitionRecordingState;
	private NotificationManager mNotificationManager;
	// add release wakelock
	private PowerManager mPowerManager;
	private WakeLock mWakeLock;
	private AudioManager mAudioManager;
	private boolean mLastSessionDone = false;
	private Runnable mTTSEndRunnable;

	private String mName, mNumber;
	private UserPreference mUserPreference;
	private int mPhoneState;

	private String mSessionStatus = "";
	private String mType = "";

	// add by wuwen 2015.08.31
	private boolean mStartTalkFlag = true;
	private Handler mHandler = new Handler();

	private Runnable mStartTalkRunnable = new Runnable() {
		@Override
		public void run() {
			mStartTalkFlag = true;
		}
	};

	public final static String TYPE_COMMAND = "TYPE_COMMAND";
	public final static String TYPE_MUTIPLE = "TYPE_MUTIPLE";
	public final static String TYPE_FREETEXT = "TYPE_FREETEXT";

	View loadingView;

	/**
	 * @Description : TODO 提供主线程的方法调用
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private Handler mSessionManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "handleMessage:what "
					+ SessionPreference.getMessageName(msg.what));
			switch (msg.what) {
			// TODO 开始语音识别流程
			case SessionPreference.MESSAGE_START_TALK:
				if (mWakeupListener != null) {
					mWakeupListener.onSuccess();
				}
				break;
			// TODO 结束语音录音
			case SessionPreference.MESSAGE_STOP_TALK:
				stopTalk();
				break;
				
			// TODO 取消语音识别流程
			case SessionPreference.MESSAGE_CANCEL_TALK:
				cancelTalk(true);
				break;
				
			// TODO 取消Session操作流程
			case SessionPreference.MESSAGE_SESSION_CANCEL:
				setSessionEnd(true);
				releaseCurrentSession();
				releaseSessionCenterSession();
				releaseWakeLock();
				requestStartWakeup();
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;
			// TODO Session操作流程已完成
			case SessionPreference.MESSAGE_SESSION_DONE:
				setSessionEnd(true);
				releaseCurrentSession();
				releaseSessionCenterSession();
				releaseWakeLock();
				requestStartWakeup();
				// mSessionViewContainer.removeAllSessionViews();
				sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;

			// TODO 设置麦克风为不可用状态
			case SessionPreference.MESSAGE_DISABLE_MICROPHONE:
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setEnabled(false);
				}
				break;
			// TODO 设置麦克风为可用状态
			case SessionPreference.MESSAGE_ENABLE_MICROPHONE:
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setEnabled(true);
				}
				break;
			// TODO 播报文字
			case SessionPreference.MESSAGE_REQUEST_PLAY_TTS:
				Bundle bundle = msg.getData();
				Runnable r = null;
				if (msg.obj != null) {
					r = (Runnable) msg.obj;
				}
				playTTSWithEndRunnable(
						bundle.getString(SessionPreference.KEY_TEXT), r);
				break;
			// TODO 取消播报文字
			case SessionPreference.MESSAGE_REQUEST_CANCEL_TTS:
				cancelTTS();
				break;
			// TODO 直接跳转到新的Session
			case SessionPreference.MESSAGE_NEW_PROTOCAL:
				if (mCurrentSession != null) {
					mCurrentSession.release();
					mCurrentSession = null;
				}
				cancelTTS();
				String strProtocolString = (String) msg.obj;
				createSession(strProtocolString);
				break;
			// TODO VAD方式自动结束录音
			case SessionPreference.MESSAGE_VAD_RECORD_STOP:
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setEnabled(true);
				}
				break;
			// TODO 添加答句文字（语音魔方回答的文字）
			case SessionPreference.MESSAGE_ADD_ANSWER_TEXT:
				String text = (String) msg.obj;
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setAnswerText(MsgSource.Rebot, text);
				}
				break;
				
			// TODO 添加回答的View（语音魔方需要展现的View）
			case SessionPreference.MESSAGE_ADD_ANSWER_VIEW:
				View view = (View) msg.obj;
				boolean fullScroll = msg.arg1 != 0;
				if (mSessionViewContainer != null) {
					mSessionViewContainer.removeAllSessionViews();
				}
				LOG.writeMsg(
						this,
						LOG.MODE_VOICE,
						"SessionPreference.MESSAGE_ADD_ANSWER_VIEW, sessionViewContainer add answer view. fullScroll : "
								+ fullScroll);
				if (mSessionViewContainer != null) {
					mSessionViewContainer.addSessionView(view, fullScroll);
				}
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setAnswerView(fullScroll);
				}
				break;

			// TODO 添加问句文字（用户说的话）
			case SessionPreference.MESSAGE_ADD_QUESTION_TEXT:
				// mSessionViewContainer.addQustionView((String) msg.obj);
				if (mMicrophoneControl != null) {
					LOG.writeMsg(this, LOG.MODE_VOICE, "用户说的话 : " + (String) msg.obj);
					mMicrophoneControl.setAnswerText(MsgSource.Mine,
							(String) msg.obj);
				}
				break;

			// TODO 返还给语音魔方的协议（用于业务流程跳转）
			case SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL:
				String protocal = (String) msg.obj;
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"MESSAGE_UI_OPERATE_PROTOCAL protocal : " + protocal);

				JSONObject jsonObj = null;

				try {
					jsonObj = new JSONObject(protocal);
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
				if (jsonObj.has("confirm")) {
					mTalkServicePresentor.setProtocal(protocal);
					String confirm = JsonTool.getJsonValue(jsonObj, "confirm");
					if ("cancel".equals(confirm)) {
						cancelSession();
					}
				} else if (jsonObj.has("select")) {
					cancelTalk(false);
					mTalkServicePresentor.setProtocal(protocal);
					setSessionEnd(true);
				} else {
					mTalkServicePresentor.setProtocal(protocal);
				}
				break;
			case SessionPreference.MESSAGE_DISMISS_WINDOW:
				if (windowService != null) {
					windowService.dismiss();
				}
				break;
			case SessionPreference.MESSAGE_SHOW_HELP_VIEW:
				showHelpView();
				break;
			case SessionPreference.MESSAGE_PLAY_REVEIVED_MESSAGE:
				break;

			case SessionPreference.MESSAGE_PLAY_BEEP_SOUND:
				if (mBeepPlayer != null) {
					mBeepPlayer.playBeepSound(msg.arg1, false, null);
				} else {
					LOG.writeMsg(this, LOG.MODE_VOICE, "mBeepPlayer null!");
				}
				break;
			case SessionPreference.MESSAGE_ANSWER_CALL:
				setSessionEnd(true);
				cancelSession();
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;
			}
		}

	};

	private void resetRingMode() {
		if (mAudioManager != null) {
			mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
		}
	}

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

		public void onCallStateChanged(int state, String incomingNumber) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onCallStateChanged:state "
					+ state + ",incomingNumber " + incomingNumber + ",state : "
					+ state);
			String text = "";

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"--TelephonyManager.CALL_STATE_RINGING--");
				cancelSession();
				LOG.writeMsg(
						this,
						LOG.MODE_VOICE,
						"STREAM_MUSIC Volume before:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC));
				LOG.writeMsg(
						this,
						LOG.MODE_VOICE,
						"STREAM_RING Volume before:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_RING));
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
				LOG.writeMsg(
						this,
						LOG.MODE_VOICE,
						"STREAM_MUSIC Volume after:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC));
				LOG.writeMsg(
						this,
						LOG.MODE_VOICE,
						"STREAM_RING Volume after:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_RING));
				String personName = mTalkServicePresentor
						.getName(incomingNumber);
				if (TextUtils.isEmpty(personName)) {
					text = incomingNumber
							+ windowService
									.getString(R.string.phone_answer_cancel);
					mName = windowService.getString(R.string.stranger);
				} else {
					text = personName
							+ windowService
									.getString(R.string.phone_answer_cancel);
					mName = personName;
				}

				LOG.writeMsg(this, LOG.MODE_VOICE, "--personName-->"
						+ personName);
				FIELD_TYPE = FIELD_TYPE_CALL;
				mTalkType = mTalkType_CALL;
				mNumber = incomingNumber;
				playTTSWithEndRunnable(text, new Runnable() {
					@Override
					public void run() {
						mWakeupListener.onSuccess();
					}
				});
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"--TelephonyManager.CALL_STATE_OFFHOOK--");
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"--TelephonyManager.CALL_STATE_IDLE--");
				FIELD_TYPE = FIELD_TYPE_NORMAL;
				if (mPhoneState == TelephonyManager.CALL_STATE_RINGING) {
					cancelTalk(false);
					text = windowService.getString(R.string.call_canceled);
					if (mMicrophoneControl != null) {
						mMicrophoneControl.setAnswerText(MsgSource.Rebot, text);
					}
					playTTSWithEndRunnable(text, new Runnable() {
						@Override
						public void run() {
							cancelSession();
						}
					});
				}
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
				break;
			}
			mPhoneState = state;
		};
	};

	/**
	 * @Description : TODO 播报TTS的回调
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private TTSPlayerListener mTTSListener = new TTSPlayerListener() {

		/**
		 * @Description : TODO TTS开始播报
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onPlayBegin()
		 */
		@Override
		public void onPlayBegin() {
			if (mSessionViewContainer != null) {
				mSessionViewContainer.onTTSPlay();
			}
		}

		/**
		 * @Description : TODO TTS播报结束
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onPlayEnd()
		 */
		@Override
		public void onPlayEnd() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onPlayEnd: mCurrentSession "
					+ mCurrentSession + ";mTTSEndRunnable : " + mTTSEndRunnable);
			if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.onTTSStop();
				}
			} else {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.onTTSUnenabled();
				}
			}
			if (mCurrentSession != null) {
				mCurrentSession.onTTSEnd();

			} else if (mTTSEndRunnable != null) {
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"mTTSEndRunnable is not null");
				mTTSEndRunnable.run();
				mTTSEndRunnable = null;
			}

			// TODO
			// mTTSEndRunnable为空的时候，需要releaseSession dismisWindow cancel
			// startWakeup
			else if (mTTSEndRunnable == null) {
				cancelSession();
			}
		}

		/**
		 * @Description : TODO TTS缓冲中
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onBuffer()
		 */
		@Override
		public void onBuffer() {
			if (mSessionViewContainer != null) {
				mSessionViewContainer.onTTSBuffer();
			}
		}

		/**
		 * @Description : TODO TTS被取消
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onCancel()
		 */
		@Override
		public void onCancel() {
			if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.onTTSStop();
				}
			} else {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.onTTSUnenabled();
				}
			}
		}

		/**
		 * @Description : TODO TTS异常回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onError(cn.yunzhisheng.tts.offline.common.USCError)
		 */
		@Override
		public void onError(USCError arg0) {

		}

		/**
		 * @Description : TODO TTS初始化完成
		 * @Author : Dancindream
		 * @CreateDate : 2014-5-10
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onInitFinish()
		 */
		@Override
		public void onInitFinish() {

		}

		@Override
		public void onTtsData(byte[] arg0) {
		}
	};

	/**
	 * @Description : TODO 唤醒功能抽象回调
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private ITalkServiceWakeupListener mWakeupListener = new ITalkServiceWakeupListener() {
		@Override
		public void onSuccess() {
			LOG.writeMsg(this, LOG.MODE_VOICE,
					"mWakeupListener.onSuccess, mWakeupRecordingState : "
							+ mWakeupRecordingState);
			mWakeupResult = true;
			if (mWakeupRecordingState) {
				mTalkServicePresentor.stopWakeup();
			} else {
				startTalk();
			}
		}

		@Override
		public void onStop() {
			LOG.writeMsg(this, LOG.MODE_VOICE,
					"mWakeupListener.onStop, mWakeupRecordingState : "
							+ mWakeupRecordingState + ", mWakeupResult : "
							+ mWakeupResult);
			mWakeupRecordingState = false;
			cancelWakeupNotification();
			if (mWakeupResult) {
				requestStartTalk();
			}
		}

		@Override
		public void onStart() {
			LOG.writeMsg(this, LOG.MODE_VOICE,
					"mWakeupListener.onStart, mWakeupRecordingState : "
							+ mWakeupRecordingState + ", mStartTalkFlag : "
							+ mStartTalkFlag);
			// mStartTalkFlag = false;
			mWakeupRecordingState = true;
			mWakeupResult = false;
			notifyWakeupRunning();

		}

		@Override
		public void onInitDone() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener.onInitDone");
			mWakeUpInitDone = true;
			requestStartWakeup();
		}

		@Override
		public void onError(int code, String message) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "mWakeupListener.onError: code "
					+ code + ", message " + message);
			mWakeupResult = false;
			mTalkServicePresentor.stopWakeup();
		}
	};

	public SessionManager(VoiceViewService windowService) {
		this.windowService = windowService;
		this.mUserPreference = new UserPreference(windowService);
		mPowerManager = (PowerManager) windowService
				.getSystemService(Context.POWER_SERVICE);
		mAudioManager = (AudioManager) windowService
				.getSystemService(Context.AUDIO_SERVICE);
		mNotificationManager = (NotificationManager) windowService
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mTalkServicePresentor = new TalkServicePresentor(windowService, this,
				mWakeupListener, mTTSListener);
		mPreference = new UserPreference(windowService);
		mBeepPlayer = new BeepPlayer(windowService);
		mBeepPlayer.setVolume(0.8f);
		init();
	}

	public void setRelativedView(VoiceSessionContainer mSessionContainer,
			VoiceBottomMicroControlBar mVControlBar) {
		mSessionViewContainer = mSessionContainer;
		mMicrophoneControl = mVControlBar;
		setListener();
	}

	private void init() {
		loadingView = View.inflate(windowService, R.layout.view_loading, null);
		showHelpView();
	}

	public void showInitView() {
		if ((TalkService.TALK_INITDONE | mRecognitionIintDone)
				&& mRecognitionDataDone) {
			windowService.dimissView(loadingView);
			// LogUtil.d(TAG, "showInitView dismiss");
		} else {
			LogUtil.d(TAG, "showInitView removeAllSessionViews");
			if (mSessionViewContainer != null) {
				mSessionViewContainer.removeAllSessionViews();
				mMicrophoneControl.setVisibility(View.GONE);
				mMicrophoneControl.onPrepare();
				mSessionViewContainer.removeAllSessionViews();
			}
			TextView tx = (TextView) loadingView.findViewById(R.id.init_text);
			tx.setText(R.string.grammar_data_compile);
			windowService.addPrepareView(loadingView);
		}
	}

	private void showHelpView() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "showHelpView: FIELD_TYPE "
				+ FIELD_TYPE);
		View view = null;
		if (mSessionViewContainer != null) {
			mSessionViewContainer.removeAllSessionViews();
		}

		if (FIELD_TYPE == FIELD_TYPE_NORMAL) {
			windowService.hideCancelBtn(true);
			view = getFunctionView();
			view.setVisibility(View.VISIBLE);
		} else if (FIELD_TYPE == FIELD_TYPE_SMS) {
		} else if (FIELD_TYPE == FIELD_TYPE_CALL) {
			windowService.hideCancelBtn(true);
			view = getReceiveCALLView();
		}

		FIELD_TYPE = FIELD_TYPE_NORMAL;
		Message msg = new Message();

		if (view != null) {
			msg.what = SessionPreference.MESSAGE_ADD_ANSWER_VIEW;
			msg.obj = view;

		} else {
			msg.what = SessionPreference.MESSAGE_SESSION_CANCEL;
		}
		mSessionManagerHandler.sendMessage(msg);
	}

	private void setListener() {
		if (mSessionViewContainer != null) {
			mSessionViewContainer.setTTSListener(new ITTSStatusListener() {

				@Override
				public void onStatusChanged(int oldStatus, String obj) {
					switch (oldStatus) {
					case SessionContainer.UNENABLED:
						mPreference.putBoolean(UserPreference.KEY_ENABLE_TTS,
								true);
						mSessionViewContainer.onTTSStop();
						break;

					case SessionContainer.STOPPED:
						// playTTS(obj);
						break;

					case SessionContainer.BUFFERING:
						// cancelTTS();
						break;
					case SessionContainer.PLAYING:
						mPreference.putBoolean(UserPreference.KEY_ENABLE_TTS,
								false);
						// cancelTTS();
						break;
					}
				}
			});
		}

		if (mMicrophoneControl != null) {
			mMicrophoneControl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LOG.writeMsg(this, LOG.MODE_VOICE,
							"mMicrophoneControl on click: "
									+ mCurrentTalkStatus);
					/*
					 * if (v.getId() == R.id.voice_control_cancel_btn) {
					 * cancelSession(); String operationCancel = windowService
					 * .getString(R.string.operation_cancel);
					 * playTTS(operationCancel);
					 * mMicrophoneControl.setAnswerText(MsgSource.Rebot,
					 * operationCancel); } else
					 */if (v.getId() == R.id.voice_start_speak_btn) {
						// startTalk("press talk button to start talk");
						if (windowService != null) {
							windowService
									.startTalk("VoiceViewService, press talk button to start talk");
						} else {
							startTalk("SessionManager, press talk button to start talk");
						}

					} else {
						switch (mCurrentTalkStatus) {
						case TALK_STATUS_IDLE:
							if (mWakeupRecordingState) {
								// 模拟唤醒成功
								mWakeupResult = true;
								mTalkServicePresentor.stopWakeup();
							} else {
								requestStartTalk();
							}
							break;
						case TALK_STATUS_LISTENING:
							stopTalk();
							break;
						case TALK_STATUS_WAITING:
							cancelTalk(true);
							String cancel = windowService
									.getString(R.string.click_mic);
							Toast.makeText(windowService, cancel,
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				}
			});
		}
		PhoneStateReceiver.registerPhoneStateListener(mPhoneStateListener);
	}

	private void disableKeyguard() {
		KeyguardManager km = (KeyguardManager) windowService
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock("WakeupKeyguard");
		kl.disableKeyguard();
	}

	private void acquireWakeLock(int flags, String tag) {
		if (mWakeLock == null || !mWakeLock.isHeld()) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "acquireWakeLock:flags " + flags
					+ ",tag " + tag);
			mWakeLock = mPowerManager.newWakeLock(flags, tag);
			mWakeLock.acquire();
		}
	}

	// 释放设备电源锁i
	private void releaseWakeLock() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "releaseWakeLock");
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@SuppressWarnings("deprecation")
	private void notifyWakeupRunning() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "notifyWakeupRunning");
		String tickerText = windowService.getString(R.string.wakeup_started);
		Notification notification = new Notification(R.drawable.ic_launcher,
				tickerText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		notification.setLatestEventInfo(windowService, tickerText,
				windowService.getString(R.string.wakeup_started_prompt), null);
		mNotificationManager.notify(0, notification);
	}

	private void cancelWakeupNotification() {
		mNotificationManager.cancel(0);
	}

	/**
	 * @Description : TODO 主动启动语音识别流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private void startTalk() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "startTalk");
		cancelTTS();
		cancelTalk(false);

		// mWindowService.getActivity().show();

		if (mTalkServicePresentor != null) {

			if (mTalkType == mTalkType_NORMAL) {
				if (mType.startsWith("MUTIPLE")) {
					mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
				} else if (mType.startsWith("INPUT_FREETEXT")) {
					mTalkServicePresentor.setRecognizerType(TYPE_FREETEXT);
				} else {
					mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
				}
				mType = "";
			} else {
				mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
			}
			mCurrentTalkStatus = TALK_STATUS_LISTENING;
			mBeepPlayer.playBeepSound(R.raw.start_tone, false, null);
			mTalkServicePresentor.startTalk();
			acquireWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, "SessionWakeLock");
			disableKeyguard();
		} else {
			mBeepPlayer.playBeepSound(R.raw.error_tone, false, null);
			if (mMicrophoneControl != null) {
				mMicrophoneControl.setAnswerText(MsgSource.Rebot,
						windowService.getString(R.string.unknown_error));
			}
		}
	}

	public void startTalk(String from) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "startTalk:from " + from);
		requestStartTalk();
	}

	/**
	 * @Description : TODO 主动停止录音
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void stopTalk() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "stopTalk");
		mBeepPlayer.stop();
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.stopTalk();
		}
		waitForRecognitionResult();
	}

	/**
	 * @Description : TODO 主动取消识别流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public boolean cancelTalk(boolean callback) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "cancelTalk: callback " + callback);

		/*
		 * if (!mRecognitionRecordingState) { return false; }
		 */
		mBeepPlayer.stop();
		resetTalk();
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.cancelTalk(callback);
		}

		if (!callback) {
			mRecognitionRecordingState = false;
		}
		return true;
	}

	public void releaseSessionCenterSession() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "releaseSessionCenterSession");
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.releaseSession();
		}
	}

	public void cancelSession() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "cancelSession");
		if (mTalkType == mTalkType_NORMAL) {
			mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
		}
		mTTSEndRunnable = null;
		releaseSessionCenterSession();
		releaseCurrentSession();
		cancelTalk(false);
		cancelTTS();
		mBeepPlayer.stop();
		// 当收到MESSAGE_SESSION_DONE的消息再removeView，否则只发消息的session将不会removeView
		// mSessionViewContainer.removeAllSessionViews();
		mSessionManagerHandler
				.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		mSessionManagerHandler
				.sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
	}

	public void cancelSessionWithTTS() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "cancelSession with TTS");
		cancelSession();
		playTTS(windowService.getString(R.string.operation_cancel));
	}

	/**
	 * @Description : TODO 等待识别结果流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private void waitForRecognitionResult() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "waitForRecognitionResult");

		/*
		 * if (TALK_STATUS_WAITING == mCurrentTalkStatus) { return; }
		 */
		mCurrentTalkStatus = TALK_STATUS_WAITING;
		mBeepPlayer.playBeepSound(R.raw.wait_tone, true, null);

		// sound.play(int_wait, 1, 1, 0, 0, 1);
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onProcess();
			mMicrophoneControl.setEnabled(true);
		}
	}

	/**
	 * @Description : 恢复初始状态
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void resetTalk() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "resetTalk");
		if (mCurrentTalkStatus == TALK_STATUS_IDLE) {
			return;
		}

		mCurrentTalkStatus = TALK_STATUS_IDLE;
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onIdle(false);
			mMicrophoneControl.setEnabled(true);
		}
	}

	public void onPause() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onPause");
		cancelTTS();
		mBeepPlayer.stop();
		cancelTalk(false);
	}

	public void onResume() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onResume");
	}

	// 初始化完成
	@Override
	public void onTalkInitDone() {
		LOG.writeMsg(this, LOG.MODE_VOICE,
				"SessionManager.class, onTalkInitDone()");
		mRecognitionIintDone = true;
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onIdle(true);
			mMicrophoneControl.setEnabled(true);
		}

		if (mFunctionView != null) {
			mFunctionView.initFunctionViews();
		}
	}

	// 语法数据编译完成
	@Override
	public void onTalkDataDone() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkDataDone mIsFirstInitDone : "
				+ mIsFirstInitDone);
		mRecognitionDataDone = true;

		LOG.writeMsg(this, LOG.MODE_VOICE, "TalkService.TALK_INITDONE : "
				+ TalkService.TALK_INITDONE);
		LOG.writeMsg(this, LOG.MODE_VOICE, "mRecognitionIintDone : "
				+ mRecognitionIintDone);
		LOG.writeMsg(this, LOG.MODE_VOICE, "mRecognitionDataDone : "
				+ mRecognitionDataDone);

		if ((TalkService.TALK_INITDONE | mRecognitionIintDone)
				&& mRecognitionDataDone) {
			if (loadingView != null && loadingView.isShown()) {
				LOG.writeMsg(this, LOG.MODE_VOICE, "loadingView.isShown()");
				windowService.dimissView(loadingView);
			}

			if (mIsFirstInitDone) {
				requestStartWakeup();
			}

		} else {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkDataDone  else  : ");
		}
	}

	@Override
	public void onTalkStart() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkStart");
		// mWindowService.show();
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onPrepare();
			mMicrophoneControl.setEnabled(true);
		}
	}

	@Override
	public void onTalkStop() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkStop");
		mBeepPlayer.playBeepSound(R.raw.stop_tone, false, null);
		waitForRecognitionResult();
	}

	@Override
	public void onTalkRecordingStart() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkRecordingStart");
		mRecognitionRecordingState = true;
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onRecording();
		}
	}

	@Override
	public void onTalkRecordingStop() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkRecordingStop");
		mRecognitionRecordingState = false;
	}

	@Override
	public void onTalkCancel() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkCancel");
		mRecognitionRecordingState = false;
		cancelSessionWithTTS();
	}

	@Override
	public void onTalkResult(String result) {
		//语音识别结果
//		mTalkResult = 
		LOG.writeMsg(this, LOG.MODE_VOICE, "onTalkResult: result " + result);
		LOG.writeMsg(this, LOG.MODE_VOICE, "语音识别结果： " + result);
//		if (mMicrophoneControl != null) {
//			mMicrophoneControl.setAnswerText(MsgSource.Mine, result);
//		}
	}

	@Override
	public void onSessionProtocal(String protocol) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onSessionProtocal: protocol "
				+ protocol);
		
		Log.i(TAG, "语义 ： " + protocol);
		resetTalk();
		if (protocol != null && (protocol.indexOf("\"type\":\"WAITING\"") < 0)) {
			mBeepPlayer.playBeepSound(R.raw.stop_tone, false, null);
		}
		createSession(protocol);
	}

	/**
	 * @Description : onActiveStatusChanged
	 * @Author : Brant˝Ó
	 * @CreateDate : 2014-11-25
	 * @see com.spt.carengine.voice.assistant.talk.ITalkServicePresentorListener#onActiveStatusChanged(int)
	 * @param flag
	 *            :0注册,1限制注册,2不允许注册,4没有试用过,5,正在试用,6,试用期已过
	 */
	@Override
	public void onActiveStatusChanged(int flag) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onActiveStatusChanged:flag " + flag);
	}

	@Override
	public void isActive(boolean status) {
		LOG.writeMsg(this, LOG.MODE_VOICE,
				"talk service presentor listener is active: " + status + "");
	}

	private void playTTS(String tts) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "playTTS:tts " + tts);
		mTalkServicePresentor.playTTS(tts);
	}

	private void playTTSWithEndRunnable(String tts, Runnable runnable) {
		mTTSEndRunnable = runnable;
		playTTS(tts);
	}

	private void cancelTTS() {
		mTalkServicePresentor.cancelTTS();
	}

	private void requestStartTalk() {
		if (!mStartTalkFlag) {
			Toast.makeText(windowService.getApplicationContext(), R.string.voice_init,
					Toast.LENGTH_LONG).show();
			cancelTalk(false);
			if (mMicrophoneControl != null) {
				mMicrophoneControl.onIdle(false);
				mMicrophoneControl.setEnabled(true);
			}
			return;
		}

		LOG.writeMsg(this, LOG.MODE_VOICE, "requestStartTalk");
		if (mTalkServicePresentor != null) {
			if (mTalkType == mTalkType_NORMAL) {
				if (mType.startsWith("MUTIPLE")) {
					mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
				} else if (mType.startsWith("INPUT_FREETEXT")) {
					mTalkServicePresentor.setRecognizerType(TYPE_FREETEXT);
				} else {
					mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
				}
				mType = "";
			} else {
				mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
			}
		}

		LOG.writeMsg(this, LOG.MODE_VOICE, "mSessionStatus: " + mSessionStatus);

		if (mMicrophoneControl != null) {
			mMicrophoneControl.setVisibility(View.VISIBLE);
		}
		if (mLastSessionDone
				|| TextUtils.isEmpty(mSessionStatus)
				|| SessionPreference.VALUE_SESSION_END.equals(mSessionStatus)
				|| mTalkType == mTalkType_SMS
				|| (SessionPreference.VALUE_SESSION_SHOW.equals(mSessionStatus) && SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW
						.equals(mType))) {
			// mSessionViewContainer.removeAllSessionViews();
			showHelpView();
		}

		LOG.writeMsg(this, LOG.MODE_VOICE, "startTalk mTalkType : " + mTalkType
				+ ", mWakeupRecordingState : " + mWakeupRecordingState
				+ ", mWakeupResult : " + mWakeupResult);

		if (mWakeupRecordingState) {
			mWakeupResult = true;
			mTalkServicePresentor.stopWakeup();
		} else {
			startTalk();
		}
	}

	private void releaseCurrentSession() {
		LOG.writeMsg(this, LOG.MODE_VOICE,
				"releaseCurrentSession, mCurrentSession : " + mCurrentSession);
		setSessionEnd(true);
		mBeepPlayer.stop();
		if (mCurrentSession != null) {
			mCurrentSession.release();
			mCurrentSession = null;
		}
	}

	public void requestStartWakeup() {
//		mStartTalkFlag = true;
		boolean enableWakeup = mUserPreference
				.getBoolean(UserPreference.KEY_ENABLE_WAKEUP,
						UserPreference.DEFAULT_WAKEUP);
		LOG.writeMsg(this, LOG.MODE_VOICE, "唤醒功能，enableWakeup : "
				+ enableWakeup);
		if (enableWakeup) {
			requestStartWakeup(TalkService.WAKEUP_INITDONE | mWakeUpInitDone,
					TalkService.TALK_INITDONE | mRecognitionIintDone,
					mRecognitionDataDone, mRecognitionRecordingState,
					mWakeupRecordingState);
		}
	}

	public void requestStopWakeup() {
		if (mTalkServicePresentor != null && mWakeupRecordingState) {
			LOG.writeMsg(this, LOG.MODE_VOICE,
					"requestStopWakeup mWakeupRecordingState : "
							+ mWakeupRecordingState);
			mTalkServicePresentor.stopWakeup();
		}
	}

	/**
	 * @Description : requestStartWakeup
	 * @Author : Brant
	 * @CreateDate : 2014-2-27
	 * @param wakeupInitDone
	 * @param recognitionInitDone
	 * @param recordingState
	 * @param grammarExist
	 */
	private void requestStartWakeup(boolean wakeupInitDone,
			boolean recognitionInitDone, boolean talkDataDone,
			boolean recordingState, boolean wakeupRecordingState) {
		LOG.writeMsg(this, LOG.MODE_VOICE,
				"requestStartWakeup: wakeupInitDone " + wakeupInitDone
						+ ", recognitionInitDone " + recognitionInitDone
						+ ", talkDataDone " + talkDataDone
						+ ", recordingState " + recordingState
						+ ", wakeupRecordingState " + wakeupRecordingState);

		if (wakeupInitDone && recognitionInitDone && talkDataDone
				&& !recordingState && !wakeupRecordingState) {
			mIsFirstInitDone = false;
			if (mTalkServicePresentor != null) {
				//wuwen add by 2015.09.02 start 
				mStartTalkFlag = false;
				mHandler.removeCallbacks(mStartTalkRunnable);
				mHandler.postDelayed(mStartTalkRunnable, 1000);
				//end
				
				mTalkServicePresentor.startWakeup();
			} else {
				LOG.writeMsg(this, LOG.MODE_VOICE,
						"mTalkServicePresentor null!");
			}
		}
	}

	public void onDestroy() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onDestroy");
		cancelSession();
		mTTSListener = null;
		mWakeupListener = null;
		mRecognitionDataDone = false;
		TalkService.TALK_INITDONE = false;
		TalkService.WAKEUP_INITDONE = false;
		resetTalk();

		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.onDestroy();
		}
		if (mSessionViewContainer != null) {
			mSessionViewContainer.removeAllSessionViews();
		}
		mBeepPlayer.release();
		if (mMicrophoneControl != null) {
			mMicrophoneControl.onDestroy();
		}
		mMicrophoneControl = null;

		if (mFunctionView != null) {
			mFunctionView.release();
			mFunctionView = null;
		}
		// cancel notification
		cancelWakeupNotification();
		mNotificationManager.cancelAll();
		mNotificationManager = null;
		PhoneStateReceiver.unregisterPhoneStateListener(mPhoneStateListener);
		mPhoneStateListener = null;
		mAudioManager = null;
	}

	public boolean onBackPressed() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onBackPressed");
		boolean result = false;
		// cancelTTS();
		if (mCurrentSession != null) {
			mCurrentSession = null;
			mTalkServicePresentor.setProtocal(VoiceAssistant.BACK_PROTOCAL);
			if (mSessionViewContainer != null) {
				mSessionViewContainer.removeAllSessionViews();
			}
			String ttsString = KnowledgeMode.getKnowledgeAnswer(windowService,
					KnowledgeMode.KNOWLEDGE_STAGE_HELP);
			LOG.writeMsg(this, LOG.MODE_VOICE,
					"onBackPressed(), sessionViewContainer add answer view.");
			if (mSessionViewContainer != null) {
				mSessionViewContainer.addAnswerView(ttsString);
			}
			View view = getFunctionView();
			if (mSessionViewContainer != null) {
				mSessionViewContainer.addSessionView(view, false);
			}
			cancelTalk(true);
			result = true;
		}

		return result;
	}

	public void onNetWorkChanged() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onNetWorkChanged");
		if (mFunctionView != null) {
			mFunctionView.initFunctionViews();
		}
	}

	private void setSessionEnd(boolean end) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "setSessionEnd:end " + end);
		mTalkType = mTalkType_NORMAL;
		mLastSessionDone = end;
	}

	/**
	 * @Description : TODO 核心方法，根据协议生成Session对象，并处理协议分发
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param protocal
	 */
	private void createSession(String protocal) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "createSession() protocal : "
				+ protocal);

		if (mLastSessionDone) {
			releaseCurrentSession();
		}
		JSONObject obj = JsonTool.parseToJSONObject(protocal);
		BaseSession base = null;
		boolean isNeedClear = true;
		String text  = "";
		String domain = "";
		String rebotAnswer = "";
		
		if (obj != null) {
			mSessionStatus = JsonTool.getJsonValue(obj,
					SessionPreference.KEY_DOMAIN, "");
			mType = JsonTool.getJsonValue(obj, SessionPreference.KEY_TYPE, "");
			domain = JsonTool.getJsonValue(obj, SessionPreference.KEY_DOMAIN, "");
			
			if(SessionPreference.VALUE_SESSION_SHOW.equals(domain) || 
					SessionPreference.VALUE_SESSION_END.equals(domain)){
				text =  JsonTool.getJsonValue(obj, SessionPreference.KEY_TEXT, "");
				mMicrophoneControl.setAnswerText(MsgSource.Mine, text);
				
				rebotAnswer =  JsonTool.getJsonValue(obj, SessionPreference.KEY_ANSWER, "");
				mMicrophoneControl.setAnswerText(MsgSource.Rebot, rebotAnswer);
			}

			LOG.writeMsg(this, LOG.MODE_VOICE, "mSessionStatus: "
					+ mSessionStatus);
			// TODO 如果是一个新领域流程的开始，是否需要将历史记录清理（参数控制）
			if (SessionPreference.VALUE_SESSION_BENGIN.equals(mSessionStatus)
					&& mPreference.getEnableCleanViewWhenSessionBegin()) {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.removeAllSessionViews();
				}
			} else if (SessionPreference.VALUE_SESSION_END
					.equals(mSessionStatus)) {
				onSessionDone();
			}

			// TODO 根据type进行实例创建
			if (SessionPreference.VALUE_TYPE_SOME_NUMBERS.equals(mType)) {
				base = new MultipleNumbersShowSession(windowService,
						mSessionManagerHandler);
				windowService.hideCancelBtn(false);

			} else if (SessionPreference.VALUE_TYPE_SOME_PERSON.equals(mType)) {
				base = new MultiplePersonsShowSession(windowService,
						mSessionManagerHandler);
				windowService.hideCancelBtn(false);

			} else if (SessionPreference.VALUE_TYPE_CALL_ONE_NUMBER
					.equals(mType)) {
				base = new CallConfirmShowSession(windowService,
						mSessionManagerHandler);
				windowService.hideCancelBtn(false);

			} else if (SessionPreference.VALUE_TYPE_CALL_OK.equals(mType)) {
				base = new CallShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_INPUT_MSG_CONTENT
					.equals(mType)) {
				if (mCurrentSession != null
						&& mCurrentSession instanceof SmsInputShowSession) {
					windowService.hideCancelBtn(true);
					base = mCurrentSession;
					base.mIsNeedAddTextView = false;
					isNeedClear = false;

				} else {
					windowService.hideCancelBtn(true);
					base = new SmsInputShowSession(windowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_MUTIPLE_UNSUPPORT
					.equals(mType)
					|| SessionPreference.VALUE_TYPE_CONFIRM_UNSUPPORT
							.equals(mType)) {
				isNeedClear = false;
				base = new UnsupportShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_UNSUPPORT_BEGIN_SHOW
					.equals(mType)) {
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					showHelpView();
					base = new UnsupportShowSession(windowService,
							mSessionManagerHandler);
				} else {
					showHelpView();
					releaseCurrentSession();
					base = new UnsupportEndSession(windowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW
					.equals(mType)) {
				showHelpView();
				if (mSessionViewContainer != null) {
					mSessionViewContainer.removeAllSessionViews();
				}
				releaseCurrentSession();
				base = new UnsupportEndSession(windowService,
						mSessionManagerHandler);

			}/*
			 * else if (SessionPreference.VALUE_TYPE_SMS_OK.equals(mType)) {
			 * base = new SmsShowSession(windowService, mSessionManagerHandler);
			 * }
			 */else if (SessionPreference.VALUE_TYPE_WAITING.equals(mType)) {
				base = new WaitingSession(windowService, mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_WEATHER_SHOW.equals(mType)) {
				base = new WeatherShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_WEB_SHOW.equals(mType)) {
				base = new WebShowSession(windowService, mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_TRANSLATION_SHOW
					.equals(mType)) {
				base = new TranslationShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_STOCK_SHOW.equals(mType)) {
				if (mMicrophoneControl != null) {
					mMicrophoneControl.setAnswerText(MsgSource.Rebot, "");
				}
				base = new StockShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_MUSIC_SHOW.equals(mType)) {
				base = new MusicShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_ROUTE_SHOW.equals(mType)) {
				windowService.hideCancelBtn(false);
				base = new RouteShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_POSITION_SHOW.equals(mType)) {
				base = new PositionShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_APP_LAUNCH.equals(mType)) {
				base = new AppLaunchSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_APP_UNINSTALL.equals(mType)) {
				base = new AppUninstallSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_APP_EXIT.equals(mType)) {
				base = new AppExitSession(windowService, mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_SETTING.equals(mType)) {
				base = new SettingSession(windowService, mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_REMINDER_CONFIRM
					.equals(mType)) {
				base = new ReminderConfirmSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_REMINDER_OK.equals(mType)) {
				base = new ReminderOkSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_INPUT_CONTACT.equals(mType)) {
				base = new TalkShowMiddleSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_TALK_SHOW.equals(mType)) {
				showHelpView();
				base = new TalkShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_POI_SHOW.equals(mType)) {
				if (mCurrentSession != null
						&& mCurrentSession instanceof PoiShowSession) {
					base = mCurrentSession;
					isNeedClear = false;

				} else {
					base = new PoiShowSession(windowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_MULTIPLE_SHOW.equals(mType)) {
				base = new MultipleShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_ERROR_SHOW.equals(mType)) {
				base = new ErrorShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_ALARM_SHOW.equals(mType)) {
				base = new AlarmSettingSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_CONTACT_ADD.equals(mType)) {
				base = new ContactAddSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_APP_MUTIPLEP_SHOW
					.equals(mType)) {
				base = new MultipleApplicationShowSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_BROADCAST_SHOW
					.equals(mType)) {
				base = new BroadcastSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_MUTIPLE_LOCATION.equals(mType)) {
				base = new MultipleLocationSession(windowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_UI_HANDLE_SHOW
					.equals(mType)) {
				base = new UIHandleShowSession(windowService,
						mSessionManagerHandler);
			}
		}

		if (base != null) {
			if (isNeedClear) {
				if (mSessionViewContainer != null) {
					mSessionViewContainer.clearTemporaryViews();
				}
			}
			if (mCurrentSession != null
					&& (mCurrentSession instanceof CallConfirmShowSession)) {
				mCurrentSession.release();
			}
			mCurrentSession = base;
			mCurrentSession.putProtocol(obj);
		}
	}

	public FunctionView getFunctionView() {
		LayoutInflater layoutInflater = (LayoutInflater) windowService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFunctionView = (FunctionView) layoutInflater.inflate(
				R.layout.funtion_main, null);
		if (mFunctionView != null) {
			mFunctionView.setTextViews();
		}
		return mFunctionView;
	}

	/** 2014-12-4 yujun */
	public ReceiveCallView getReceiveCALLView() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "--getReceiveCALLView--");
		mReceiveCallView = new ReceiveCallView(windowService, mName, mNumber,
				mSessionManagerHandler);
		mReceiveCallView.initReceiveCallView();
		return mReceiveCallView;
	}

	public void onFunctionClick() {
		onBackPressed();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurrentSession != null) {
			mCurrentSession.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void replySms(String name, String phone) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "replySms : " + name + " phone : "
				+ phone);
		String message = windowService.getString(R.string.sms_to, name);
		String format = "{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.sms\",\"code\":\"SMS_SEND\",\"semantic\":{\"intent\":{\"number\":\"{1}\",\"name\": \"{2}\"}},\"history\":\"cn.yunzhisheng.sms\"}";
		String replyMessage = DataTool.formatString(format, message, phone,
				name);
		Message msg = mSessionManagerHandler
				.obtainMessage(SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL);
		msg.obj = replyMessage;
		mSessionManagerHandler.sendMessage(msg);
	}

	private void onSessionDone() {
		LOG.writeMsg(this, LOG.MODE_VOICE, "onSessionDone");
		releaseWakeLock();
		releaseSessionCenterSession();
	}

}
