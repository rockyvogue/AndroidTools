
package com.spt.carengine.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.spt.carengine.adapter.WifiapAdapter;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.utils.WifiUtils;
import com.spt.carengine.utils.WifiUtils.WifiCipherType;
import com.spt.carengine.view.DialogBluetooth.OnCustomDialogListener;
import com.spt.carengine.view.DialogView.OnDialogListener;
import com.spt.carengine.wifiap.WifiApConst;

import de.greenrobot.event.EventBus;

public class WiFiMainView extends PercentRelativeLayout {
    private List<ScanResult> mWifiList;
    private ListView listView;
    private TextView mTvStatusInfo;
    private CheckBox mCheckBox;
    private ReturnBarView mReturnBarView;
    private int[] mTitle = {
            R.string.wifi_add_wlan, R.string.wifi_connect_wlan,
            R.string.wifi_advanced_setup,
    };

    public WiFiMainView(Context context) {
        super(context);
    }

    public WiFiMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WiFiMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listView = (ListView) findViewById(R.id.wifi_listView);
        mLlApInfo = (LinearLayout) findViewById(R.id.wifi_lv_create_ok);
        mTvStatusInfo = (TextView) findViewById(R.id.wifi_tv_createap_ssid);
        mCheckBox = (CheckBox) findViewById(R.id.ck_clouds_dog_switch);
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);

        if (listView == null || mLlApInfo == null || mTvStatusInfo == null
                || mCheckBox == null || mReturnBarView == null) {
            throw new InflateException("Miss a child?");
        }

        listView.setDivider(null);
        mReturnBarView.init(ReturnBarView.TYPE_WIFI);
        // mReturnBarView.setcheckBoxState(fmswitch.equals("1") ? false : true);
        mReturnBarView.setOncheckBoxListener(changeListener, mOnClickListener);
        mReturnBarView.setListener(mOnClickListener);
        mReturnBarView.setBackListener(mOnClickListener);
        mHandler = new ApHandler();
        mSearchWifiThread = new SearchWifiThread(mHandler);
        if (isWiFiActive(getContext())) {
            mCheckBox.setChecked(true);
            init();
            initAction();
        } else {
            mCheckBox.setChecked(false);
            listView.setVisibility(View.GONE);
            mLlApInfo.setVisibility(View.VISIBLE);
            mTvStatusInfo.setText(R.string.wifiap_text_wifi_0);
            return;
        }

    }

    /** 初始化控件设置 **/
    protected void initAction() {

        if (!WifiUtils.isWifiConnect() && !WifiUtils.isWifiApEnabled()) { // 无开启热点无连接WIFI
            mCheckBox.setChecked(false);
            mLlApInfo.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            mTvStatusInfo.setText(R.string.wifiap_text_wifi_0);
        }
        if (WifiUtils.isWifiConnect()) { // 已开启热点
            WifiUtils.OpenWifi();
            listView.setVisibility(View.VISIBLE);
        } else {
            WifiUtils.OpenWifi();
            mTvStatusInfo.setText(R.string.wifiap_text_wifi_1_0);
        }

        if (WifiUtils.isWifiEnabled() && !WifiUtils.isWifiConnect()) { // Wifi已开启，未连接
            mTvStatusInfo.setText(R.string.wifiap_text_wifi_1_0);
        }
        mSearchWifiThread.start();
    }

    private void init() {
        mWifiList = new ArrayList<ScanResult>();

        mWifiApAdapter = new WifiapAdapter(getContext(), mWifiList);
        listView.setAdapter(mWifiApAdapter);
        // mWifiApAdapter.notifyDataSetChanged();
        listView.setOnScrollListener(onScroll);
        listView.setOnItemLongClickListener(longClickListener);
        listView.setOnItemClickListener(onItemClickListener);
        /*
         * int wifiState = WifiUtils.checkState(); if (wifiState ==
         * WifiManager.WIFI_STATE_DISABLED || wifiState ==
         * WifiManager.WIFI_STATE_DISABLING || wifiState ==
         * WifiManager.WIFI_STATE_UNKNOWN) { mCheckBox.setChecked(false); } else
         * { mCheckBox.setChecked(true); }
         */
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        private View selectedItem;
        String wifiItemSSID = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                long id) {

            ScanResult ap = mWifiList.get(pos);
            if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
                // mTvStatusInfo.setText(R.string.wifiap_btn_connecting +
                // ap.SSID);
                // 连接网络
                boolean connFlag = WifiUtils.connectWifi(ap.SSID,
                        WifiApConst.WIFI_AP_PASSWORD,
                        WifiCipherType.WIFICIPHER_WPA);
                if (!connFlag) {
                    mTvStatusInfo
                            .setText(R.string.wifiap_toast_connectap_error_1);
                    mHandler.sendEmptyMessage(WifiApConst.WiFiConnectError);
                }
            } else if (!WifiUtils.isWifiConnect()
                    || !ap.BSSID.equals(WifiUtils.getBSSID())) {
                showDialogBluetooth(pos, view);
            }
        }
    };
    private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view,
                int position, long id) {

            ScanResult ap = mWifiList.get(position);
            if (WifiUtils.isConnect(ap)) {
                showCancleDialog(position, ap);
            } else {
                showDialogBluetooth(position, view);
            }

            return true;
        }
    };
    private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
            if (isChecked) {
                WifiUtils.OpenWifi();
                mCheckBox.setChecked(true);
                mHandler.sendEmptyMessage(WifiApConst.WiFiConnectSuccess);
            } else {
                WifiUtils.closeWifi();
                mCheckBox.setChecked(false);
                mHandler.sendEmptyMessage(WifiApConst.WiFiConnectError);
                mSearchWifiThread.stop();
            }

        }
    };
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.wifi_refresh:
                    if (!WifiUtils.isWifiConnect()
                            && !WifiUtils.isWifiApEnabled()) {
                        return;
                    } else {
                        getWifiList();
                        refreshAdapter(mWifiList);
                    }

                    break;
                case R.id.wifi_more:
                    showPopupWindow(view); // showDialogMore(view); break;

                case R.id.back:
                    EventBus.getDefault().post(
                            Constant.MODULE_TYPE_SYSTEM_SETTINGS);
                    break;
                default:
                    break;
            }
        }
    };

    private void showCancleDialog(final int arg2, ScanResult ap) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final Dialog dialog = new Dialog(getContext(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_cancel_save_net, null);
        dialog.addContentView(view, lp);
        DialogView dialogView = (DialogView) dialog
                .findViewById(R.id.dialog_cancel_save);
        TextView titleTv = (TextView) dialogView.findViewById(R.id.message);
        titleTv.setText(ap.SSID);
        dialogView.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onExecutive() {
                WifiUtils.disconnectWifi(arg2);
                mWifiApAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.wifi_more_popuwindow, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        sListView = (ListView) contentView
                .findViewById(R.id.wifi_popupwindow_listView);
        WifiPopupWindowAdapter sAdapter = new WifiPopupWindowAdapter(
                getContext(), mTitle);
        sListView.setDivider(null);
        sListView.setAdapter(sAdapter);
        sListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                    case 0:

                        Toast.makeText(getContext(), R.string.wifi_close, 0)
                                .show();
                        break;
                    case 1:
                        // mWiFiMainView.setVisibility(View.GONE);
                        // mWiFiConnectView.setVisibility(View.VISIBLE);
                        // mWiFiConnectView.showPhotos(info);
                        break;
                    case 2:

                        Toast.makeText(getContext(), R.string.wifi_close, 0)
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

    private SearchWifiThread mSearchWifiThread;
    private ApHandler mHandler;

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
                case WifiApConst.WiFiConnectSuccess:
                    mLlApInfo.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    init();
                    mSearchWifiThread.start();
                    break;

                case WifiApConst.WiFiConnectError:

                    listView.setVisibility(View.GONE);
                    mLlApInfo.setVisibility(View.VISIBLE);
                    mWifiList.clear();
                    mTvStatusInfo.setText(R.string.wifiap_text_wifi_0);
                    break;

                case WifiApConst.NetworkChanged: // Wifi状态变化
                    if (WifiUtils.isWifiEnabled()) {
                        mTvStatusInfo.setText(R.string.wifiap_text_wifi_1_0);
                    } else {

                        mTvStatusInfo.setText(R.string.wifiap_text_wifi_0);
                        Toast.makeText(getContext(),
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

                    Thread.sleep(3500); // 扫描间隔
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
        mWifiApAdapter.setData(list);
        mWifiApAdapter.notifyDataSetChanged();
    }

    private void getWifiList() {
        mWifiList.clear();
        WifiUtils.startScan();
        List<ScanResult> scanResults = WifiUtils.getScanResults();
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

    private ListView sListView;
    private WifiManager wifiManager;
    private WifiLock wifiLock;
    private LinearLayout mLlApInfo;
    private String wifiPassword = null;
    private WifiapAdapter mWifiApAdapter;

    public void setOnClickListener(OnClickListener l, int tag) {
        mReturnBarView.setOnClickListener(l);
    }

    private void showDialogBluetooth(final int position, View view) {
        try {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            final Dialog dialog = new Dialog(getContext(),
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view1 = inflater.inflate(R.layout.dialog_bluetooth, null);
            dialog.addContentView(view1, lp);
            DialogBluetooth bluetooth = (DialogBluetooth) dialog
                    .findViewById(R.id.dialog_blue);
            bluetooth.setOnDialogListener(new OnCustomDialogListener() {

                @Override
                public void onCloseDialog() {
                    dialog.dismiss();

                }

                @Override
                public void back(String str) {
                    if (TextUtils.isEmpty(str)) {

                        return;
                    } else {

                        ScanResult ap = mWifiList.get(position);
                        WifiCipherType type = null;
                        String capString = ap.capabilities;
                        if (capString.toUpperCase().contains("WPA")) {
                            type = WifiCipherType.WIFICIPHER_WPA;
                        } else if (capString.toUpperCase().contains("WEP")) {
                            type = WifiCipherType.WIFICIPHER_WEP;
                        } else {
                            type = WifiCipherType.WIFICIPHER_NOPASS;
                        }

                        // 连接网络
                        boolean connFlag = WifiUtils.connectWifi(ap.SSID, str,
                                type);
                        if (connFlag) {
                            dialog.cancel();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    R.string.wifiap_toast_connectap_error
                                            + "++++", 0).show();
                            dialog.dismiss();
                        }
                        Toast.makeText(getContext(), str.toString() + "++++", 0)
                                .show();
                        dialog.dismiss();

                    }

                }

            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
