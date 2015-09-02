
package com.spt.carengine.recordvideo;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.utils.UtilTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年7月30日 下午3:45:23
 * @description
 */
public class FileUtils {

    /**
     * SD卡的根目录
     */
    private static final String sdRoot = Environment
            .getExternalStorageDirectory() + File.separator;

    /**
     * app应用目录的字段
     */
    private static final String appField = MyApplication.getInstance()
            .getString(R.string.app_name);

    /**
     * 拍照目录的字段
     */
    private static final String TAKE_PICTURE_FIELD = "/"
            + MyApplication.getInstance().getString(R.string.home_photo);

    /**
     * 视频拍摄目录的字段
     */
    private static final String VIDEO_RECORD = "/"
            + MyApplication.getInstance().getString(R.string.home_video);

    /**
     * 全部视频目录的字段
     */
    private static final String VIDEO_TOTAL_RECORD = "/"
            + MyApplication.getInstance().getString(
                    R.string.file_label_total_video);

    /**
     * 已锁定视频目录字段
     */
    private static final String VIDEO_LOCK_RECORD = "/"
            + MyApplication.getInstance().getString(
                    R.string.file_label_lock_video);

    /**
     * 创建应用的目录
     * 
     * @return 返回是否创建成功
     */
    public static boolean createAppDir() {
        boolean state = mkdirectory(getVideoPath().get(0) + appField);
        return state;
    }

