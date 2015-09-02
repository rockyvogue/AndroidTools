
package com.spt.carengine.log;

import android.content.Context;
import android.content.Intent;

import com.spt.carengine.MyApplication;
import com.spt.carengine.activity.MainActivity;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @description 异常类的处理，如果应用程序崩溃了，那么这个类可以将崩溃的信息打印到文件中，这样就可以方便查询是哪里出错了
 * @author Rocky
 * @version 1.0.0
 * @date 2014年6月10日,上午11:40:05
 */
public class AppException implements UncaughtExceptionHandler {

    private static AppException instance;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private AppException(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * Java Single Module
     * 
     * @param context
     * @return
     */
    public static AppException getInstance(Context context) {
        if (instance == null) {
            instance = new AppException(context);
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);

        } else {
            restartApp(MyApplication.getInstance());
        }
    }

    /**
     * 初始化启动界面Activity
     * 
     * @param context
     */
    public void restartApp(Context context) {
        if (context != null) {
            Intent mIntent = new Intent(context, MainActivity.class);
            context.startActivity(mIntent);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     * 
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        /**
         * 将崩溃的错误信息写入到文件中
         */
        LOG.writeMsg(ex);
        return true;
    }
}
