
package com.spt.carengine.db.bean;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午5:25:13
 * @description 视频文件锁的实体类,从数据库里面取出来的实体类
 */
public class VideoFileLockBean {

    private String name;

    private String createTime;

    private String size;

    private String path;

    private String date;

    public VideoFileLockBean() {
        this.name = "";
        this.createTime = "";
        this.size = "";
        this.path = "";
        this.date = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "VideoFileLockInfo [name=" + name + ", createTime=" + createTime
                + ", size=" + size + ", path=" + path + ", date=" + date + "]";
    }

}
