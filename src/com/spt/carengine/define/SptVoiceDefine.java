package com.spt.carengine.define;

public interface SptVoiceDefine {
	public  final String MAIN_ACTION = "action";
	public  final String MAIN_ACT_CLASS_NAME = "com.spt.carengine.activity.MainActivity";
	public  final String KWMUSIC_ACT_CLASS_NAME = "cn.kuwo.kwmusiccar";
	
	public  final String ACTION_START_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.START_HOME_TIMER";
	public  final String ACTION_STOP_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.STOP_HOME_TIMER";

	public  final String ACTION_START_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.START_WAKEUP";
	public  final String ACTION_STOP_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.STOP_WAKEUP";

	
	public final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	public final String ACTION_STOP_TALK = "cn.yunzhisheng.intent.action.STOP_TALK";
	public final String ACTION_CANCEL_TALK = "cn.yunzhisheng.intent.action.CANCEL_TALK";

	public final String ACTION_COMPILE_GRAMMER = "cn.yunzhisheng.intent.action.COMPILE_GRAMMER";

	
    public interface WINDOWS_SERVER {
    	public final String ACTION_TALK = "com.spt.carengine.voice.talk";
    	public final String ACTION_TALK_EXTRA_KEY = "data";
    	public final String KEY_START_TALK = "START_TALK";
    	public final String KEY_CANCEL_TALK = "START_TALK";
    }	
	
	
	
    public interface TALK {
    	public final String TALK_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.talk.onInitDone";
    	public final String TALK_EVENT_ON_RECORDING_START = "cn.yunzhisheng.intent.talk.onRecordingStart";
    	public final String TALK_EVENT_ON_RECORDING_STOP = "cn.yunzhisheng.intent.talk.onRecordingStop";
    	public final String TALK_EVENT_ON_START = "cn.yunzhisheng.intent.talk.onTalkStart";
    	public final String TALK_EVENT_ON_STOP = "cn.yunzhisheng.intent.talk.onTalkStop";
    	public final String TALK_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.talk.onTalkCancel";
    	public final String TALK_EVENT_ON_DATA_DONE = "cn.yunzhisheng.intent.talk.onDataDone";
    	public final String TALK_EVENT_RESULT = "cn.yunzhisheng.intent.talk.onResult";
    	public final String TALK_EVENT_ON_SESSION_PROTOCAL = "cn.yunzhisheng.intent.talk.onSessionProtocal";


    	public final String WAKEUP_EVENT_START = "cn.yunzhisheng.intent.wakeup.start";
    	public final String WAKEUP_EVENT_STOP = "cn.yunzhisheng.intent.wakeup.stop";
    	public final String WAKEUP_EVENT_CANCEL = "cn.yunzhisheng.intent.wakeup.cancel";
    	public final String SESSION_EVENT_REALEASE = "cn.yunzhisheng.intent.session.release";
    	
    	public final String SET_RECOGNIZERTYPE = "cn.yunzhisheng.intent.talk.settype";

    	public final String WAKEUP_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.wakeup.onInitDone";
    	public final String WAKEUP_EVENT_ON_START = "cn.yunzhisheng.intent.wakeup.onStart";
    	public final String WAKEUP_EVENT_ON_STOP = "cn.yunzhisheng.intent.wakeup.onStop";
    	public final String WAKEUP_EVENT_ON_SUCCESS = "cn.yunzhisheng.intent.wakeup.onSuccess";
    	public final String WAKEUP_EVENT_ON_ERROR = "cn.yunzhisheng.intent.wakeup.onError";

    	public final String TTS_EVENT_ON_PLAYEND_STARTTALK = "cn.yunzhisheng.intent.tts.onPlayEnd_starttalk";
    	public final String TTS_EVENT_ON_PLAY_TEXT = "cn.yunzhisheng.intent.tts.text";
    	public final String TTS_EVENT_ON_PLAY_BEGIN = "cn.yunzhisheng.intent.tts.onPlayBegin";
    	public final String TTS_EVENT_ON_PLAY_END = "cn.yunzhisheng.intent.tts.onPlayEnd";
    	public final String TTS_EVENT_ON_ERROR = "cn.yunzhisheng.intent.tts.onError";
    	public final String TTS_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.tts.onCancel";
    	public final String TTS_EVENT_ON_BUFFER = "cn.yunzhisheng.intent.tts.onBuffer";

    	public final String ACTIVE_EVENT_STATUS_CHANGED_MESSAGE = "cn.yunzhisheng.intent.active.data.status_changed";

    	public final String TALK_DATA_PROTOCAL = "cn.yunzhisheng.intent.talk.data.protocal";
    	public final String TALK_DATA_RESULT = "cn.yunzhisheng.intent.talk.data.result";
    	public final String TALK_DATA_ERROR_CODE = "cn.yunzhisheng.intent.talk.data.error_code";
    	public final String TALK_DATA_ERROR_MESSAGE = "cn.yunzhisheng.intent.talk.data.error_message";

    	public final String ACTIVE_DATA_STATUS = "cn.yunzhisheng.intent.active.data.status";
    	public final String ACTIVE_STATUS = "cn.yunzhisheng.intent.active.data.status.isactive";
    	public final String TTS_EVENT_ON_VOCIE_SPEED = "cn.yunzhisheng.intent.tts.setVoiceSpeed";
    	
    	public final String ACTIVE_EVENT_ISACTIVE_MESSAGE = "cn.yunzhisheng.intent.active.data.isactive";	
    }
	

	public  final String EXTRA_KEY_DISMISS_FLOAT_WINDOW = "DISMISS_FLOAT_WINDOW";
	public  final String EXTRA_KEY_START_TALK_FROM = "START_TALK_FROM";
	public  final String START_TALK_FROM_MAIN_ACTIVITY = "START_FROM_MAIN_ACTIVITY";
	public  final String START_TALK_FROM_FLOAT_MIC = "START_FROM_FLOAT_MIC";
	public  final String START_TALK_FROM_SHAKE = "START_FROM_SHAKE";
	public  final String START_TALK_FROM_SIMULATE = "START_FROM_SIMULATE";
	public  final String START_TALK_FROM_WIDGET = "START_FROM_WIDGET";
	public  final String START_TALK_FROM_WAKEUP = "START_FROM_WAKEUP";
	public  final String START_TALK_FROM_HARDWARE = "START_FROM_HARDWARE";
	public  final String START_TALK_FROM_OTHER = "START_FROM_OTHER";

	public  final String CANCEL_TALK_FROM_SCREEN_OFF = "CANCEL_FROM_SCREEN_OFF";
	public  final String CANCEL_TALK_FROM_BACK_KEY = "CANCEL_FROM_BACK_KEY";
	public  final String CANCEL_TALK_FROM_MANUAL = "CANCEL_FROM_MANUAL";
}
