
package com.spt.carengine.view.explore.data;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月4日 下午4:41:01
 * @description 文件夹
 */
public class FileFolder extends AbstractFile {

    private List<AbstractFile> children;

    public FileFolder(String name) {
        this.fileName = name;
        children = new ArrayList<AbstractFile>();
    }

    @Override
    public boolean add(AbstractFile file) {
        // TODO Auto-generated method stub
        return children.add(file);
    }

    @Override
    public boolean remove(AbstractFile file) {
        return children.remove(file);
    }

    @Override
    public List<AbstractFile> getChild() {
        return children;
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
        // TODO Auto-generated method stub
        return false;
    }

}
