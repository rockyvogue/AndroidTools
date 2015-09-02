
package com.spt.carengine.define;

/**
 * 常量定义
 * 
 * @author Administrator
 */
public interface Define {

    public final String TAG = "wujie";

    public final int HOME_BTN = 0x1001;
    public final String ACTION_ACTIVATE_UPLOAD = "com.yrc.up.activate";// 激活
    public final String ACTION_GPS_UPLOAD = "com.yrc.up.gpsinfo";// 位置信息
    public final String ACTION_PHOTO_UPLOAD = "com.yrc.up.photo";// 图片
    public final String ACTION_VIDEO_UPLOAD = "com.yrc.up.video";// 录像
    public final String ACTION_DEVICE_SETTINGS = "com.yrc.dev.settings";// 设置
    public final String ACTION_XG_REGSTATE = "com.yrc.xg.regist";// 信鸽注册
    public final String ACTION_UPLOADFILE_RESULT = "com.yrc.aly.uploadfile";// 上传文件
    public final String ACTION_SHOW_DIALOG = "com.yrc.showDialog";// 用于弹出对话框
    public final String ACTION_CLOSE_ACTIVITY = "com.android.close.activity";// 关闭Activity

    public final String ALIYUN_ACCESSKEY = "P90sA7J0kBxAtMoj"; // 阿里云accessKey
    public final String ALIYUN_SCRECTKEY = "U882gUTqtAA25eJjNdlgQKdZ59K41B"; // 阿里云screctKey
    public final String ALIYUN_OSS_BUCKET = "android-test-yrc";// Bucket
    public final String ALIYUN_BUCKET_HOST = "oss-cn-shenzhen.aliyuncs.com";// Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名

    public final String XG_APP_SECRET_KEY = "5781c9cd55793dd4f53b984e6f05a8f0";// XG_SECRET_KEY

    // 云之声的appkey 和 appSecret
	public final String YZS_APPKEY 					= "4qoewnounjdytflxv5cyrtjgzwvsdfyd74frfiif";
	public final String YZS_SECRET 					= "993368b3d99c0994c593c032de95533a";

    //
    /**
     * 上行API接口编号
     * 
     * @author Administrator
     */
    public interface UP_WEB_API {
        /** 激活 **/
        final int API_ACTIVATE = 0x01;
        /** 上传图片 **/
        final int API_UPLOAD_PHOTO = 0x02;
        /** 上传视频 **/
        final int API_UPLOAD_VIDEO = 0x03;
        /** 上传位置 **/
        final int API_UPLOAD_LOCATION = 0x04;
    }

    // 上行命令接口
    /** 激活 **/
    final String API_FUNC_CAR_TO_SERVER_ACTIVATE = "activate";
    /** 上传图片 **/
    final String API_FUNC_CAR_TO_SERVER_PHOTO = "photo";
    /** 上传视频 **/
    final String API_FUNC_CAR_TO_SERVER_VIDEO = "video";
    /** 上传位置 **/
    final String API_FUNC_CAR_TO_SERVER_LOCATION = "location";

    // 下行命令接口
    /** 上传图片 **/
    final String API_FUNC_SERVER_TO_CAR_PHOTO = "getPhoto";
    /** 上传视频 **/
    final String API_FUNC_SERVER_TO_CAR_VIDEO = "getVideo";
    /** 上传位置 **/
    final String API_FUNC_SERVER_TO_CAR_LOCATION = "getLocation";
    /** 设置设备相关参数 **/
    final String API_FUNC_SERVER_TO_CAR_SETTINGS = "settings";

    // 界面模式
    public interface PAGEMODE {
        final int PAGEMODE_NORMAL = 0x01;// 默认界面
        final int PAGEMODE_BLUETOOTH = 0x02;// 蓝牙
        final int PAGEMODE_DIRRECORD = 0x03;// 行车记录
        final int PAGEMODE_FMSEND = 0x04;// FM发射
        final int PAGEMODE_NAVI = 0x05;// 导航
        final int PAGEMODE_SETTINGS = 0x06;// 设置
        final int PAGEMODE_APPLIST = 0x07;// 程序列表
        final int PAGEMODE_FILEEXPLORER = 0x08;// 文件浏览
    }

    // Activity关闭之后返回的结果
    public interface ACTIVITY_RESULT {
        final int CODE_SUCESS_NODATA = 0x01;// 返回成功的结果，但是无数据返回
        final int CODE_FAILD_NODATA = 0x02;// 返回失败的结果，但是无数据返回
        final int CODE_SUCESS_HASDATA = 0x03;// 返回成功的结果，但是有数据返回
        final int CODE_FAILD_HASDATA = 0x04;// 返回失败的结果，但是有数据返回

        final int CODE_YES = 0x05;// 点击了YES
        final int CODE_NO = 0x06;// 点击了NO
    }

    public interface MUSIC_CTRL {
        final int PLAY = 0x00;
        final int PAUSE = 0x01;
        final int STOP = 0x02;
        final int PRE = 0x03;
        final int NEXT = 0x04;

        final String ACTION_MUSIC_CTRL = "com.yrc.music.ctrl";
        final String VALUE_CTRL_CMD = "music_ctrlcmd";
        final String VALUE_MUSIC_PATH = "music_path";
    }

