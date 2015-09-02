/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WaitingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;

import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-2
 * @Modified:
 * 2013-9-2: 实现基本功能
 */
public class UnsupportShowSession extends BaseSession {
	public static final String TAG = "UnsupportShowSession";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-2
	 * @param context
	 * @param sessionManagerHandler
	 * @param sessionViewContainer
	 */
	public UnsupportShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addAnswerViewText(mAnswer);
		playTTS(mTTS);
	}
	
	
	@Override
	public void onTTSEnd() {
	    LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSEnd");
		super.onTTSEnd();
        mSessionManagerHandler
            .sendEmptyMessage(SessionPreference.MESSAGE_START_TALK);
	}
	
	@Override
	public void release() {
		super.release();
	}
}
