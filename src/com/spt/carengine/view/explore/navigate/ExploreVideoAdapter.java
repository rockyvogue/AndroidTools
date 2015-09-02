
package com.spt.carengine.view.explore.navigate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spt.carengine.R;
import com.spt.carengine.db.bean.VideoFileLockBean;
import com.spt.carengine.db.manager.VideoFileLockDBManager;
import com.spt.carengine.utils.UtilTools;
import com.spt.carengine.view.explore.data.AbstractFile;
import com.spt.carengine.view.explore.data.FileFolder;
import com.spt.carengine.view.explore.data.SingleFile;
import com.spt.carengine.view.swipelistview.SwipeListView;

import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月4日 下午5:23:21
 * @description 浏览视频的适配器
 */
public class ExploreVideoAdapter extends BaseAdapter {

    private List<AbstractFile> ifiles = null;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private SwipeListView mSwipeListView;
    private VideoFileLockDBManager videoFileLockDataOpt = null;

    public ExploreVideoAdapter(SwipeListView swipeListView,
            List<AbstractFile> ifiles, Context context) {
        super();
        this.ifiles = ifiles;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.mSwipeListView = swipeListView;
        videoFileLockDataOpt = new VideoFileLockDBManager(context);
    }

    @Override
    public int getCount() {
        return ifiles != null ? ifiles.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return ifiles != null ? ifiles.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (ifiles == null)
            return convertView;
        int size = ifiles.size();
        if ((size == 0) || (size <= position))
            return convertView;

        final AbstractFile iFile = ifiles.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.explore_video_item,
                    parent, false);
            viewHolder.fileThumbImage = (ImageView) convertView
                    .findViewById(R.id.explorevideo_item_thumb);
            viewHolder.deleteImage = (ImageView) convertView
                    .findViewById(R.id.explorevideo_item_delete);
            viewHolder.lockImage = (ImageView) convertView
                    .findViewById(R.id.explorevideo_item_lock);
            viewHolder.fileNameTv = (TextView) convertView
                    .findViewById(R.id.explorevideo_item_name);
            viewHolder.fileSizeTv = (TextView) convertView
                    .findViewById(R.id.explorevideo_item_size);
            viewHolder.fileCreateDateTv = (TextView) convertView
                    .findViewById(R.id.explorevideo_item_time);
            viewHolder.rightSideLayout = (RelativeLayout) convertView
                    .findViewById(R.id.explorevideo_item_rightside_detail);
            viewHolder.rightSideName = (TextView) convertView
                    .findViewById(R.id.explorevideo_item_rightside_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (iFile instanceof SingleFile) {
            if ("0".equals(iFile.getLockFlag())) {
                mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
                viewHolder.lockImage
                        .setImageResource(R.drawable.explore_video_icon_unlock_btn);
            } else {
                mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
                viewHolder.lockImage
                        .setImageResource(R.drawable.explore_video_icon_lock_btn);
            }
            viewHolder.rightSideName.setVisibility(View.GONE);
            viewHolder.rightSideLayout.setVisibility(View.VISIBLE);
            viewHolder.lockImage.setVisibility(View.VISIBLE);
            viewHolder.fileNameTv.setText(ifiles.get(position).getFileName());
            viewHolder.fileSizeTv.setText(ifiles.get(position).getFileSize());
            viewHolder.fileCreateDateTv.setText(ifiles.get(position)
                    .getFileCreateDate());

            viewHolder.deleteImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mSwipeListView.closeAnimate(position);
                    mSwipeListView.dismiss(position);
                }
            });

            viewHolder.lockImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if ("0".equals(iFile.getLockFlag())) {
                        lockVideoFile(position);

                    } else {
                        unlockVideoFile(position);
                    }
                    notifyDataSetChanged();
                }
            });

            viewHolder.fileThumbImage
                    .setImageResource(R.drawable.video_default_thumbnail);

            ImageLoader.getInstance().displayImage(
                    "file://"
                            + UtilTools.getUTF8CodeInfoFromURL(ifiles.get(
                                    position).getFilePath()),
                    viewHolder.fileThumbImage);

        } else if (iFile instanceof FileFolder) {
            mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
            viewHolder.rightSideName.setVisibility(View.VISIBLE);
            viewHolder.rightSideLayout.setVisibility(View.GONE);
            viewHolder.lockImage.setVisibility(View.GONE);
            viewHolder.fileThumbImage
                    .setImageResource(R.drawable.record_file_icon_list);
            viewHolder.rightSideName
                    .setText(ifiles.get(position).getFileName());
        }

        return convertView;
    }

    private void lockVideoFile(int position) {
        VideoFileLockBean vLockInfo = new VideoFileLockBean();
        AbstractFile abstractFile = ifiles.get(position);
        vLockInfo.setName(abstractFile.getFileName());
        vLockInfo.setPath(abstractFile.getFilePath());
        vLockInfo.setSize(abstractFile.getFileSize());
        vLockInfo.setCreateTime(abstractFile.getFileCreateDate());
        vLockInfo.setDate(abstractFile.getFileCreateDate().substring(0, 10));
        if (videoFileLockDataOpt.saveInfoToDatabase(vLockInfo)) {
            ifiles.get(position).setLockFlag("1");
        }
    }

    private void unlockVideoFile(int position) {
        AbstractFile abstractFile = ifiles.get(position);
        boolean isDelete = videoFileLockDataOpt
                .deleteInfoRecordFromPath(abstractFile.getFilePath());
        if (isDelete) {
            ifiles.get(position).setLockFlag("0");
        }
    }

    class ViewHolder {
        ImageView fileThumbImage;

        public ImageView getFileThumbImage() {
            return fileThumbImage;
        }

        TextView fileNameTv;
        TextView fileSizeTv;
        TextView fileCreateDateTv;
        ImageView deleteImage;
        ImageView lockImage;

        RelativeLayout rightSideLayout;
        TextView rightSideName;
    }

}
