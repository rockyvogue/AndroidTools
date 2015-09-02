
package com.spt.carengine.entertainment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.spt.carengine.R;
import com.spt.carengine.entertainment.adapter.MusicPlayListAdapter;
import com.spt.carengine.entertainment.bean.MusicListBean;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;

public class MusicPlayListFragment extends Fragment {

    private Activity mActivity;
    private View rootView;
    private ListView mLVPlayList;
    private ArrayList<MusicListBean> mDatas;
    private MusicPlayListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music_list, container,
                false);

        initView();
        initData();
        initListerner();
        return rootView;
    }

    private void initListerner() {
        mLVPlayList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                MusicListBean item = adapter.getItem(position);
                EventBus.getDefault().post(item);

            }
        });
    }

    private void initData() {
        if (mDatas == null) {
            mDatas = new ArrayList<MusicListBean>();
        }

        for (int i = 0; i < 8; i++) {
            MusicListBean bean = new MusicListBean();
            bean.setSongUrl("http://abv.cn/music/光辉岁月.mp3");
            bean.setImageUrl("https://upload.wikimedia.org/wikipedia/en/b/b3/MichaelsNumberOnes.JPG");
            bean.setSongName("光辉岁月");
            bean.setSinger("黄家驹");
            bean.setSongTime("03:26");

            mDatas.add(bean);
        }

        adapter.addAll(mDatas);
        adapter.notifyDataSetChanged();

    }

    private void initView() {
        mLVPlayList = (ListView) rootView.findViewById(R.id.lv_music_playlist);
        adapter = new MusicPlayListAdapter(mActivity.getApplicationContext());
        mLVPlayList.setAdapter(adapter);

    }
}
