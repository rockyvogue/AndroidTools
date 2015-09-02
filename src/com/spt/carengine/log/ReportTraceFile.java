
package com.spt.carengine.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

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
 * @Time 2015年7月14日 下午2:11:11
 * @descrition Write to information to File :
 */
public class ReportTraceFile {
    // 是否记录行车轨迹
    private static final boolean IS_RECORD = true;

    private static final String TAG = "RecordWheelpathFile";

    // 记录行车轨迹的文件夹
    public static final String RECORD_WHEEL_PATH = "/RecordWheelPath/";
    /** 轨迹名称的前缀 */
    private static final String TRACE_PREFIX_NAME = "trace_";

    // handler消息机制
    private Handler handler;
    // Handler线程
    private HandlerThread handlerThread;
    // 日志文件名称
    private String mLogFileName = "";

    // 随机文件
    private RandomAccessFile randomAccessFile = null;

    private static long mLastGenerateFileTime = 0;

    private static final ReportTraceFile instance = new ReportTraceFile();

    public static ReportTraceFile getInstance() {
        return instance;
    }

    public ReportTraceFile() {
        // 初始化handler线程处理，
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        mLogFileName = getDefaultRecordFileName();
    }

    private static int FIVE_MINUTES_INTERNAL = 5 * 60 * 1000;

    public void writeMsg(String msg) {

        // 模块开关，是否打印
        if (!IS_RECORD)
            return;

        if (mLogFileName.equals("")) {
            reBuildRecordTraceName();
        }

        if (System.currentTimeMillis() - mLastGenerateFileTime >= FIVE_MINUTES_INTERNAL) {
            reBuildRecordTraceName();
        }

        android.util.Log.d(TAG, msg);

        startThreadWriteMsg(msg);
    }

    /**
     * 重新改变记录的名称
     */
    public void reBuildRecordTraceName() {
        mLogFileName = getDefaultRecordFileName();
    }

    /**
     * 得到默认的日志Log日志。
     * 
     * @return
     */
    private String getDefaultRecordFileName() {
        String logFileName = "";
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmm",
                Locale.CHINESE);// 24小时制
        logFileName = TRACE_PREFIX_NAME + sdformat.format(date).toString();
        mLastGenerateFileTime = System.currentTimeMillis();
        return logFileName;
    }

    /**
     * 开始将行车轨迹写入文件,
     * 
     * @param msg
     */
    private void startThreadWriteMsg(String msg) {
        handler.post(new WriteRunnable(msg));
    }

    /**
     * 写入文件线程
     * 
     * @author wuwen
     * @version 1.0
     * @create 2015年4月7日 下午2:06:49
     */
    class WriteRunnable implements Runnable {

        private String msg;

        public WriteRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            // 获取文件路径
            randomAccessFile = makeRandomFile();
            // 将日志写入文件
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.seek(randomAccessFile.length());
                    byte[] bytemsg = (msg + "\n").getBytes("utf-8");
                    randomAccessFile.write(bytemsg);
                    randomAccessFile.close();
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
            File rootDir = new File(getSdcardRootPath() + RECORD_WHEEL_PATH);
            if (!rootDir.exists()) {
                rootDir.mkdir();
            }

            // 如果日志文件的名称为空，则返回
            if (mLogFileName.equals(""))
                return null;
            // 得到日志文件所在位置的绝对路径
            String filename = rootDir.getPath() + File.separator + mLogFileName
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
