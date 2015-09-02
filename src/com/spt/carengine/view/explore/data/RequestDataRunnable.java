
package com.spt.carengine.view.explore.data;

import java.lang.ref.WeakReference;

/**
 * @author Rocky
 * @Time 2015年8月5日 下午5:39:40
 * @description 请求数据的线程
 */
public class RequestDataRunnable implements Runnable {

    public static final int REQUEST_DATA = 1;

    public static final int REQUEST_LOCK_DATA = 2;

    private WeakReference<FileListDataSourceHandle> weakReference;

    private int type = REQUEST_DATA;

    public RequestDataRunnable(FileListDataSourceHandle fHandle, int type) {
        weakReference = new WeakReference<FileListDataSourceHandle>(fHandle);
        this.type = type;
    }

    @Override
    public void run() {
        if (type == REQUEST_DATA) {
            weakReference.get().acceptTrackRecordFiles();

        } else if (type == REQUEST_LOCK_DATA) {
            weakReference.get().acceptLockVideoFiles();
        }
    }

}
