
package com.spt.carengine.recordvideo;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Rocky
 * @Time 2015年7月30日 下午3:43:50
 * @description 对图片进行旋转处理
 */
public class TakePictureImageUtil {
    /**
     * 旋转Bitmap
     * 
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    /**
     * 将拍照的图片保存到存储卡中
     * 
     * @param bitmap
     * @throws IOException
     */
    public static void storageTakePictureBitmap(Bitmap bitmap)
            throws IOException {
        String jpegName = FileUtils.getCurStorageTakePictureDateDir()
                + File.separator + "IMG_" + TimeUtils.getTimeStamp() + ".jpg";
        FileOutputStream fout = new FileOutputStream(jpegName);
        BufferedOutputStream bos = new BufferedOutputStream(fout);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

}
