
package com.spt.carengine.camera;

import android.os.Environment;

import java.io.File;

public class CameraValues {
    // 分辨率
    public static final String KEY_OPT = "option_format";
    // 时间
    public static final String KEY_TIME = "option_time";

    // 文件名格式
    public static String FILE_NAME_FMT = "HHmmss_SSS";
    // 文件夹名格式
    public static String DIR_NAME_FMT = "yyyyMMdd";

    // 日期显示格式
    public static final String DT_FMT = "yyyy-MM-dd HH:mm:ss";
    
    public static final String SAVE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final File SAVE_DIR = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

    public static final String SAVE_DIR_NAME = "CarCameraApp";

    public static final File REC_SAVE_DIR = new File(SAVE_DIR, SAVE_DIR_NAME);

    // 到达指定空间后需要预警处理
    public static final long FREE_SPACE_LIMIT =  1000 * 1024 * 1024; //500MB

}
