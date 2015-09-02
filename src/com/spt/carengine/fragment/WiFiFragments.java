
package com.spt.carengine.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.adapter.WifiPopupWindowAdapter;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.localBroadcastManager.LocalBroadcastManager;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.utils.WifiUtils;
import com.spt.carengine.utils.WifiUtils.WifiCipherType;
import com.spt.carengine.view.DialogBluetooth;
import com.spt.carengine.view.DialogBluetooth.OnCustomDialogListener;
import com.spt.carengine.view.ReturnBarView;
import com.spt.carengine.view.WiFiConnectView;
import com.spt.carengine.view.WiFiMainView;
import com.spt.carengine.wifiap.WifiApConst;
import com.spt.carengine.wifiap.WifiapBroadcast;
import com.spt.carengine.wifiap.WifiapBroadcast.NetWorkChangeListener;

import de.greenrobot.event.EventBus;

public class WiFiFragments extends Fragment {
    private int[] mTitle = {
            R.string.wifi_add_wlan, R.string.wifi_connect_wlan,
            R.string.wifi_advanced_setup,
    };
    private String wifiPassword = null;
    private List<ScanResult> mWifiList;
    private LocalBroadcastManager broadcastManager;
    private WifiapBroadcast broadcast;
    private static final int ALBUM_ITEM_WIDTH = 764;
    private static final int ALBUM_ITEM_HEIGHT = 90;
    private String fmswitch = "0";
    private ListView listView;
    private TextView mTvStatusInfo;
    private CheckBox mCheckBox;
    private WifiManager wifiManager;
    private WifiLock wifiLock;
    private ReturnBarView mReturnBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        wifiManager = (WifiManager) getActivity().getSystemService(
                getActivity().WIFI_SERVICE);
        initBroadcast(); // 注册广播
        View view = inflater.inflate(R.layout.fragment_wifi_main, container,
                false);
        initView(view);
        initEventes();

