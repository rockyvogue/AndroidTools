/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WebShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.WebContentView;

import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class WebShowSession extends BaseSession {
	public static final String TAG = "WebShowSession";
	private String mUrl = "";

	private WebContentView webContentView = null;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public WebShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		mUrl = getJsonValue(resultObject, SessionPreference.KEY_URL, "");

		addQuestionViewText(mQuestion);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--WebShowSession mAnswer : " + mAnswer + "--");
		addAnswerViewText(mAnswer);
		playTTS(mTTS);
		//playTTS(mAnswer);
		// if (SessionPreference.DOMAIN_VIDEO.equals(mOriginType)) {
		// String name = getJsonValue(resultObject, SessionPreference.KEY_NAME);
		// String keyword = getJsonValue(resultObject,
		// SessionPreference.KEY_KEYWORD);
		// } else {
		webContentView = new WebContentView(mContext);
		webContentView.setUrl(mUrl);
		addAnswerView(webContentView);
		// }
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
