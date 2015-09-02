
package com.spt.carengine;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import cn.yunzhisheng.vui.assistant.IDataControl;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.log.AppException;
import com.spt.carengine.port.SerialPort;
import com.spt.carengine.port.SerialPortFinder;
import com.spt.carengine.recordvideo.BackgroundRecordVideoService;
import com.spt.carengine.recordvideo.RecordService;
import com.spt.carengine.recordvideo.RecordService.RecordVideoLocalBinder;
import com.spt.carengine.recordvideo.StartRecordRunnable;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.voice.assistant.talk.TalkService;
import com.spt.carengine.recordvideo.SwitchSVMessage;

import de.greenrobot.event.EventBus;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.InvalidParameterException;

public class MyApplication extends Application {

    private static MyApplication instance = null;
    private static final int THREAD_POOL_SIZE = 2;
    public boolean m_bUserActivate = false; // 是否激活

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    private SerialPort mEDogSerialPort = null;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        instance = this;
        init();
    }

    public SerialPort getSerialPort() throws SecurityException, IOException,
            InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences(
                    "com.spt.carengine.android_serialport_api.sample_preferences",
                    MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            path = "/dev/ttyMT1";
            baudrate = 115200;

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            try {
                /* Open the serial port */

                mSerialPort = new SerialPort(new File(path), baudrate, 0);

                Log.e("Application", "path:" + path + " baudrate:" + baudrate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("Application", "mSerialPort:" + mSerialPort);
        if (mSerialPort != null) {
            return mSerialPort;
        }
        return null;
    }

    public SerialPort getEDogSerialPort() throws SecurityException,
            IOException, InvalidParameterException {
        if (mEDogSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences(
                    "com.spt.carengine.android_serialport_api.sample_preferences2",
                    MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            path = "/dev/ttyMT2";
            baudrate = 9600;

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            try {
                /* Open the serial port */
                mEDogSerialPort = new SerialPort(new File(path), baudrate, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("Application", "path:" + path + " baudrate:" + baudrate);
        }

        Log.e("Application", "mSerialPort:" + mEDogSerialPort);
        if (mEDogSerialPort != null) {
            return mEDogSerialPort;
        }
        return null;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mEDogSerialPort != null) {
            mEDogSerialPort.close();
            mEDogSerialPort = null;
        }
    }

    /**
     * 单例
     * 
     * @return
     */
    public static MyApplication getInstance() {
        return instance;
    }

    private void init() {
        initImageLoader(getApplicationContext());
        // initUncaughtException();
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // // 注册app奔溃后重启app ////
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化未知的异常错误
     */
    private void initUncaughtException() {
        /** 注册App异常崩溃处理器 */
        if (this.getResources().getBoolean(R.bool.uncaught_log_switch)) {
            Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
        }
    }

    /**
     * App异常崩溃处理器
     */
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    private UncaughtExceptionHandler getUncaughtExceptionHandler() {
        if (uncaughtExceptionHandler == null) {
            uncaughtExceptionHandler = AppException.getInstance(this);
        }
        return uncaughtExceptionHandler;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // // 绑定录像服务和解绑录像服务 ////
    // /////////////////////////////////////////////////////////////////////////////////

    private boolean isBindedService;

    private RecordService bindService = null;

    /**
     * <功能描述> 在线程中启动后来录像，和初始化imageloader图片加载器模块 [参数说明]
     * 
     * @return void [返回类型说明]
     */
    public void bindRecordVideoService() {
        Intent intent = new Intent(instance, RecordService.class);
        bindService(intent, recordVideoConn, Context.BIND_AUTO_CREATE);
    }

    public void startRecordVideoServer() {
        StartRecordRunnable startRecordRunnable = new StartRecordRunnable(this,
                StartRecordRunnable.BIND_RECORD_VIDEO_SERVERR);
        Thread thread = new Thread(startRecordRunnable);
        thread.start();
    }

    public void unBindRecordVideoService() {
        if (isBindedService == true) {
            unbindService(recordVideoConn);
            // 如果处于倒车状态，发送消息
            if (SharePrefsUtils.getRebackCarState(MyApplication.getInstance()
                    .getApplicationContext())) {
                bindService.getGameDisplay().releaseCamera();
            }
            isBindedService = false;
        }
    }

    private ServiceConnection recordVideoConn = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordVideoLocalBinder binder = (RecordVideoLocalBinder) service;
            bindService = binder.getService();
            isBindedService = true;

            SwitchSVMessage message = new SwitchSVMessage();
            message.setMessageType(Constant.BINDSERVICE_SUCCESS);

            EventBus.getDefault().post(message);

        }
    };

    public RecordService getBindService() {
        return bindService;
    }

    /**
     * 初始化图片加载器
     */
    private void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .threadPoolSize(THREAD_POOL_SIZE)
                // .memoryCacheSize(40 * 1024 * 1024)// 设置程序的缓存 ，默认为程序内存的1/8
                .tasksProcessingOrder(QueueProcessingType.FIFO).build();
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }
        ImageLoader.getInstance().init(config);
    }

    public static int mErrorRetalkCount = 0;

    public IDataControl getDataControl() {
        return TalkService.getDataControl();
    }

    public Context getContext() {

        return mContext;
    }
}
