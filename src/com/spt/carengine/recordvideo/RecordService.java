
package com.spt.carengine.recordvideo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageEvents.Event;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.spt.carengine.MyApplication;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.fragment.RecordVideoFragment;
import com.spt.carengine.log.LOG;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.view.MainMenuView;

import de.greenrobot.event.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordService extends Service {
    FileOutputStream outputStream = null;
    private static final int NUM_FRAMES = 5400;
    private static final String TAG = "zhentao";
    private static final boolean VERBOSE = true;
    private static final String DEBUG_FILE_NAME_BASE = "/sdcard/TheCarDev/test";
    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int IFRAME_INTERVAL = 1;
    private int mWidth = Constant.VIDEO_WIDTH;
    private int mHeight = Constant.VIDEO_HEIGHT;
    private int mBitRate = 6000000;
    byte nCamdata[] = new byte[mWidth * mHeight * 3 / 2];
    byte mBuffer[] = new byte[mWidth * mHeight * 3 / 2];
    Time t = new Time();
    byte[] frameData = new byte[mWidth * mHeight * 3 / 2];
    MediaMuxer mMuxer;
    MediaCodec encoder = null;
    String fileName;
    int generateIndex = 0;
    int mTrackIndex = -1;
    MediaFormat format;

    int videoTrackIndex;
    public static boolean endOfStream = false;
    public static boolean stopRec = false;
    public static boolean sendEos = false;
    long audioBytesReceived = 0;
    MediaCodec.BufferInfo info;
    MediaCodecInfo codecInfo;
    private SurfaceView mSurfaceView;
    private GetVideoOutputPath getVideoOutputPath;
    // 现在录制文件的路径
    private String currRecordFilePath;
    private boolean isTakePicture = false;

    @Override
    public IBinder onBind(Intent binder) {

        MainMenuView.getSurfaceView().setVisibility(View.VISIBLE);
        
        gameDisplay = new GameDisplay(MyApplication.getInstance()
                .getApplicationContext(), MainMenuView.getSurfaceView(),
                Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT, 0, 0, 156, 156);

        getVideoOutputPath = new GetVideoOutputPath();
        Log.d("zhentao", "----onBind");
        
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("zhentao", "----onUnbind");
        // 隐藏surfaceview 终端数据的传输
        MainMenuView.getSurfaceView().setVisibility(View.GONE);
        return super.onUnbind(intent);
    }

    /**
     * 获取服务对象的类声明
     */
    private IBinder mBinder = new RecordVideoLocalBinder();
    private GameDisplay gameDisplay;

    public class RecordVideoLocalBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }

    }

    public GameDisplay getGameDisplay() {
        return gameDisplay;
    }

    public void OnDestroy() {
        super.onDestroy();
        Log.d("zhentao", "----OnDestroy");
        releaseResource();
    }

    @SuppressLint("NewApi")
    private void releaseResource() {
        // 停止录制，给文件加上
        stop();

        if (gameDisplay != null)
            gameDisplay.stop();

        Log.d("zhentao", "----stopppppp");
        if (encoder != null) {
            encoder.stop();
            encoder.release();
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }

        if (currRecordFilePath != null) {
            // 为了记录最后的视频文件大小
            getFileSize(currRecordFilePath);
        }
    }

    public void onCreate() {
        super.onCreate();

        Log.d("zhentao", "----------->>>>>>onCreate()");
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // // 拍摄照片的方法 ////
    // ////////////////////////////////////////////////////////////////////////////////

    /**
     * 拍照的方法
     */
    public void doTakePicture() {
        isTakePicture = true;

    }

    public class GameDisplay extends SurfaceView implements
            SurfaceHolder.Callback, Camera.PreviewCallback {
        public final static String TAG = "zhentao";
        private static final int MAGIC_TEXTURE_ID = 10;

        private SurfaceView sf;
        public SurfaceHolder gHolder;
        public SurfaceTexture gSurfaceTexture;
        @SuppressWarnings("deprecation")
        public Camera gCamera;
        public byte gBuffer[];
        private int bufferSize;
        public Bitmap gBitmap;
        private Camera.Parameters parameters;
        public int previewWidth, previewHeight;
        byte[] mCamdata = new byte[mWidth * mHeight * 3 / 2];
        int nFrame = 0;
        Size size;
        int X, Y, W, H;

        public Camera getCamera() {
            return gCamera;

        }

        // 倒车释放资源
        @SuppressLint("NewApi")
        public void releaseCamera() {

            stopRec = true;
            sendEos = true;

            if (gCamera != null) {
                gCamera.setOneShotPreviewCallback(null);
                gCamera.stopPreview();
                gCamera.release();
                gCamera = null;

                Log.d("zhentao", "----releaseCamera");
            }

            if (currRecordFilePath != null) {
                // 为了记录最后的视频文件大小
                getFileSize(currRecordFilePath);
            }

            EventBus.getDefault().post(Constant.REBACK_CAR_STATE);
            SharePrefsUtils.saveRebackCarState(MyApplication.getInstance()
                    .getApplicationContext(), false);

        }

        public GameDisplay(Context context, SurfaceView msf, int pWidth,
                int pHeight, int x, int y, int w, int h) {
            super(context);
            Log.d("zhentao", "----GameDisplay构造");
            X = x;
            Y = y;
            H = h;
            W = w;
            previewWidth = pWidth;
            previewHeight = pHeight;
            sf = msf;
            gHolder = sf.getHolder();
            gSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
            gHolder.addCallback(this);
        }

        public void changeView(SurfaceView msf, int pWidth, int pHeight, int x,
                int y, int w, int h) {
            X = x;
            Y = y;
            H = h;
            W = w;
            previewWidth = pWidth;
            previewHeight = pHeight;
            sf = msf;
        }

        public void stop() {
            if (gCamera != null) {
                gCamera.stopPreview();
                gCamera.release();
                gCamera = null;
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            Log.d("zhentao", "----GameDisplay构造");
            if (gCamera == null)
                return;

            parameters = gCamera.getParameters();
            gBitmap = Bitmap.createBitmap(previewWidth, previewHeight,
                    Bitmap.Config.ARGB_8888);

            size = parameters.getPreviewSize();
            previewWidth = mWidth;
            previewHeight = mHeight;
            parameters.setPreviewSize(previewWidth, previewHeight);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setPreviewFormat(ImageFormat.NV21);
            // Camera的范围，重要参数
            parameters.set("mtk-cam-mode", 1);
            gCamera.setParameters(parameters);
            bufferSize = previewWidth * previewHeight;
            bufferSize = bufferSize
                    * ImageFormat
                            .getBitsPerPixel(parameters.getPreviewFormat()) / 8;
            gBuffer = new byte[bufferSize];
            gCamera.addCallbackBuffer(gBuffer);
            gCamera.setPreviewCallbackWithBuffer(this);

            gHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            initCodec();
            gCamera.startPreview();
        }

        public void drawOnSurfaceview(SurfaceView mSurfaceView,
                byte[] mCamData, int preWidth, int preHeight, int x, int y,
                int mWidth, int mHeight) {

            Rect gRect = new Rect(x, y, mWidth, mHeight);
            try {
                YuvImage image = new YuvImage(mCamData,
                        parameters.getPreviewFormat(), preWidth, preHeight,
                        null);
                if (image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(
                            new Rect(0, 0, image.getWidth(), image.getHeight()),
                            100, stream);
                    Bitmap bm = BitmapFactory.decodeByteArray(
                            stream.toByteArray(), 0, stream.size());

                    // 因为系统的拍照后预览会停止，那么我就在这保存图片就可以，不用使用系统的照相
                    if (isTakePicture) {
                        TakePictureImageUtil.storageTakePictureBitmap(bm);
                        isTakePicture = !isTakePicture;
                    }

                    stream.close();
                    Canvas canvas = mSurfaceView.getHolder().lockCanvas();
                    if (canvas != null) {
                        canvas.drawBitmap(bm, null, gRect, null);
                        mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.v(TAG, "GameDisplay surfaceCreated");
            if (gCamera == null) {
                gCamera = getCameraInstance();
            }
            if (gSurfaceTexture == null)
                gSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
            try {
                if (gCamera != null) {// 有可能getCameraInstance() 返回的还是空！
                    gCamera.setPreviewTexture(gSurfaceTexture);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Camera getCameraInstance() {
            Camera c = null;
            try {
                c = openFacingBackCamera();
            } catch (Exception e) {
            }
            return c;
        }

        @SuppressLint("NewApi")
        private Camera openFacingBackCamera() {
            Log.d("zhentao", "----openFacingBackCamera");
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

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(TAG, "GameDisplay surfaceDestroyed");
            if (gCamera != null) {
                gCamera.stopPreview();
                gCamera.release();
                gCamera = null;
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d("zhentao", "-----" + sendEos + " " + stopRec
                    + generateIndex++ + " data.len=" + data.length);
            camera.addCallbackBuffer(gBuffer);

            if (camera != null) {
                drawOnSurfaceview(sf, data, previewWidth, previewHeight, X, Y,
                        H, W);
            }
            if (sendEos) {
                sendVideoToEncoder(null, true);
                sendEos = false;
            }
            if (!stopRec) {
                sendVideoToEncoder(data, false);
                if (generateIndex == NUM_FRAMES) {
                    sendVideoToEncoder(null, true);
                    generateIndex = 0;
                }
            }

        }
    }

    public void sendVideoToEncoder(byte[] data, boolean endOfVideo) {
        if (data != null)
            frameData = swapNV21toI420(data, mWidth, mHeight);

        ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
        boolean inputDone = false;
        if (!inputDone) {
            int inputBufIndex = encoder.dequeueInputBuffer(-1);
            if (inputBufIndex >= 0) {
                long presentationVideo = computePresentationTime(generateIndex);
                if (endOfVideo) {
                    encoder.queueInputBuffer(inputBufIndex, 0, 0,
                            presentationVideo,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    doEncodeVideoFromBuffer(encoder, endOfVideo);

                    // 存储视频文件的大小
                    getFileSize(currRecordFilePath);

                } else {
                    ByteBuffer inputBuf = encoderInputBuffers[inputBufIndex];
                    inputBuf.clear();
                    inputBuf.put(frameData);
                    encoder.queueInputBuffer(inputBufIndex, 0,
                            frameData.length, presentationVideo, 0);
                    doEncodeVideoFromBuffer(encoder, endOfVideo);
                }
            }
        }
    }

    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

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

    @SuppressLint("NewApi")
    private void doEncodeVideoFromBuffer(MediaCodec genCoder, boolean end) {
        while (true) {
            ByteBuffer[] encoderOutputBuffers = genCoder.getOutputBuffers();
            boolean encoderDone = false;
            if (!end) {
                if (!encoderDone) {
                    int encoderStatus = genCoder.dequeueOutputBuffer(info,
                            10000);
                    Log.d("zhentao", "-00000-" + mTrackIndex + " erStatus="
                            + encoderStatus + " " + info.size + " "
                            + info.flags + " " + info.presentationTimeUs
                            + "gen= " + generateIndex);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        if (!end)
                            break;

                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        encoderOutputBuffers = genCoder.getOutputBuffers();
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = genCoder.getOutputFormat();
                        videoTrackIndex = mMuxer.addTrack(newFormat);
                        mMuxer.start();
                    } else if (encoderStatus < 0) {
                    } else {
                        ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                        if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            info.size = 0;
                        }
                        if (info.size != 0) {
                            encodedData.position(info.offset);
                            encodedData.limit(info.offset + info.size);
                            mMuxer.writeSampleData(videoTrackIndex,
                                    encodedData, info);
                        }
                        genCoder.releaseOutputBuffer(encoderStatus, false);

                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            if (!end) {
                                Log.w(TAG, "reached end of stream unexpectedly");
                            } else {
                                Log.d(TAG, "end of stream reached");
                            }
                            break;
                        }
                    }
                }
                generateIndex++;
            } else {
                Log.d(TAG, "releasing codecs");
                if (mMuxer != null) {
                    mMuxer.stop();
                    mMuxer.release();
                    mMuxer = null;
                }

                if (genCoder != null) {
                    genCoder.stop();
                    // genCoder.release();
                    // genCoder = null;
                }
                if (sendEos)
                    stopRec = true;
                if (!stopRec) {
                    // genCoder =
                    // MediaCodec.createByCodecName(codecInfo.getName());
                    genCoder.configure(format, null, null,
                            MediaCodec.CONFIGURE_FLAG_ENCODE);
                    genCoder.start();
                    t.setToNow();
                    fileName = DEBUG_FILE_NAME_BASE + mWidth + "x" + mHeight
                            + "-" + t.monthDay + "-" + t.hour + "-" + t.minute
                            + "-" + t.second + ".mp4";
                    try {

                        // 使用以前的文件存储功能
                        currRecordFilePath = getVideoOutputPath
                                .getOutputMediaFile();

                        // 文件输出
                        mMuxer = new MediaMuxer(currRecordFilePath,
                                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                        mTrackIndex = -1;
                    } catch (IOException ioe) {
                        throw new RuntimeException(
                                "MediaMuxer creation failed", ioe);
                    }
                }
                break;
            }
        }
    }

    private int computePresentationTime(int frameIndex) {
        int mid = 1000000 / FRAME_RATE;
        return (int) (132 + frameIndex * mid);

    }

    public byte[] swapNV21toI420(byte[] nv21bytes, int width, int height) {
        byte[] yuv420bytes = new byte[nv21bytes.length];
        for (int i = 0; i < width * height * 2 / 2; i++)
            yuv420bytes[i] = nv21bytes[i];

        for (int i = 0; i < width * height / 4; i++) {
            yuv420bytes[width * height + i] = nv21bytes[(width * height) + 2
                    * i - 1];
            yuv420bytes[i + width * height * 5 / 4] = nv21bytes[(width * height)
                    + 2 * i];
        }
        return yuv420bytes;
    }

    private void setParameters(int width, int height, int bitRate) {
        mWidth = width;
        mHeight = height;
        mBitRate = bitRate;
    }

    @SuppressLint("NewApi")
    public void initCodec() {
        setParameters(mWidth, mHeight, 1200000);
        t.setToNow();

        fileName = DEBUG_FILE_NAME_BASE + mWidth + "x" + mHeight + "-"
                + t.monthDay + "-" + t.hour + "-" + t.minute + "-" + t.second
                + ".mp4";

        // 使用以前的文件存储功能
        currRecordFilePath = getVideoOutputPath.getOutputMediaFile();

        MediaCodecInfo codecInfo = selectCodec(MIME_TYPE);
        if (codecInfo == null)
            return;
        info = new MediaCodec.BufferInfo();
        int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
        format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        try {
            encoder = MediaCodec.createByCodecName(codecInfo.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();

        try {
            mMuxer = new MediaMuxer(currRecordFilePath,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }
    }

    public void stop() {
        Log.d("zhentao", "---------stoprec");
        stopRec = true;
        sendEos = true;
    }
}