	public static final String WEB_IP_HTTP_URL = "http://wechat.newman.mobi/cc/api/";// 服务端网址
	
	public interface DEVICE_NODES {
		/**FM发射开关**/
		final String SYS_KT0806_FM_SWITCH	= "/sys/class/KT0806/KT0806/fm_on";
		/**FM发射频率设置**/
		final String SYS_KT0806_FM_FREQUENCY	= "/sys/class/KT0806/KT0806/fm_hz"; 
		/**蓝牙模块设备节点**/
		final String SYS_KT0806_BT_STATUS	= "/sys/class/KT0806/KT0806/bt_status";
		/**雷达模块设备节点**/
		final String SYS_KT0806_DOG_STATUS	= "/sys/class/KT0806/KT0806/dog_status";

    }

	
	/***
	 * 语音定义
	 * @author Administrator
	 *
	 */
	public interface VOICE_ACTION {
		final String VOICE_ACTION_INIT = "cn.yunzhisheng.intent.action.INIT";
		
		final String VOICE_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.talk.onInitDone";
		final String VOICE_EVENT_ON_RECORDING_START = "cn.yunzhisheng.intent.talk.onRecordingStart";
		final String VOICE_EVENT_ON_RECORDING_STOP = "cn.yunzhisheng.intent.talk.onRecordingStop";
		final String VOICE_EVENT_ON_START = "cn.yunzhisheng.intent.talk.onTalkStart";
		final String VOICE_EVENT_ON_STOP = "cn.yunzhisheng.intent.talk.onTalkStop";
		final String VOICE_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.talk.onTalkCancel";
		final String VOICE_EVENT_ON_DATA_DONE = "cn.yunzhisheng.intent.talk.onDataDone";
		final String VOICE_EVENT_RESULT = "cn.yunzhisheng.intent.talk.onResult";
		final String VOICE_EVENT_ON_SESSION_PROTOCAL = "cn.yunzhisheng.intent.talk.onSessionProtocal";

		final String WAKEUP_EVENT_START = "cn.yunzhisheng.intent.wakeup.start";
		final String WAKEUP_EVENT_STOP = "cn.yunzhisheng.intent.wakeup.stop";
		final String WAKEUP_EVENT_CANCEL = "cn.yunzhisheng.intent.wakeup.cancel";
		final String SESSION_EVENT_REALEASE = "cn.yunzhisheng.intent.session.release";
		
		final String SET_RECOGNIZERTYPE = "cn.yunzhisheng.intent.talk.settype";

		final String WAKEUP_EVENT_ON_INITDONE = "cn.yunzhisheng.intent.wakeup.onInitDone";
		final String WAKEUP_EVENT_ON_START = "cn.yunzhisheng.intent.wakeup.onStart";
		final String WAKEUP_EVENT_ON_STOP = "cn.yunzhisheng.intent.wakeup.onStop";
		final String WAKEUP_EVENT_ON_SUCCESS = "cn.yunzhisheng.intent.wakeup.onSuccess";
		final String WAKEUP_EVENT_ON_ERROR = "cn.yunzhisheng.intent.wakeup.onError";

		final String TTS_EVENT_ON_PLAY_BEGIN = "cn.yunzhisheng.intent.tts.onPlayBegin";
		final String TTS_EVENT_ON_PLAY_END = "cn.yunzhisheng.intent.tts.onPlayEnd";
		final String TTS_EVENT_ON_ERROR = "cn.yunzhisheng.intent.tts.onError";
		final String TTS_EVENT_ON_CANCEL = "cn.yunzhisheng.intent.tts.onCancel";
		final String TTS_EVENT_ON_BUFFER = "cn.yunzhisheng.intent.tts.onBuffer";
		final String TTS_EVENT_ON_PLAYEND_STARTTALK = "cn.yunzhisheng.intent.tts.onPlayEnd_starttalk";
		final String TTS_EVENT_ON_TEXT = "cn.yunzhisheng.intent.tts.text";

		final String ACTIVE_EVENT_STATUS_CHANGED_MESSAGE = "cn.yunzhisheng.intent.active.data.status_changed";

		final String TALK_DATA_PROTOCAL = "cn.yunzhisheng.intent.talk.data.protocal";
		final String TALK_DATA_RESULT = "cn.yunzhisheng.intent.talk.data.result";
		final String TALK_DATA_ERROR_CODE = "cn.yunzhisheng.intent.talk.data.error_code";
		final String TALK_DATA_ERROR_MESSAGE = "cn.yunzhisheng.intent.talk.data.error_message";

		final String ACTIVE_DATA_STATUS = "cn.yunzhisheng.intent.active.data.status";
		final String ACTIVE_STATUS = "cn.yunzhisheng.intent.active.data.status.isactive";
		final String TTS_EVENT_ON_VOCIE_SPEED = "cn.yunzhisheng.intent.tts.setVoiceSpeed";
		
		final String ACTIVE_EVENT_ISACTIVE_MESSAGE = "cn.yunzhisheng.intent.active.data.isactive";
		

    }	
}
