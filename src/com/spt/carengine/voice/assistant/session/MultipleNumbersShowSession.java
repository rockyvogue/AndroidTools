package com.spt.carengine.voice.assistant.session;
import android.content.Context;
import android.os.Handler;

import cn.yunzhisheng.vui.modes.PhoneNumberInfo;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.view.PickPhoneNumberView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultipleNumbersShowSession extends ContactSelectBaseSession {
	public static final String TAG = "MultipleNumbersShowSession";
	PickPhoneNumberView mPickPhoneNumberView;
	public MultipleNumbersShowSession(Context context,Handler handle){
		super(context,handle);
	}
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		
		JSONArray dataArray = getJsonArray(mJsonObject, "numbers");
		if (dataArray != null) {
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject item = getJSONObject(dataArray, i);
				LOG.writeMsg(this, LOG.MODE_VOICE, "--item "+i+" : "+item);
				PhoneNumberInfo phoneItem = new PhoneNumberInfo();
				phoneItem.setNumber(getJsonValue(item, "number"));
				phoneItem.setAttribution(getJsonValue(item, "numberAttribution"));
				mPhoneNumberInfoList.add(phoneItem);
				mDataItemProtocalList.add(getJsonValue(item, "onSelected"));
			}
			String displayName = getJsonValue(mJsonObject, "name");
			String photoId = "0";
			if (mJsonObject.has("pic")) {
				photoId = getJsonValue(mJsonObject, "pic");
			}
			if (mPickPhoneNumberView == null) {
				mPickPhoneNumberView = new PickPhoneNumberView(mContext);
				mPickPhoneNumberView.initView(displayName, Integer.parseInt(photoId), mPhoneNumberInfoList);

				mPickPhoneNumberView.setPickListener(mPickViewListener);
			}
			addSessionView(mPickPhoneNumberView);

		}
		addAnswerViewText(mAnswer);
		playTTS(mTTS);
	}
}