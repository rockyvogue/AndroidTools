
package com.spt.carengine.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @description 工具类,大家在这个工具类中添加方法的时候请注意分类，
 * @author Rocky
 * @version 1.0.0
 * @date 2014-6-6
 */
public class UtilTools {

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return mWifi.isConnected();
        }
        return false;
    }

    /**
     * 判断互联网是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isInternetAvaible(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = connManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();

            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否为飞行模式
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**************************************** 文件相关类 *****************************************/
    /**
     * 获取后缀名
     * 
     * @param name
     * @return
     */
    public static String getExtensionFromName(String name) {
        String prefix = name.substring(name.lastIndexOf(".") + 1);
        return prefix;
    }

    /**
     * 获取名称的前缀
     * 
     * @param name
     * @return
     */
    public static String getPrefixFromName(String name) {
        String prefix = name.substring(0, name.lastIndexOf("."));
        return prefix;
    }

    /**
     * 获取名称的"/"分隔符后面的字段
     * 
     * @param name
     * @return
     */
    public static String getSpecialFromName(String name) {
        String prefix = name.substring(name.lastIndexOf("/") + 1);
        return prefix;
    }

    /**
     * 获取名称的"/"分隔符前面的字段
     * 
     * @param name
     * @return
     */
    public static String getPrefixSpecialFromName(String name) {
        String prefix = name.substring(0, name.lastIndexOf("/"));
        return prefix;
    }

    /**
     * 获取前缀的名称的个数
     * 
     * @param name
     * @return
     */
    public static int getFilePrefixLength(String name) {
        String prefix = name.substring(0, name.lastIndexOf("."));
        return prefix.length();
    }

    /**
     * 创建文件
     * 
     * @param createdir
     * @return
     */
    public static String createFolderPath(String createdir) {
        File downfolder = new File(createdir);
        if (!downfolder.exists()) {
            downfolder.mkdirs();
        }
        return createdir;
    }

    /**
     * 创建本地文件夹
     * 
     * @param createdir
     * @return
     */
    public static boolean createFolderInSdcard(String createdir) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;

        boolean isCreateSu = false;
        File downfolder = new File(createdir);
        if (!downfolder.exists()) {
            downfolder.mkdirs();
            isCreateSu = true;
        } else {
            isCreateSu = false;
        }
        return isCreateSu;
    }

    /**
     * 从路径中获取文件名称.
     * 
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        int idx = filePath.lastIndexOf("/");
        filePath = filePath.substring(idx + 1, filePath.length());
        return filePath;
    }

    /**
     * 去掉文件名得到为文件所在的目录
     * 
     * @param fileAllpath
     * @return
     */
    public static String getFilePath(String fileAllpath) {
        if (fileAllpath == null) {
            return null;
        }
        int idx = fileAllpath.lastIndexOf("/");
        if (idx == -1) {
            return null;
        }
        String filepath = fileAllpath.substring(0, idx);
        return filepath;
    }

    /**
     * 文件集合列表排序
     * 
     * @param files
     */
    public static void do_sort(List<File> files) {
        if (!files.isEmpty()) {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return (object1.getPath().substring(
                            object1.getPath().lastIndexOf("/"))
                            .toLowerCase(Locale.getDefault()))
                            .compareTo((String) object2
                                    .getPath()
                                    .substring(
                                            object1.getPath().lastIndexOf("/"))
                                    .toLowerCase(Locale.getDefault()));
                }

            });
        }
    }

    /**
     * 删除路径中的最后一个组成部分，即"/"后的部分
     * 
     * @param path 传入的路径参数
     * @return 返回"/"之前的字符串
     */
    public static String deleteFileNameFromPath(String path) {
        int idx = path.lastIndexOf("/");
        if (idx == -1) {
            return null;
        }
        String filePath = "";
        filePath = path.substring(0, idx);
        return filePath;
    }

    /**************************************** 字符串转码相关类 *****************************************/
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
            e.printStackTrace();
        }
        return info;
    }

    /**
     * UTF8编码转URL编码
     * 
     * @param utf8Str
     * @return
     */
    public static String getURLCodeInfoFromUTF8(String utf8Str) {
        String info = "";
        try {
            // 转成UTF-8
            info = URLEncoder.encode(utf8Str, "UTF-8");
            // 把空格转成%20
            info = replaceSpecialStingFormString(info, "+", "%20");

            // 去掉特殊字符
            info = getURLCodeInfoForSpecialChar(info);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 替换特殊字符，将strFind字符串替换为strInstead
     * 
     * @param strText 目标字符串
     * @param strFind 需要转换的字符
     * @param strReplay 转换成需要的字符
     * @return 返回转换之后的字符
     */
    public static String replaceSpecialStingFormString(String strText,
            String strFind, String strInstead) {

        if ((strText == null) || (strFind == null) || (strInstead == null))
            return null;

        String strTempFront = null;
        String strTempBehind = null;
        int length = strFind.length();

        // 循环查找，替换
        int index = strText.indexOf(strFind);
        while (index != -1) {
            strTempFront = strText.substring(0, index);
            strTempBehind = strText.substring(index + length);
            strText = strTempFront + strInstead + strTempBehind;

            // 继续下一次查找
            index = strText.indexOf(strFind);
        }

        return strText;
    }

    /**
     * 将 "%2F"字符 转换为 "/" 字符。
     * 
     * @param urlStr 需要转换的字符串
     * @return 返回已经修改了的字符串
     */
    public static String getURLCodeInfoForSpecialChar(String urlStr) {
        // 替换掉%2F为/特殊字符
        urlStr = replaceSpecialStingFormString(urlStr, "%2F", "/");
        return urlStr;
    }

    /**
     * 将utf-8编码的字符转换为string
     * 
     * @param utf8Str
     * @return
     */
    public static String getInfoUTF8toStr(String utf8Str) {
        String info = "";
        try {
            info = URLDecoder.decode(utf8Str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 去掉字符串中的%20
     */
    public static String strSpaceConversionTo20(String ufilename) {
        String strSC = "";
        strSC = ufilename.replace(" ", "%20");
        return strSC;
    }

    /** 16进制数字字符集 */
    private static String hexString = "0123456789ABCDEF";

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }

    /**************************************** 时间相关类 *****************************************/

    public static final String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"
    };

    /**
     * 设置时间格式
     * 
     * @param file
     * @return
     */
    public static String getFileLastModifiedTime(File file) {
        long modifiedTime = file.lastModified();
        Date date = new Date(modifiedTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM",
                Locale.getDefault());
        String dd = sdf.format(date);
        return dd;
    }

    /**
     * 修改获取的时间的显示方式 获取到的时间显示方式：Fri, 06 Dec 2013 06:05:45 GMT 修改为：2013/12/06
     * 06:05:45
     * 
     * @author Wangcan
     */
    public static String modifyTimeShowStyle(String time) {
        String timeShowStyle = "";
        String[] times = time.split(" ");

        if (times.length == 2) {
            // 显示为 2014-12-06 06:05:45
            timeShowStyle = time.replace("-", "/");
        } else if (times.length == 6) {
            String createDay = times[1];
            String createMonth = times[2];
            String createYear = times[3];
            String createTime = times[4];

            int i = 0;
            for (; i < months.length; i++) {
                if (createMonth.equals(months[i])) {
                    break;
                }
            }

            String createMonthNew = "";
            if (i < 10) {
                createMonthNew = "0" + (i + 1);
            } else {
                createMonthNew = Integer.toString(i);
            }

            // 日期为单个字符，前面加0
            if (createDay.length() == 1) {
                createDay += "0";
            }

            timeShowStyle = createYear + "/" + createMonthNew + "/" + createDay
                    + " " + createTime;
        } else {
            timeShowStyle = time;
        }

        return timeShowStyle;
    }

    /**
     * 转化格林时间
     * 
     * @param formatDate 传入的参数单位为秒
     * @return
     */
    public static String formatGMTDate(String formatDate) {
        long date = Long.parseLong(formatDate) * 1000;

        String convertDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            convertDate = sdf.format(new Date(date));

        } catch (Exception e) {

        } finally {
            return convertDate;
        }
    }

    /**************************************** MD5值转换相关类 *****************************************/
    /**
     * 将key路径的文件转换为MD5值
     * 
     * @param key
     * @return
     */
    public static String cachePathMD5ValueForKey(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(
                    "String to encript cannot be null or zero length");
        }

        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] hash = md.digest();

            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hexString.toString();
    }

    /**************************************** sdcard的空间大小相关类 *****************************************/
    /**
     * 获得card的可用空间大小
     * 
     * @param path
     * @return
     */
    @SuppressWarnings("deprecation")
    static public long getCardMemorySize(String path) {
        StatFs stat = new StatFs(path);
        // StatFs stat = new StatFs(UtilTools.getInfoUTF8toStr(path));
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 转换文件大小(MB、GB...).
     * 
     * @param fileSize
     * @return
     */
    public static String FormetFileSize(String fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");

        long fileS = 0;
        if (fileSize.equals("")) {
            fileS = 0;
        } else {
            fileS = Long.parseLong(fileSize);
        }

        String fileSizeString = "";

        if (fileS == 0) {
            fileSizeString = "0.0B";

        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";

        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";

        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";

        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }

        return fileSizeString;
    }

    /**
     * 转换文件大小(MB为单位)
     * 
     * @param fileSize
     * @return
     */
    public static String formetFileSizeToMB(String fileSize) {
        DecimalFormat df = new DecimalFormat();
        long fileS = 0;
        if (fileSize.equals("")) {
            fileS = 0;
        } else {
            fileS = Long.parseLong(fileSize);
        }
        String fileSizeString = "";
        fileSizeString = df.format(fileS / 1048576);
        fileSizeString = fileSizeString.replace(",", "");

        return fileSizeString;
    }

    /**
     * 判断是否有SD卡是否存在
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(String fileDevPath) {
        int start = fileDevPath.lastIndexOf(".");
        if (start < 0) {
            return null;
        }
        int end = fileDevPath.length();
        return fileDevPath.substring(start, end);
    }

    /**
     * 获取存储路径
     * 
     * @return
     */
    public static String getExternalStoragePath() {
        // 获取SdCard状态
        String state = android.os.Environment.getExternalStorageState();
        // 判断SdCard是否存在并且是可用的
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
                return android.os.Environment.getExternalStorageDirectory()
                        .getPath();
            }
        }
        return null;
    }

    /**
     * 获取指定路径文件剩余容量.
     * 
     * @param filePath
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableStore(String filePath) {
        // 取得sdcard文件路径
        StatFs statFs = new StatFs(filePath);
        // 获取block的SIZE
        long blocSize = statFs.getBlockSize();
        // 获取BLOCK数量
        // long totalBlocks = statFs.getBlockCount();
        // 可使用的Block的数量
        long availaBlock = statFs.getAvailableBlocks();
        // long total = totalBlocks * blocSize;
        long availableSpare = availaBlock * blocSize;
        return availableSpare;
    }

    public static String getFileTypeWithNoNod(String fileDevPath) {
        int start = fileDevPath.lastIndexOf(".");
        if (start < 0) {
            return null;
        }
        int end = fileDevPath.length();
        return fileDevPath.substring(start + 1, end);
    }

    /**
     * 隐藏键盘
     * 
     * @param context
     * @param window
     */
    public static void hideSoftKeyboard(Context context, IBinder windowTockn) {
        InputMethodManager inputManger = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManger != null) {
            inputManger.hideSoftInputFromWindow(windowTockn, 0);
        }
    }

    public static String getUTF8Info2(String ufilename) {

        int index = -1;
        StringBuilder retName = new StringBuilder();
        String tmpstr;
        while (true) {
            index = ufilename.indexOf(' ');

            if (index != -1) {
                tmpstr = ufilename.substring(0, index);
                try {
                    retName.append(URLEncoder.encode(tmpstr, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                retName.append("%20");
                ufilename = ufilename.substring(index + 1);
            } else
                break;
        }

        try {
            retName.append(URLEncoder.encode(ufilename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retName.toString();

    }

    /******************************************* 获取本地音乐缩略图的方法 *************************************/
    /**
     * 获取本地音乐缩略图的方法
     * 
     * @param filePath
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap getLocalMusicBitmap(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        bitmap = Bytes2Bimap(mmr.getEmbeddedPicture());
        return bitmap;
    }

    @SuppressLint("NewApi")
    public static Bitmap getLocalVideoBitmap(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        return mmr.getFrameAtTime();
    }

    /**
     * 将字节转换为Bitmap图片
     * 
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b == null)
            return null;

        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);

        } else {
            return null;
        }
    }

    public static String FormetkbTo(String fileSize) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.0");
        long fileS = 0;
        if (fileSize.equals("")) {
            fileS = 0;
        } else {
            try {
                fileS = Long.parseLong(fileSize);
            } catch (Exception e) {
                fileS = 0;
            }

        }
        String fileSizeString = "";
        String temp = "";
        if (fileS < 1024) {
            temp = df.format((double) fileS);
            if (temp.equals(".0")) {
                temp = "0";
            }
            fileSizeString = temp + "MB";
        } else if (fileS < 1048576) {
            temp = df.format((double) fileS / 1024);
            if (temp.equals(".0")) {
                temp = "0";
            }
            fileSizeString = temp + "GB";
        } else if (fileS < 1073741824) {
            temp = df.format((double) fileS / 1048576);
            if (temp.equals(".0")) {
                temp = "0";
            }
            fileSizeString = temp + "TB";
        } else {
            // fileSizeString = df.format((double) fileS / 1073741824) + "T";
        }

        return fileSizeString;
    }

    /**
     * 匹配由数字、26个英文字母或者下划线空格组成的字符串
     * 
     * @param str
     * @return
     */
    public static boolean isMatchValidator(String str) {
        return str.matches("^[A-Za-z0-9 -]+$");
    }

    /**
     * 密码由英文字母（字母区分大小写）和数字组成，且长度5~32位!
     * 
     * @param str
     * @return
     */
    public static boolean isMatchValidator3(String str) {
        boolean b = str.matches("^[A-Za-z0-9]+$");
        return b;
    }

    /**
     * 以英文字母开头，由字母、数字和‘-’组成，且长度2~8位.
     * 
     * @param str
     * @return
     */
    public static boolean isMatchValidator2(String str) {
        boolean b = str.matches("^[A-Za-z0-9-]+$");
        if (b) {
            String s = str.substring(0, 1);
            b = s.matches("^[A-Za-z]+$");
        }
        return b;
    }

    // ----------------------------------fildnode相关
    /**
     * 截取文件类型，如"爱笑的眼睛.mp3",调用这个方法，则返回"mp3";
     * 
     * @param name ： 文件名称 ，如： "爱笑的眼睛.mp3"
     * @return 返回文件的类型，如 : mp3
     */
    public static String getFileTypeFromName(String name) {
        String prefix = name.substring(name.lastIndexOf(".") + 1);
        return prefix;
    }

    /**
     * 弹出键盘
     * 
     * @param editText
     */
    public static void showKeyboard(final EditText editText) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {

                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }

        }, 1000);
    }

    /************************************ 视频相关类 *********************************************/
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
     * 传入一个long型的时间值得到一个表示时间的字符串
     * 
     * @param timeMs
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeToStr(long time) {

        // 一秒:1 * 1000
        // 一分钟 : 1 * 60 * 1000;
        // 一小时 : 1 * 60 * 60 * 1000;
        long deriod = 1 * 60 * 60 * 1000;
        String ruleTime = "HH:mm:ss";
        if (time < deriod) {
            ruleTime = "mm:ss";
        } else {
            ruleTime = "HH:mm:ss";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(ruleTime);// 初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String hms = formatter.format(time);
        return hms;
    }

    /**
     * 将进度长度转变为进度时间
     * 
     * @param length
     * @return
     */
    public static String lengthTime(long length, int byteBit) {
        length /= 1000L;
        long minute = length / 60L;
        long hour = minute / 60L;
        long second = length % 60L;
        minute %= 60L;
        if (byteBit == 1) {
            return String.format("%02d", second);

        } else if (byteBit == 2) {
            return String.format("%02d:%02d", minute, second);

        } else if (byteBit == 3) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }

        return String.format("%02d:%02d:%02d", hour, minute, second);
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

    private static final int DEFAULT_HD_FILE_SIZE = 2 * 1024 * 1024; // 2M

    private static final int DEFAULT_ORIGAL_FILE_SIZE = 500 * 1024; // 500kb

    /**
     * 判断是否需要做高清缩略图
     */
    public static boolean isLoadingHDThumb(String fileSizeStr) {
        int fileSize = Integer.parseInt(fileSizeStr);

        if (fileSize >= DEFAULT_HD_FILE_SIZE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否需要做高清缩略图
     */
    public static boolean isLoadingOrigalPicture(String fileSizeStr) {
        int fileSize = Integer.parseInt(fileSizeStr);

        if (fileSize >= DEFAULT_ORIGAL_FILE_SIZE) {
            return true;
        }
        return false;
    }

    /***
     * 加密和解密都用这一个方法。 也就是说参数String aInput 可以传一个明文，也可以传一个加密后的字符串，
     * 程序会自动的识别。然后执行加解密的响应操作。
     * 
     * @param aInput
     * @param aKey
     * @return
     */
    public static String encryptRC4(String aInput, String aKey) {
        int[] iS = new int[256];
        byte[] iK = new byte[256];

        for (int i = 0; i < 256; i++)
            iS[i] = i;

        int j = 1;

        for (short i = 0; i < 256; i++) {
            iK[i] = (byte) aKey.charAt((i % aKey.length()));
        }

        j = 0;

        for (int i = 0; i < 255; i++) {
            j = (j + iS[i] + iK[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
        }

        int i = 0;
        j = 0;
        char[] iInputChar = aInput.toCharArray();
        char[] iOutputChar = new char[iInputChar.length];
        for (short x = 0; x < iInputChar.length; x++) {
            i = (i + 1) % 256;
            j = (j + iS[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
            int t = (iS[i] + (iS[j] % 256)) % 256;
            int iY = iS[t];
            char iCY = (char) iY;
            iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
        }

        return new String(iOutputChar);
    }

    // 默认参数
    public static final int DEFAULT_ARG1 = -1;
    public static final int DEFAULT_ARG2 = -1;
    public static final Object DEFAULT_Obj = null;

    /** 发送命令结果消息 */
    public static void sendCommandHandlerMessage(Handler cmdHandler, int what,
            int arg1, int arg2, Object obj) {
        if (cmdHandler != null) {
            Message msg = new Message();
            msg.what = what;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msg.obj = obj;
            cmdHandler.sendMessage(msg);
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // ("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getYesterdayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // ("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis() - 24 * 60 * 60
                * 1000);// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getBehaviorId() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 跳转到指定的网页
     * 
     * @param mContext
     * @param url
     */
    public static final void startDefineUrl(Context mContext, String url) {
        if (mContext == null)
            return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        mContext.startActivity(intent);
    }

    /** 获取当前IP地址 */
    public static String getLocalIpAddress() {
        try {
            // 遍历网络接口
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                // 遍历IP地址
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // 非回传地址时返回
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocalIpAddress2() {
        String networkIp = null;
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                if (iface.getDisplayName().equals("eth0")) {
                    List<InetAddress> addresses = Collections.list(iface
                            .getInetAddresses());
                    for (InetAddress address : addresses) {
                        if (address instanceof Inet4Address) {
                            networkIp = address.getHostAddress();
                        }
                    }
                } else if (iface.getDisplayName().equals("wlan0")) {
                    List<InetAddress> addresses = Collections.list(iface
                            .getInetAddresses());
                    for (InetAddress address : addresses) {
                        if (address instanceof Inet4Address) {
                            networkIp = address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return networkIp;
    }

    /**
     * 获取手机型号
     * 
     * @return
     */
    public static String getTypeInfo() {
        return android.os.Build.MODEL; // 手机型号
    }

    /**
     * 获取手机系统版本
     * 
     * @return
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取本机的ip
     * 
     * @return
     */
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                                    .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;
    }

    /**
     * 判断是否含有特殊字符
     * 
     * @param name
     * @return
     */
    public static boolean isNameContainSpecChar(String name) {

        if (name.length() < 1)
            return true;

        if (name.contains("\\") || name.contains("/") || name.contains(":")
                || name.contains("*") || name.contains("\"")
                || name.contains("<") || name.contains(">")
                || name.contains("|") || name.contains("?")) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            // 如果仅仅是用来判断网络连接
            // 则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**************************************** android系统设备获取信息相关类 *****************************************/
    /**
     * @description 获取手机IMEI号
     * @author Snow
     * @date 2014-4-10
     * @param mContext
     * @return
     */
    public static String getDevicesID(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取手机型号
     * 
     * @author Snow
     * @date 2014-4-10
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 得到android的id
     * 
     * @author Snow
     * @date 2014-4-10
     * @param mContext
     * @return
     */
    public static String getAndroidID(Context mContext) {
        String androidId = Settings.Secure.getString(
                mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取手机号码
     * 
     * @param context
     * @return 返回获取到的手机号码
     */
    public static String getLocalTelPhoneNumber(Context context) {
        // 获取手机号码
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();// 获取智能设备唯一编号
        String te1 = tm.getLine1Number();// 获取本机号码
        String imei = tm.getSimSerialNumber();// 获得SIM卡的序号
        String imsi = tm.getSubscriberId();// 得到用户Id
        return te1;
    }

    public static String getFileModifiedTime(long time) {
        Date dat = new Date(time);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");
        String sb = format.format(gc.getTime());
        return sb;
    }

    /**
     * <功能描述> 通过报名打开其他软件的方法
     * 
     * @param packagename 需要打开软件的报名
     * @param mContext [参数说明] 上下文对象
     * @return void [返回类型说明]
     * @throws NameNotFoundException
     */
    public static void doStartApplicationWithPackageName(String packagename,
            Context mContext) throws NameNotFoundException {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        packageinfo = mContext.getPackageManager().getPackageInfo(packagename,
                0);
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            mContext.startActivity(intent);
        }
    }

}
