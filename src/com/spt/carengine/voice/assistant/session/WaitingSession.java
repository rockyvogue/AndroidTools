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
import com.spt.carengine.voice.assistant.view.WaitingContentView;
import com.spt.carengine.voice.assistant.view.WaitingContentView.IWaitingContentViewListener;

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
public class WaitingSession extends BaseSession {
	public static final String TAG = "WaitingSession";
	private String mCancelProtocal = "";
	private WaitingContentView mWaitingContentView = null;

	private IWaitingContentViewListener mListener = new IWaitingContentViewListener() {

		@Override
		public void onCancel() {
			onUiProtocal(mCancelProtocal);
		}
	};

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-2
	 * @param context
	 * @param sessionManagerHandler
	 * @param sessionViewContainer
	 */
	public WaitingSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		mCancelProtocal = getJsonValue(mDataObject, SessionPreference.KEY_ON_CANCEL, "");
		mAnswer = getJsonValue(jsonProtocol, "ttsAnswer");
		addQuestionViewText(mQuestion);

		if (mWaitingContentView == null) {
			mWaitingContentView = new WaitingContentView(mContext);
		}
		mWaitingContentView.setTitle(mAnswer);
		mWaitingContentView.setLisener(mListener);

		addAnswerView(mWaitingContentView);
		addAnswerViewText(mAnswer);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--WaitingSession mAnswer : " + mAnswer + "--");
		playTTS(mAnswer);
	}
	
	@Override
	public void release() {
		mWaitingContentView = null;
		super.release();
	}
}
