
package com.spt.carengine.fragment;

/*
 * 文 件 名:  MainContentFragment.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  徐浩
 * 创建时间:  2015年8月11日 9：25
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.spt.carengine.R;
import com.spt.carengine.adapter.UserAdapter;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.user.entity.UserListBean;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

/**
 * 个人中心
 * 
 * @author 徐浩
 */

public class UserFragment extends Fragment implements OnItemClickListener {
    private int[] mStrIds = {
            R.string.user_list_item_fm, R.string.user_list_item_bluetooth,
            R.string.user_list_item_traffic,
            R.string.user_list_item_system_settings
    };
    private int[] mIcons = {
            R.drawable.my_enter_icon, R.drawable.my_enter_icon,
            R.drawable.my_enter_icon, R.drawable.my_enter_icon
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_main, container,
                false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        ListView listView = (ListView) view
                .findViewById(R.id.myCenter_listView);
        initShowReturnBarView(view);

        listView.setDivider(null);
        // listView.setCacheColorHint(Color.TRANSPARENT);
        UserAdapter adapter = new UserAdapter(getActivity(), visibleItemCounts);
        adapter.addAll(getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(listener);

    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_USER);
        mReturnBarView.setBackListener(mOnClickListener);

    }

    private List<UserListBean> getData() {
        List<UserListBean> list = new ArrayList<UserListBean>();
        int size = mStrIds.length;
        for (int i = 0; i < size; i++) {
            UserListBean bean = new UserListBean();
            bean.titleId = mStrIds[i];
            bean.iconId = mIcons[i];
            list.add(bean);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

        switch (position) {
            case 0:

                /**
                 * Fm 調頻
                 */
                EventBus.getDefault().post(Constant.MODULE_TYPE_FM);
                break;
            case 1:
                /**
                 * 藍牙
                 */
                EventBus.getDefault().post(Constant.MODULE_TYPE_BLUETOOTH);
                break;
            /*
             * case 2:
             *//**
             * 云狗
             */
            /*
             * EventBus.getDefault().post(Constant.MODULE_TYPE_CDOG); break;
             */
            case 2:
                /**
                 * 流量管理
                 */
                EventBus.getDefault().post(Constant.MODULE_TYPE_TRAFFIC);
                break;
            case 3:
                /**
                 * 系统设置
                 */
                EventBus.getDefault()
                        .post(Constant.MODULE_TYPE_SYSTEM_SETTINGS);
                break;

            default:
                break;
        }

    }

    private int visibleItemCounts;
    private OnScrollListener listener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            visibleItemCounts = firstVisibleItem + visibleItemCount;

        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
                    break;

                default:
                    break;
            }
        }

    };
    private ReturnBarView mReturnBarView;
	private View view;
}
