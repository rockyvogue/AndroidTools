package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import cn.yunzhisheng.vui.modes.ContactInfo;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.oem.RomContact;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.SmsContentView;
import com.spt.carengine.voice.assistant.view.SmsContentView.ISmsContentViewListener;

import org.json.JSONObject;

public class SmsInputShowSession extends CommBaseSession {
	public static final String TAG = "SmsInputShowSession";

	private StringBuilder mContentBuilder = new StringBuilder();
	private SmsContentView mSmsContentView = null;
	private String mContent = "";
	private ISmsContentViewListener mSmsContentViewListener = new ISmsContentViewListener() {
		@Override
		public void onBeginEdit() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onBeginEdit");
			cancelTTS();
		}

		@Override
		public void onEndEdit(String msg) {
			LOG.writeMsg(this, LOG.MODE_VOICE, "--onEndEdit--> " + msg);

			mSmsContentView.setMessage(msg);
			String protocolStr = "{\"service\":\"DOMAIN_HAND_INPUT_CONTENT\" , \"content\":\"" + msg + "\"}";
			onUiProtocal(protocolStr);
		}

		@Override
		public void onCancel() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onCancel");
			onUiProtocal(mCancelProtocal);
		}

		@Override
		public void onOk() {
			LOG.writeMsg(this, LOG.MODE_VOICE, "onOk");
			onUiProtocal(mOkProtocal);
		}

		@Override
		public void onClearMessage() {
//			mContentBuilder.setLength(0);
//			mSmsContentView.setMessage("");
			LOG.writeMsg(this, LOG.MODE_VOICE, "on clean");
			String protocolStr = mContext.getString(R.string.sms_re_enter);
			onUiProtocal(protocolStr);
		}
	};

	public SmsInputShowSession(Context context, Handler handle) {
		super(context, handle);
		mIsNeedAddTextView = true;
	}

	public void putProtocol(JSONObject jsonProtocol) {
		LOG.writeMsg(this, LOG.MODE_VOICE, "--jsonProtocol-->" + jsonProtocol);
		super.putProtocol(jsonProtocol);
		ContactInfo mSelectedContactInfo = new ContactInfo();
		String name = "";
		int photoId = 0;
		String tag = "";
		// String content = "";

		if (mSmsContentView == null) {
			mSmsContentView = new SmsContentView(mContext);
			mSmsContentView.setListener(mSmsContentViewListener);
		}

		if (mJsonObject != null) {
			if (mJsonObject.has(SessionPreference.KEY_NAME)) {
				name = getJsonValue(mJsonObject, SessionPreference.KEY_NAME);
			}
			mSelectedContactInfo.setDisplayName(name);
			if (mJsonObject.has(SessionPreference.KEY_PIC)) {
				String photoIdStr = getJsonValue(mJsonObject, SessionPreference.KEY_PIC);
				photoId = Integer.parseInt(photoIdStr);
				tag = getJsonValue(mJsonObject, "numberAttribution");
			}
			mSelectedContactInfo.setPhotoId(photoId);
			String number = getJsonValue(mJsonObject, "number");
			if (mJsonObject.has(SessionPreference.KEY_CONTENT)) {
				mContent = getJsonValue(mJsonObject, SessionPreference.KEY_CONTENT);
			}
			Drawable drawable = RomContact.loadContactDrawable(mContext, mSelectedContactInfo.getPhotoId());
			mSmsContentView.initRecipient(drawable, mSelectedContactInfo.getDisplayName()+":", number + " " + tag);
		}

		if (mIsNeedAddTextView) addSessionView(mSmsContentView);
		mSmsContentView.setMessage(mContent);
		playTTS(mTTS);
		addSessionAnswerText(mAnswer);
	}

	protected void editShowContent() {
		mSmsContentView.becomeFirstRespond();
	}
}