
package com.spt.carengine.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

public class TrafficFragment extends Fragment {
    private ReturnBarView mReturnBarView;
    private TextView mTrafficValue;
    private TextView mRightText;
    private Activity mActivity;
    private SharedPreferences mTrafficSP;
    private static final String TRAFFIC_CONFIG = "traffic_config";
    private static final String TRAFFIC_VALUE = "traffic_value";
    private static final String TRAFFIC_CURRENT = "traffic_current";
    private static final String TRAFFIC_BUFFER_POWER_OFF = "buffer_power_off";// 关机之前缓存的数据，用来累加使用过的流量值。
    private static final String TRAFFIC_BUFFER_CLEAN_BEFORE = "buffer_clean_before";// 清除之前的缓存数据，用来计算当前显示的流量值
    private static final String IS_CLEANED = "isCleaned";// 是否清理过，根据该值来计算当前要显示的流量值
    private static final String IS_POWER_OFF = "isPowerOff";// 是否关过机，在关机广播中置为true，执行完gettext方法之后就置为false

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traffic_count,
                container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mTrafficSP = mActivity.getSharedPreferences(TRAFFIC_CONFIG,
                mActivity.MODE_PRIVATE);
        mTrafficValue.setText(getText());
    }

    private void initView(View view) {
        if (null == view) {
            return;
        }
        mTrafficValue = (TextView) view.findViewById(R.id.traffic_value);
        initReturnBarView(view);
    }

    private void initReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view
                .findViewById(R.id.traffic_return_bar);
        mReturnBarView.init(ReturnBarView.TYPE_TRAFFIC);
        mRightText = (TextView) mReturnBarView.findViewById(R.id.edit);
        mReturnBarView.setBackListener(mReturnBarBack);
        mRightText.setOnClickListener(mRightTextListenner);
    }

    private OnClickListener mRightTextListenner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            clearTrafficData();
            mTrafficSP.edit().putBoolean(IS_CLEANED, true);
            mTrafficValue.setText(getText());
        }
    };
    private OnClickListener mReturnBarBack = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // 返回到上一个界面
            EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
        }
    };

    private void clearTrafficData() {
        if (mTrafficSP == null) {
            return;
        }
        long cleanBefore = mTrafficSP.getLong(TRAFFIC_BUFFER_CLEAN_BEFORE, 0);
        cleanBefore = cleanBefore + mTrafficSP.getLong(TRAFFIC_VALUE, 0);
        if (cleanBefore == 0) {
            return;
        }
        mTrafficSP.edit().putLong(TRAFFIC_BUFFER_CLEAN_BEFORE, cleanBefore)
                .commit();
        mTrafficSP.edit().putLong(TRAFFIC_VALUE, 0).commit();
        mTrafficSP.edit().putBoolean(IS_CLEANED, true).commit();
    }

    private SpannableString getText() {
        long trafficValue = 0;
        boolean isCleaned = mTrafficSP.getBoolean(IS_CLEANED, false);
        boolean powerOff = mTrafficSP.getBoolean(IS_POWER_OFF, false);
        if (isCleaned && !powerOff) {
            trafficValue = mTrafficSP.getLong(TRAFFIC_CURRENT, 0)
                    + mTrafficSP.getLong(TRAFFIC_BUFFER_POWER_OFF, 0)
                    - mTrafficSP.getLong(TRAFFIC_BUFFER_CLEAN_BEFORE, 0);
        } else {
            trafficValue = mTrafficSP.getLong(TRAFFIC_CURRENT, 0)
                    + mTrafficSP.getLong(TRAFFIC_BUFFER_POWER_OFF, 0);
        }
        mTrafficSP.edit().putLong(TRAFFIC_VALUE, trafficValue).commit();
        mTrafficSP.edit().putBoolean(IS_POWER_OFF, false).commit();

        String traffic = mActivity.getResources().getString(
                R.string.traffic_use)
                + " " + Formatter.formatFileSize(mActivity, trafficValue);
        SpannableString trafficUsed = new SpannableString(traffic);
        trafficUsed.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        R.color.traffic_text_color)), 7, traffic.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return trafficUsed;
    }

}
