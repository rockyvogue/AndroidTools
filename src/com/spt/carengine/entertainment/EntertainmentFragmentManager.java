
package com.spt.carengine.entertainment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.define.BTApi;
import com.spt.carengine.entertainment.bean.MusicInfoBean;
import com.spt.carengine.entertainment.bean.MusicListBean;
import com.spt.carengine.entertainment.interfaces.IPlayControl;
import com.spt.carengine.service.MusicService;

import de.greenrobot.event.EventBus;

/**
 * <功能描述> 娱乐功能
 * 
 * @author 杨明明
 */
public class EntertainmentFragmentManager extends Fragment implements
        OnClickListener {

    private static final String TAG = "EntertainmentFragment";
    private FragmentManager fMgr;
    private View root;
    private ImageView iv_back;
    private TextView tv_title;
    private ImageButton ib_music;
    private ImageButton ib_book;
    private ImageButton ib_recent;
    private ImageButton ib_blmu;
    private int nBtState = 0;// 蓝牙状态
    private Activity mActivity;
    private IPlayControl mPlayControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        Intent service = new Intent(mActivity.getApplicationContext(),
                MusicService.class);
        mActivity.startService(service);
        mActivity.bindService(service, conn, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);

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

        root = inflater.inflate(R.layout.fragment_entertainmen, container,
                false);
        fMgr = getFragmentManager();

        initView(root);
        return root;
    }

    private void initView(View root) {

        iv_back = (ImageView) root.findViewById(R.id.iv_back);
        tv_title = (TextView) root.findViewById(R.id.tv_title);

        ib_music = (ImageButton) root.findViewById(R.id.ib_music);
        ib_book = (ImageButton) root.findViewById(R.id.ib_book);
        ib_recent = (ImageButton) root.findViewById(R.id.ib_recent);
        ib_blmu = (ImageButton) root.findViewById(R.id.ib_blmu);

        iv_back.setOnClickListener(this);
        ib_blmu.setOnClickListener(this);
        ib_book.setOnClickListener(this);
        ib_music.setOnClickListener(this);
        ib_recent.setOnClickListener(this);

        initFragment();
        switchImageButton(R.id.ib_music);

    }

    public void onEventMainThread(Object info) {

        if (info instanceof MusicInfoBean) {
            MusicInfoBean bean = (MusicInfoBean) info;
            handleMusicIndo(bean);
        }

        if (info instanceof String) {
            handleString(info.toString());
        }

        if (info instanceof MusicListBean) {
            MusicListBean bean = (MusicListBean) info;
            handleMusicListBean(bean);
        }

    }

    private void handleMusicListBean(MusicListBean bean) {
        if (musicPlayFragment == null) {
            musicPlayFragment = new MusicPlayFragment(bean);
        }

        switchFragmentAll(musicPlayFragment, "MusicPlayFragment");
    }

    private void handleString(String cmd) {
        if (Constant.MUSIC_PLAY_LIST.equals(cmd)) {
            if (musicPlayListFragment == null) {
                musicPlayListFragment = new MusicPlayListFragment();
            }
            switchFragmentAll(musicPlayListFragment, "MusicPlaylistFragment");

            return;
        }

    }

    private void switchFragmentAll(Fragment fragment, String tag) {

        Toast.makeText(mActivity, fragment.toString(), 0).show();

        FragmentTransaction ft = fMgr.beginTransaction();
        ft.replace(R.id.fl_entertainment_root, fragment);

        if (fMgr.findFragmentByTag("tag") != null) {
            ft.addToBackStack(null);
        } else {
            ft.addToBackStack("tag");
        }

        ft.commit();

    }

    private void handleMusicIndo(MusicInfoBean info) {
        String url = info.getMusicUrl();

        if (info != null) {
            if (url != null) {
                mPlayControl.play(url);
            }
        }
    }

    private void initFragment() {

        MusicFragment blmuFragment = new MusicFragment();
        switchFragment(blmuFragment, "MusicFragment");

    }

    /**
     * 接受蓝牙状态
     */
    private BroadcastReceiver btReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null)
                return;
            String sAction = intent.getAction();

            if (BTApi.ACTION_YRC_BD.equals(sAction)) {
                nBtState = intent.getIntExtra("btstate", -1);
                handlerBtState(nBtState);
            }
        }

    };
    private MusicPlayListFragment musicPlayListFragment;
    private MusicPlayFragment musicPlayFragment;

    private void handlerBtState(int nBtState) {
        /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/

        if (nBtState < 3) {// 蓝牙未连接 隐藏蓝牙音乐
            ib_blmu.setVisibility(View.GONE);
        } else {// 蓝牙已连接 开启蓝牙音乐
            ib_blmu.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:// 返回键 返回主界面

                EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);

                break;
            case R.id.ib_blmu:// 蓝牙音乐

                BlmuFragment blmuFragment = new BlmuFragment();
                switchFragment(blmuFragment, "蓝牙音乐");
                switchImageButton(R.id.ib_blmu);
                break;
            case R.id.ib_book:// 电子书
                BookFragment bookFragment = new BookFragment();
                switchFragment(bookFragment, "BookFragment");
                switchImageButton(R.id.ib_book);
                break;
            case R.id.ib_music:// 音乐
                MusicFragment musicFragment = new MusicFragment();
                switchFragment(musicFragment, "MusicFragment");
                switchImageButton(R.id.ib_music);
                break;
            case R.id.ib_recent://
                RecentFragment recentFragment = new RecentFragment();
                switchFragment(recentFragment, "RecentFragment");
                switchImageButton(R.id.ib_recent);

                break;

            default:
                break;
        }

    }

    private void switchImageButton(int id) {

        switchImageButtonBackgroud(id);

        switch (id) {
            case R.id.ib_music:
                ib_music.setImageResource(R.drawable.entertainment_music_icon_p);
                ib_book.setImageResource(R.drawable.entertainment_book_icon_n);
                ib_recent
                        .setImageResource(R.drawable.entertainment_recent_icon_n);
                ib_blmu.setImageResource(R.drawable.entertainment_blmu_icon_n);
                break;
            case R.id.ib_book:
                ib_music.setImageResource(R.drawable.entertainment_music_icon_n);
                ib_book.setImageResource(R.drawable.entertainment_book_icon_p);
                ib_recent
                        .setImageResource(R.drawable.entertainment_recent_icon_n);
                ib_blmu.setImageResource(R.drawable.entertainment_blmu_icon_n);
                break;
            case R.id.ib_recent:
                ib_music.setImageResource(R.drawable.entertainment_music_icon_n);
                ib_book.setImageResource(R.drawable.entertainment_book_icon_n);
                ib_recent
                        .setImageResource(R.drawable.entertainment_recent_icon_p);
                ib_blmu.setImageResource(R.drawable.entertainment_blmu_icon_n);
                break;
            case R.id.ib_blmu:
                ib_music.setImageResource(R.drawable.entertainment_music_icon_n);
                ib_book.setImageResource(R.drawable.entertainment_book_icon_n);
                ib_recent
                        .setImageResource(R.drawable.entertainment_recent_icon_n);
                ib_blmu.setImageResource(R.drawable.entertainment_blmu_icon_p);
                break;

        }

    }

    private void switchImageButtonBackgroud(int id) {
        switch (id) {
            case R.id.ib_music:
                ib_music.setBackgroundResource(R.drawable.backgroud_p);
                ib_book.setBackgroundResource(R.drawable.backgroud_n);
                ib_recent.setBackgroundResource(R.drawable.backgroud_n);
                ib_blmu.setBackgroundResource(R.drawable.backgroud_n);
                break;
            case R.id.ib_book:
                ib_music.setBackgroundResource(R.drawable.backgroud_n);
                ib_book.setBackgroundResource(R.drawable.backgroud_p);
                ib_recent.setBackgroundResource(R.drawable.backgroud_n);
                ib_blmu.setBackgroundResource(R.drawable.backgroud_n);
                break;
            case R.id.ib_recent:
                ib_music.setBackgroundResource(R.drawable.backgroud_n);
                ib_book.setBackgroundResource(R.drawable.backgroud_n);
                ib_recent.setBackgroundResource(R.drawable.backgroud_p);
                ib_blmu.setBackgroundResource(R.drawable.backgroud_n);
                break;
            case R.id.ib_blmu:
                ib_music.setBackgroundResource(R.drawable.backgroud_n);
                ib_book.setBackgroundResource(R.drawable.backgroud_n);
                ib_recent.setBackgroundResource(R.drawable.backgroud_n);
                ib_blmu.setBackgroundResource(R.drawable.backgroud_p);
                break;

            default:
                break;
        }

    }

    private void switchFragment(Fragment fragment, String tag) {
        // 替换标题
        tv_title.setText(tag);

        FragmentTransaction ft = fMgr.beginTransaction();
        ft.replace(R.id.ll_entertainment_root, fragment);

        if (fMgr.findFragmentByTag("tag") != null) {
            ft.addToBackStack(null);
        } else {
            ft.addToBackStack("tag");
        }

        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 取消注册
        EventBus.getDefault().unregister(this);
        mActivity.unbindService(conn);
        Intent service = new Intent(mActivity.getApplicationContext(),
                MusicService.class);
        mActivity.stopService(service);
    }

}
