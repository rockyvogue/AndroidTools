
package com.spt.carengine.recordvideo;

import java.lang.ref.WeakReference;

/**
 * @author Rocky
 * @Time 2015年7月31日 上午9:01:16
 * @description 为了防止线程导致的内存泄露，而定义了一个弱引用的类
 */
public class CreateFileRunnable implements Runnable {

    /**
     * 新建应用名称的目录
     */
    public static final int MK_APPLICATION_DIRECTORY = 0;

    @SuppressWarnings("unused")
    private WeakReference<BackgroundRecordVideoService> weakReference = null;

    private int operateState = -1;

    public CreateFileRunnable(BackgroundRecordVideoService bService, int state) {
        weakReference = new WeakReference<BackgroundRecordVideoService>(
                bService);
        this.operateState = state;
    }

    @Override
    public void run() {
        if (operateState == MK_APPLICATION_DIRECTORY) {
            // 新建目录文件夹
            FileUtils.createVideoRecordDir();
        }
    }

}
