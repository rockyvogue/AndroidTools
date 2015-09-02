
package com.spt.carengine.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.utils.WifiUtils;
import com.spt.carengine.wifiap.WifiApConst;
import com.squareup.picasso.Picasso;

public class WifiapAdapter extends BaseAdapter {
    private static final int ALBUM_ITEM_HEIGHT = 80;
    private LayoutInflater mInflater;
    private List<ScanResult> mDatas;
    private Context mContext;
    private boolean isWifiConnected;

    public WifiapAdapter(Context context, List<ScanResult> list) {
        super();
        this.mDatas = list;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.isWifiConnected = false;
    }

    // 新加的一个函数，用来更新数据
    public void setData(List<ScanResult> list) {
        this.mDatas = list;
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ScanResult ap = mDatas.get(position);
        ViewHolder viewHolder = null;
        isWifiConnected = false;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wifi_list_item,
                    null);
            viewHolder = new ViewHolder();

            viewHolder.ssid = (TextView) convertView
                    .findViewById(R.id.wifi_name);
            viewHolder.desc = (TextView) convertView
                    .findViewById(R.id.wifi_status);
            viewHolder.rssi = (ImageView) convertView
                    .findViewById(R.id.wifi_sigmal);

            int itemHeight = ScreenUtils.getRealHeightValue(
                    parent.getContext(), ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        if (WifiUtils.isWifiConnect() && ap.BSSID.equals(WifiUtils.getBSSID())) {
            isWifiConnected = true;
            
        }

        viewHolder.ssid.setText(ap.SSID);
        viewHolder.desc.setText(getDesc(ap));
        Picasso.with(mContext).load(getRssiImgId(ap)).into(viewHolder.rssi);
        return convertView;
    }

    private String getDesc(ScanResult ap) {
        String desc = "";
        if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
            desc = mContext.getString(R.string.wifi_ap_ssid);
        } else {
            String descOri = ap.capabilities;
            if (descOri.toUpperCase().contains("WPA-PSK")
                    || descOri.toUpperCase().contains("WPA2-PSK")) {
                desc = mContext.getString(R.string.wifi_ap_wap_psk);
            } else {
                desc = mContext.getString(R.string.wifi_ap_wap2_psk);
            }
        }
        // 是否连接此热点
        if (isWifiConnected) {
            desc = mContext.getString(R.string.wifi_ap_true);
        }
        return desc;
    }
 
    public static final int DESC_RSSI1 = 100;
    public static final int DESC_RSSI2 = 80;
    public static final int DESC_RSSI3 = 70;
    public static final int DESC_RSSI4 = 60;
    private int getRssiImgId(ScanResult ap) {
        int imgId;
        if (isWifiConnected) {
            imgId = R.drawable.clouddog_icon_selected;
        } else {
            int rssi = Math.abs(ap.level);
            if (rssi > DESC_RSSI1) {
                imgId = R.drawable.wifi_signal_icon1;
            } else if (rssi > DESC_RSSI2) {
                imgId = R.drawable.wifi_signal_icon1;
            } else if (rssi > DESC_RSSI3) {
                imgId = R.drawable.wifi_signal_icon2;
            } else if (rssi > DESC_RSSI4) {
                imgId = R.drawable.wifi_signal_icon3;
            } else {
                imgId = R.drawable.wifi_signal_icon4;
            }
        }
        return imgId;
    }

    public static class ViewHolder {
        public ImageView rssi;
        public TextView ssid;
        public TextView desc;
    }
}
