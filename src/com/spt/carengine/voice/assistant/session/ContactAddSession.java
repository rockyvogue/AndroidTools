package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;

import com.spt.carengine.log.LOG;

import org.json.JSONObject;
public class ContactAddSession extends CommBaseSession{
	public static final String TAG = "ContactAddSession";

	public ContactAddSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		// TODO Auto-generated constructor stub
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		String name = getJsonValue(mJsonObject, "name");
		String phone = getJsonValue(mJsonObject, "number");
		Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		i.putExtra(Insert.NAME, name);
		i.putExtra(Insert.PHONE, phone);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
		
		/**2014-11-10 yujun*/
		LOG.writeMsg(this, LOG.MODE_VOICE, "--ContactAddSession mAnswer : "+mAnswer);
		//playTTS(mAnswer);
		playTTS(mTTS);
		/**------------------*/
	}
}
