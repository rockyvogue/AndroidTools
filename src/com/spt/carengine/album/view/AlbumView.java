/*
 * 文 件 名:  AlbumView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月13日
 */

package com.spt.carengine.album.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.album.adapter.AlbumAdapter;
import com.spt.carengine.album.entity.AlbumListInfo;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.utils.FileUtils;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.view.DialogView;
import com.spt.carengine.view.DialogView.OnDialogListener;
import com.spt.carengine.view.LoadingView;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 相册界面
 * 
 * @author Heaven
 */
public class AlbumView extends PercentRelativeLayout {
    private static final String TAG = "AlbumView";
    private static final int SPACING = 15;
    private AlbumAdapter mAlbumAdapter;
    private GridView mAlbumGridView;
    private View mNoPhotoView;
    private LoadingView mLoading;
    private ReturnBarView mReturnBarView;
    private ImageLoader mImageLoader;
    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int arg2, long arg3) {
            AlbumListInfo info = mAlbumAdapter.getItem(arg2);
            showDeleteDialog(info);
            return true;
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

    private void deleteAlbumDir(AlbumListInfo info) {
        if (info != null) {
            int size = info.paths.size();
            for (int i = 0; i < size; i++) {
                String path = info.paths.get(i);
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                    boolean result = file.delete();
                    LogUtil.d(TAG, "delete result:" + result);
                }
            }
            mAlbumAdapter.remove(info);
        }
    }

    private void showDeleteDialog(final AlbumListInfo info) {
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
                deleteAlbumDir(info);
                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        dialogView.setMessage(getMsg(info.title));
        dialog.show();
    }

    private Spanned getMsg(String title) {
        String result = String.format(
                getContext().getString(R.string.delete_msg), title, 1);
        return Html.fromHtml(result);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
        }
    };

    public AlbumView(Context context) {
        super(context);
    }

    public AlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mAlbumGridView = (GridView) findViewById(R.id.gridview);
        mNoPhotoView = (View) findViewById(R.id.no_photo);
        mLoading = (LoadingView) findViewById(R.id.loading_dialog);
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        if (mAlbumGridView == null || mNoPhotoView == null || mLoading == null
                || mReturnBarView == null) {
            throw new InflateException("Miss a child?");
        }
        init();
    }

    private void init() {
        mImageLoader = ImageLoader.getInstance();
        mReturnBarView.init(ReturnBarView.TYPE_ALBUM);
        mReturnBarView.setBackListener(mOnClickListener);
        int spacing = ScreenUtils.getRealHeightValue(getContext(), SPACING);
        mAlbumGridView.setHorizontalSpacing(spacing);
        mAlbumGridView.setCacheColorHint(Color.TRANSPARENT);
        mAlbumGridView.setSelector(R.color.transparent);
        mAlbumAdapter = new AlbumAdapter(getContext());
        mAlbumGridView.setAdapter(mAlbumAdapter);
        mAlbumGridView.setOnItemLongClickListener(mOnItemLongClickListener);
        mAlbumGridView.setOnScrollListener(mOnScrollListener);
        new MyTask().execute();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mAlbumGridView.setOnItemClickListener(l);
    }

    private class MyTask extends AsyncTask<Void, Void, List<AlbumListInfo>> {

        @Override
        protected List<AlbumListInfo> doInBackground(Void... params) {
            List<AlbumListInfo> picList = new ArrayList<AlbumListInfo>();

            List<String> savePaths = com.spt.carengine.recordvideo.FileUtils
                    .getVideoPath();
            if (savePaths.size() == 1) {// 没有外置的SD卡
                List<AlbumListInfo> temp = scanFiles(savePaths.get(0)
                        + getString(R.string.app_name) + File.separator
                        + getString(R.string.home_photo));
                if (temp != null) {
                    picList.addAll(temp);
                }
            } else if (savePaths.size() == 2) {// 有外置的SD卡
                List<AlbumListInfo> temp1 = scanFiles(savePaths.get(0)
                        + getString(R.string.app_name) + File.separator
                        + getString(R.string.home_photo));
                List<AlbumListInfo> temp2 = scanFiles(savePaths.get(1)
                        + getString(R.string.app_name) + File.separator
                        + getString(R.string.home_photo));
                if (temp1 != null) {
                    picList.addAll(temp1);
                }
                if (temp2 != null) {
                    picList.addAll(temp2);
                }
            }
            Collections.sort(picList, new Comparator<AlbumListInfo>() {
                @Override
                public int compare(AlbumListInfo lhs, AlbumListInfo rhs) {
                    return rhs.title.compareTo(lhs.title);
                }
            });
            return picList;
        }

        @Override
        protected void onPostExecute(List<AlbumListInfo> result) {
            super.onPostExecute(result);
            mLoading.setVisibility(View.GONE);
            if (result == null || result.size() <= 0) {
                mNoPhotoView.setVisibility(View.VISIBLE);
                return;
            }
            mAlbumAdapter.addAll(result);
        }

        private List<AlbumListInfo> scanFiles(String rootDirPath) {
            File rootDir = new File(rootDirPath);
            if (rootDir == null || !rootDir.exists()) {
                rootDir.mkdir();
                return null;
            }
            File[] subFiles = rootDir.listFiles();
            int size = 0;
            if (subFiles == null || subFiles.length == 0) {
                return null;
            }
            size = subFiles.length;
            Map<String, AlbumListInfo> map = new HashMap<String, AlbumListInfo>();
            for (int i = 0; i < size; i++) {
                handleFile(subFiles[i], map);
            }
            if (map != null && map.size() > 0) {
                List<AlbumListInfo> list = new ArrayList<AlbumListInfo>(
                        map.values());
                return list;
            } else {
                return null;
            }
        }

        /**
         * 获取每个月份的图片信息，包括路径，title，文件夹下的图片列表
         * 
         * @param file
         * @return [参数说明]
         * @return AlbumListInfo [返回类型说明]
         */
        private void handleFile(File file, Map<String, AlbumListInfo> map) {
            if (!file.isDirectory()) {
                return;
            }

            File[] subFiles = file.listFiles();
            int size = subFiles.length;
            String title = parseMonthFolder(file);
            if (TextUtils.isEmpty(title) || subFiles.length == 0) {
                return;
            }
            String month = parseMonth(file);
            AlbumListInfo info = map.get(title);
            if (info == null) {
                info = new AlbumListInfo();
                info.title = title;
                info.month = Integer.valueOf(month);
                LogUtil.d(TAG, "in handleFile,title:" + title);
                map.put(title, info);
            }
            info.paths.add(file.getAbsolutePath());

            for (int i = 0; i < size; i++) {
                File f = subFiles[i];
                Pattern p = Pattern.compile(".*\\.[jpg|png]",
                        Pattern.CASE_INSENSITIVE);
                String path = f.getAbsolutePath();
                LogUtil.d(TAG, "in handleFile,path:" + path);
                Matcher m = p.matcher(path);
                if (!f.isDirectory() && m.find()) {
                    PhotoListInfo photoInfo = new PhotoListInfo();
                    photoInfo.path = path;
                    info.subFilePaths.add(photoInfo);
                }
            }
            if (info.subFilePaths.size() > 0) {
                Collections.sort(info.subFilePaths,
                        new Comparator<PhotoListInfo>() {
                            @Override
                            public int compare(PhotoListInfo lhs,
                                    PhotoListInfo rhs) {
                                return rhs.path.compareTo(lhs.path);
                            }
                        });
            } else {
                if (info.subFilePaths.size() == 0) {
                    map.remove(title);
                }
            }
        }

        /**
         * 解析日期文件夹，如：把"20150812"解析为"2015年8月"
         * 
         * @param 文件夹名字：file
         * @return [参数说明]
         * @return String [返回类型说明]
         */
        private String parseMonthFolder(File file) {
            Pattern p = Pattern.compile("^[0-9]{8}$");
            String fileName = file.getName();
            LogUtil.d(TAG, "in parseMonthFolder,filename:" + fileName);
            Matcher m = p.matcher(fileName);
            if (!m.find()) {
                return null;
            }
            String yearStr = getString(R.string.year);
            String monthStr = getString(R.string.month);
            String year = fileName.substring(0, 4);
            String month = fileName.substring(4, 6);
            return year + yearStr + month + monthStr;
        }

        /**
         * 解析日期文件夹，如：把"20150812"解析为"8"
         * 
         * @param 文件夹名字：file
         * @return [参数说明]
         * @return String [返回类型说明]
         */
        private String parseMonth(File file) {
            Pattern p = Pattern.compile("^[0-9]{8}$");
            String fileName = file.getName();
            LogUtil.d(TAG, "in parseMonthFolder,filename:" + fileName);
            Matcher m = p.matcher(fileName);
            if (!m.find()) {
                return "";
            }
            String month = fileName.substring(4, 6);
            return month;
        }

        private String getString(int resId) {
            return getContext().getString(resId);
        }
    }

    public void updateData() {
        mAlbumAdapter.clear();
        mLoading.setVisibility(View.VISIBLE);
        new MyTask().execute();
    }
}