        if (isWiFiActive(getActivity())) {
            WifiUtils.OpenWifi();
            getWifiList();
            refreshAdapter(mWifiList);
        }
        initAction();
        // utilss = new WifiUtils(getActivity());
        return view;
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(broadcast); // 撤销广播
        mSearchWifiThread.stop();
        mSearchWifiThread = null;
        super.onDestroy();
    }

    /** 初始化全局设置 **/
    private void initEventes() {
        mWifiList = new ArrayList<ScanResult>();
        mHandler = new ApHandler();
        mSearchWifiThread = new SearchWifiThread(mHandler);
        listView.setOnScrollListener(onScroll);
        listView.setOnItemClickListener(onItemClickListener);

    }

    private void initBroadcast() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcast = new WifiapBroadcast(netWorkChangeListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        broadcastManager.registerReceiver(broadcast, filter);
    }

    private NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener() {

        @Override
        public void wifiStatusChange() {
            mHandler.sendEmptyMessage(WifiApConst.NetworkChanged);
        }

        @Override
        public void WifiConnected() {

            mHandler.sendEmptyMessage(WifiApConst.WiFiConnectSuccess);
        }
    };

    /** 初始化控件设置 **/
    protected void initAction() {

        if (!WifiUtils.isWifiConnect() && !WifiUtils.isWifiApEnabled()) { // 无开启热点无连接WIFI
            WifiUtils.OpenWifi();
        }

        if (WifiUtils.isWifiConnect()) { // Wifi已连接
            mTvStatusInfo
                    .setText(getString(R.string.wifiap_text_wifi_connected)
                            + WifiUtils.getSSID());

        }

        if (WifiUtils.isWifiEnabled() && !WifiUtils.isWifiConnect()) { // Wifi已开启，未连接
            mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_1_0));
        }
        mSearchWifiThread.start();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.myCenter_listView);
        mTvStatusInfo = (TextView) view
                .findViewById(R.id.wifi_tv_createap_ssid);
        mCheckBox = (CheckBox) view.findViewById(R.id.ck_clouds_dog_switch);

        mWiFiMainView = (WiFiMainView) view.findViewById(R.id.wifi_main_ly);
        mWiFiConnectView = (WiFiConnectView) view
                .findViewById(R.id.wifi_connect_wlan_ly);
        // mWiFiMainView.setOnItemClickListener(onItemClickListener);
        mWiFiMainView.setOnClickListener(clickListener);

        int itemWidth = ScreenUtils.getRealWidthValue(getActivity(),
                ALBUM_ITEM_WIDTH);
        int itemHeight = ScreenUtils.getRealHeightValue(getActivity(),
                ALBUM_ITEM_HEIGHT);
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                itemWidth, itemHeight);
        ReturnBarView.LayoutParams lp = new ReturnBarView.LayoutParams(
                itemWidth, itemHeight);

        wifiLock = wifiManager.createWifiLock(
                WifiManager.WIFI_MODE_FULL_HIGH_PERF, "lock");
        // 添加wifi锁
        wifiLock.acquire();
        initShowReturnBarView(view);
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_WIFI);
        mReturnBarView.setcheckBoxState(fmswitch.equals("1") ? false : true);
        mReturnBarView.setListener(mOnClickListener);
        mReturnBarView.setBackListener(mOnClickListener);
        mReturnBarView.setOncheckBoxListener(changeListener, clickListener);

    }

    private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {

            if (isChecked) {
                mCheckBox.setChecked(true);
                getWifiList();
                refreshAdapter(mWifiList);
            }

        }

    };
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:

                    EventBus.getDefault().post(
                            Constant.MODULE_TYPE_SYSTEM_SETTINGS);
                    break;

                default:
                    break;
            }
        }

    };
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.ck_clouds_dog_switch:
                    if (((CheckBox) view).isChecked()) {
                        /*
                         * closeWiFi(); listView.removeAllViews();
                         * textView.setVisibility(View.VISIBLE);
                         */
                    } else {
                        /*
                         * openWifi(); textView.setVisibility(View.GONE);
                         * scanWifi();
                         */
                    }

                    break;
                case R.id.wifi_refresh:
                    getWifiList();
                    refreshAdapter(mWifiList);
                    break;
                case R.id.wifi_more:
                    showPopupWindow(view);
                    // showDialogMore(view);
                    break;
                default:
                    break;
            }

        }

    };
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        private View selectedItem;
        String wifiItemSSID = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            ScanResult ap = mWifiList.get(position);
            if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
                mTvStatusInfo.setText(getString(R.string.wifiap_btn_connecting)
                        + ap.SSID);
                // 连接网络
                boolean connFlag = WifiUtils.connectWifi(ap.SSID,
                        WifiApConst.WIFI_AP_PASSWORD,
                        WifiCipherType.WIFICIPHER_WPA);
                if (!connFlag) {
                    mTvStatusInfo
                            .setText(getString(R.string.wifiap_toast_connectap_error_1));

                }
            } else if (!WifiUtils.isWifiConnect()
                    || !ap.BSSID.equals(WifiUtils.getBSSID())) {
                showDialogBluetooth(position, view);

            }

        }

    };

    private void showDialogBluetooth(final int position, View view) {
        try {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            final Dialog dialog = new Dialog(getActivity(),
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View view1 = inflater.inflate(R.layout.dialog_bluetooth, null);
            dialog.addContentView(view1, lp);
            DialogBluetooth bluetooth = (DialogBluetooth) dialog
                    .findViewById(R.id.dialog_blue);
            encodTv = (TextView) dialog.findViewById(R.id.et_encod);

            bluetooth.setOnDialogListener(new OnCustomDialogListener() {

                @Override
                public void onCloseDialog() {
                    dialog.dismiss();

                }

                @Override
                public void back(String str) {

                    /*
                     * ScanResult ap = mWifiList.get(position); WifiCipherType
                     * type = null; String capString = ap.capabilities; if
                     * (capString.toUpperCase().contains("WPA")) { type =
                     * WifiCipherType.WIFICIPHER_WPA; } else if
                     * (capString.toUpperCase().contains("WEP")) { type =
                     * WifiCipherType.WIFICIPHER_WEP; } else { type =
                     * WifiCipherType.WIFICIPHER_NOPASS; } // 连接网络 boolean
                     * connFlag = WifiUtils .connectWifi(ap.SSID, str, type); if
                     * (connFlag) { encodTv.setText(""); dialog.cancel(); } else
                     * {
                     * mHandler.sendEmptyMessage(WifiApConst.WiFiConnectError);
                     * }
                     */
                    Toast.makeText(getActivity(), str.toString(), 0).show();
                    dialog.show();
                }

            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showDialogMore() {
        try {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            final Dialog dialog = new Dialog(getActivity(),
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            dialog.setCanceledOnTouchOutside(true);
            View view1 = inflater.inflate(R.layout.wifi_more_item, null);
            dialog.addContentView(view1, lp);
            DialogBluetooth dlg = (DialogBluetooth) dialog
                    .findViewById(R.id.dialog_more);

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.wifi_more_popuwindow, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        ListView sListView = (ListView) contentView
                .findViewById(R.id.wifi_popupwindow_listView);
        WifiPopupWindowAdapter sAdapter = new WifiPopupWindowAdapter(
                getActivity(), mTitle);
        sListView.setDivider(null);
        sListView.setAdapter(sAdapter);
        sListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                    case 0:

                        Toast.makeText(getActivity(), R.string.wifi_close, 0)
                                .show();
                        break;
                    case 1:
                        mWiFiMainView.setVisibility(View.GONE);
                        mWiFiConnectView.setVisibility(View.VISIBLE);
                        // mWiFiConnectView.showPhotos(info);
                        break;
                    case 2:

                        Toast.makeText(getActivity(), R.string.wifi_close, 0)
                                .show();
                        break;

                    default:
                        break;
                }
            }
        });
        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return false;
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.wifi_more_bg));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight = contentView.getMeasuredHeight() * mTitle.length;
        int[] location = new int[2];
        // 设置好参数之后再show
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                (location[0] - view.getWidth()), location[1] - popupHeight);

    }

    // 只要在 系统
    // 设置中开启了wifi，isWifiEnable()接口就一定会返回true。想要判断当前wifi网络是否可用，可以使用本文的第二种方法才能判断。
    private SearchWifiThread mSearchWifiThread;

    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class ApHandler extends Handler {

        private boolean isRespond = true;

        public ApHandler() {
        }

        public void setRespondFlag(boolean flag) {
            isRespond = flag;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case WifiApConst.ApScanResult: // 扫描Wifi列表
                    if (isRespond) {
                        getWifiList();
                        refreshAdapter(mWifiList);
                    }
                    break;
                case WifiApConst.WiFiConnectSuccess: // 连接热点成功
                    String str = getString(R.string.wifiap_text_wifi_connected)
                            + WifiUtils.getSSID();
                    mTvStatusInfo.setText(str);
                    Toast.makeText(getActivity(), str, 0).show();
                    break;

                case WifiApConst.WiFiConnectError: // 连接热点错误
                    Toast.makeText(getActivity(),
                            R.string.wifiap_toast_connectap_error, 0).show();
                    break;

                case WifiApConst.NetworkChanged: // Wifi状态变化
                    if (WifiUtils.isWifiEnabled()) {
                        mTvStatusInfo
                                .setText(getString(R.string.wifiap_text_wifi_1_0));
                    } else {
                        mTvStatusInfo
                                .setText(getString(R.string.wifiap_text_wifi_0));
                        Toast.makeText(getActivity(),
                                R.string.wifiap_text_wifi_disconnect, 0).show();
                    }

                default:
                    break;
            }
        }
    }

    class SearchWifiThread implements Runnable {
        private boolean running = false;
        private Thread thread = null;
        private Handler handler = null;

        SearchWifiThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            while (!WifiUtils.isWifiApEnabled()) {
                if (!this.running)
                    return;
                try {
                    Thread.sleep(2000); // 扫描间隔
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(WifiApConst.ApScanResult);
            }
        }

        public void start() {
            try {
                this.thread = new Thread(this);
                this.running = true;
                this.thread.start();
            } finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
            } finally {
            }
        }
    }

    /**
     * 刷新热点列表UI
     * 
     * @param list
     */
    public void refreshAdapter(List<ScanResult> list) {
        // mWifiApAdapter.setData(list);
        // mWifiApAdapter.notifyDataSetChanged();
    }

    private void getWifiList() {
        mWifiList.clear();
        WifiUtils.startScan();
        scanResults = WifiUtils.getScanResults();
        mWifiList.addAll(scanResults);
    }

    private OnScrollListener onScroll = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    mHandler.setRespondFlag(true);
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    mHandler.setRespondFlag(false); // 滚动时不刷新列表
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    mHandler.setRespondFlag(false); // 滚动时不刷新列表
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

        }
    };

    private ApHandler mHandler;
    private WiFiConnectView mWiFiConnectView;
    private WiFiMainView mWiFiMainView;
    private List<ScanResult> scanResults;
    private TextView encodTv;
    private TextView okTv;
}
