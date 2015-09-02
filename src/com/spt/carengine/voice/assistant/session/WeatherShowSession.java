/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WeatherSessionNew.java
 * @ProjectName : iShuoShuo2_work
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : CavanShi
 * @CreateDate : 2013-4-22
 */
package com.spt.carengine.voice.assistant.session;

import android.content.Context;
import android.os.Handler;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.model.WeatherDay;
import com.spt.carengine.voice.assistant.model.WeatherInfo;
import com.spt.carengine.voice.assistant.preference.SessionPreference;
import com.spt.carengine.voice.assistant.view.WeatherContentView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-4-22
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-4-22
 * @Modified:
 * 2013-4-22: 实现基本功能
 */
public class WeatherShowSession extends BaseSession {
	public static final String TAG = "WeatherShowSession";

	private String mCity;
	private String mCityCode;

	private WeatherContentView mWeatherView;

	private JSONObject mWeatherResultJson;

	public WeatherShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		mWeatherResultJson = getJSONObject(mDataObject, SessionPreference.KEY_RESULT);
		showWeather();
	}

	private WeatherDay getweatherItemFromJsonObject(JSONObject jObject) {
		WeatherDay w = new WeatherDay();
		String year = getJsonValue(jObject, "year", "0");
		String month = getJsonValue(jObject, "month", "0");
		String day = getJsonValue(jObject, "day", "0");
		String dayofweek = getJsonValue(jObject, "dayOfWeek", "2");
		String weather = getJsonValue(jObject, "weather", "");
		String highTemp = getJsonValue(jObject, "highestTemperature", "0");
		String lowTemp = getJsonValue(jObject, "lowestTemperature", "0");
		String currentTemp = getJsonValue(jObject, "currentTemperature", "0");
		String wind = getJsonValue(jObject, "wind", "");
		w.setYear(Integer.parseInt(year));
		w.setMonth(Integer.parseInt(month));
		w.setDay(Integer.parseInt(day));
		/* 2013.05.13 added by shichao for amend weather */
		w.setDayOfWeek(Integer.parseInt(dayofweek));
		String s = modfifyWeatherImage(weather);
		w.setWeather(s);
		w.setImageTitle(s);
		/* end */
		w.setTemperatureRange(Integer.parseInt(highTemp), Integer.parseInt(lowTemp));
		w.setCurrentTemp(Integer.parseInt(currentTemp));
		w.setWind(wind, null);
		return w;
	}

	private String modfifyWeatherImage(String weather) {
		String weatherDao = mContext.getString(R.string.to);
		String weatherZhuan = mContext.getString(R.string.turn);

		if (weather != null && !(weather = weather.trim()).equals("")) {
			int zhuanIndex = -1;
			int daoIndex = -1;

			if ((zhuanIndex = weather.indexOf(weatherZhuan)) > 0) {
				weather = weather.substring(0, zhuanIndex);
			}

			if ((daoIndex = weather.indexOf(weatherDao)) > 0) {
				weather = weather.substring(daoIndex + weatherDao.length(), weather.length());
			}
		}
		return weather;
	}

	private void showWeather() {
		mCity = getJsonValue(mWeatherResultJson, "cityName");
		mCityCode = getJsonValue(mWeatherResultJson, "cityCode");

		String cityStr = mContext.getString(R.string.city);
		if (mCity.endsWith(cityStr)) {
			mCity = mCity.substring(0, mCity.lastIndexOf(cityStr));
		}

		WeatherInfo weatherInfo = new WeatherInfo(mCity, mCityCode);

		JSONArray weatherArray = getJsonArray(mWeatherResultJson, "weatherDays");
		String updateTime = getJsonValue(mWeatherResultJson, "updateTime");
		String focusIndexString = getJsonValue(mWeatherResultJson, "focusDateIndex", "0");
		int focusIndex = Integer.parseInt(focusIndexString);

		weatherInfo.setUpdateTime(updateTime);

		if (weatherArray != null && weatherArray.length() > 0) {
			for (int i = 0; i < weatherArray.length() && i < WeatherInfo.DAY_COUNT; i++) {
				JSONObject object = getJSONObject(weatherArray, i);
				WeatherDay day = getweatherItemFromJsonObject(object);
				if (focusIndex == i) {
					day.setFocusDay();
				}
				// wList.add(day);
				weatherInfo.setWeatherDay(day, i);
			}
		} else {
			String answer = mContext.getString(R.string.no_weather_info);
			addAnswerViewText(answer);
			LOG.writeMsg(this, LOG.MODE_VOICE, "--WeatherShowSession mAnswer : " + mAnswer + "--");
			playTTS(answer);
			return;
		}
		mWeatherView = new WeatherContentView(mContext);
		mWeatherView.setWeatherInfo(weatherInfo);
		addAnswerView(mWeatherView);
		mTTS = mTTS.replaceAll(mContext.getString(R.string.concrete), "");
		addAnswerViewText(mTTS);
		playTTS(mTTS);
	}

	@Override
	public void release() {
		mWeatherView = null;
		super.release();
	}

	@Override
	public void onTTSEnd() {
	    LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
