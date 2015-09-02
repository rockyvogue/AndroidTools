
package com.spt.carengine.view.explore.data;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.db.bean.VideoFileLockBean;
import com.spt.carengine.db.manager.VideoFileLockDBManager;
import com.spt.carengine.utils.UtilTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileListDataSourceHandle {

    protected Lock mNodeArrayReadLock; // 资源锁

    protected IDataSourceHandleCallBack idatafinishCallBack;

    protected List<AbstractFile> arryListIFiles;
    protected List<AbstractFile> tempIFiles;
    private VideoFileLockDBManager videoFileLockDataOpt;
    private List<VideoFileLockBean> vFileLockInfos;

    /**
     * 当前路径
     */
    protected String currentPath = "";

    public FileListDataSourceHandle(
            IDataSourceHandleCallBack idatafinishCallBack) {
        this.idatafinishCallBack = idatafinishCallBack;
        mNodeArrayReadLock = new ReentrantLock();
        arryListIFiles = new ArrayList<AbstractFile>();
        tempIFiles = new ArrayList<AbstractFile>();
        videoFileLockDataOpt = new VideoFileLockDBManager(
                MyApplication.getInstance());
        vFileLockInfos = new ArrayList<VideoFileLockBean>();
    }

    /**
     * 请求行车记录仪文件
     */
    public void requestTrackRecordFiles(String path) {
        mNodeArrayReadLock.lock();
        this.currentPath = path;
        if (currentPath.contains(MyApplication.getInstance().getString(
                R.string.file_label_lock_video))) {
            requestType = RequestType.LockVideo;
        } else {
            requestType = RequestType.TotalVideo;
        }
        startRequestDataThread();
        mNodeArrayReadLock.unlock();
    }

    private RequestType requestType;

    public enum RequestType {
        LockVideo, TotalVideo;
    }

    /**
     * 请求行车记录仪文件
     */
    public void requestTrackRecordFiles() {
        mNodeArrayReadLock.lock();
        startRequestDataThread();
        mNodeArrayReadLock.unlock();
    }

    private void startRequestDataThread() {
        int type = RequestDataRunnable.REQUEST_DATA;

        if (requestType == RequestType.LockVideo) {
            type = RequestDataRunnable.REQUEST_LOCK_DATA;

        } else {
            type = RequestDataRunnable.REQUEST_DATA;
        }
        RequestDataRunnable runnable = new RequestDataRunnable(this, type);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 请求数据
     */
    public void acceptTrackRecordFiles() {
        if (tempIFiles == null) {
            callback(IDataSourceHandleCallBack.RESULT_FAIL);
            return;
        }
        tempIFiles.clear();

        File file = new File(currentPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            AbstractFile ifile = null;
            for (File f : files) {
                if (f.isDirectory()) {
                    ifile = new FileFolder(f.getName());

                } else {
                    ifile = new SingleFile(f.getName());
                }

                ifile.setFileCreateDate(UtilTools.getFileModifiedTime(f
                        .lastModified()));
                ifile.setFileSize(UtilTools.FormetFileSize(f.length() + ""));
                ifile.setFilePath(f.getAbsolutePath());
                VideoFileLockBean vFileLockInfo = videoFileLockDataOpt
                        .acceptVideoInfoFromPath(ifile.getFilePath());
                if (vFileLockInfo != null) {
                    ifile.setLockFlag("1");

                } else {
                    ifile.setLockFlag("0");
                }

                if (ifile instanceof FileFolder) {
                    tempIFiles.add(ifile);

                } else if (ifile instanceof SingleFile) {
                    // 判断是否为mp4
                    String prefix = UtilTools.getExtensionFromName(ifile
                            .getFileName());
                    if ("mp4".equals(prefix)) {
                        tempIFiles.add(ifile);
                    }
                }
            }
        }
        callback(IDataSourceHandleCallBack.RESULT_SUCCESS);
    }

    /**
     * 获取锁住视频的文件
     */
    public void acceptLockVideoFiles() {
        vFileLockInfos = videoFileLockDataOpt.acceptTotalVideoFiles();
        callback(IDataSourceHandleCallBack.RESULT_SUCCESS);
    }

    /**
     * Update data source
     */
    public void updateArrayListFiles() {
        if (requestType == RequestType.LockVideo) {
            for (VideoFileLockBean videoFileLockInfo : vFileLockInfos) {
                SingleFile abstractFile = new SingleFile(
                        videoFileLockInfo.getName());
                abstractFile.setFilePath(videoFileLockInfo.getPath());
                abstractFile.setFileSize(videoFileLockInfo.getSize());
                abstractFile.setFileCreateDate(videoFileLockInfo
                        .getCreateTime());
                abstractFile.setLockFlag("1");
                arryListIFiles.add(abstractFile);
            }

        } else {
            for (AbstractFile iFile : tempIFiles) {
                arryListIFiles.add(iFile);
            }
        }

    }

    public void updateArrayListLockVideoFiles() {
        for (VideoFileLockBean videoFileLockInfo : vFileLockInfos) {
            AbstractFile abstractFile = new SingleFile(
                    videoFileLockInfo.getName());
            abstractFile.setFilePath(videoFileLockInfo.getPath());
            abstractFile.setFileSize(videoFileLockInfo.getSize());
            abstractFile.setLockFlag("1");
            arryListIFiles.add(abstractFile);
        }
    }

    private void callback(int result) {
        if (idatafinishCallBack != null) {
            idatafinishCallBack.finishAcceptDataHandle(result);
        } else {
            throw new NullPointerException();
        }
    }

    public List<AbstractFile> getArryListIFiles() {
        return arryListIFiles;
    }

    /**
     * 获取文件的名称
     * 
     * @return
     */
    public String getCurrentFolderName() {
        return UtilTools.getFileName(currentPath);
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}
