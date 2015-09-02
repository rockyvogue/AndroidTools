
package com.spt.carengine.entertainment;

import cn.yunzhisheng.asr.utils.LogUtil;

import com.spt.carengine.R;
import com.spt.carengine.define.BTApi;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BlmuFragment extends Fragment {

    private Activity mActivity;
    private View rootView;
    private ImageButton mIBPrevious;
    private ImageButton mIBPlay;
    private ImageButton mIBNext;
    private int nBtState = 0;// 蓝牙状态

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_et_blmu, null);
        regBroadRecevier(true);
        initView();

        LogUtil.e("BlmuFragment 被创建");
        return rootView;
    }

    private void initView() {

        mIBPrevious = (ImageButton) rootView
                .findViewById(R.id.ib_blmu_previous);
        mIBPlay = (ImageButton) rootView.findViewById(R.id.ib_blmu_play);
        mIBNext = (ImageButton) rootView.findViewById(R.id.ib_blmu_next);

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

    private void handlerBtState(int nBtState) {
        /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/

        if (nBtState < 3) {// 蓝牙未连接 隐藏蓝牙音乐

        } else {// 蓝牙已连接 开启蓝牙音乐

        }

    }

    private void regBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BTApi.ACTION_YRC_BD);// 蓝牙的状态
            mActivity.getApplicationContext().registerReceiver(btReceiver,
                    filter);
        } else {
            mActivity.getApplicationContext().unregisterReceiver(btReceiver);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LogUtil.e("BlmuFragment 被销毁");
        regBroadRecevier(false);
    }
}
