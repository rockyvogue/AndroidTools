
package com.spt.carengine.utils;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit._Original_WebView.FindListener;

import com.spt.carengine.R;
import com.spt.carengine.db.bean.TimeZoneCity;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午1:51:00
 * @description SharePreference save key--value,
 */
public class SharePrefsUtils {

    // //////////////////////////////////////////////////////////////////
    // // 保存视频是否循环播放 ////
    // //////////////////////////////////////////////////////////////////
    private static final String LOOP_PLAY_FILE_NAME = "video_loop_play_flag";

    private static final String LOOP_PLAY_KEY = "video_loop_play_flag_key";

    /**
     * Store loop play video flag,
     * 
     * @param context Context
     */
    public static final void saveVideoLoopPlayFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                LOOP_PLAY_FILE_NAME, Context.MODE_PRIVATE);
        boolean sourceFlag = sp.getBoolean(LOOP_PLAY_KEY, false);
        Editor editor = sp.edit();
        editor.putBoolean(LOOP_PLAY_KEY, !sourceFlag);
        editor.commit();
    }

    /**
     * Get loop play video flag
     * 
     * @param context
     * @return
     */
    public static final boolean getVideoLoopPlayFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                LOOP_PLAY_FILE_NAME, Context.MODE_PRIVATE);
        boolean sourceFlag = sp.getBoolean(LOOP_PLAY_KEY, false);
        return sourceFlag;
    }

    // //////////////////////////////////////////////////////////////////
    // // 保存视频录制的时间间隔 ////
    // //////////////////////////////////////////////////////////////////

    private static final String RECORD_VIDEO_TIME_INVERAL_FILE_NAME = "record_video_time_inveral";

    private static final String RECORD_VIDEO_TIME_INVERAL_KEY = "record_video_time_inveral_key";

    public static final long DEFAULT_TIME_INVERAL_5M = 5 * 60 * 1000;

    public static final long DEFAULT_TIME_INVERAL_3M = 3 * 60 * 1000;

    /**
     * Set record video time inveral
     * 
     * @param context Context
     */
    public static final void saveRecordVideoTimeInveral(Context context,
            long timeInveral) {
        SharedPreferences sp = context.getSharedPreferences(
                RECORD_VIDEO_TIME_INVERAL_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong(RECORD_VIDEO_TIME_INVERAL_KEY, timeInveral);
        editor.commit();
    }

    /**
     * Get record video time inveral
     * 
     * @param context
     * @return
     */
    public static final long getRecordVideoTimeInveral(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                RECORD_VIDEO_TIME_INVERAL_FILE_NAME, Context.MODE_PRIVATE);
        long timeInveral = sp.getLong(RECORD_VIDEO_TIME_INVERAL_KEY,
                DEFAULT_TIME_INVERAL_5M);
        return timeInveral;
    }

    // //////////////////////////////////////////////////////////////////
    // // 保存视频录制的锁 ////
    // //////////////////////////////////////////////////////////////////

    private static final String RECORD_VIDEO_LOCK_FILE_NAME = "record_video_lock";

    private static final String RECORD_VIDEO_LOCK_KEY = "record_video_lock_key";

    /**
     * Set record video lock
     * 
     * @param context Context
     */
    public static final void saveRecordVideoLock(Context context,
            boolean lockFlag) {
        SharedPreferences sp = context.getSharedPreferences(
                RECORD_VIDEO_LOCK_FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(RECORD_VIDEO_LOCK_KEY, lockFlag);
        editor.commit();
    }

    /**
     * Get record video lock
     * 
     * @param context
     * @return
     */
    public static final boolean getRecordVideoLock(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                RECORD_VIDEO_LOCK_FILE_NAME, Context.MODE_PRIVATE);
        boolean lockFlag = sp.getBoolean(RECORD_VIDEO_LOCK_KEY, false);
        return lockFlag;
    }

    // //////////////////////////////////////////////////////////////////
    // // 保存超时自动删除文件 ////
    // //////////////////////////////////////////////////////////////////

    private static final String AUTO_DELETE_VIDEO_FILE = "auto_delete_record_video";

    private static final String AUTO_DELETE_VIDEO_KEY = "auto_delete_record_video_key";

    /**
     * Set record video lock
     * 
     * @param context Context
     */
    public static final void saveTimeoutDeleteFile(Context context,
            boolean lockFlag) {
        SharedPreferences sp = context.getSharedPreferences(
                AUTO_DELETE_VIDEO_FILE, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(AUTO_DELETE_VIDEO_KEY, lockFlag);
        editor.commit();
    }

    /**
     * Get record video lock
     * 
     * @param context
     * @return true:表示超时，用户没有操作，false:表示已经处理
     */
    public static final boolean getTimeoutDeleteFile(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                AUTO_DELETE_VIDEO_FILE, Context.MODE_PRIVATE);
        boolean lockFlag = sp.getBoolean(AUTO_DELETE_VIDEO_KEY, true);
        return lockFlag;
    }

    // //////////////////////////////////////////////////////////////////
    // // 保存用户点击自动清除按钮的时候保存这个值 ////
    // //////////////////////////////////////////////////////////////////

    private static final String AUTO_CLEAR_FILE = "auto_clear_record_video";

    private static final String AUTO_CLEAR_KEY = "auto_clear_record_video_key";

    private static final String MAX_VIDEO_SIZE = "max_video_file_size";

    /**
     * Set record video lock
     * 
     * @param context Context
     */
    public static final void saveAutoClearRecordVideoFile(Context context,
            boolean autoClearFlag) {
        SharedPreferences sp = context.getSharedPreferences(AUTO_CLEAR_FILE,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(AUTO_CLEAR_KEY, autoClearFlag);
        editor.commit();
    }

    /**
     * Get record video lock
     * 
     * @param context
     * @return true:表示超时，用户没有操作，false:表示已经处理
     */
    public static final boolean getAutoClearRecordVideoFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AUTO_CLEAR_FILE,
                Context.MODE_PRIVATE);
        boolean autoClearFlag = sp.getBoolean(AUTO_CLEAR_KEY, false);
        return autoClearFlag;
    }

    /**
     * 存取最大视频文件的大小（单位 byte）
     * 
     * @param context Context
     */
    public static final void saveMaxRecordVideoFile(Context context, long size,
            String path) {
        SharedPreferences sp = context.getSharedPreferences(MAX_VIDEO_SIZE,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong("size", size);
        editor.putString("filePath", path);
        editor.commit();
    }

    /**
     * 读取最大视频文件的大小（单位 byte）
     * 
     * @param context
     * @return
     */
    public static final long getMaxRecordVideoFile(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MAX_VIDEO_SIZE,
                Context.MODE_PRIVATE);
        long size = sp.getLong("size", 0L);
        return size;
    }

    /**
     * 存取倒车状态
     * 
     * @param context Context
     */
    public static final void saveRebackCarState(Context context, boolean state) {
        SharedPreferences sp = context.getSharedPreferences("back_car_state",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("state", state);
        editor.commit();
    }

    /**
     * 读取倒车状态
     * 
     * @param context
     * @return
     */
    public static final boolean getRebackCarState(Context context) {
        SharedPreferences sp = context.getSharedPreferences("back_car_state",
                Context.MODE_PRIVATE);
        boolean state = sp.getBoolean("state", false);
        return state;
    }

    // /////保存本机设置的时区
    public static final String TIME_ZONE_FILE = "time_zone";
    private static final String TIME_ZONE_KEY = "time_zone_key";
    private static final String TIME_ZONE_CITY_KEY = "time_zone_city_key";
    private static final String TIME_ZONE_BOOLEAN_KEY = "time_zone_boolean_key";

    public static final void saveTimeZone(Context context, String mCityName,
            String mTimeZone, boolean flag) {
        SharedPreferences sp = context.getSharedPreferences(TIME_ZONE_FILE,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(TIME_ZONE_CITY_KEY, mCityName);
        editor.putString(TIME_ZONE_KEY, mTimeZone);
        editor.putBoolean(TIME_ZONE_BOOLEAN_KEY, flag);
        editor.commit();

    }

    public static final TimeZoneCity getTimeZone(Context context) {
        TimeZoneCity cityTime = new TimeZoneCity();
        SharedPreferences sp = context.getSharedPreferences(TIME_ZONE_FILE,
                Context.MODE_PRIVATE);
        String mCityName = sp.getString(TIME_ZONE_CITY_KEY,
                context.getString(R.string.city_Beijing));
        String mTimeZone = sp.getString(TIME_ZONE_KEY,
                context.getString(R.string.AsiaShanghai));
        boolean flag = sp.getBoolean(TIME_ZONE_BOOLEAN_KEY, true);
        cityTime.mCityName = mCityName;
        cityTime.mTimeZone = mTimeZone;
        cityTime.mFlag = flag;
        return cityTime;

    }

}
