package com.spt.carengine.voice;

import java.io.File;

import android.content.Context;
import cn.yunzhisheng.asr.utils.LogUtil;

public class AssistantPreference {
	public static final String TAG = "PrivatePreference";

	public static volatile float mRecordingVoiceVolume = 0.0f;

	// TODO 百度地图的key
	// 申请密钥地址：http://developer.baidu.com/map/
	public static final String BAIDU_MAP_API_KEY = "";

	public static final int APPLICATION_ID = 0x6CE898A4;
	public static final int REQUEST_CONTACT_CODE = 0x6CE898A5;
	public static final int REQUEST_START_INTENT_CODE = 0x6CE898A6;
	public static final int REQUEST_SELECT_DATETIME_CODE = 0x6CE898A7;

	public static int SCREEN_WIDTH = -1;
	public static int SCREEN_HEIGHT = -1;
	public static double SCREEN_SIZE = -1;

	public static int HTTP_NETWORK_TIMEOUT = 10000;

	public static String IMEI = "";
	public static String CURRENT_CITY = "";
	public static String VERSION = "";

	public static final String FOLDER_HOME = "YunZhiSheng/";
	public static final String FOLDER_ISHUOSHUO = "iShuoShuo/";
	public static final String FOLDER_DEBUG = "debug/";
	public static final String FOLDER_BACKUP = "backup/";
	public static final String FOLDER_PCM = "pcm/";
	public static final String FOLDER_DUMP = "dump/";
	public static final String FOLDER_COMPILER = "compiler/";
	public static final String FOLDER_IMG = "img/";
	public static final String FOLDER_MODEL = "model/";
	public static String FOLDER_PACKAGE_CACHE = "";
	public static String FOLDER_PACKAGE_FILES = "";

	public static void init(Context context) {
		if (context == null) {
			LogUtil.e(TAG, "init: context null!");
			return;
		}
		IMEI = RomDevice.getDeviceId(context);
		VERSION = RomDevice.getAppVersionName(context);
		FOLDER_PACKAGE_CACHE = context.getCacheDir().getAbsolutePath() + File.separator;
		FOLDER_PACKAGE_FILES = context.getFilesDir().getAbsolutePath() + File.separator;
	}
}
