
package com.spt.carengine.recordvideo;

import android.content.Intent;
import android.os.StatFs;

import com.spt.carengine.MyApplication;
import com.spt.carengine.activity.MainActivity;
import com.spt.carengine.camera.CameraValues;
import com.spt.carengine.db.bean.VideoFileLockBean;
import com.spt.carengine.db.manager.VideoFileLockDBManager;
import com.spt.carengine.log.LOG;
import com.spt.carengine.utils.SharePrefsUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Rocky
 * @Time 2015年7月31日 下午12:13:30
 * @description 获取视频输出的路径
 */
public class GetVideoOutputPath {

    public GetVideoOutputPath() {
    }

    public String getOutputMediaFile() {

        FileUtils.createTotalVideoDateDir();

        File mediaFile = new File(FileUtils.getCurStorageTotalVideoDateDir(),
                "VID_" + TimeUtils.getTimeStamp() + ".mp4");
        if (!mediaFile.exists()) {
            try {
                mediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 文件已经存在了
        }
        LOG.writeMsg(GetVideoOutputPath.class, LOG.MODE_RECORD_VIDEO,
                "record save to " + mediaFile);
        // 开线程检测存储盘的情况。
        checkDiskStorageSpace();
        return mediaFile.toString();
    }

    public void checkDiskStorageSpace() {
        CleanStorageFileRunnable cleanStorageFileRunnable = new CleanStorageFileRunnable(
                this, CleanStorageFileRunnable.CHECK_DISK_STORAGE_SPACE);
        Thread thread = new Thread(cleanStorageFileRunnable);
        thread.start();
    }

    public void deleteOldVideoFile() {

        SharePrefsUtils.saveTimeoutDeleteFile(MyApplication.getInstance(),
                false);

        CleanStorageFileRunnable cleanStorageFileRunnable = new CleanStorageFileRunnable(
                this, CleanStorageFileRunnable.DELETE_VIDEO_FILE);
        Thread thread = new Thread(cleanStorageFileRunnable);
        thread.start();
    }

    private void showDeleteDialog() {
        if (!SharePrefsUtils.getAutoClearRecordVideoFlag(MyApplication
                .getInstance())) {
            sendBroadcastShowDialog(true);
            // start timer
            startTimer();

        } else {
            deleteVideoFile();
        }
    }

    private void sendBroadcastShowDialog(boolean isShowDialog) {
        Intent intent = new Intent(MainActivity.SHOW_DIALOG_DISK_FULL_ACTION);
        intent.putExtra(MainActivity.IS_SHOW_DELETE_DIALOG_KEY, isShowDialog);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * <功能描述> 递归删除文件
     * 
     * @param dirOrFile
     * @param root
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    private boolean recursionDeleteOldVideoFile(File dirOrFile, File root) {
        if (root.getUsableSpace() < CameraValues.FREE_SPACE_LIMIT) {
            if (dirOrFile.isDirectory()) {
                File[] files = dirOrFile.listFiles();
                int len = files.length;
                for (int i = 0; i < len; i++) { // 移除多余的，目前移除前头
                    if (recursionDeleteOldVideoFile(files[i], root)) {
                        break;
                    }
                }
            } else {
                boolean isSuccess = dirOrFile.delete();
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "delete the "
                        + dirOrFile.toString()
                        + " file ,and delete, isSuccess : " + isSuccess);
                return false;
            }
        }
        return true;
    }

    /**
     * <功能描述> 递归删除文件
     * 
     * @param dirOrFile
     * @param root
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean deleteUnlockVideoFile(File dirOrFile, File root) {
        if (dirOrFile.isDirectory()) {
            File[] files = dirOrFile.listFiles();
            List<File> fileArr = new ArrayList<File>();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteUnlockVideoFile(files[i], files[i]);
                    continue;
                }
                fileArr.add(files[i]);
            }

            // 获取加锁的文件
            VideoFileLockDBManager videoFileLockDBManager = new VideoFileLockDBManager(
                    MyApplication.getInstance().getApplicationContext());
            List<VideoFileLockBean> lockVideos = videoFileLockDBManager
                    .acceptTotalVideoFiles();

            // 仅删除为加锁文件
            for (VideoFileLockBean videoFileLockBean : lockVideos) {
                String path = videoFileLockBean.getPath();
                ListIterator<File> listIterator = fileArr.listIterator();
                while (listIterator.hasNext()) {
                    File file = listIterator.next();

                    if (file.getAbsolutePath().equals(path)) {
                        listIterator.remove();
                    }
                }
            }

            int len = fileArr.size();
            for (int i = 0; i < len; i++) { // 移除多余的，目前移除前头
                fileArr.get(i).delete();
            }
        } else {
            boolean isSuccess = dirOrFile.delete();
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO,
                    "delete the " + dirOrFile.toString()
                            + " file ,and delete, isSuccess : " + isSuccess);
            return false;
        }

        // 删除空文件夹

        return true;
    }

    /**
     * <功能描述> 检测sd卡内存的Runnalbe
     * 
     * @author Rocky
     * @modify_date 2015.8.18
     */
    class CleanStorageFileRunnable implements Runnable {

        public static final int CHECK_DISK_STORAGE_SPACE = 0;
        public static final int DELETE_VIDEO_FILE = 1;

        private WeakReference<GetVideoOutputPath> gReference = null;

        private int type = -1;

        public CleanStorageFileRunnable(GetVideoOutputPath gOutputPath, int type) {
            gReference = new WeakReference<GetVideoOutputPath>(gOutputPath);
            this.type = type;
        }

        @Override
        public void run() {
            if (type == CHECK_DISK_STORAGE_SPACE) {
                gReference.get().ensureMediaRootDir();

            } else if (type == DELETE_VIDEO_FILE) {
                gReference.get().deleteVideoFile();
            }
        }
    }

    /**
     * <功能描述> 检测sd卡的空间是否足够，如果不足，则删除文件
     * 
     * @return [参数说明]
     * @return File [返回类型说明]
     */
    @SuppressWarnings("deprecation")
    private File ensureMediaRootDir() {

        File mediaStorageDir = new File(FileUtils.getStorageVideoDir());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LOG.writeMsg(GetVideoOutputPath.class, LOG.MODE_RECORD_VIDEO,
                        "failed to create directory : " + mediaStorageDir);
            }

        } else {

            String path = FileUtils.getVideoPath().get(0);
            // 优先使用T卡，如果没有T卡，那么就是用内部SD卡
            StatFs sf = new StatFs(path.substring(0, path.length() - 1));

            long freeSize = 0;
            // 这种计算方法已经不准了，不再使用

            freeSize = sf.getBlockSize() * 1L * sf.getAvailableBlocks() * 1L;
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "freeSize:" + freeSize);

            if (freeSize < FileUtils.getFreeSpaceLimit()) {
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO,
                        "need to delete file.");
                showDeleteDialog();

            } else {
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO,
                        "do not need delete file:");
            }
        }
        return mediaStorageDir;
    }

    private void deleteVideoFile() {
        cancelTimer();
        File mediaStorageDir = new File(FileUtils.getStorageTotalVideoDir()); // 仅删除未锁定的视频也就是全部视频文件夹
        // recursionDeleteOldVideoFile(mediaStorageDir, mediaStorageDir);
        deleteUnlockVideoFile(mediaStorageDir, mediaStorageDir);
    }

    /********************* 超时30秒时，自动删除文件 ***************************/

    private static final int TIMEOUT_AUTO_DELETE = 1 * 1000;

    private Timer timeoutTimer = null;

    private void startTimer() {
        if (timeoutTimer == null) {
            timeoutTimer = new Timer();
        }

        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!SharePrefsUtils.getTimeoutDeleteFile(MyApplication
                        .getInstance())) {
                    deleteVideoFile();
                    sendBroadcastShowDialog(false);

                } else {

                }
            }
        }, TIMEOUT_AUTO_DELETE);
    }

    private void cancelTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
        }
    }

}
