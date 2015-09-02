/*
 * 文 件 名:  PhotosView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月13日
 */

package com.spt.carengine.album.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.album.adapter.PhotosAdapter;
import com.spt.carengine.album.entity.AlbumListInfo;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.view.DialogView;
import com.spt.carengine.view.DialogView.OnDialogListener;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 照片展示界面
 * 
 * @author Heaven
 */
public class PhotosView extends PercentRelativeLayout {
    private static final String TAG = "PhotosView";
    private static final int SPACING = 15;
    private PhotosAdapter mPhotosAdapter;
    private GridView mPhotosGridView;
    private View mNoPhotoView;
    private List<PhotoListInfo> mInfoList;
    private ReturnBarView mReturnBarView;
    private ImageLoader mImageLoader;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit:
                    handleEditMode();
                    break;
                case R.id.delete_photo:
                    handlerDeleteMode();
                    break;
            }
        }
    };
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    mImageLoader.resume();
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    mImageLoader.pause();
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    mImageLoader.pause();
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

        }

    };

    private void handleEditMode() {
        mPhotosAdapter.changeEditMode(true);
        mPhotosAdapter.notifyDataSetChanged();
        mReturnBarView.showEdit(false);
    }

    private void handlerDeleteMode() {
        showDeleteDialog();
    }

    private void deletePhoto() {
        if (mInfoList == null) {
            LogUtil.d(TAG, "in deletePhoto,mInfoList is null");
            return;
        }
        Iterator<PhotoListInfo> it = mInfoList.iterator();
        while (it.hasNext()) {
            PhotoListInfo info = it.next();
            File file = new File(info.path);
            if (info.mode == PhotoItemView.MODE_SELECTED) {
                mPhotosAdapter.remove(info);
                if (file.exists()) {
                    file.delete();
                }
                it.remove();
            } else {
                if (!file.exists()) {
                    mPhotosAdapter.remove(info);
                    it.remove();
                }
            }
        }
    }

    private void showDeleteDialog() {
        int deleteCount = getDeleteCount();
        if (deleteCount == 0) {
            mPhotosAdapter.changeEditMode(false);
            mPhotosAdapter.notifyDataSetChanged();
            mReturnBarView.showEdit(true);
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final Dialog dialog = new Dialog(getContext(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_album_delete, null);
        dialog.addContentView(view, lp);
        DialogView dialogView = (DialogView) dialog
                .findViewById(R.id.album_delete_dialog);
        dialogView.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onExecutive() {
                deletePhoto();
                mPhotosAdapter.changeEditMode(false);
                mPhotosAdapter.notifyDataSetChanged();
                mReturnBarView.showEdit(true);
                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        dialogView.setMessage(getMsg(deleteCount));
        dialog.show();
    }

    public boolean isEditMode() {
        return mPhotosAdapter.getEditMode();
    }

    private int getDeleteCount() {
        int count = 0;
        int size = mInfoList.size();
        for (int i = 0; i < size; i++) {
            PhotoListInfo info = mInfoList.get(i);
            if (info.mode == PhotoItemView.MODE_SELECTED) {
                count++;
            }
        }
        return count;
    }

    private Spanned getMsg(int deleteCount) {
        String result = String.format(
                getContext().getString(R.string.delete_photo_msg), deleteCount);
        return Html.fromHtml(result);
    }

    public PhotosView(Context context) {
        super(context);
    }

    public PhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotosView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotosGridView = (GridView) findViewById(R.id.gridview);
        mNoPhotoView = (View) findViewById(R.id.no_photo);
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        if (mPhotosGridView == null || mNoPhotoView == null
                || mReturnBarView == null) {
            throw new InflateException("Miss a child?");
        }
        init();
    }

    private void init() {
        mImageLoader = ImageLoader.getInstance();
        mReturnBarView.init(ReturnBarView.TYPE_PHOTOS);
        int spacing = ScreenUtils.getRealHeightValue(getContext(), SPACING);
        mPhotosGridView.setHorizontalSpacing(spacing);
        mPhotosGridView.setCacheColorHint(Color.TRANSPARENT);
        mPhotosGridView.setSelector(R.color.transparent);
    }

    public void setListener(OnClickListener l, OnItemClickListener otcl) {
        mReturnBarView.setBackListener(l);
        mReturnBarView.setListener(mOnClickListener);
        mPhotosGridView.setOnItemClickListener(otcl);
        mPhotosGridView.setOnScrollListener(mOnScrollListener);
    }

    public void showPhotos(AlbumListInfo info) {
        mInfoList = new ArrayList<PhotoListInfo>(info.subFilePaths);
        int size = mInfoList.size();
        for (int i = 0; i < size; i++) {
            PhotoListInfo pInfo = mInfoList.get(i);
            pInfo.mode = PhotoItemView.MODE_UNSELECTED;
        }
        mPhotosAdapter = new PhotosAdapter(getContext());
        mPhotosGridView.setAdapter(mPhotosAdapter);
        mReturnBarView.setTitle(info.title);
        mPhotosAdapter.addAll(mInfoList);
        setTag(mInfoList);
    }

    public void reset() {
        mReturnBarView.setTitle("");
        if (mPhotosAdapter != null) {
            mPhotosAdapter.changeEditMode(false);
            mPhotosAdapter.clear();
        }
        if (mInfoList != null) {
            mInfoList.clear();
        }
        mReturnBarView.showEdit(true);
    }

    public void deletePhotoBack() {
        deletePhoto();
        mPhotosAdapter.notifyDataSetChanged();
    }
}
