
package com.spt.carengine.view.listitemmenu;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ListMenu {

    private Context mContext;
    private List<ListMenuItem> mItems;
    private int mViewType;

    public ListMenu(Context context) {
        mContext = context;
        mItems = new ArrayList<ListMenuItem>();
    }

    public Context getContext() {
        return mContext;
    }

    public void addMenuItem(ListMenuItem item) {
        mItems.add(item);
    }

    public void removeMenuItem(ListMenuItem item) {
        mItems.remove(item);
    }

    public List<ListMenuItem> getMenuItems() {
        return mItems;
    }

    public ListMenuItem getMenuItem(int index) {
        return mItems.get(index);
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }

}
