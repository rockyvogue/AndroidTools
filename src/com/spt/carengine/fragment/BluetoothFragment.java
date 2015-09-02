
package com.spt.carengine.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.adapter.BluetoothAdapter;
import com.spt.carengine.adapter.BluetoothAdapter.OnListViewOnItem;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.db.Provider;
import com.spt.carengine.view.DialogView;
import com.spt.carengine.view.DialogView.OnDialogListener;
import com.spt.carengine.view.ReturnBarView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/** 蓝牙界面 **/
@SuppressLint("InflateParams")
public class BluetoothFragment extends Fragment implements OnClickListener,
        OnListViewOnItem {
    private ListView myCenter_listView;
    private BluetoothAdapter mBlAdpter;
    private TextView mTvUpdate;
    private EditText mEtencoding;
    private ReturnBarView mReturnBarView;
    private List<String[]> mListBlue;
    private boolean mFlag;
    /*** 连接上的蓝牙设备数据 ***/
    private String mStrDeviceConnect[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListBlue = new ArrayList<String[]>();
        getConectDevice();
        queryBuleToothDevices(mStrDeviceConnect);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container,
                false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEtencoding = (EditText) view.findViewById(R.id.et_encoding);
        mTvUpdate = (TextView) view.findViewById(R.id.tv_update);
        mTvUpdate.setOnClickListener(this);
        myCenter_listView = (ListView) view
                .findViewById(R.id.myCenter_listView);
        mBlAdpter = new BluetoothAdapter(getActivity(), mListBlue, mFlag);
        mBlAdpter.setOnListViewOnItem(this);
        myCenter_listView.setDivider(null);
        myCenter_listView.setAdapter(mBlAdpter);
        initShowReturnBarView(view);
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_BLUETOOTH);
        mReturnBarView.setBackListener(this);

    }

    /***
     * 查询本机所有 连接过的蓝牙设备
     */
    private void queryBuleToothDevices(String str[]) {

        String[] stringArray = new String[] {
                Provider.Bluetooth.DEVICEID, Provider.Bluetooth.BDADDR,
                Provider.Bluetooth.DEVNAME
        };
        Cursor c = getActivity().getContentResolver().query(
                Provider.Bluetooth.CONTENT_URI, stringArray, null, null, null);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String deviceid = c.getColumnName(c
                        .getColumnIndex(stringArray[0]));
                String bdaddr = c.getColumnName(c
                        .getColumnIndex(stringArray[1]));
                String devname = c.getColumnName(c
                        .getColumnIndex(stringArray[2]));
                String[] strArray = new String[3];
                if (null != deviceid && !"".equals(deviceid)) {
                    strArray[0] = deviceid;
                }
                if (null != bdaddr && !"".equals(bdaddr)) {
                    strArray[1] = bdaddr;
                }
                if (null != devname && !"".equals(devname)) {
                    strArray[2] = devname;
                }
                if (mFlag) {
                    if (!mStrDeviceConnect[0].equals(deviceid)) {
                        mListBlue.add(strArray);
                    }

                } else {
                    mListBlue.add(strArray);
                }

            }
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
                break;
            case R.id.tv_update:// 修改本机匹配码

                break;

            default:
                break;
        }

    }

    private void showSaveEcond(final String str) {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final Dialog dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_album_delete, null);
        dialog.addContentView(view, lp);
        DialogView dialogView = (DialogView) dialog
                .findViewById(R.id.album_delete_dialog);
        dialogView.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onExecutive() {

                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        dialogView.setMessage(getString(R.string.tv_save_econd));
        dialog.show();
    }

    /***
     * 获取连接上 的蓝牙设备
     */
    public void getConectDevice() {

        if (mStrDeviceConnect != null) {
            if (null != mStrDeviceConnect[0] && null != mStrDeviceConnect[1]
                    && null != mStrDeviceConnect[2]) {
                mListBlue.add(mStrDeviceConnect);
                mFlag = true;
            }

        }

    }

    @Override
    public void setListViewOnItemLisenter(int position) {// 断开蓝牙连接

        if (mBlAdpter != null) {
            mFlag = false;
            mBlAdpter.notifyDataSetChanged();

        }

    }
}
