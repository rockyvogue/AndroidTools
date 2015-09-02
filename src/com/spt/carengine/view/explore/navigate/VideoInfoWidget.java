
package com.spt.carengine.view.explore.navigate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.utils.UtilTools;
import com.spt.carengine.utils.VideoUtils;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午3:48:08
 * @description 视频的View信息
 */
public class VideoInfoWidget {

    private Context context;
    private PopupWindow popupWindow;

    private View contentView;

    private TextView mVideoName;
    private TextView mVideoDuration;
    private TextView mVideoSize;
    private TextView mVideoFormat;

    public VideoInfoWidget(Context context) {
        this.context = context;
    }

    public void showSharePopupWindow(View dropView, int popBackgroundID) {
        initShowLayout();

        initPopWindow(dropView, popBackgroundID);

    }

    private void initShowLayout() {
        LayoutInflater mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = mLayoutInflater.inflate(R.layout.videoplay_popwindow,
                null);
        mVideoName = (TextView) contentView.findViewById(R.id.video_name);
        mVideoName.setSelected(true);
        mVideoDuration = (TextView) contentView
                .findViewById(R.id.video_duration);
        mVideoSize = (TextView) contentView.findViewById(R.id.video_size);
        mVideoFormat = (TextView) contentView.findViewById(R.id.video_format);
    }

    private void initPopWindow(View dropView, int popBackgroundID) {

        int popupwindowWidth = (int) context.getResources().getDimension(
                R.dimen.video_more_view_width);
        int popupwindowHeigth = (int) context.getResources().getDimension(
                R.dimen.video_more_view_height);

        popupWindow = new PopupWindow(contentView, popupwindowWidth,
                popupwindowHeigth);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(
                popBackgroundID));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAsDropDown(dropView,
                dropView.getWidth() - dropView.getWidth() / 4, 0);
    }

    public void setInformation(VideoInfo videoInfo) {
        mVideoName.setText(UtilTools.getPrefixFromName(videoInfo.name));
        mVideoDuration.setText(VideoUtils.timeToStr(videoInfo.time));
        mVideoSize.setText(videoInfo.size);
        mVideoFormat.setText(UtilTools.getExtensionFromName(videoInfo.name));
    }

    public static class VideoInfo {
        public String name;
        public long time;
        public String size;
    }
}
