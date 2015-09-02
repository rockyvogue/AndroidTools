/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : TalkShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.preference.UserPreference;

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
public class ErrorShowSession extends BaseSession {
	public static final String TAG = "ErrorShowSession";
	protected UserPreference mUserPreference = new UserPreference(mContext);

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public ErrorShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);
		MyApplication.mErrorRetalkCount++;
		if ("-63605".equals(mErrorCode)) {
			Message msg = mSessionManagerHandler.obtainMessage(
				SessionPreference.MESSAGE_PLAY_BEEP_SOUND,
				R.raw.error_tone,
				0);
			mSessionManagerHandler.sendMessage(msg);
		}
		addAnswerViewText(mAnswer);
		playTTS(mTTS);
	}

	@Override
	public void onTTSEnd() {
	    LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
