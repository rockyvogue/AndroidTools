
package com.spt.carengine.recordvideo;

import com.spt.carengine.MyApplication;

import java.lang.ref.WeakReference;

/**
 * <功能描述>
 * 
 * @author Administrator
 */
public class StartRecordRunnable implements Runnable {

    /**
     * 新建应用名称的目录
     */
    public static final int BIND_RECORD_VIDEO_SERVERR = 0;

    private WeakReference<MyApplication> weakReference = null;

    private int type = -1;

    public StartRecordRunnable(MyApplication myApplication, int type) {
        weakReference = new WeakReference<MyApplication>(myApplication);
        this.type = type;
    }

    @Override
    public void run() {
        if (type == BIND_RECORD_VIDEO_SERVERR) {
            if (weakReference.get() != null) {
                weakReference.get().bindRecordVideoService();
            }
        }
    }

}
