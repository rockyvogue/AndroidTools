/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ReminderOkSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import cn.yunzhisheng.vui.assistant.IDataControl;
import cn.yunzhisheng.vui.modes.MemoInfo;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.memo.ReminderBroadCastReceiver;
import com.spt.carengine.voice.assistant.preference.SessionPreference;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified:
 * 2013-9-9: 实现基本功能
 */
public class ReminderOkSession extends BaseSession {
	public static final String TAG = "ReminderOkSession";

	private MyApplication mApplication = null;
	private MemoInfo mMemoInfo = null;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-9
	 * @param context
	 * @param sessionManagerHandler
	 */
	public ReminderOkSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		mApplication = (MyApplication) context.getApplicationContext();
	}

	/**
	 * @Description : putProtocol
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-9
	 * @see com.spt.carengine.voice.assistant.session.BaseSession#putProtocol(org.json.JSONObject)
	 */
	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--ReminderOkSession mAnswer : " + mAnswer + "--");
		playTTS(mAnswer);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		mMemoInfo = new MemoInfo();
		String time = getJsonValue(resultObject, "time", "");
		String content = getJsonValue(resultObject, "content", mContext.getString(R.string.domain_alarm));

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mMemoInfo.createTime = System.currentTimeMillis();
		mMemoInfo.status = 1;
		mMemoInfo.title = content;
		try {
			mMemoInfo.dueTime = dateformat.parse(time).getTime();
		} catch (ParseException e) {
			mMemoInfo.dueTime = -9999;
		}

		IDataControl mDataControl = mApplication.getDataControl();
		mDataControl.insertMemo(mMemoInfo);

		mContext.sendBroadcast(new Intent(ReminderBroadCastReceiver.ACTION_APP_UPGRADE));

		mAnswer = mContext.getString(R.string.create_success);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--ReminderOkSession mAnswer : " + mAnswer + "--");
		playTTS(mAnswer);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
