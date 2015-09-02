
package com.spt.carengine.btcall;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.define.BTApi;

import de.greenrobot.event.EventBus;

import java.util.Timer;
import java.util.TimerTask;

public class CallFragment extends Fragment implements OnClickListener {

    private View fragmentView = null;
    private String mPhoneNumber;
    private TextView tvCallNumber;
    private TextView tvCallTime;
    private ImageView ivCallAnime;

    private Button btnCallHandsfree;
    private Button btnCallHang;
    private Button btnCallAnswer;
    private Context mContext;
    private Timer timer;
    private boolean isHandsfree = false;
    private CallTimerTask mTask;

    private int mSeconds = 0;

    private int mState;// 界面状态
    public static final int CALL_STATE = 1;// 去电状态
    public static final int RING_STATE = 2;// 来电状态
    public static final int RING_RECEIVER = 5;// 来电广播
    private boolean isStartTime = false;

    /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/
    private int nBtState = 0;

    public CallFragment(Context mContext, String mPhoneNumber, int mState) {
        this.mPhoneNumber = mPhoneNumber;
        this.mState = mState;
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = (View) inflater.inflate(R.layout.fragment_bt_call, null);
        initdata();
        initeview();

        return fragmentView;
    }

    private void initdata() {
        RegBroadRecevier(true);
    }

    private void initeview() {
        tvCallNumber = (TextView) fragmentView
                .findViewById(R.id.tv_call_number);
        tvCallTime = (TextView) fragmentView.findViewById(R.id.tv_call_time);

        ivCallAnime = (ImageView) fragmentView.findViewById(R.id.iv_call_anime);
        btnCallHandsfree = (Button) fragmentView
                .findViewById(R.id.btn_call_handsfree);
        btnCallHang = (Button) fragmentView.findViewById(R.id.btn_call_hang);
        btnCallAnswer = (Button) fragmentView
                .findViewById(R.id.btn_call_answer);

        btnCallHandsfree.setOnClickListener(this);
        btnCallHang.setOnClickListener(this);
        btnCallAnswer.setOnClickListener(this);

        tvCallNumber.setText(mPhoneNumber);

        // 初始化状态
        if (mState == RING_STATE) {
            tvCallTime.setText("...来电...");
            btnCallHandsfree.setVisibility(View.GONE);
            btnCallAnswer.setVisibility(View.VISIBLE);
        }

        if (mState == CALL_STATE) {
            tvCallTime.setText("拨号中...");
            ivCallAnime.setImageResource(R.drawable.call_animation_list);
        }
    }

    public void RegBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BTApi.ACTION_YRC_BD);// 蓝牙的状态
            getActivity().getApplicationContext().registerReceiver(btReceiver,
                    filter);
        } else {
            getActivity().getApplicationContext()
                    .unregisterReceiver(btReceiver);
        }

    }

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null)
                return;
            String sAction = intent.getAction();

            if (BTApi.ACTION_YRC_BD.equals(sAction)) {
                int nCmd = intent.getIntExtra("cmd", -1);
                if (nCmd == -1)
                    return;
                nBtState = intent.getIntExtra("btstate", -1);

//                Log.e("nBtState", ":蓝牙状态-->" + nBtState);
                handlerBtState(nBtState);
            }
        }

    };

    private void handlerBtState(int nBtState) {
        /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/

        switch (nBtState) {
            case 3:// 正常状态
                finishSelf();
                break;

            case 4:// 拨号中
                tvCallTime.setText("拨号中...");
                ivCallAnime.setImageResource(R.drawable.call_animation_list);
                break;

            case 6:// 通话中 需要计时
                communicateTime();
                isStartTime = true;
                break;

            default:
                break;
        }

    }

    private void communicateTime() {

        if (isStartTime) {
            return;
        }

        ivCallAnime.setImageResource(R.drawable.calling_loading_on);
        startTime();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        RegBroadRecevier(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        // 需要判断是否录音和挂断

        switch (view.getId()) {
            case R.id.btn_call_handsfree: // 免提

                if (isHandsfree) {
                    btnCallHandsfree
                            .setBackgroundResource(R.drawable.select_btn_bt_handsfree_but);
                    isHandsfree = false;

                } else {
                    btnCallHandsfree
                            .setBackgroundResource(R.drawable.select_btn_bt_handsfree_off);
                    isHandsfree = true;
                }

                sendBdcast(BTApi.BT_CMD_TRANSFER, null);

                break;
            case R.id.btn_call_answer:// 接听按钮
                btnCallAnswer.setVisibility(View.GONE);
                btnCallHandsfree.setVisibility(View.VISIBLE);

                sendBdcast(BTApi.BT_CMD_ANSWER, null);
                startTime();
                isStartTime = true;

                break;
            case R.id.btn_call_hang:// 挂断
                // finishSelf(); 等到挂断时蓝牙状态是3，会自动销毁该界面
                // 发送挂断广播
                sendBdcast(BTApi.BT_CMD_HANG, null);
                break;

            default:
                break;
        }

    }

    private void finishSelf() {
        tvCallNumber.setText(R.string.call_end);
        tvCallTime.setVisibility(View.INVISIBLE);
        ivCallAnime.setImageResource(R.drawable.calling_loading_off);

        if (timer != null) {
            timer.cancel(); // 退出计时器
        }
        if (mTask != null) {
            mTask.cancel();
        }
        // 回退到主界面
        EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);

    }

    /**
     * 通话计时
     */
    private void startTime() {

        if (isStartTime) {
            return;
        }

        timer = new Timer(true);
        if (mTask != null) {
            mTask.cancel();
        }
        mTask = new CallTimerTask();
        timer.schedule(mTask, 0, 1000); // 1000ms执行一次

    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mSeconds++;
                    tvCallTime.setText(GetRecTime(mSeconds));
                    break;
            }
        }

    };

    private String GetRecTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
                        + unitFormat(second);
            }
        }
        return timeStr;

    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    // 通话计时
    private class CallTimerTask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

        }

    }

    /**
     * <功能描述> 发送蓝牙广播
     * 
     * @param nCmd 指令
     * @param sData 携带的数据，不是必须的
     * @return void
     */
    private void sendBdcast(int nCmd, String sData) {
        Intent intent = new Intent();
        intent.setAction(BTApi.ACTION_YRC_BT);
        intent.putExtra("cmd", nCmd);

        if (sData != null) {
            intent.putExtra("data", sData);
        }
        mContext.sendBroadcast(intent, null);
    }

}
