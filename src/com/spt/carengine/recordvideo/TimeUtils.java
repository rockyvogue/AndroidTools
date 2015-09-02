
package com.spt.carengine.recordvideo;

import com.spt.carengine.camera.CameraValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    /**
     * 得到当前时间
     * 
     * @return 返回当前的时间
     */
    public static final String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss", Locale.CHINESE);
        Calendar calendar = Calendar.getInstance();
        String timeNow = sdf.format(calendar.getTime());
        return timeNow;
    }

    /**
     * 得到时间戳
     * 
     * @return
     */
    public static String getTimeStamp() {
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                CameraValues.FILE_NAME_FMT, Locale.CHINA);
        // Create a media file name
        String timeStamp = sdfFileName.format(new Date());
        return timeStamp;
    }

    /**
     * 得到当前日期的格式
     * 
     * @return
     */
    public static String getCurDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                CameraValues.DIR_NAME_FMT, Locale.CHINA);
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        return dateStr;
    }

    /**
     * 得到当前日期的格式
     * 
     * @return
     */
    public static String getCurDateSlashFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd",
                Locale.CHINA);
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        return dateStr;
    }

    /**
     * 得到当前时间
     * 
     * @return 返回当前的时间
     */
    public static final String getCurTimeColonFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE);
        Calendar calendar = Calendar.getInstance();
        String timeNow = sdf.format(calendar.getTime());
        return timeNow;
    }

}
