package com.spt.carengine.voice.assistant.session;
import android.content.Context;
import android.os.Handler;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;

import org.json.JSONObject;

public class AppExitSession extends BaseSession {
	public static final String TAG = "AppExitSession";

	public AppExitSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		String packageName = getJsonValue(resultObject, "package_name", "");
		String className = getJsonValue(resultObject, "class_name", "");

		String url = getJsonValue(resultObject, "url", "");
		if (packageName != null && !"".equals(packageName) && className != null && !"".equals(className)) {
			addQuestionViewText(mQuestion);
			addAnswerViewText(mAnswer);
			LOG.writeMsg(this, LOG.MODE_VOICE,"--AppLaunchSession mAnswer : " + mAnswer + "--");
			playTTS(mTTS);

		} else if (url != null && !"".equals(url)) {
			addAnswerViewText(mAnswer);
			playTTS(mTTS);
		}

		//打开应用，发送广播
		//RomCustomSetting.romCustomCloseApp(mContext, packageName);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
