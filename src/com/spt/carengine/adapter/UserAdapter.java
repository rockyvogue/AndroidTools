
package com.spt.carengine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.View.OnScrollChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.user.entity.UserListBean;
import com.spt.carengine.utils.ScreenUtils;

public class UserAdapter extends ArrayAdapter<UserListBean> {
    private static final int ALBUM_ITEM_HEIGHT = 80;
    private LayoutInflater mInflater;
    private int visibleItemCounts;

    public UserAdapter(Context context, int visibleItemCounts) {
        super(context, 0);
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_user_listitem,
                    parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imegView = (ImageView) convertView
                    .findViewById(R.id.myCenter_List_item_imageView);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.myCenter_List_item_textView);

            viewHolder.view = convertView
                    .findViewById(R.id.user_list_item_view);
            int itemHeight = ScreenUtils.getRealHeightValue(getContext(),
                    ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.title.setText(info.titleId);
        viewHolder.imegView.setImageResource(info.iconId);
        return convertView;

    }

    private static class ViewHolder {
        TextView title;
        ImageView imegView;
        View view;
    }
}
