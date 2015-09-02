
package com.spt.carengine.entertainment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.entertainment.adapter.MusicSortAdapter;
import com.spt.carengine.entertainment.bean.MusicInfoBean;
import com.spt.carengine.entertainment.bean.MusicSortBean;
import com.spt.carengine.entertainment.interfaces.IPlayControl;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;

public class MusicFragment extends Fragment implements OnClickListener {

    private Activity mActivity;
    private View rootView;
    private IPlayControl mPlayControl;
    private ArrayList<MusicSortBean> mDatas;
    private GridView mGridView;
    private MusicSortAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_et_music, null);

        initView();
        initData();
        initListener();

        return rootView;
    }

    private void initListener() {
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                // 获取数据
                MusicSortBean musicSortBean = mDatas.get(position);

                MusicInfoBean infoBean = new MusicInfoBean();
                infoBean.setMusicUrl("http://abv.cn/music/光辉岁月.mp3");
                EventBus.getDefault().post(Constant.MUSIC_PLAY_LIST);

            }
        });

    }

    private void initData() {

        if (mDatas == null) {
            mDatas = new ArrayList<MusicSortBean>();
        }
        // 从网络加载数据
        for (int i = 0; i < 8; i++) {
            MusicSortBean bean = new MusicSortBean();
            bean.setImageUrl("http://imgcache.qq.com/music/photo/singer_300/43/300_singerpic_143_0.jpg");

            mDatas.add(bean);
        }

        mAdapter.addAll(mDatas);
        // 更新数据
        mAdapter.notifyDataSetChanged();

    }

    private void initView() {
        mGridView = (GridView) rootView.findViewById(R.id.gv_music_sort);

        mAdapter = new MusicSortAdapter(mActivity.getApplicationContext());
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }

    }

}
