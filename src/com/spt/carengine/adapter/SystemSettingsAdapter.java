
package com.spt.carengine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.fragment.SystemSettingsFragment;
import com.spt.carengine.user.entity.UserListBean;
import com.spt.carengine.utils.ScreenUtils;

public class SystemSettingsAdapter extends ArrayAdapter<UserListBean> {
    private static final int ALBUM_ITEM_HEIGHT = 80;
    private LayoutInflater mInflater;
    private int visibleItemCounts;
    final int VIEW_TYPE = 3;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;

    public SystemSettingsAdapter(Context context, int visibleItemCounts) {
        super(context, visibleItemCounts);
        this.visibleItemCounts = visibleItemCounts;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserListBean info = getItem(position);
        int type = getItemViewType(position);
        ViewHolder viewHolder = null;
        ViewHolders viewHolder2 = null;
        ViewHolderes viewHolder3 = null;

        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = mInflater.inflate(
                            R.layout.system_settings_listitem1, parent, false);
                    viewHolder3 = new ViewHolderes();
                    viewHolder3.title = (TextView) convertView
                            .findViewById(R.id.system_settings_List1_item_title);
                    viewHolder3.seekbar = (SeekBar) convertView
                            .findViewById(R.id.system_settings_List1_item_seekbar);

                    int itemHeight3 = ScreenUtils.getRealHeightValue(
                            getContext(), ALBUM_ITEM_HEIGHT);
                    AbsListView.LayoutParams param3 = new AbsListView.LayoutParams(
                            LayoutParams.MATCH_PARENT, itemHeight3);
                    convertView.setLayoutParams(param3);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_2:
                    convertView = mInflater.inflate(
                            R.layout.system_settings_listitem2, parent, false);
                    viewHolder2 = new ViewHolders();
                    viewHolder2.title = (TextView) convertView
                            .findViewById(R.id.system_settings_List2_item_title);
                    viewHolder2.checkBox = (CheckBox) convertView
                            .findViewById(R.id.system_settings_List2_item_checkBox);
                    viewHolder2.checkBox.setVisibility(View.VISIBLE);

                    int itemHeight1 = ScreenUtils.getRealHeightValue(
                            getContext(), ALBUM_ITEM_HEIGHT);
                    AbsListView.LayoutParams param1 = new AbsListView.LayoutParams(
                            LayoutParams.MATCH_PARENT, itemHeight1);
                    convertView.setLayoutParams(param1);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_3:
                    convertView = mInflater.inflate(
                            R.layout.system_settings_listitem, parent, false);
                    viewHolder = new ViewHolder();

                    viewHolder.imegView = (ImageView) convertView
                            .findViewById(R.id.system_settings_List_item_img);
                    viewHolder.rightImg = (ImageView) convertView
                            .findViewById(R.id.system_settings_List_item_right_img);
                    viewHolder.title = (TextView) convertView
                            .findViewById(R.id.system_settings_List_item_title);
                    viewHolder.view = convertView
                            .findViewById(R.id.user_list_item_view);
                    int itemHeight = ScreenUtils.getRealHeightValue(
                            getContext(), ALBUM_ITEM_HEIGHT);
                    AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                            LayoutParams.MATCH_PARENT, itemHeight);
                    convertView.setLayoutParams(param);
                    convertView.setTag(viewHolder);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    viewHolder3 = (ViewHolderes) convertView.getTag();
                    break;
                case TYPE_2:
                    viewHolder2 = (ViewHolders) convertView.getTag();
                    break;
                case TYPE_3:
                    viewHolder = (ViewHolder) convertView.getTag();
                    break;

                default:
                    break;
            }
        }

        switch (type) {
            case TYPE_1:
                break;
            case TYPE_2:
                break;
            case TYPE_3:
                viewHolder.title.setText(info.titleId);
                viewHolder.imegView.setImageResource(info.iconId);
                break;

        }

        return convertView;

    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)
            return TYPE_1;
        else if (p == 1)
            return TYPE_2;
        else
            return TYPE_3;
    }

    private static class ViewHolder {
        TextView title;
        ImageView imegView;
        ImageView rightImg;
        View view;
        CheckBox checkBox;
    }

    private static class ViewHolders {
        TextView title;
        View view;
        CheckBox checkBox;
    }

    private static class ViewHolderes {
        TextView title;
        View view;
        SeekBar seekbar;
    }
}
