package com.spt.carengine.fragment;

/*
 * 文 件 名:  MainContentFragment.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  徐浩
 */
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.adapter.WiFiAdapter;
import com.spt.carengine.localBroadcastManager.LocalBroadcastManager;
import com.spt.carengine.view.ReturnBarView;
import com.spt.carengine.view.WiFiConnectView;
import com.spt.carengine.view.WiFiMainView;
import com.spt.carengine.wifiap.WifiapBroadcast;
import com.spt.carengine.wifiap.WifiapBroadcast.NetWorkChangeListener;

/**
 * WIFI
 * 
 * @author 徐浩
 */
public class WIFIFragment extends Fragment {
	private WifiManager wifiManager;
	private WifiLock wifiLock;
	private TextView centerTitle;
	private ListView listView;
	private WiFiAdapter adapter;
	private ReturnBarView mReturnBarView;
	private CheckBox mCheckBox;

	private ProgressDialog dialog;

	private TextView mTvStatusInfo;
	private WifiapBroadcast broadcast;
	private LocalBroadcastManager broadcastManager;
	private WiFiMainView mWiFiMainView;
	private WiFiConnectView mWiFiConnectView;
	private View view;
	private WifiapBroadcast mWifiapBroadcast;
	private List<ScanResult> mWifiList;
	private static final int ALBUM_ITEM_WIDTH = 764;
	private static final int ALBUM_ITEM_HEIGHT = 90;
	private String fmswitch = "0";
	private int[] mTitle = { R.string.wifi_add_wlan,
			R.string.wifi_connect_wlan, R.string.wifi_advanced_setup, };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		wifiManager = (WifiManager) getActivity().getSystemService(
				getActivity().WIFI_SERVICE);
		initBroadcast();
		view = inflater.inflate(R.layout.fragment_wifi_main, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mWiFiMainView = (WiFiMainView) view.findViewById(R.id.wifi_main_ly);
		// mWiFiMainView.setOnClickListener(l, tag);

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

	@Override
	public void onDestroy() {
		broadcastManager.unregisterReceiver(mWifiapBroadcast); // 撤销广播
		super.onDestroy();
	}

	private NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener() {

		@Override
		public void wifiStatusChange() {

		}

		@Override
		public void WifiConnected() {
		}
	};

	// 只要在 系统
	// 设置中开启了wifi，isWifiEnable()接口就一定会返回true。想要判断当前wifi网络是否可用，可以使用本文的第二种方法才能判断。

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

}
