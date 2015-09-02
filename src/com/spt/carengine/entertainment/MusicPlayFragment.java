
package com.spt.carengine.entertainment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.entertainment.bean.MusicListBean;
import com.spt.carengine.entertainment.interfaces.IPlayControl;
import com.spt.carengine.entertainment.view.MusicPlayerView;
import com.spt.carengine.service.MusicService;

public class MusicPlayFragment extends Fragment implements OnClickListener {

    private MusicListBean musicListBean;
    private Activity mActivity;
    private View rootView;
    private MusicPlayerView musicPlayerView;
    private IPlayControl mPlayControl;

    public MusicPlayFragment(MusicListBean bean) {
        musicListBean = bean;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        Intent service = new Intent(mActivity.getApplicationContext(),
                MusicService.class);
        mActivity.startService(service);
        mActivity.bindService(service, conn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(mActivity.getApplicationContext(), "连接音乐服务失败！", 0)
                    .show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 控制服务的对象
            mPlayControl = (IPlayControl) service;

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music_play, container,
                false);

        initView();
        initData();
        initListerner();
        return rootView;
    }

    private void initListerner() {

    }

    private void initData() {

    }

    private void initView() {
        ImageButton mPlayButton = (ImageButton) rootView
                .findViewById(R.id.ib_blmu_play);
        ImageButton mNextButton = (ImageButton) rootView
                .findViewById(R.id.ib_blmu_next);
        ImageButton mPreButton = (ImageButton) rootView
                .findViewById(R.id.ib_blmu_previous);

        mPlayButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPreButton.setOnClickListener(this);

        musicPlayerView = (MusicPlayerView) rootView.findViewById(R.id.mpv);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_blmu_play:
                musicPlayerView.setCoverURL(musicListBean.getImageUrl());

                if (!mPlayControl.isPlaying()) {
                    mPlayControl.play(musicListBean.getSongUrl());
                    musicPlayerView.start();
                } else {
                    mPlayControl.pause();
                    musicPlayerView.stop();
                }
                break;
            case R.id.ib_blmu_next:
                break;
            case R.id.ib_blmu_previous:

            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mActivity.unbindService(conn);
        Intent service = new Intent(mActivity.getApplicationContext(),
                MusicService.class);
        mActivity.stopService(service);
    }

}
