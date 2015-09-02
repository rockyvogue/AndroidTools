
package com.spt.carengine.view.explore.data;

import android.os.Parcelable;

import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月4日 下午4:39:12
 * @description 文件的抽象类
 */
public abstract class AbstractFile implements Parcelable {

    // 文件名称
    protected String fileName;

    // 文件路径
    protected String filePath;

    // 文件创建的时间
    protected String fileCreateDate;

    // 文件修改的时间
    protected String fileModifyDate;

    // 文件大小
    protected String fileSize;

    // 文件缩略图路径
    protected String fileThumbPath;

    // 文件类型，是视频，图片还是文件夹
    protected FileType fileType;

    // 视频是否被锁住，0：未锁住; 1：锁住
    protected String lockFlag = "0";

    // 添加文件
    public abstract boolean add(AbstractFile file);

    // 移除文件
    public abstract boolean remove(AbstractFile file);

    // 获得子节点
    public abstract List<AbstractFile> getChild();

    public abstract boolean delete();

    // 文件类型
    public enum FileType {
        FOLDER, PICTURE, VIDEO;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileCreateDate() {
        return fileCreateDate;
    }

    public void setFileCreateDate(String fileCreateDate) {
        this.fileCreateDate = fileCreateDate;
    }

    public String getFileModifyDate() {
        return fileModifyDate;
    }

    public void setFileModifyDate(String fileModifyDate) {
        this.fileModifyDate = fileModifyDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileThumbPath() {
        return fileThumbPath;
    }

    public void setFileThumbPath(String fileThumbPath) {
        this.fileThumbPath = fileThumbPath;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * 0：未锁住; 1：锁住
     * 
     * @return
     */
    public String getLockFlag() {
        return lockFlag;
    }

    /**
     * @param lockFlag 0：未锁住; 1：锁住
     */
    public void setLockFlag(String lockFlag) {
        this.lockFlag = lockFlag;
    }

}
