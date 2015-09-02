
package com.spt.carengine.recordvideo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <功能描述> 行车记录仪的SurfaceView
 * 
 * @author Administrator
 */
public class SurfaceDraw extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PreviewCallback {

    private SurfaceView sf;
    private SurfaceHolder sHolder; // surfaceView
    boolean stop = false;
    public SurfaceTexture gSurfaceTexture;
    public Camera gCamera;
    public byte gBuffer[];
    private int bufferSize;
    private Camera.Parameters parameters;
    public int previewWidth, previewHeight;
    public Bitmap gBitmap;
    int num = 0;
    int X, Y, W, H;

    public SurfaceDraw(Context context, SurfaceView msf, int pWidth,
            int pHeight, int x, int y, int w, int h) {
        super(context);
        X = x;
        Y = y;
        H = h;
        W = w;
        previewWidth = pWidth;
        previewHeight = pHeight;
        sf = msf;
        sHolder = sf.getHolder();
        gSurfaceTexture = new SurfaceTexture(10);
        sHolder.addCallback(this);
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

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        if (gCamera == null)
            return;
        parameters = gCamera.getParameters();
        gBitmap = Bitmap.createBitmap(previewWidth, previewHeight,
                Bitmap.Config.ARGB_8888);
        parameters.setPreviewSize(previewWidth, previewHeight);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFormat(ImageFormat.NV21);
        gCamera.setParameters(parameters);
        bufferSize = previewWidth * previewHeight;
        bufferSize = bufferSize
                * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())
                / 8;
        gBuffer = new byte[bufferSize];
        gCamera.addCallbackBuffer(gBuffer);
        gCamera.setPreviewCallbackWithBuffer(this);
        gCamera.startPreview();
    }

    public void drawOnSurfaceview(SurfaceView mSurfaceView, byte[] mCamData,
            int preWidth, int preHeight, int x, int y, int mWidth, int mHeight) {
        Rect gRect = new Rect(x, y, mHeight, mHeight);
        try {
            YuvImage image = new YuvImage(mCamData,
                    parameters.getPreviewFormat(), preWidth, preHeight, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(
                        new Rect(0, 0, image.getWidth(), image.getHeight()),
                        100, stream);
                Bitmap bm = BitmapFactory.decodeByteArray(stream.toByteArray(),
                        0, stream.size());
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

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (camera != null) {
            camera.addCallbackBuffer(gBuffer);

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gCamera == null) {
            gCamera = getCameraInstance();
        }

        if (gSurfaceTexture == null)
            gSurfaceTexture = new SurfaceTexture(10);
        if (gCamera != null) {
            try {
                gCamera.setPreviewTexture(gSurfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (gCamera != null) {
            gCamera.stopPreview();
            gCamera.release();
            gCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gCamera != null) {
            gCamera.stopPreview();
            gCamera.release();
            gCamera = null;
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
}
