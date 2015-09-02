/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : StockShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import cn.yunzhisheng.vui.modes.StockInfo;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.StockContentView;

import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class StockShowSession extends BaseSession {
	public static final String TAG = "StockShowSession";
	private StockContentView mContentView = null;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public StockShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");
		mQuestion = getJsonValue(resultObject, "name", "") + mContext.getString(R.string.to_stock);
		addQuestionViewText(mQuestion);

		StockInfo stockInfo = new StockInfo();

		stockInfo.setChartImgUrl(getJsonValue(resultObject, "imageUrl", ""));
		stockInfo.setName(getJsonValue(resultObject, "name", ""));
		stockInfo.setCode(getJsonValue(resultObject, "code", ""));
		stockInfo.setCurrentPrice(getJsonValue(resultObject, "currentPrice", ""));
		stockInfo.setChangeAmount(Double.parseDouble(getJsonValue(resultObject, "changeAmount", "0.0")));
		stockInfo.setChangeRate(getJsonValue(resultObject, "changeRate", ""));
		stockInfo.setTurnover(getJsonValue(resultObject, "turnover", ""));
		stockInfo.setHighestPrice(getJsonValue(resultObject, "highestPrice", ""));
		stockInfo.setLowestPrice(getJsonValue(resultObject, "lowestPrice", ""));
		stockInfo.setYesterdayClosingPrice(getJsonValue(resultObject, "yesterdayClosePrice", ""));
		stockInfo.setTodayOpeningPrice(getJsonValue(resultObject, "todayOpenPrice", ""));
		stockInfo.setUpdateTime(getJsonValue(resultObject, "updateTime", ""));

		mContentView = new StockContentView(mContext);
		mContentView.updateUI(stockInfo);
		addAnswerView(mContentView, true);
		LOG.writeMsg(this, LOG.MODE_VOICE, "--StockShowSession mAnswer : " + mAnswer + "--");
		//playTTS(mAnswer);
		playTTS(mTTS);
	}
	
	@Override
	public void onTTSEnd() {
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	};
}
