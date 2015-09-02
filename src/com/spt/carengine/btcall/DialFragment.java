
package com.spt.carengine.btcall;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.define.BTApi;

public class DialFragment extends Fragment implements OnClickListener,
        View.OnLongClickListener {

    private View fragmentView = null;
    private String sCallNumber = "";
    private TextView mNumberView = null;
    private ICountService iCountService = null;
    /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/
    private int nBtState = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_bt_dial, container,
                false);
        initdata();
        initeview();
        return fragmentView;
    }

    private void initdata() {
        // TODO Auto-generated method stub
        // Intent intent = new Intent("com.yrc.bt.server");
        // getActivity().bindService(intent, serConn,
        // getActivity().getApplicationContext().BIND_AUTO_CREATE);
        // nBtState = iCountService.getBTState();
        RegBroadRecevier(true);
    }

    private void initeview() {
        // TODO Auto-generated method stub
        LinearLayout btn_num0 = (LinearLayout) fragmentView
                .findViewById(R.id.num_0);
        Button btn_num1 = (Button) fragmentView.findViewById(R.id.num_1);
        Button btn_num2 = (Button) fragmentView.findViewById(R.id.num_2);
        Button btn_num3 = (Button) fragmentView.findViewById(R.id.num_3);
        Button btn_num4 = (Button) fragmentView.findViewById(R.id.num_4);
        Button btn_num5 = (Button) fragmentView.findViewById(R.id.num_5);
        Button btn_num6 = (Button) fragmentView.findViewById(R.id.num_6);
        Button btn_num7 = (Button) fragmentView.findViewById(R.id.num_7);
        Button btn_num8 = (Button) fragmentView.findViewById(R.id.num_8);
        Button btn_num9 = (Button) fragmentView.findViewById(R.id.num_9);
        Button btn_xin = (Button) fragmentView.findViewById(R.id.num_xin);
        Button btn_jin = (Button) fragmentView.findViewById(R.id.num_jin);
        LinearLayout btn_dial = (LinearLayout) fragmentView
                .findViewById(R.id.btn_bt_dial);
        RelativeLayout btn_delete = (RelativeLayout) fragmentView
                .findViewById(R.id.btn_bt_delete);
        mNumberView = (TextView) fragmentView.findViewById(R.id.tv_call_num);

        btn_num0.setOnClickListener(this);
        btn_num1.setOnClickListener(this);
        btn_num2.setOnClickListener(this);
        btn_num3.setOnClickListener(this);
        btn_num4.setOnClickListener(this);
        btn_num5.setOnClickListener(this);
        btn_num6.setOnClickListener(this);
        btn_num7.setOnClickListener(this);
        btn_num8.setOnClickListener(this);
        btn_num9.setOnClickListener(this);
        btn_xin.setOnClickListener(this);
        btn_jin.setOnClickListener(this);
        btn_num0.setOnLongClickListener(this);
        btn_dial.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    public void RegBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BTApi.ACTION_YRC_BD);// BD

            getActivity().getApplicationContext().registerReceiver(btReceiver,
                    filter);
        } else {
            getActivity().getApplicationContext().unregisterReceiver(btReceiver);
        }

    }

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent == null)
                return;

            String sAction = intent.getAction();
            if (sAction == null)
                return;
            if (sAction.equals(BTApi.ACTION_YRC_BD)) {
                int nCmd = intent.getIntExtra("cmd", -1);
                if (nCmd == -1)
                    return;
                nBtState = intent.getIntExtra("btstate", -1);

            }
        }

    };

    @Override
    public boolean onLongClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.num_0:// 按键1
            {
                showCallNumber("+");
                return true;
            }
            case R.id.btn_bt_delete: {

                if (mNumberView == null) {
                    return false;
                }
                sCallNumber = "";
                mNumberView.setText(sCallNumber);
            }
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.num_0:// 按键1
            {
                showCallNumber("0");
            }
                break;
            case R.id.num_1:// 按键1
            {
                showCallNumber("1");
            }
                break;
            case R.id.num_2:// 按键1
            {
                showCallNumber("2");
            }
                break;
            case R.id.num_3:// 按键1
            {
                showCallNumber("3");
            }
                break;
            case R.id.num_4:// 按键1
            {
                showCallNumber("4");
            }
                break;
            case R.id.num_5:// 按键1
            {
                showCallNumber("5");
            }
                break;
            case R.id.num_6:// 按键1
            {
                showCallNumber("6");
            }
                break;
            case R.id.num_7:// 按键1
            {
                showCallNumber("7");
            }
                break;
            case R.id.num_8:// 按键1
            {
                showCallNumber("8");
            }
                break;
            case R.id.num_9:// 按键1
            {
                showCallNumber("9");
            }
                break;
            case R.id.num_xin:// 按键1
            {
                showCallNumber("*");
            }
                break;
            case R.id.num_jin:// 按键1
            {
                showCallNumber("#");
            }
                break;
            case R.id.btn_bt_dial: {
                if (nBtState < 3) {
                    Toast.makeText(getActivity(), R.string.bt_connect,
                            Toast.LENGTH_SHORT).show();
                } else if (sCallNumber.length() < 1) {
                    Toast.makeText(getActivity(), R.string.number_not,
                            Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent();
                    intent.setAction(BTApi.ACTION_YRC_BT);
                    intent.putExtra("cmd", BTApi.BT_CMD_DIAL);
                    intent.putExtra("tel", sCallNumber);
                    getActivity().sendOrderedBroadcast(intent, null);
                }

            }
                break;

            case R.id.btn_bt_delete: {
                if (!sCallNumber.isEmpty()) {
                    sCallNumber = sCallNumber.substring(0,
                            sCallNumber.length() - 1);
                    if (mNumberView == null)
                        return;
                    mNumberView.setText(sCallNumber);
                }
            }
                break;
            default:
                break;
        }
    }

    /**
     * 号码显示
     * 
     * @param sNum 数字
     */
    private void showCallNumber(String sNum) {
        if (sNum.isEmpty())
            return;
        if (sCallNumber.length() > 20)// 电话号码长度过长，错误电话
            return;
        sCallNumber = String.format("%s%s", sCallNumber, sNum);

        if (mNumberView == null)
            return;
        mNumberView.setText(sCallNumber);
    }

    @Override
    public void onDestroy() {
        RegBroadRecevier(false);
        super.onDestroy();
    }

    private ServiceConnection serConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            iCountService = (ICountService) service;
            Toast.makeText(getActivity(), "绑定成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            iCountService = null;
            Toast.makeText(getActivity(), "绑定失败", Toast.LENGTH_SHORT).show();
        }
    };

}