    /**
     * 获取应用的目录
     * 
     * @return
     */
    public static String getAppDir() {
        String dir = getVideoPath().get(0) + appField;
        return dir;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // // 拍照存储相关的路径 ////
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建 拍照的目录
     * 
     * @return
     */
    public static boolean createTakePictureDir() {
        // 确定应用目录创建成功
        createAppDir();
        boolean state = mkdirectory(getVideoPath().get(0) + appField
                + TAKE_PICTURE_FIELD);
        return state;
    }

    /**
     * 获取拍照的目录
     * 
     * @return
     */
    public static String getStorageTakePictureDir() {
        String dir = getVideoPath().get(0) + appField + TAKE_PICTURE_FIELD;
        return dir;
    }

    /**
     * 创建拍照的日期目录
     * 
     * @return
     */
    public static boolean createTakePictureDateDir() {
        // 先创建录制视频的存储目录
        createTakePictureDir();
        String videoDateDir = getVideoPath().get(0) + appField
                + TAKE_PICTURE_FIELD + File.separator
                + TimeUtils.getCurDateFormat();
        boolean state = mkdirectory(videoDateDir);
        return state;
    }

    /**
     * 得到当前的存储拍照日期目录
     * 
     * @return
     */
    public static String getCurStorageTakePictureDateDir() {
        createTakePictureDateDir();
        String videoDateDir = getVideoPath().get(0) + appField
                + TAKE_PICTURE_FIELD + File.separator
                + TimeUtils.getCurDateFormat();
        return videoDateDir;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // // 视频存储相关的路径 ////
    // /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 创建拍摄视频的目录
     * 
     * @return
     */
    public static boolean createVideoRecordDir() {
        // 确定应用目录创建成功
        createAppDir();
        boolean state = mkdirectory(getVideoPath().get(0) + appField
                + VIDEO_RECORD);
        return state;
    }

    /**
     * 获取存储视频的目录
     * 
     * @return
     */
    public static String getStorageVideoDir() {
        // String dir = sdRoot + appField + VIDEO_RECORD;

        return getVideoPath().get(0) + appField + VIDEO_RECORD;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // // 全部视频存储相关的路径 ////
    // /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 全部视频目录
     * 
     * @return
     */
    public static boolean createTotalVideoRecordDir() {
        // 确定全部视频目录创建成功
        createVideoRecordDir();
        boolean state = mkdirectory(getVideoPath().get(0) + appField
                + VIDEO_RECORD + VIDEO_TOTAL_RECORD);
        return state;
    }

    /**
     * 获取全部视频目录
     * 
     * @return
     */
    public static String getStorageTotalVideoDir() {
        String dir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_TOTAL_RECORD;
        return dir;
    }

    /**
     * 创建视频的日期目录
     * 
     * @return
     */
    public static boolean createTotalVideoDateDir() {
        // 先创建录制视频的存储目录
        createTotalVideoRecordDir();
        String videoDateDir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_TOTAL_RECORD + File.separator
                + TimeUtils.getCurDateFormat();
        boolean state = mkdirectory(videoDateDir);
        return state;
    }

    /**
     * 得到当前的存储视频日期目录
     * 
     * @return
     */
    public static String getCurStorageTotalVideoDateDir() {
        String videoDateDir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_TOTAL_RECORD + File.separator
                + TimeUtils.getCurDateFormat();
        return videoDateDir;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // // 锁定视频存储相关的路径 ////
    // /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 已锁定视频目录
     * 
     * @return
     */
    public static boolean createLockVideoRecordDir() {
        // 确定全部视频目录创建成功
        createVideoRecordDir();
        boolean state = mkdirectory(getVideoPath().get(0) + appField
                + VIDEO_RECORD + VIDEO_LOCK_RECORD);
        return state;
    }

    /**
     * 获取已锁定视频目录
     * 
     * @return
     */
    public static String getStorageLockVideoDir() {
        String dir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_LOCK_RECORD;
        return dir;
    }

    /**
     * 创建已锁定视频的日期目录
     * 
     * @return
     */
    public static boolean createLockVideoDateDir() {
        // 先创建录制视频的存储目录
        createVideoRecordDir();
        String videoDateDir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_LOCK_RECORD + File.separator
                + TimeUtils.getCurDateFormat();
        boolean state = mkdirectory(videoDateDir);
        return state;
    }

    /**
     * 得到当前的存储已锁定视频日期目录
     * 
     * @return
     */
    public static String getCurStorageLockVideoDateDir() {
        String videoDateDir = getVideoPath().get(0) + appField + VIDEO_RECORD
                + VIDEO_LOCK_RECORD + File.separator
                + TimeUtils.getCurDateFormat();
        return videoDateDir;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // // 创建目录 ////
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建目录
     * 
     * @param path
     * @return
     */
    private static boolean mkdirectory(String path) {
        boolean state = false;
        File appDir = new File(path);

        // 判断示存在
        if (!appDir.exists()) {
            state = appDir.mkdir();

        } else {
            // 判断是否为文件夹
            if (!appDir.isDirectory()) {
                state = appDir.mkdir();

            } else {
                // 已经存在应用的目录了
                state = true;
            }
        }
        return state;
    }

    /**
     * <功能描述> 获取所有SD卡路径
     * 
     * @return [参数说明]
     * @return String[] 返回路径
     */
    public static String[] getAllScardPath() {
        String[] str = new String[2];
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (!columns[1].contains("mnt")) {// media_rw
                            if (columns[1].contains("sdcard0")) {
                                str[0] = columns[1];
                            } else {
                                str[1] = columns[1];
                            }
                        }
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (!columns[1].contains("mnt")) {// media_rw
                            if (columns[1].contains("sdcard0")) {
                                str[0] = columns[1];
                            } else {
                                str[1] = columns[1];
                            }
                        }
                    }
                }
            }
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String[] getPicturePath() {

        if (UtilTools.hasSDCard()) {// 判断是否有SD卡存在
            return getAllScardPath();

        }
        return new String[] {
            Environment.getRootDirectory().getPath()
        };
    }

    /**
     * <功能描述> 获取存储视频的内置和外置SD卡路径
     * 
     * @return [参数说明]
     * @return String [返回类型说明]
     */
    @SuppressLint("NewApi")
    public static List<String> getVideoPath() {
        // 对于车机设备来说内置的存储是在sdcard，挂载的T卡是在sdcard2
        // 首先判断是否挂载T卡
        List<String> paths = new ArrayList<String>();

        File sdcard2 = new File(File.separator + "storage" + File.separator
                + "sdcard1");

        // sdcard2 是否挂载
        if (Environment.getStorageState(sdcard2).equals(
                Environment.MEDIA_MOUNTED)) {
            // /storage/sdcard1
            paths.add(File.separator + "storage" + File.separator + "sdcard1"
                    + File.separator);

        }
        // storage/sdcard0
        paths.add(File.separator + "storage" + File.separator + "sdcard0"
                + File.separator);
        return paths;
    }

    /**
     * <功能描述> 动态计算剩余空间的大小是否需要删除视频 录制文件中最大文件的2倍
     * 
     * @return [参数说明]
     * @return long [返回类型说明]
     */
    public static long getFreeSpaceLimit() {
        // 首先获取最大文件的SP信息
        long maxSize = SharePrefsUtils.getMaxRecordVideoFile(MyApplication
                .getInstance().getContext());

        return (long) (maxSize * 2.5);

    }
}
