
package com.spt.carengine.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spt.carengine.log.LOG;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaRecorderHelper implements SurfaceHolder.Callback {

    private static final int MAX_PICE = 6; // 3个文件
    private static long MAX_TIME = 20 * 1000; // 10 s 一个段

    private static SimpleDateFormat sdfFileName = new SimpleDateFormat(
            CameraValues.FILE_NAME_FMT, Locale.CHINA);
    private static SimpleDateFormat sdfDirName = new SimpleDateFormat(
            CameraValues.DIR_NAME_FMT, Locale.CHINA);

    private Handler mainH = new Handler();
    //
    // 最后一次记录开始时间戳，
    private long lastRecordStart;
    private TextToSpeech mTextTOSpeech;
    private MediaRecorder mMediarecorder;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private int rotation;
    private boolean isRecorder = false;

    private boolean isInitOjbect = false;

    // private static MediaRecorderHelper mediaRecorderHelper = new
    // MediaRecorderHelper();
    //
    // public static MediaRecorderHelper getInstance() {
    // return mediaRecorderHelper;
    // }
    //
    // public MediaRecorderHelper() {
    // }

    public void setSharedPreferenceChangeListener(SharedPreferences sp) {
        isInitOjbect = true;
        if (mMediarecorder == null) {
            mMediarecorder = new MediaRecorder(); // 创建mediarecorder对象
        }
        // 设置录制视频源为Camera(相机)
        /*
         * mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA );
         * mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER );
         * // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
         * mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
         * // 设置录制的视频编码 h264 mediarecorder.setVideoEncoder(
         * MediaRecorder.VideoEncoder.DEFAULT ); //H264
         * mediarecorder.setAudioEncoder( MediaRecorder.AudioEncoder.DEFAULT );
         * //mediarecorder.setProfile( CamcorderProfile.get(
         * CamcorderProfile.QUALITY_LOW ) ); // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
         * mediarecorder.setVideoSize( 240, 320 ); //
         * 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错 mediarecorder.setVideoFrameRate( 15 );
         */

        // CAMERA_FACING_BACK
        if (mCamera == null) {
            mCamera = Camera.open(); // 一开始不要初始化
        }
        // 视频分辨率切换不马上生效，等录制下一段视频生效，时间切换可以马上生效
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    LOG.writeMsg(MediaRecorderHelper.this,
                            LOG.MODE_RECORD_VIDEO, "key--->>>" + key);
                }
            };
        }
        sp.registerOnSharedPreferenceChangeListener(listener);
        // mediarecorder.setOutputFile(lastFileName);
    }

    public boolean isInitOjbect() {
        return isInitOjbect;
    }

    public void setTextToSpeech(TextToSpeech speech) {
        this.mTextTOSpeech = speech;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // // 线程Runnable //////
    // /////////////////////////////////////////////////////////////////////////////////
    /**
     * 切片的Runnalbe
     */
    private Runnable cutRunnable = new Runnable() {
        @Override
        public void run() {
            long intval = SystemClock.uptimeMillis() - lastRecordStart;
            if (intval >= MAX_TIME) { // 超过了就切片
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "开始录像切片.时间:" + System.currentTimeMillis());
                stop();
                start();

            } else {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "重置录像切片");
                mainH.removeCallbacks(this);
                mainH.postDelayed(this, intval);
            }
        }
    };

    /**
     * 检测sd卡内存的Runnalbe
     */
    private Runnable spaceCheck = new Runnable() {
        @Override
        public void run() {
            // 日期旧的删掉，标记为不删除的要先保存。
            // 如果只剩下不删除的时候，需要用户手动清理
            File f = ensureMediaRootDir();
            doClean(f, f);
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        boolean doClean(File dirOrFile, File root) {

            if (root.getUsableSpace() < CameraValues.FREE_SPACE_LIMIT) {
                if (dirOrFile.isDirectory()) {
                    File[] files = dirOrFile.listFiles();
                    int len = files.length;
                    for (int i = 0; i < len; i++) { // 移除多余的，目前移除前头
                        if (doClean(files[i], root)) {
                            break;
                        }
                    }
                } else {
                    dirOrFile.delete();
                    return false;
                }
            }
            return true;
        }
    };

    // /////////////////////////---END---////////////////////////////////////////////

    @SuppressLint("NewApi")
    private File ensureMediaRootDir() {
        File mediaStorageDir = CameraValues.REC_SAVE_DIR;
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "failed to create directory : " + mediaStorageDir);
            }
        } else {
            long freeSize = 0;
            // 这种计算方法已经不准了，不再使用
            StatFs sf = new StatFs(CameraValues.SAVE_ROOT);
            freeSize = sf.getBlockSize() * 1L * sf.getAvailableBlocks() * 1L;
            printStatFs(sf);

            if (freeSize < CameraValues.FREE_SPACE_LIMIT) {
                mainH.post(spaceCheck);
            }
        }
        return mediaStorageDir;
    }

    /**
     * 打印sd卡的存储容量
     * 
     * @param statFs
     */
    @SuppressLint("NewApi")
    private void printStatFs(StatFs statFs) {
        long freeSize = (statFs.getBlockSize() * 1L
                * statFs.getAvailableBlocks() * 1L);
        freeSize = freeSize / 1024 / 1024;
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                CameraValues.SAVE_ROOT + " free space : " + freeSize);
        LOG.writeMsg(
                MediaRecorderHelper.this,
                LOG.MODE_RECORD_VIDEO,
                CameraValues.SAVE_ROOT + " getFreeBlocksLong:"
                        + statFs.getFreeBlocksLong()
                        + ", getAvailableBlocksLong:"
                        + statFs.getAvailableBlocksLong()
                        + ", getBlockCountLong:" + statFs.getBlockCountLong()
                        + ", getBlockSizeLong:" + statFs.getBlockSizeLong());
        freeSize = statFs.getAvailableBytes();
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "getAvailableBytes:" + freeSize);
        File f = new File(CameraValues.SAVE_ROOT);
        freeSize = f.getUsableSpace();
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO, "space:"
                + freeSize);
    }

    private File getOutputMediaFile() {
        Date date = new Date();
        File mediaStorageDir = ensureMediaDir(date);
        // Create a media file name
        String timeStamp = sdfFileName.format(new Date());
        File mediaFile = new File(mediaStorageDir, "VID_" + timeStamp + ".mp4");
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "record save to " + mediaFile);
        return mediaFile;
    }

    //
    private File ensureMediaDir(Date date) {
        String dir = sdfDirName.format(date);
        File file = new File(ensureMediaRootDir(), dir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "failed to create directory : " + file);
            }
        }
        return file;
    }

    public void start() {
        if (!isRecorder) {
            LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                    "Media recorder-start-");
            // mediarecorder = new MediaRecorder();// 创建mediarecorder对象,
            // mCamera.stopPreview();
            mCamera.lock();
            mCamera.unlock();
            mMediarecorder.setCamera(mCamera);
            mMediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediarecorder.setProfile(CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH));
            mMediarecorder
                    .setOutputFile(getOutputMediaFile().getAbsolutePath());
            mMediarecorder.setPreviewDisplay(mHolder.getSurface());
            try {
                mMediarecorder.prepare();
                mMediarecorder.start();
                lastRecordStart = SystemClock.uptimeMillis();
                mainH.postDelayed(cutRunnable, MAX_TIME); // 每格一定时间录制一段
                isRecorder = true;

            } catch (IllegalStateException e) {
                e.printStackTrace();
                LOG.writeMsg(
                        MediaRecorderHelper.this,
                        LOG.MODE_RECORD_VIDEO,
                        "start exception, exception message : "
                                + e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
                LOG.writeMsg(
                        MediaRecorderHelper.this,
                        LOG.MODE_RECORD_VIDEO,
                        "start exception, exception message : "
                                + e.getMessage());
            }
        }
    }

    public void stop() {
        if (isRecorder) {
            LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                    "Media recorder-stop-");
            mMediarecorder.stop();
            mMediarecorder.reset();
            isRecorder = false;
            try {
                mCamera.reconnect(); // .lock();
                mCamera.startPreview(); //
                mainH.removeCallbacks(cutRunnable);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 不一定能停止成功
        }
    }

    public void release() {
        if (mTextTOSpeech != null) {
            mTextTOSpeech.speak("exit", TextToSpeech.QUEUE_FLUSH, null);
        }
        stop();
        if (mMediarecorder != null)
            mMediarecorder.release();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
            mCamera.release();
            mCamera = null;
        }
    }

    public void setPreviewDisplay(SurfaceView sv) {
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "-setPreviewDisplay-");
        this.mSurfaceView = sv;
        // this.mSurfaceView = new SurfaceView(yrcCarnetServer.context);
        SurfaceHolder holder = mSurfaceView.getHolder();// 取得holder
        setPreviewDisplay(holder);
    }

    private void setPreviewDisplay(SurfaceHolder holder) {
        SurfaceHolder old = mHolder;
        Camera camera = mCamera;
        if (old != holder) { // TODO 切换holder
            if (old != null) {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "-remove old callback-");
                old.removeCallback(this);
            }
            // setPreviewDisplay(holder, camera);
            mHolder = holder;
        } else {
            // 否则如果一样的就不再处理
            LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                    "-same holder-" + holder);
            // holder.removeCallback(this);
        }

        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "-add this Callback-" + holder.isCreating() + " ， " + holder);
        holder.addCallback(this); // holder加入回调接口
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // mediarecorder.setPreviewDisplay( holder.getSurface() );
    }

    //
    private void setPreviewDisplay(SurfaceHolder holder, Camera camera) {
        if (camera != null) {
            LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                    "-stop camera preview-");
            camera.stopPreview();
            try {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "-set camera previewDisplay-" + holder);
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                        "setPreviewDisplay, IOException : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("InlinedApi")
    public void setRotation(int rotation) {
        this.rotation = rotation;
        setCameraDisplayOrientation(CameraInfo.CAMERA_FACING_BACK, mCamera);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void setCameraDisplayOrientation(int cameraId,
            android.hardware.Camera camera) {
        if (camera == null) {
            return;
        }

        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "- setCameraDisplayOrientation -");
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.e("Camere", "info.orientation:" + info.orientation + "degrees:"
                + degrees + "result:" + result);

        camera.setDisplayOrientation(180);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "-surfaceChanged-" + holder);
        // setPreviewDisplay( holder,mCamera );
        LOG.writeMsg(
                MediaRecorderHelper.this,
                LOG.MODE_RECORD_VIDEO,
                "-start camera preview-" + holder.isCreating() + ","
                        + holder.getSurface());
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "-surfaceCreated-" + holder);
        // surfaceHolder = holder;
        Camera camera = mCamera;
        if (camera == null) {
            mCamera = camera = Camera.open();
            LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                    "Camera:" + camera);
        }

        setCameraDisplayOrientation(CameraInfo.CAMERA_FACING_BACK, camera);
        setPreviewDisplay(holder, camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LOG.writeMsg(MediaRecorderHelper.this, LOG.MODE_RECORD_VIDEO,
                "-surfaceDestroyed-" + holder);
        // release(); // XXX 目前暂时释放
    }

}
