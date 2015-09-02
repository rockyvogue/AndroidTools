
package com.spt.carengine.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;

import com.spt.carengine.R;
import com.spt.carengine.db.bean.TimeZoneCity;
import com.spt.carengine.ui.DateTimeView;
import com.spt.carengine.ui.TimeZoneView;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.voice.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/****
 * 日期与时间
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeFragment extends Fragment {
    private static final String TAG = "DateTimeFragment";

    private DateTimeView mDateTimeView;
    private TimeZoneView mTimeZoneView;
    private TimeZoneCity mTimeZoneCity;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimeZoneCity = SharePrefsUtils.getTimeZone(getActivity());
        mFlag = mTimeZoneCity.mFlag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datetime, container,
                false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mDateTimeView = (DateTimeView) view.findViewById(R.id.datetime_view);
        mTimeZoneView = (TimeZoneView) view.findViewById(R.id.tiemzone_view);
        if (mFlag) {
            mDateTimeView.setEnabled(false);
        } else {
            mDateTimeView.setEnabled(true);
        }

        mDateTimeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.pray0:
                        mDateTimeView.setVisibility(View.GONE);
                        mTimeZoneView.setVisibility(View.VISIBLE);
                        String name = mTimeZoneCity.mCityName;
                        if (null != name && !"".equals(name)) {
                            mTimeZoneView.updateView(name);
                        }
                        break;
                    case R.id.bt_update:// 保存手动设置
                        SharePrefsUtils.saveTimeZone(getActivity(),
                                mTimeZoneCity.mCityName,
                                mTimeZoneCity.mTimeZone, mFlag);
                        if (!mFlag) {
                            updateSystemTime();
                            Toast.makeText(
                                    getActivity(),
                                    getActivity().getString(
                                            R.string.tv_save_time),
                                    Toast.LENGTH_LONG).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });
        mDateTimeView
                .setCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {// 自动更新时间

                    @Override
                    public void onCheckedChanged(CompoundButton cButton,
                            boolean flag) {
                        mFlag = flag;

                        if (flag) {// 自动更新时间
                            mDateTimeView.setEnabled(false);
                            updateSystemTime();
                            mDateTimeView.setText(mTimeZoneCity);
                        } else {
                            mDateTimeView.setEnabled(true);

                        }
                    }
                });

        mTimeZoneView.setListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back:
                        mDateTimeView.setVisibility(View.VISIBLE);
                        mTimeZoneView.setVisibility(View.GONE);
                        break;
                    case R.id.bt_update:// 保存
                        mDateTimeView.setVisibility(View.VISIBLE);
                        mTimeZoneView.setVisibility(View.GONE);
                        if (mTimeZoneCity != null) {
                            SharePrefsUtils.saveTimeZone(getActivity(),
                                    mTimeZoneCity.mCityName,
                                    mTimeZoneCity.mTimeZone, mFlag);
                            updateSystemTime();
                            mDateTimeView.setText(mTimeZoneCity);

                        }
                        break;

                    default:
                        break;
                }

            }
        });

        mTimeZoneView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                mTimeZoneCity = (TimeZoneCity) parent.getAdapter().getItem(
                        position);
                if (null != mTimeZoneCity) {
                    mTimeZoneView.updateView(mTimeZoneCity.mCityName);
                }
            }
        });
        updateTime();
    }

    private void updateTime() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };
        mTimer = new Timer();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mFlag) {
                mDateTimeView.setText(mTimeZoneCity);// 自动更新
            } else {
                mDateTimeView.setCalendar(Calendar.getInstance()); // 手动设置
            }
        }
    };

    /**
     * 修改系统时间
     */
    private void updateSystemTime() {
        Calendar calendar;
        if (mFlag) {
            calendar = TimeUtil.getCalendar(getActivity());
        } else {
            calendar = mDateTimeView.getCalendar();
        }

        Date date_time = calendar.getTime();
        long millionSeconds = date_time.getTime();
        SystemClock.setCurrentTimeMillis(millionSeconds);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mTimer && null != mTimerTask) {
            mTimer.schedule(mTimerTask, 1000, 2000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mTimer) {
            mTimer.cancel();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTimer) {
            mTimer.cancel();

        }
    }
}
