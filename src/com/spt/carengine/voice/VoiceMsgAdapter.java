
package com.spt.carengine.voice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.view.VoiceMessageItem;

import java.util.List;

public class VoiceMsgAdapter extends BaseAdapter {

    public static final int MESSAGE_TYPE_INVALID = -1;
    public static final int MESSAGE_TYPE_MINE_TETX = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_AUDIO = 0x02;
    public static final int MESSAGE_TYPE_OTHER_TEXT = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x05;
    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
    private static final int VIEW_TYPE_COUNT = 9;

    private List<VoiceMessageItem> mMsgList;

    private Context mContext;

    private LayoutInflater mInflater;

    public VoiceMsgAdapter(Context context, List<VoiceMessageItem> msgList) {
        this.mContext = context;
        this.mMsgList = msgList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgHolder holder = null;
        final VoiceMessageItem mItem = mMsgList.get(position);
        // if (convertView == null && mInflater != null) {

        holder = new MsgHolder();

        if (mItem.getMessageFromWhere() == VoiceMessageItem.MsgSource.Mine) {
            convertView = mInflater.inflate(R.layout.voice_mine_msg_item,
                    parent, false);
            holder.voiceText = (TextView) convertView.findViewById(R.id.voice_text);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
            convertView.setTag(holder);

        } else if (mItem.getMessageFromWhere() == VoiceMessageItem.MsgSource.Rebot) {
            convertView = mInflater.inflate(R.layout.voice_rebot_msg_item, parent, false);
            holder.voiceText = (TextView) convertView.findViewById(R.id.voice_text);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
            convertView.setTag(holder);
        }
        
        // } else {
        // holder = (MsgHolder) convertView.getTag();
        // }

        if (mItem != null) {
            // 文字
//            holder.voiceText.insertGif(mItem.getMessage());
            holder.voiceText.setText(mItem.getMessage());
        }

        return convertView;
    }

    class MsgHolder {
        TextView voiceText;
        RelativeLayout relativeLayout;
    }

    public void upDateMsg(VoiceMessageItem msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    public void removeHeadMsg() {
        if (mMsgList.size() - 10 > 10) {
            for (int i = 0; i < 10; i++) {
                mMsgList.remove(i);
            }
            notifyDataSetChanged();
        }
    }

    public void setmMsgList(List<VoiceMessageItem> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    public void upDateMsgByList(List<VoiceMessageItem> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mMsgList.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

}
