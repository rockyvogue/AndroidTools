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
import android.text.TextUtils;

import cn.yunzhisheng.common.JsonTool;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.NoPerSonContentView;

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
public class TalkShowSession extends BaseSession {
	public static final String TAG = "TalkShowSession";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public TalkShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);

		if (!TextUtils.isEmpty(mErrorCode)) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "ErrorCode:" + mErrorCode);
			playTTS(mTTS);
		} else {
			if (TextUtils.isEmpty(mTTS)) {
				mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
			} else {
				playTTS(mTTS);
			}
		}
		
		if(SessionPreference.DOMAIN_CALL.equals(mOriginType) || SessionPreference.DOMAIN_SMS.equals(mOriginType) ||
				SessionPreference.DOMAIN_CONTACT.equals(mOriginType)) {
			if(mDataObject != null) {
				String extra_info =  JsonTool.getJsonValue(mDataObject, "extra_info", "");
				String extra_name =  JsonTool.getJsonValue(mDataObject, "extra_name", "");
				if("NO_PERSON".equals(extra_info)) {
					NoPerSonContentView view = new NoPerSonContentView(mContext);
					view.setShowText(mContext.getString(R.string.nofind_contact)+extra_name);
					addAnswerView(view,true);
				}	
			}
		}
	}

	@Override
	public void onTTSEnd() {
	    LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
