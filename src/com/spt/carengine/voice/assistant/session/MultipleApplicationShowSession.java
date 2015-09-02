package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import cn.yunzhisheng.vui.modes.AppInfo;

import com.spt.carengine.voice.assistant.view.PickAppView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultipleApplicationShowSession extends SelectCommonSession {
	private PickAppView mAppListView;
	private ArrayList<AppInfo> mAppInfoList = new ArrayList<AppInfo>();

	public MultipleApplicationShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		JSONArray dataArray = getJsonArray(mJsonObject, "applications");
		if (dataArray != null) {
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject item = getJSONObject(dataArray, i);
				String appName = getJsonValue(item, "appName");
				String pkgName = getJsonValue(item, "packageName");
				AppInfo appItem = new AppInfo(appName, "", pkgName, "");
				mAppInfoList.add(appItem);
				mDataItemProtocalList.add(getJsonValue(item, "onSelected"));
			}
			if (mAppListView == null) {
				mAppListView = new PickAppView(mContext);
				mAppListView.initView(mAppInfoList);
				mAppListView.setPickListener(mPickViewListener);
			}
			addSessionView(mAppListView);
		}
		playTTS(mTTS);
		//mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}

	@Override
	public void release() {
		super.release();
		mAppListView.setPickListener(null);
		mAppListView = null;
		mAppInfoList.clear();
		mAppInfoList = null;
//		/**2014-11-10 yujun*/
//		LogUtil.d(TAG, "--MultipleApplicationShowSession mAnswer : "+mAnswer);
//		//playTTS(mAnswer);
//		playTTS(mTTS);
//		/**------------------*/
	}
}
