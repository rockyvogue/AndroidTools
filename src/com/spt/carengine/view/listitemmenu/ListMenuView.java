
package com.spt.carengine.view.listitemmenu;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListMenuView extends LinearLayout implements OnClickListener {

    private ListMenuListView mListView;
    private ListMenuLayout mLayout;
    private ListMenu mMenu;
    private OnSwipeItemClickListener onItemClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ListMenuView(ListMenu menu, ListMenuListView listView) {
        super(menu.getContext());
        mListView = listView;
        mMenu = menu;
        List<ListMenuItem> items = menu.getMenuItems();
        int id = 0;
        for (ListMenuItem item : items) {
            addItem(item, id++);
        }
    }

    private void addItem(ListMenuItem item, int id) {
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.MATCH_PARENT);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setId(id);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setBackgroundDrawable(item.getBackground());
        parent.setOnClickListener(this);

        addView(parent);

        if (item.getIcon() != null) {
            parent.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            parent.addView(createTitle(item));
        }

    }

    /**
     * <功能描述>
     * 
     * @param item
     * @return [参数说明] 联系人删除图标
     * @return ImageView [返回类型说明]
     */
    private ImageView createIcon(ListMenuItem item) {
        ImageView iv = new ImageView(getContext());

        iv.setImageDrawable(item.getIcon());

        return iv;
    }

    private TextView createTitle(ListMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && mLayout.isOpen()) {
            onItemClickListener.onItemClick(this, mMenu, v.getId());
        }
    }

    public OnSwipeItemClickListener getOnSwipeItemClickListener() {
        return onItemClickListener;
    }

    public void setOnSwipeItemClickListener(
            OnSwipeItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(ListMenuLayout mLayout) {
        this.mLayout = mLayout;
    }

    public static interface OnSwipeItemClickListener {
        void onItemClick(ListMenuView view, ListMenu menu, int index);
    }
}
