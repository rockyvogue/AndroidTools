package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import com.spt.carengine.R;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.NoPerSonContentView;

import org.json.JSONObject;

public class TalkShowMiddleSession extends CommBaseSession{
	public static final String TAG = "TalkShowMiddleSession";

	public TalkShowMiddleSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		// TODO Auto-generated constructor stub
	}
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		
//		addQuestionViewText(mAnswer);
		
		if(SessionPreference.DOMAIN_CALL.equals(mOriginType) || SessionPreference.DOMAIN_SMS.equals(mOriginType)) {
			NoPerSonContentView view = new NoPerSonContentView(mContext);
			view.setShowText(R.string.call_with_no_name);
			addAnswerView(view,true);
			
			addAnswerViewText(mAnswer);
		} else {
			addAnswerViewText(mAnswer);
		}
		
		playTTS(mTTS);
	}
}
