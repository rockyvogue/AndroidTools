/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RouteShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */

package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import cn.yunzhisheng.preference.PrivatePreference;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.baidu.BaiduNavi;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.preference.UserPreference;
import com.spt.carengine.voice.assistant.view.RoteSeesionConfirmView;
import com.spt.carengine.voice.assistant.view.RoteSeesionConfirmView.IRoteContentViewListener;

import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class RouteShowSession extends BaseSession {
    public static final String TAG = "RouteShowSession";

    // private RouteContentView mRouteContentView = null;
    private static final int DELAY_CALL_TIME = 5000;
    public String mCancelProtocal = "";

    private RoteSeesionConfirmView mRoteView = null;
    private IRoteContentViewListener mRoteListener = new IRoteContentViewListener() {

        @Override
        public void onCancel() {
            onUiProtocal(mCancelProtocal);
        }

        @Override
        public void onOk() {
//            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
            LOG.writeMsg(this, LOG.MODE_VOICE, "onOk(), BaiduMap showRoute," + "poiVendor : " + poiVendor + 
                    "fromLat : " + fromLat + ", fromLng : " + fromLng + ", fromCity : " + fromCity + ", fromPoi : " + fromPoi + 
                    ", toLat : " + toLat + ", toLng : " + ", toCity : " + toCity + ", toPoi : " + toPoi);
            if ("BAIDU".equals(poiVendor)) {
                LOG.writeMsg(this, LOG.MODE_VOICE, "BaiduMap showRoute Start to " + toPoi);
//              BaiduMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
                String url = "bdnavi://plan?coordType=wgs84ll&start=" + fromLat + "," + fromLng + "," + fromPoi +
                                                            "&dest="  + toLat   + "," + toLng   + "," + toPoi;
              //开始导航
              BaiduNavi.getIntance().startBaiduNaviToRoutePlan(mContext, url);
                
            } else {
                Toast.makeText(mContext, "Unsupported map.", Toast.LENGTH_SHORT).show();
            }
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    };

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-6
     * @param context
     * @param sessionManagerHandler
     */
    public RouteShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    double fromLat;
    double fromLng;
    String fromCity;
    String fromPoi;
    double toLat;
    double toLng;
    String toCity;
    String toPoi;
    String originFromPOI;
    String poiVendor;

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addQuestionViewText(mQuestion);

        JSONObject resultObject = getJSONObject(mDataObject, "result");
        mCancelProtocal = getJsonValue(resultObject,
                SessionPreference.KEY_ON_CANCEL);

        fromLat = getJsonValue(resultObject, "fromLatitude", 0d);
        fromLng = getJsonValue(resultObject, "fromLongtitude", 0d);
        fromCity = getJsonValue(resultObject, "fromCity", "");
        fromPoi = getJsonValue(resultObject, "fromPosition", "");
        toLat = getJsonValue(resultObject, "toLatitude", 0d);
        toLng = getJsonValue(resultObject, "toLongtitude", 0d);
        toCity = getJsonValue(resultObject, "toCity", "");
        toPoi = getJsonValue(resultObject, "toPosition", "");
        originFromPOI = getJsonValue(resultObject, "originFromPOI");
        poiVendor = PrivatePreference.getValue("poi_vendor",
                UserPreference.MAP_VALUE_BAIDU);

        if (mRoteView == null) {
            mRoteView = new RoteSeesionConfirmView(mContext);
            mRoteView.initView(null,
                    mContext.getResources().getString(R.string.baidu_nav_to)
                            + toPoi, "", "");

            mRoteView.setListener(mRoteListener);
        }

        Message msg = mSessionManagerHandler.obtainMessage(
                SessionPreference.MESSAGE_ADD_ANSWER_VIEW, mRoteView);
        mSessionManagerHandler.sendMessage(msg);

        addAnswerViewText(mAnswer);
        playTTS(mContext.getString(R.string.route_delay, toPoi));// 吾比5播报更自然
    }

    @Override
    public void onTTSEnd() {
        LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSEnd");
        super.onTTSEnd();
        mRoteView.startCountDownTimer(DELAY_CALL_TIME);

    }
}
