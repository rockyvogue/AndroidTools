/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : AppUninstallSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.oem.RomControl;
import com.spt.carengine.voice.assistant.preference.SessionPreference;

import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified:
 * 2013-9-6: 实现基本功能
 */
public class AppUninstallSession extends BaseSession {
	public static final String TAG = "AppUninstallSession";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public AppUninstallSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		String packageName = getJsonValue(resultObject, "package_name", "");

		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--AppUninstallSession mAnswer : " + mAnswer + "--");
		//playTTS(mAnswer);
		playTTS(mTTS);

		RomControl.enterControl(mContext, RomControl.ROM_APP_UNINSTALL, packageName);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
