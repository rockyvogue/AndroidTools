
package com.spt.carengine.log;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.format.DateFormat;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Rocky
 * @Time 2015年7月13日 下午4:43:25
 * @description 打印日志到文件中的类，
 *              (LogManager.getInstance().Log(this,LogManager.MODE_LOGIN,
 *              "登录日志测试" );这样调用)
 */
public class LogManager {

    public static final String LOG_DIRECTORY = File.separator + MyApplication.getInstance().getString(R.string.app_name);

    private Handler handler;
    private HandlerThread handlerThread;
    private String logname = "";

    /** 显示所有模块日志 */
    private static final int MODE_ALL = 0XFFFFF; // 1111 1111 1111 1111

    /** 关闭所有日志 */
    private static final int MODE_CLOSE = 0; // 0000 0000 0000 0000

    /***
     * 模块打印开关 全部打印 0xff 255 全部关闭 0
     */
    public static int LOG_SWITCH = 
//            LOG.MODE_UPLOAD_FILE 
//            | LOG.MODE_MAIN_SERVER
//     | LOG.MODE_BLUETOOTH
     LOG.MODE_VOICE;
//     | LOG.MODE_RECORD_VIDEO 

    private static final LogManager logManager = new LogManager();

    public static LogManager getInstance() {
        return logManager;
    }

    public LogManager() {
        // 初始化handler线程处理
        handlerThread = new HandlerThread("writelog");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        logname = getLogFileName();
    }

    private String getLogFileName() {
        String logFileName = "";
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdformat = new SimpleDateFormat("HH_mm",
                Locale.CHINESE);// 24小时制
        logFileName = sdformat.format(date).toString();
        return logFileName;
    }

    /***
     * 打印单条日志
     * 
     * @param obj
     * @param mode
     * @param msg
     */
    protected void Log(Object obj, int mode, String msg) {

        // 模块开关，是否打印
        if (!checkPermit(mode)) {
            return;
        }

        // 获取代码位置信息 类名 、行号
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        String strClassName = "";
        int mLineNumber = 0;
        for (StackTraceElement ste : stack) {
            if (ste.getClassName().equals(obj.getClass().getName())) {
                strClassName = ste.getClassName();
                mLineNumber = ste.getLineNumber();
                break;
            }
        }

        // 生成日志时间
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss SSS", Locale.CHINESE);// 24小时制
        String dateString = dateFormat.format(date).toString();

        // 屏幕打印日志 [Date time]*[File name +line]*[msg]
        android.util.Log.d("logmanager", "[ " + strClassName + "] * [" + msg
                + "]");

        // 写入sd卡文件 [Date time]*[File name +line]*[msg]
        writeMsg("[" + dateString + "]*[ " + strClassName + "+ " + mLineNumber
                + "]*[" + msg + "]");
    }

    /***
     * 打印单条日志
     * 
     * @param ex
     */
    @SuppressLint("SimpleDateFormat")
    public void Log(Throwable ex) {

        // 获取代码位置信息
        StackTraceElement ste = ex.getStackTrace()[1];
        String strClassName = ste.getClassName();
        int mLineNumber = ste.getLineNumber();

        // 生成日志时间
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss SSS");// 24小时制
        String dateString = dateFormat.format(date).toString();

        // 屏幕打印日志 [Date time]*[File name +line]*[msg]
        android.util.Log.d("mtg",
                "[" + dateString + "]*[ " + strClassName + "+ " + mLineNumber
                        + "]\n*[" + ex.toString() + ex.getMessage() + "]");

        // 写入sd卡文件 [Date time]*[File name +line]*[msg]
        writeError("[" + dateString + "]*[ " + strClassName + "+ "
                + mLineNumber + "]\n*[" + ex.toString() + ex.getMessage() + "]");
    }

    /***
     * 模块打印开关
     * 
     * @param mode
     * @return
     */
    private boolean checkPermit(int mode) {
        if ((mode & LOG_SWITCH) == 0) {
            return false;
        }
        return true;
    }

    /***
     * @Title: writeMsg
     * @Description: 手动书写信息进入sd卡
     * @param head 代码位置
     * @param msg 错误信息
     * @return void
     * @author wangqing
     * @Date 2013-5-3 上午11:54:11
     */
    private void writeMsg(String msg) {
        handler.post(new WriteRunnable(msg));
    }

    /***
     * 打印程序退出错误信息 此时程序已经退出，无法新建线程，直接打印
     * 
     * @param msg
     */
    private void writeError(String msg) {
        new WriteRunnable(msg).run();
    }

    /***
     * 写入文件线程
     * 
     * @author walt
     * @version 创建时间：2014-6-11 类说明
     */
    class WriteRunnable implements Runnable {
        private String msg;

        public WriteRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            // 获取文件路径
            RandomAccessFile rFile = makeRandomFile();
            // 将日志写入文件
            if (rFile != null) {
                try {
                    rFile.seek(rFile.length());
                    byte[] bytemsg = ("log," + msg + "\n").getBytes("gbk");
                    rFile.write(bytemsg);
                    rFile.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * 新建日志文件
     * 
     * @return
     */
    private RandomAccessFile makeRandomFile() {
        // 判断是否有sd卡
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);

        RandomAccessFile randomAccessFile = null;

        if (sdCardExist) {

            // 检查根目录
            File rootDir = new File(getSdcardRootPath() + LOG_DIRECTORY);
            if (!rootDir.exists()) {
                rootDir.mkdir();
            }

            // 新建日期文件夹
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            DateFormat dateFormat = new DateFormat();
            @SuppressWarnings("static-access")
            String dateString = dateFormat.format("yyyy-MM-dd", date)
                    .toString();
            File logDir = new File(rootDir.getPath() + File.separator
                    + dateString);
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            String filename = logDir.getPath() + File.separator + logname
                    + ".txt";
            File logfile = new File(filename);

            // 新建RandomAccessFile 文件
            try {
                randomAccessFile = new RandomAccessFile(logfile, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return randomAccessFile;
    }

    /**
     * 获取手机sdcard的根目录路径
     * 
     * @return
     */
    public static String getSdcardRootPath() {
        String sdcardRootPath = Environment.getExternalStorageDirectory()
                .toString();
        return sdcardRootPath;
    }

}
