
package com.spt.carengine.recordvideo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

import com.spt.carengine.MyApplication;
import com.spt.carengine.log.LOG;
import com.spt.carengine.utils.SharePrefsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年7月22日 下午2:15:47
 * @description 后台录制视频的服务
 */
public class BackgroundRecordVideoService extends Service {

    private static final String TAG = "BackgroundRecordVideoService";

    private static final String VIDEO_PREFIX = "/VID_";

    /**
     * 视频输出格式为.mp4
     */
    private static final String OUTPUT_FILE_TYPE = ".mp4";

    /**
     * 拍摄照片的时间间隔
     */
    private static final int RECORD_VIDEO_INTERNAL = 5 * 60 * 1000;

    /**
     * 默认的视频窗口大小
     */
    private static final int DEFAULT_SIZE = 160;

    /**
     * 不可见的视频窗口尺寸
     */
    private static final int INVISIBLE_SIZE = 1;

    private static final int OFSET_X = 15;
    private static final int OFSET_Y = 10;

    static enum FloatWindowState {
        LittleWindow, FullScreenWindow, InvisibleWindow;
    };

    private FloatWindowState floatWindowState = FloatWindowState.LittleWindow;

    /**
     * 获取服务对象的类声明
     */
    private IBinder mBinder = new RecordVideoLocalBinder();

    /**
     * 录制视频的类
     */
    private MediaRecorder mediarecorder;

    private Camera mCamera;

    private SurfaceHolder mSurfaceHolder;

    /**
     * 应用程序使用的接口和窗口管理器
     */
    private WindowManager mWindowManager;

    private LayoutParams mFrameLayoutParams;

    private CameraPreview mCameraPreview;

    private FrameLayout mFrameLayout;

    private GetVideoOutputPath getVideoOutputPath;
    // 现在录制文件的路径
    private String currRecordFilePath;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        threadCreateDirectory();

        initWindowView();

        registerBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isServerOnPause = true;
        unRegisterBroadcast();
        stopRecordVideo();
        mWindowManager.removeView(mFrameLayout);
        super.onDestroy();
    }

    @SuppressLint("InlinedApi")
    private void initWindowView() {
        getVideoOutputPath = new GetVideoOutputPath();

        mMediaRecorderRecording = true;
        mWindowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        mFrameLayoutParams = new WindowManager.LayoutParams();
        // 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 那么优先级会降低一些,即拉下通知栏不可见
        mFrameLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mFrameLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        /*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         */
        mFrameLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        mFrameLayoutParams.x = OFSET_X;
        mFrameLayoutParams.y = OFSET_Y;
        // 设置悬浮窗的长得宽
        mFrameLayoutParams.width = DEFAULT_SIZE;
        mFrameLayoutParams.height = DEFAULT_SIZE;
        floatWindowState = FloatWindowState.LittleWindow;
        // 左上角位置
        mFrameLayoutParams.gravity = Gravity.TOP | Gravity.START;
        mCameraPreview = new CameraPreview(this);
        mFrameLayout = new FrameLayout(this);
        Button button = new Button(this);
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = 1;
        layoutParams.height = 1;
        mFrameLayout.addView(mCameraPreview);
        mFrameLayout.addView(button, layoutParams);

        // 窗口模式和全屏模式切换
        mFrameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (floatWindowState == FloatWindowState.LittleWindow) {
                    broadcastReceiver(FULLSCREEN_WINDOW_VALUE);

                } else if (floatWindowState == FloatWindowState.FullScreenWindow) {
                    broadcastReceiver(LITTLE_WINDOW_VALUE);
                }
            }
        });

        mFrameLayout.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                broadcastReceiver(INVISIBLE_WINDOW_VALUE);
                return false;
            }
        });

        mWindowManager.addView(mFrameLayout, mFrameLayoutParams);
    }

    /**
     * 开始录制视频
     */
    @SuppressLint({
            "InlinedApi", "NewApi"
    })
    public void startRecordVideo() {
        if (mSurfaceHolder == null) {
            return;
        }

        mMediaRecorderRecording = true;

        // 创建mediarecorder对象
        if (mediarecorder == null) {
            mediarecorder = new MediaRecorder();

        } else {
            mediarecorder.reset();
            mediarecorder.release();
            mediarecorder = null;
            mediarecorder = new MediaRecorder();
        }

        if (mCamera == null) {
            mCamera = getCameraInstance();
        }

        if (mCamera != null) {
            try {
                // 解锁camera
                mCamera.unlock();
                mediarecorder.setCamera(mCamera);

                mediarecorder.setOnInfoListener(onInfoListener);
                // 设置录制视频源为Camera(相机)
                mediarecorder
                        .setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                // mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                // 设置录制文件质量，格式，分辨率之类，这个全部包括了
                // mediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
                // mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                // mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                // mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                CamcorderProfile mProfile = CamcorderProfile
                        .get(CamcorderProfile.QUALITY_HIGH);
                mProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
                mProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
                mProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;

                mProfile.videoFrameHeight = VIDEO_HEIGHT;
                mProfile.videoFrameWidth = VIDEO_WIDTH;
                boolean is_low = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_LOW);
                boolean is_high = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_HIGH);
                boolean is_480p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_480P);
                boolean is_720p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_720P);
                boolean is_1080p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_1080P);
                boolean is_qvga = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_QVGA);
                boolean is_time_480p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_TIME_LAPSE_480P);
                boolean is_time_720p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_TIME_LAPSE_720P);
                boolean is_time_1080p = CamcorderProfile.hasProfile(
                        Camera.CameraInfo.CAMERA_FACING_BACK,
                        CamcorderProfile.QUALITY_TIME_LAPSE_1080P);

                String profileStr = "wuwen-->>>is_low:" + is_low + ",is_high:"
                        + is_high + ",is_480p:" + is_480p + ",is_720p:"
                        + is_720p + ",is_1080p:" + is_1080p + ",is_qvga："
                        + is_qvga + ",is_time_480p:" + is_time_480p
                        + ",is_time_720p:" + is_time_720p + ",is_time_1080p:"
                        + is_time_1080p;
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, profileStr);
                mediarecorder.setProfile(mProfile);
                mediarecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                currRecordFilePath = getVideoOutputPath.getOutputMediaFile();

                // 设置视频文件输出的路径
                mediarecorder.setOutputFile(currRecordFilePath);
                mediarecorder.setMaxDuration(RECORD_VIDEO_INTERNAL); // 每隔5分钟

                // 准备录制
                mediarecorder.prepare();
                // 开始录制
                mediarecorder.start();

            } catch (IllegalStateException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {

        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                String filePath = new String(currRecordFilePath);
                mMediaRecorderRecording = false;
                startRecordVideo();

                // 计算文件大小，存取最大文件的大小
                getFileSize(filePath);

            } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {

            } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN) {

            }
        }

    };

    /**
     * <功能描述> 存取最大视频文件的大小（byte）
     * 
     * @param filePath [参数说明]
     * @return void [返回类型说明]
     */
    private void getFileSize(String filePath) {

        try {
            long size = 0;
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();

                // 写进sp文件并比较大小，存储最大值
                long maxSize = SharePrefsUtils
                        .getMaxRecordVideoFile(getApplicationContext());

                if (maxSize < size) {
                    SharePrefsUtils.saveMaxRecordVideoFile(
                            getApplicationContext(), size, filePath);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecordVideo() {
        mMediaRecorderRecording = false;
        if (mediarecorder != null) {
            try {
                // 停止录制
                mediarecorder.stop();
                // 释放资源
                mediarecorder.release();
                mediarecorder = null;

                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
                // 为了防止服务的突然中止时还能够计算文件的大小
                getFileSize(currRecordFilePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MyApplication.getInstance().unBindRecordVideoService();
    }

    /**
     * Get video outputFile
     * 
     * @return
     */
    private static final String getVideoOutputPath() {
        String path = FileUtils.getStorageVideoDir() + VIDEO_PREFIX
                + TimeUtils.getCurrentTime() + OUTPUT_FILE_TYPE;
        return path;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // // 获取摄像头Camera的对象 ////
    // ////////////////////////////////////////////////////////////////////////////////
    private int VIDEO_WIDTH = 3328;
    private int VIDEO_HEIGHT = 1872;

    /**
     * 初始化Camera
     */
    @SuppressLint("NewApi")
    private void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera == null)
            return;
        try {

            Camera.Parameters parameters = mCamera.getParameters();// 得到摄像头的参数

            // 获取自己手机合适的尺寸
            StringBuffer sb = new StringBuffer();
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            Iterator<Camera.Size> itor1 = sizeList.iterator();

            while (itor1.hasNext()) {
                Camera.Size cur = itor1.next();
                String str = cur.height + "x" + cur.width + ":";
                sb.append(str);
                VIDEO_WIDTH = cur.width;
                VIDEO_HEIGHT = cur.height;
            }

            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "wuwen-->>sb : " + sb
                    + ",pic_Width:" + VIDEO_WIDTH + ", pic_Height : "
                    + VIDEO_HEIGHT);

            // parameters.setPictureSize(VIDEO_WIDTH, 1080);

            // parameters.setPreviewSize(VIDEO_WIDTH, 1080);
            mCamera.setParameters(parameters);
            parameters = mCamera.getParameters();

            mCamera.startPreview();// 开始预览
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                c = openFacingBackCamera();

            } else {
                c = openLowVersionCamera();
            }
        } catch (Exception e) {
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO,
                    "init camera, in this 'getCameraInstance()' method , open camera err!");
        }
        return c;
    }

    @SuppressLint("NewApi")
    private Camera openFacingBackCamera() {
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return cam;
    }

    private Camera openLowVersionCamera() {
        Camera camera = Camera.open();
        return camera;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // // 拍摄照片的方法 ////
    // ////////////////////////////////////////////////////////////////////////////////

    /**
     * 拍照的方法
     */
    public void doTakePicture() {
        if (mCamera != null) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        } else {
            // 无法拍照
        }
    }

    /* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
    private ShutterCallback mShutterCallback = new ShutterCallback() {
        /**
         * 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
         */
        public void onShutter() {
            LOG.writeMsg(BackgroundRecordVideoService.this,
                    LOG.MODE_RECORD_VIDEO, "mShutterCallback--->>>onShutter...");
        }
    };

    private PictureCallback mJpegPictureCallback = new PictureCallback() {

        /**
         * 对jpeg图像数据的回调,最重要的一个回调
         */
        public void onPictureTaken(byte[] data, Camera camera) {
            LOG.writeMsg(BackgroundRecordVideoService.this,
                    LOG.MODE_RECORD_VIDEO,
                    "mJpegPictureCallback--->>>onPictureTaken...");
            try {
                Bitmap bitmap = null;
                if (null != data) {
                    // data是字节数据，将其解析成位图
                    bitmap = BitmapFactory
                            .decodeByteArray(data, 0, data.length);
                }

                if (null != bitmap) {
                    // 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
                    // 90)失效。
                    // 图片竟然不能旋转了，故这里要旋转下

                    try {
                        TakePictureImageUtil.storageTakePictureBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////
    // // 摄像的预览界面类 ////
    // ////////////////////////////////////////////////////////////////////////////////

    private boolean mMediaRecorderRecording = false;

    /**
     * @descrition 预览界面CameraPreview
     */
    @TargetApi(9)
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        @SuppressWarnings("deprecation")
        public CameraPreview(Context context) {
            super(context);
            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
            // 4.0+ auto
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            isServerOnPause = false;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaRecorderRecording && !isServerOnPause) {
                initCamera();
                startRecordVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            mMediaRecorderRecording = false;
            isServerOnPause = true;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            mSurfaceHolder = holder;
        }
    }

    private boolean isServerOnPause = false;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class RecordVideoLocalBinder extends Binder {
        public BackgroundRecordVideoService getService() {
            return BackgroundRecordVideoService.this;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // // 摄像视频预览窗口的广播接收器 ////
    // ////////////////////////////////////////////////////////////////////////////////

    public static final String CHANGED_SIZE_ACTION = "com.changed.video.preview.window";
    /**
     * 改变屏幕的大小, true 为大， false为小
     */
    public static final String IS_CHANGED_KEY = "is.window.changed";

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHANGED_SIZE_ACTION);
        registerReceiver(changedWindowReceiver, filter);
    }

    private void unRegisterBroadcast() {
        unregisterReceiver(changedWindowReceiver);
    }

    /**
     * 发送广播改变后台录制视频的尺寸
     */
    public void broadcastReceiver(int flag) {
        Intent intent = new Intent(
                BackgroundRecordVideoService.CHANGED_SIZE_ACTION);
        intent.putExtra(BackgroundRecordVideoService.IS_CHANGED_KEY, flag);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 改变预览窗口的尺寸的广播接收器
     */
    private BroadcastReceiver changedWindowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CHANGED_SIZE_ACTION.equals(intent.getAction())) {
                if (mWindowManager != null && null != mFrameLayoutParams) {

                    int isFlag = intent.getIntExtra(IS_CHANGED_KEY,
                            LITTLE_WINDOW_VALUE);
                    if (isFlag == INVISIBLE_WINDOW_VALUE) {
                        // 不可见的窗口
                        mFrameLayoutParams.width = INVISIBLE_SIZE;
                        mFrameLayoutParams.height = INVISIBLE_SIZE;
                        floatWindowState = FloatWindowState.InvisibleWindow;

                    } else if (isFlag == LITTLE_WINDOW_VALUE) {
                        // 左上角的小窗口
                        mFrameLayoutParams.width = DEFAULT_SIZE;
                        mFrameLayoutParams.height = DEFAULT_SIZE;
                        mFrameLayoutParams.x = OFSET_X;
                        mFrameLayoutParams.y = OFSET_Y;
                        floatWindowState = FloatWindowState.LittleWindow;

                    } else if (isFlag == FULLSCREEN_WINDOW_VALUE) {
                        // 全屏窗口
                        WindowManager wm = (WindowManager) context
                                .getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics dm = new DisplayMetrics();
                        // 获取屏幕信息
                        wm.getDefaultDisplay().getMetrics(dm);
                        int screenWidth = dm.widthPixels;
                        int screenHeigh = dm.heightPixels;

                        mFrameLayoutParams.x = 0;
                        mFrameLayoutParams.y = 0;

                        mFrameLayoutParams.width = screenWidth;
                        mFrameLayoutParams.height = screenHeigh;
                        floatWindowState = FloatWindowState.FullScreenWindow;
                    }
                    // Update Window View Size
                    mWindowManager.updateViewLayout(mFrameLayout,
                            mFrameLayoutParams);
                }
            }
        }
    };

    /**
     * 不可见的小窗口
     */
    public static final int INVISIBLE_WINDOW_VALUE = 0;

    /**
     * 左上角的小窗口
     */
    public static final int LITTLE_WINDOW_VALUE = 1;

    /**
     * 全屏窗口
     */
    public static final int FULLSCREEN_WINDOW_VALUE = 2;

    // /////////////////////////////////////////////////////////////////////////////////
    // // 新建应用的目录文件夹 ////
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 线程中新建目录
     */
    private void threadCreateDirectory() {
        CreateFileRunnable runnable = new CreateFileRunnable(this,
                CreateFileRunnable.MK_APPLICATION_DIRECTORY);
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
