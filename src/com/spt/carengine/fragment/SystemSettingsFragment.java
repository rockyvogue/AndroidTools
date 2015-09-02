
package com.spt.carengine.fragment;

/*
 * 文 件 名:  MainContentFragment.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  徐浩
 * 创建时间:  2015年8月17日 9：20
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.spt.carengine.R;
import com.spt.carengine.adapter.SystemSettingsAdapter;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.user.entity.UserListBean;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心
 * 
 * @author 徐浩
 */

public class SystemSettingsFragment extends Fragment {

    private int[] mStrIds = {
            R.string.wifi_collision_detection, R.string.wifi_extinguish_screen,
            R.string.user_system_settings_wifi,
            R.string.user_system_settings_sd,
            R.string.user_system_settings_data_time,
            R.string.user_system_settings_about,
    };
    private int[] mTitle = {
            R.drawable.my_enter_icon, R.drawable.my_enter_icon,
            R.drawable.set_wlan_icon, R.drawable.set_about_icon,
            R.drawable.set_sdcark_icon, R.drawable.set_time_icon
    };

    private List<Map<String, Object>> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_system_settings_main1,
                container, false);
        // initView(view);
        init(view, container);
        return view;
    }

    private void initView(View view) {
        ListView listView = (ListView) view
                .findViewById(R.id.myCenter_listView);
        listView.setDivider(null);

        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnScrollListener(listener);
        initShowReturnBarView(view);
    }

    private void initShowReturnBarView(View view2) {
        mReturnBarView = (ReturnBarView) view2.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_SYSTEM_SETTINGS);
        mReturnBarView.setBackListener(mOnClickListener);
    }

    private void init(View view, ViewGroup container) {
        ListView listView = (ListView) view
                .findViewById(R.id.myCenter_listView);
        listView.setDivider(null);

        SystemSettingsAdapter adapter = new SystemSettingsAdapter(
                getActivity(), visibleItemCounts);
        adapter.addAll(getDatas());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnScrollListener(listener);
        initShowReturnBarView(view);
    }

    private List<UserListBean> getDatas() {
        List<UserListBean> list = new ArrayList<UserListBean>();
        int size = mStrIds.length;
        for (int i = 0; i < size; i++) {
            UserListBean bean = new UserListBean();
            bean.titleId = mStrIds[i];
            bean.iconId = mTitle[i];
            list.add(bean);
        }
        return list;
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            switch (position) {
                case 2:
                    EventBus.getDefault()
                            .post(Constant.MODULE_TYPE_SYSTEM_WIFI);
                    break;
                case 3:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_SDSET);
                    break;
                case 4:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_DATE);
                    break;
                case 5:
                    EventBus.getDefault().post(Constant.MOUDLE_TYPE_ABOUT);
                    break;

                default:
                    break;
            }

        }
    };

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
                    EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
                    break;
                default:
                    break;
            }

        }

    };

    private View view;

    private ReturnBarView mReturnBarView;

}
