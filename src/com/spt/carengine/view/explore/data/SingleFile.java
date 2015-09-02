
package com.spt.carengine.view.explore.data;

import android.os.Parcel;

import com.spt.carengine.MyApplication;
import com.spt.carengine.db.manager.VideoFileLockDBManager;
import com.spt.carengine.view.explore.data.AbstractFile;

import java.io.File;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月4日 下午4:42:31
 * @description 文件
 */
public class SingleFile extends AbstractFile {

    public SingleFile(String name) {
        this.fileName = name;
    }

    @Override
    public boolean add(AbstractFile file) {
        return false;
    }

    @Override
    public boolean remove(AbstractFile file) {
        return false;
    }

    @Override
    public List<AbstractFile> getChild() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {

    }

    @Override
    public boolean delete() {
        boolean isDelete = false;
        File file = new File(getFilePath());
        if (file.exists()) {
            isDelete = file.delete();

        } else {
            isDelete = true;
        }

        deleteRecordFromDatabase();

        return isDelete;
    }

    private void deleteRecordFromDatabase() {
        VideoFileLockDBManager videoFileLockDataOpt = new VideoFileLockDBManager(
                MyApplication.getInstance());
        videoFileLockDataOpt.deleteInfoRecordFromPath(getFilePath());
    }
}
