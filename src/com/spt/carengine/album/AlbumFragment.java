/*
 * 文 件 名:  AlbumFragment.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月11日
 */

package com.spt.carengine.album;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.spt.carengine.R;
import com.spt.carengine.album.entity.AlbumListInfo;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.album.view.AlbumView;
import com.spt.carengine.album.view.PhotoItemView;
import com.spt.carengine.album.view.PhotosView;
import com.spt.carengine.album.view.ShowPhotoView;
import com.spt.carengine.utils.LogUtil;

import java.util.List;

/**
 * 相册主界面
 * 
 * @author Heaven
 */
public class AlbumFragment extends Fragment {
    private static final String TAG = "AlbumFragment";
    private AlbumView mAlbumView;
    private PhotosView mPhotosView;
    private ShowPhotoView mShowPhotoView;
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            PhotoItemView itemView = null;
            if (view instanceof PhotoItemView) {
                itemView = (PhotoItemView) view;
            }
            if (itemView != null) {
                if (mPhotosView.isEditMode()) {
                    itemView.handleEditModeItem();
                    return;
                }
            }
            PhotoListInfo info = (PhotoListInfo) parent.getAdapter().getItem(
                    position);
            if (info == null || !(info instanceof PhotoListInfo)) {
                return;
            }
            List<PhotoListInfo> all = (List<PhotoListInfo>) mPhotosView
                    .getTag();
            if (all == null || !(all instanceof List<?>)) {
                LogUtil.d(TAG, "all is :" + all);
                return;
            }

            mShowPhotoView.showPhoto(info, position, all);
            mPhotosView.setVisibility(View.GONE);
            mShowPhotoView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        mAlbumView = (AlbumView) root.findViewById(R.id.album);
        mPhotosView = (PhotosView) root.findViewById(R.id.photos);
        mShowPhotoView = (ShowPhotoView) root.findViewById(R.id.photo_layout);
        mAlbumView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                AlbumListInfo info = (AlbumListInfo) parent.getAdapter()
                        .getItem(position);
                if (info == null) {
                    return;
                }
                mAlbumView.setVisibility(View.GONE);
                mPhotosView.setVisibility(View.VISIBLE);
                mPhotosView.showPhotos(info);
            }
        });
        mPhotosView.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back:
                        mPhotosView.setVisibility(View.GONE);
                        mPhotosView.reset();
                        mAlbumView.setVisibility(View.VISIBLE);
                        mAlbumView.updateData();
                        break;
                }
            }
        }, mOnItemClickListener);

        mShowPhotoView.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back:
                        mShowPhotoView.reset();
                        mShowPhotoView.setVisibility(View.GONE);
                        mPhotosView.deletePhotoBack();
                        mPhotosView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}
