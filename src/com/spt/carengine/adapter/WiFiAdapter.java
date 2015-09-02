
package com.spt.carengine.adapter;

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

import java.util.List;

public class WiFiAdapter extends BaseAdapter {
    private static final int ALBUM_ITEM_HEIGHT = 80;
    private LayoutInflater mInflater;
    private List<ScanResult> list;

    public WiFiAdapter(Context context, List<ScanResult> list) {

        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanResult scanResult = list.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wifi_list_item, parent,
                    false);
            viewHolder = new ViewHolder();

            viewHolder.titleName = (TextView) convertView
                    .findViewById(R.id.wifi_name);
            viewHolder.status = (TextView) convertView
                    .findViewById(R.id.wifi_status);
            viewHolder.imegView = (ImageView) convertView
                    .findViewById(R.id.wifi_sigmal);

            viewHolder.view = convertView
                    .findViewById(R.id.wifi_underline_view);
            int itemHeight = ScreenUtils.getRealHeightValue(
                    parent.getContext(), ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.titleName.setText(scanResult.SSID);
    //    viewHolder.status.setText(getDesc(scanResult));
        // 判断信号强度，显示对应的指示图标
        if (Math.abs(scanResult.level) > 90) {
            viewHolder.imegView.setImageResource(R.drawable.state_wifi_icon_4);
        } else if (Math.abs(scanResult.level) > 70) {
            viewHolder.imegView.setImageResource(R.drawable.state_wifi_icon_3);
        } else if (Math.abs(scanResult.level) > 60) {
            viewHolder.imegView.setImageResource(R.drawable.state_wifi_icon_2);
        } else if (Math.abs(scanResult.level) > 50) {
            viewHolder.imegView.setImageResource(R.drawable.state_wifi_icon_1);
        } else {
            viewHolder.imegView.setImageResource(R.drawable.state_wifi_icon_1);
        }
        return convertView;

    }
   /* private String getDesc(ScanResult ap) {
        String desc = "";
        if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
            desc = "专用网络，可以直接连接";
        }
        else {
            String descOri = ap.capabilities;
            if (descOri.toUpperCase().contains("WPA-PSK")
                    || descOri.toUpperCase().contains("WPA2-PSK")) {
                desc = "受到密码保护";
            }
            else {
                desc = "未受保护的网络";
            }
        }

        // 是否连接此热点
        if (isWifiConnected) {
            desc = "已连接";
        }
        return desc;
    }*/
    private static class ViewHolder {
        TextView titleName;
        TextView status;
        ImageView imegView;
        View view;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
