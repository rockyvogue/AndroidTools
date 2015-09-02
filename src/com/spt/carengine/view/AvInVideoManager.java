
package com.spt.carengine.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import com.spt.carengine.R;
import com.spt.carengine.constant.CameraConstant;
import com.spt.carengine.utils.LogUtil;
import java.util.List;

/****
 * 倒车后视
 */
public class AvInVideoManager implements SurfaceHolder.Callback {
    private static final String TAG = "AvInVideoManager";
    public static AvInVideoManager aInVideoManager;

    private Context mContext;
    /**
     * 应用程序使用的接口和窗口管理器
     */
    private WindowManager mWindowManager;
    private LayoutParams mFrameLayoutParams;
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceholder;
    private Camera mCamera;
    private View view;
    // 音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;
    private AudioManager mAudioManager;
    private int width, height;

    public AvInVideoManager() {

    }

    public static AvInVideoManager getInstance() {
        if (aInVideoManager == null) {
            aInVideoManager = new AvInVideoManager();
        }
        return aInVideoManager;
    }

    public void startAvInVideo(Context context) {
        inintView(context);
        inintUi();
        initPrams();
        mWindowManager.addView(view, mFrameLayoutParams); // 创建View
        surfaceholder = mSurfaceView.getHolder();
        surfaceholder.addCallback(this);
        setSound();
    }

    private void inintView(Context context) {
        this.mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mFrameLayoutParams = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);

        /**
         * 以下都是WindowManager.LayoutParams的相关属性 具体用途请参考SDK文档
         */
        mFrameLayoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mFrameLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        /**
         * 这里的flags也很关键 代码实际是mFrameLayoutParams.flags |= FLAG_NOT_FOCUSABLE;
         * 40的由来是mFrameLayoutParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        mFrameLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        width = dm.widthPixels;
        height = dm.heightPixels;
        mFrameLayoutParams.width = width;
        mFrameLayoutParams.height = height;

    }

    private void inintUi() {
        view = LayoutInflater.from(mContext).inflate(R.layout.surface_view,
                null);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface_view);

    }

    private void initPrams() {
        android.view.ViewGroup.LayoutParams params = mSurfaceView
                .getLayoutParams();
        if (null != params) {
            params.width = width;
            params.height = height;
            mSurfaceView.setLayoutParams(params);
        }

    }

    private void setSound() {

        // 音量控制,初始化定义
        mAudioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        // 最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 当前音量
        currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /***
     * 判断前置摄像头是否存在
     */
    private int findFrontCamera() {

        int cameraCount = 0;
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                    return camIdx;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /****
     * 打开前置摄像头
     */
    private Camera openFrontCamera() {
        Camera camera = null;
        int cammeraIndex = findFrontCamera();
        if (cammeraIndex == -1) {
            Toast.makeText(mContext,
                    mContext.getString(R.string.avin_camera_error),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        camera = Camera.open(cammeraIndex);
        return camera;
    }

    /****
     * 调节音量
     */
    private void adjustVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0); // tempVolume:音量绝对值
        }
    }

    /***
     * 移除倒车视频
     */
    public Camera stopAvInVideo() {
        try {

            if (null != mWindowManager && null != mCamera) {
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.unlock();
                mCamera.release();
                mCamera = null;

            }
            adjustVolume(currentVolume);
            if (null != view)
                mWindowManager.removeView(view);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "error::::" + e.getMessage());
        }
        return mCamera;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        surfaceholder = holder;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        surfaceholder = holder;
        mCamera = openFrontCamera();

        if (mCamera == null)
            return;
        try {
            mCamera.lock();

            Camera.Parameters parameters = mCamera.getParameters();// 实例化照相机参数
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes(); // 获取自己手机合适的尺寸
            Size size = getOptimalPreviewSize(sizeList, width, height);
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
                parameters.setPreviewSize(size.height, size.width);

            } else {
                parameters.setPreviewSize(size.width, size.height);

            }
            parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            parameters.setColorEffect(Parameters.EFFECT_AQUA);
            parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
            parameters.setJpegQuality(100);
            parameters.set(CameraConstant.KEY_CAMERA_MODE,
                    CameraConstant.CAMERA_MODE_MTK_PRV);
            mCamera.setPreviewDisplay(holder);// 固定展示该照相机显示的SurfaceView
            mCamera.setParameters(parameters);
            mCamera.startPreview();// 开始取景，预览
            int volume = currentVolume / 3;// 设置成当前音量的三分之一
            adjustVolume(volume);

        } catch (Exception e) {
            e.printStackTrace();
            if (mCamera != null)
                mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceholder = holder;
        if (null != mCamera) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
