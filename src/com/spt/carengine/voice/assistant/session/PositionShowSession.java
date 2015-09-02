/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PositionShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.baidu.BaiduMap;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.preference.UserPreference;

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
public class PositionShowSession extends BaseSession {
	public static final String TAG = "PositionShowSession";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public PositionShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        addQuestionViewText(mQuestion);

        JSONObject resultObject = getJSONObject(mDataObject, "result");

        double lat = getJsonValue(resultObject, "latitude", 0d);
        double lng = getJsonValue(resultObject, "longtitude", 0d);
        String poi = getJsonValue(resultObject, "position", "");
        String address = getJsonValue(resultObject, "address", "");
        String poiVendor = cn.yunzhisheng.preference.PrivatePreference.getValue("poi_vendor", UserPreference.MAP_VALUE_BAIDU);

        addAnswerViewText(mAnswer);
        LOG.writeMsg(this, LOG.MODE_VOICE, "--PositionShowSession mAnswer : " + mAnswer + "--");
        playTTS(mTTS);
        
       /* if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
            GaodeMap.showLocation(mContext, poi, address, lat, lng);
        } else */if ("BAIDU".equals(poiVendor)) {
            BaiduMap.showLocation(mContext, poi, address, String.valueOf(lat), String.valueOf(lng));
            
        } else {
            Toast.makeText(mContext, "Unsupported map.", Toast.LENGTH_SHORT).show();
        }

        
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }
}
