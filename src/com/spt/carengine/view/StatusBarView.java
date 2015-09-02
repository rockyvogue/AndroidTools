/*
 * 文 件 名:  StatusBarView.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月6日
 */

package com.spt.carengine.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.view.DropDownMenuView.OnHideListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 状态栏
 * 
 * @author Heaven
 */
public class StatusBarView extends PercentLinearLayout {
    private static final String TAG = "StatusBarView";
    private TextView mTime;
    private ImageView mSigner;
    private ImageView mGPS;
    private ImageView mBT;
    private ImageView mWIFI;
    private GestureDetector mGesture; // 手势监听
    private Dialog mDialog;

    public StatusBarView(Context context) {
        super(context, null);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTime = (TextView) findViewById(R.id.status_time);
        mSigner = (ImageView) findViewById(R.id.status_signer);
        mGPS = (ImageView) findViewById(R.id.status_gps);
        mBT = (ImageView) findViewById(R.id.status_bluetooth);
        mWIFI = (ImageView) findViewById(R.id.status_wifi);
        if (mTime == null || mSigner == null || mGPS == null || mBT == null
                || mWIFI == null) {
            throw new InflateException("Miss a child?");
        }
        showTime();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropDownMenu();
            }
        });
        mGesture = new GestureDetector(getContext(),
                new MyGestureDetectorListener());
    }

    public void showTime() {
        mTime.setText(getTime());
    }

    private String getTime() {
        Date date = new Date();
        GregorianCalendar ca = new GregorianCalendar();
        int flag = ca.get(GregorianCalendar.AM_PM);
        String pmOrAm = "";
        if (flag == 0) {
            pmOrAm = getContext().getString(R.string.status_time_am);
        } else {
            pmOrAm = getContext().getString(R.string.status_time_pm);
        }
        SimpleDateFormat format = new SimpleDateFormat("hh:mm",
                Locale.getDefault());
        return pmOrAm + format.format(date);
    }

    public void showSigner(int level) {
        switch (level) {
            case 0:
                mSigner.setImageResource(R.drawable.state_signal_icon_0);
                break;
            case 1:
                mSigner.setImageResource(R.drawable.state_signal_icon_1);
                break;
            case 2:
                mSigner.setImageResource(R.drawable.state_signal_icon_2);
                break;
            case 3:
                mSigner.setImageResource(R.drawable.state_signal_icon_3);
                break;
            default:
                mSigner.setImageResource(R.drawable.state_signal_icon_4);
                break;
        }
    }

    public void showGpsStatus(boolean on) {
        if (on) {
            mGPS.setImageResource(R.drawable.state_gps_on_icon);
        } else {
            mGPS.setImageResource(R.drawable.state_gps_off_icon);
        }
    }

    /**
     * wifi开和关的监听
     * 
     * @param intent
     */
    public void showWifiOnOff(Intent intent) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            mWIFI.setImageResource(R.drawable.state_wifi_icon_1);
            mWIFI.setVisibility(View.VISIBLE);
        } else {
            mWIFI.setVisibility(View.GONE);
        }
    }

    /**
     * wifi信号强度显示
     */
    public void showWifiSignalLevel(Intent intent) {
        Parcelable parcelableExtra = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
            boolean isConnected = networkInfo.isAvailable();
            if (isConnected) {
                updateWifiSingalLevel();
            }
        }
    }

    public void updateWifiSingalLevel() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
        switch (level) {
            case 1:
                mWIFI.setImageResource(R.drawable.state_wifi_icon_2);
                break;
            case 2:
                mWIFI.setImageResource(R.drawable.state_wifi_icon_3);
                break;
            case 3:
                mWIFI.setImageResource(R.drawable.state_wifi_icon_4);
                break;
            case 0:
            default:
                mWIFI.setImageResource(R.drawable.state_wifi_icon_1);
                break;
        }
    }

    /**
     * @param isOpen 蓝牙是否开启
     */
    public void showBTStatus(boolean isOpen) {
        mBT.setVisibility(View.VISIBLE);
        if (isOpen) {
            mBT.setImageResource(R.drawable.state_bt_connected_icon);
        } else {
            mBT.setImageResource(R.drawable.state_bt_not_connected_icon);
        }
    }

    private void showDropDownMenu() {
        if (mDialog != null) {
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDialog = new Dialog(getContext(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        DropDownMenuView view = (DropDownMenuView) inflater.inflate(
                R.layout.dialog_drop_down_menu, null);
        mDialog.addContentView(view, lp);
        view.setOnHideListener(new OnHideListener() {
            @Override
            public void onHide() {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        view.showView();
        mDialog.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 手势监听
     */
    class MyGestureDetectorListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {

            if (mDialog == null && e2.getY() > e1.getY()
                    && e2.getY() - e1.getY() > 10) {
                showDropDownMenu();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
