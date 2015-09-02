
package com.spt.carengine.ui;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.datepicker.DatePicker;
import com.spt.carengine.datepicker.TimePicker;
import com.spt.carengine.db.bean.TimeZoneCity;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import java.util.Calendar;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class DateTimeView extends PercentRelativeLayout {

    private PercentRelativeLayout mPray;
    private ReturnBarView mReturnBarView;
    private TextView mTvDateTime;
    private CheckBox mCkDateSwitch;
    private TimeZoneCity mTimeZoneCity;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private Calendar mCalendar;

    public DateTimeView(Context context) {
        super(context);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(Constant.MODULE_TYPE_SYSTEM_SETTINGS);
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendar = Calendar.getInstance();
        getTiemZoneCity();
        mPray = (PercentRelativeLayout) findViewById(R.id.pray0);
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        mTvDateTime = (TextView) findViewById(R.id.tv_date_time);
        mCkDateSwitch = (CheckBox) findViewById(R.id.ck_date_switch);
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mCkDateSwitch.setChecked(mTimeZoneCity.mFlag);
        mReturnBarView.init(ReturnBarView.TYPE_DATE);
        mReturnBarView.setBackListener(mOnClickListener);
        String cityName;
        if (mTimeZoneCity != null) {
            cityName = mTimeZoneCity.mCityName;

        } else {
            cityName = mContext.getString(R.string.city_Beijing);

        }
        mTvDateTime.setText(getMsg(cityName));

    }

    public void setOnClickListener(OnClickListener l) {
        mPray.setOnClickListener(l);
        mReturnBarView.setListener(l);

    }

    public void setCheckedChangeListener(OnCheckedChangeListener ocl) {
        mCkDateSwitch.setOnCheckedChangeListener(ocl);

    }

    private Spanned getMsg(String title) {
        String result = String.format(
                getContext().getString(R.string.tv_time_date_c), title);
        return Html.fromHtml(result);
    }

    public void setText(TimeZoneCity mTimeZoneCity) {
        this.mTimeZoneCity = mTimeZoneCity;
        String cityName;
        String mTimeZone;
        if (mTimeZoneCity != null) {
            cityName = mTimeZoneCity.mCityName;
            mTimeZone = mTimeZoneCity.mTimeZone;

        } else {
            cityName = mContext.getString(R.string.city_Beijing);
            mTimeZone = mContext.getString(R.string.AsiaShanghai);
        }
        mTvDateTime.setText(getMsg(cityName));
        TimeZone timezone = TimeZone.getTimeZone(mTimeZone);
        mCalendar = Calendar.getInstance(timezone);

        updateTime();

    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
        updateTime();

    }

    public void updateTime() {

        mDatePicker.setCalendar(mCalendar);
        mTimePicker.setCalendar(mCalendar);

    }

    public void getTiemZoneCity() {
        mTimeZoneCity = SharePrefsUtils.getTimeZone(getContext());

    }

    /***
     * 控制焦点
     * 
     * @param flag
     */
    public void setEnabled(boolean flag) {
        mPray.setEnabled(flag);
        mTvDateTime.setEnabled(flag);
        mDatePicker.setEnabled(flag);
        mTimePicker.setEnabled(flag);

    }

    public Calendar getCalendar() {

        return mCalendar;
    }

}
