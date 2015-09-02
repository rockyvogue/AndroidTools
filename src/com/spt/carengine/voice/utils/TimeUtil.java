
package com.spt.carengine.voice.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.spt.carengine.R;
import com.spt.carengine.db.bean.TimeZoneCity;
import com.spt.carengine.utils.SharePrefsUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @desc:时间工具类
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }

        return result;
    }

    /**
     * 将秒转成分秒
     * 
     * @return
     */
    public static String getVoiceRecorderTime(int time) {
        int minute = time / 60;
        int second = time % 60;
        if (minute == 0) {
            return String.valueOf(second);
        }
        return minute + ":" + second;

    }

    /***
     * 返回日期<一句话功能简述>
     * 
     * @param str 日期格式
     * @param GMT 时区
     */
    public static String getDateTime(String str, String GMT) {

        Calendar claCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(str);
        dateFormat.setTimeZone(TimeZone.getTimeZone(GMT));
        String strDate = dateFormat.format(claCalendar.getTime());
        return strDate;

    }

    public static Calendar getCalendar(Context context) {
        String GMT;
        TimeZoneCity mTimeZoneCity = SharePrefsUtils.getTimeZone(context);
        if (mTimeZoneCity == null) {
            GMT = context.getString(R.string.AsiaShanghai);

        } else {
            GMT = mTimeZoneCity.mTimeZone;
        }
        TimeZone timezone = TimeZone.getTimeZone(GMT);
        Calendar calendar = Calendar.getInstance(timezone);
        return calendar;
    }
}
