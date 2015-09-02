
package com.spt.carengine.utils;

import android.annotation.SuppressLint;
import android.os.Handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 视频工具类
 * 
 * @author rocky
 */
public class VideoUtils {

    /**
     * 得到文件的后缀名
     * 
     * @param url
     * @return
     */
    public static String getUrlExtension(String url) {
        if (!isEmpty(url)) {
            int i = url.lastIndexOf('.');
            if (i > 0 && i < url.length() - 1) {
                return url.substring(i + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 传入一个视频地址获得一个视频地址的视频名称
     * 
     * @return
     */
    public static String getVideoNameFromUrl(String path) {
        path = getUTF8CodeInfoFromURL(path);
        if (!isEmpty(path)) {
            int i = path.lastIndexOf('/');
            if (i > 0 && i < path.length() - 1) {
                return path.substring(i + 1);
            }
        }
        return "";
    }

    /**
     * 判断字符是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 将时间转换为字符串
     * 
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String millisToString(long millis) {
        boolean negative = millis < 0;
        millis = java.lang.Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat
                .getInstance(Locale.US);
        format.applyPattern("00");
        if (millis > 0) {
            time = (negative ? "-" : "") + hours + ":" + format.format(min)
                    + ":" + format.format(sec);
        } else {
            time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }

    /**
     * 传入一个long型的时间值得到一个表示时间的字符串
     * 
     * @param timeMs
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeToStr(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
                Locale.CHINA);// 初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String hms = formatter.format(time);
        return hms;
    }

    /**
     * 传入一个int值的时间值得到一个表示时间的字符串
     * 
     * @param timeMs
     * @return
     */
    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        }
    }

    /**
     * handler发送消息
     * 
     * @param handler
     * @param msg
     */
    public static void handlerMsgToUI(Handler handler, int msg) {
        handler.sendEmptyMessage(msg);
    }

    /**
     * handler发送消息
     * 
     * @param handler
     * @param msg
     */
    public static void handlerMsgToUI(Handler handler, int msg, long delay) {
        handler.sendEmptyMessageDelayed(msg, delay);
    }

    /**
     * URL编码转UTF8编码
     * 
     * @param urlStr
     * @return
     */
    public static String getUTF8CodeInfoFromURL(String urlStr) {
        String info = "";
        try {
            info = URLDecoder.decode(urlStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info;
    }

}
