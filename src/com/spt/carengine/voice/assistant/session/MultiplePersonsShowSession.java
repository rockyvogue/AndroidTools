package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import cn.yunzhisheng.vui.modes.ContactInfo;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.view.PickPersonView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiplePersonsShowSession extends ContactSelectBaseSession {
	public static final String TAG = "MultiplePersonsShowSession";
	int personNum = 0;
	private PickPersonView mPickPersonView = null;

	public MultiplePersonsShowSession(Context context, Handler handle) {
		super(context, handle);

	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--jsonProtocol-->"+jsonProtocol);
		JSONArray dataArray = getJsonArray(mJsonObject, "person");
		if (dataArray != null) {
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject item = getJSONObject(dataArray, i);
				ContactInfo contactItem = new ContactInfo();
				contactItem.setPhotoId(Integer.parseInt(getJsonValue(item, "pic")));
				contactItem.setDisplayName(getJsonValue(item, "name"));
				mContactInfoList.add(contactItem);
				mDataItemProtocalList.add(getJsonValue(item, "onSelected"));
			}
			addAnswerViewText(mAnswer);

			if (mPickPersonView == null) {
				mPickPersonView = new PickPersonView(mContext);
				mPickPersonView.initView(mContactInfoList);

				mPickPersonView.setPickListener(mPickViewListener);
			}
			
			addSessionView(mPickPersonView);
		}
		
		playTTS(mTTS);
	}
}
