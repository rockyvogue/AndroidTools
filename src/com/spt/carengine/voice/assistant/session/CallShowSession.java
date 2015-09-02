package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import cn.yunzhisheng.vui.modes.PhoneNumberInfo;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.CallContentView.ICallContentViewListener;

import org.json.JSONObject;

public class CallShowSession extends CallConfirmShowSession {
	public static final String TAG = "CallShowSession";
	PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo();

	public CallShowSession(Context context, Handler handle) {
		super(context, handle);
		mCallContentViewListener = new ICallContentViewListener() {
			public void onCancel() {
				cancelSession();
				mAnswer = mContext.getString(R.string.operation_cancel);
				addSessionAnswerText(mAnswer);
				LOG.writeMsg(this, LOG.MODE_VOICE, "--CallShowSession mAnswer : " + mAnswer + "--");
				playTTS(mAnswer);
			}

			public void onOk() {
				// onUiProtocal(mOkProtocal);
				String number = getJsonValue(mJsonObject, "number");
				PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo();
				phoneNumberInfo.setNumber(number);
				phoneNumberInfo.setAttribution(getJsonValue(mJsonObject, "numberAttribution"));
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumberInfo.getNumber()));
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (mContext != null) {
					mContext.startActivity(callIntent);
				} else {
					LOG.writeMsg(this, LOG.MODE_VOICE, "call onOk context is null...");
				}
			}
		};
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		mBlockAutoStart = true;
		String number = getJsonValue(mJsonObject, "number");

		phoneNumberInfo.setNumber(number);
		phoneNumberInfo.setAttribution(getJsonValue(mJsonObject, "numberAttribution"));

		if (mContext != null) {
			try {
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumberInfo.getNumber()));
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(callIntent);
			} catch (Exception e) {
				cancelSession();
				LOG.writeMsg(this, LOG.MODE_VOICE, mContext.getString(R.string.no_support_phone));
				Toast.makeText(mContext, R.string.no_support_phone_toast, Toast.LENGTH_LONG).show();
			}
		} else {
			LOG.writeMsg(this, LOG.MODE_VOICE, "call onOk context is null...");
		}

		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}

	protected void startCallAction() {
		addSessionView(mCallContentView);
		mCallContentView.setModeNomal();
		// mCallContentView.startCountDownTimer(3000);
	}
}
