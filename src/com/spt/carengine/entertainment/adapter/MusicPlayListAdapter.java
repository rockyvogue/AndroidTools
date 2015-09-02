
package com.spt.carengine.entertainment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.entertainment.bean.MusicListBean;

import org.apache.http.Header;

/**
 * <功能描述> 音乐排行榜中GridView 适配器
 * 
 * @author ymm
 */
public class MusicPlayListAdapter extends ArrayAdapter<MusicListBean> {

    private LayoutInflater mInflater;
    private int itemWidth = 0;
    private int itemHeight = 0;

    public MusicPlayListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (itemWidth == 0 && itemHeight == 0) {
            // 获取listView的高度
            int listviewHeight = parent.getHeight();
            int listviewWidth = parent.getWidth();

            itemWidth = listviewWidth;
            itemHeight = (int) ((listviewHeight * 1.0f) / 5 + 0.5); // 一页能容纳5个item
        }

        final MusicListBean info = getItem(position);

        View view = null;
        Holder holder = null;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_item_music_list,
                    parent, false);
            RelativeLayout subject_ll = (RelativeLayout) convertView
                    .findViewById(R.id.rl_subject);

            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) subject_ll
                    .getLayoutParams();
            linearParams.height = itemHeight;

            subject_ll.setLayoutParams(linearParams);

            holder = new Holder();

            holder.songName = (TextView) convertView
                    .findViewById(R.id.tv_song_name);
            holder.singer = (TextView) convertView.findViewById(R.id.tv_singer);
            holder.songTime = (TextView) convertView
                    .findViewById(R.id.tv_song_time);

            convertView.setTag(holder);
        } else {

            holder = (Holder) convertView.getTag();
        }

        holder.songName.setText(info.getSongName());
        holder.singer.setText(info.getSinger());
        holder.songTime.setText(info.getSongTime());

        return convertView;
    }

    private static class Holder {
        TextView songName;
        TextView singer;
        TextView songTime;
    }

}
