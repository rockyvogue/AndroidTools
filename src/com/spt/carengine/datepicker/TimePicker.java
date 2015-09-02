
package com.spt.carengine.datepicker;

import java.util.Calendar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.spt.carengine.R;
import com.spt.carengine.datepicker.NumberPicker.OnValueChangeListener;

public class TimePicker extends FrameLayout {

    private Context mContext;
    private NumberPicker hourPicker;
    private NumberPicker minPicker;
    private Calendar mCalendar;
    boolean is24Hour;
    private String[] mHourDisplay;
    private String[] mMinuteDisplay;

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCalendar = Calendar.getInstance();
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.time_picker, this, true);
        hourPicker = (NumberPicker) findViewById(R.id.time_hours);
        minPicker = (NumberPicker) findViewById(R.id.time_minutes);

        minPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        initMinuteDisplay();
        minPicker.setDisplayedValues(mMinuteDisplay);
        minPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

        hourPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

        is24Hour = android.text.format.DateFormat.is24HourFormat(context);

        minPicker.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                    int newVal) {
                mCalendar.set(Calendar.MINUTE, newVal);

            }
        });

        hourPicker.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                    int newVal) {
                mCalendar.set(Calendar.HOUR, newVal);
            }
        });

        updateTime();

    }

    public TimePicker(Context context) {
        this(context, null);
    }

    private void updateTime() {
        System.out.println(mCalendar.getTime());

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        initHourDisplay();
        hourPicker.setDisplayedValues(mHourDisplay);
        hourPicker.setValue(mCalendar.get(Calendar.HOUR_OF_DAY));

        minPicker.setValue(mCalendar.get(Calendar.MINUTE));
    }

    public boolean isIs24Hour() {
        return is24Hour;
    }

    public void setIs24Hour(boolean is24Hour) {
        this.is24Hour = is24Hour;
    }

    public String getTime() {
        String time = "";
        if (is24Hour) {
            time = hourPicker.getValue() + ":" + minPicker.getValue();
        }
        return time;
    }

    public int getHourOfDay() {
        return hourPicker.getValue();
    }

    public int getHour() {
        return hourPicker.getValue();
    }

    public int getMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public void setCalendar(Calendar calendar) {
        if (null != calendar) {
            mCalendar = calendar;
        }
        this.mCalendar.set(Calendar.HOUR_OF_DAY,
                mCalendar.get(Calendar.HOUR_OF_DAY));
        this.mCalendar.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE));
        updateTime();
    }

    private void initHourDisplay() {
        String strDay = mContext.getString(R.string.date_hour);
        mHourDisplay = new String[24];
        for (int i = 0; i < 24; i++) {
            mHourDisplay[i] = i + strDay;
        }
    }

    private void initMinuteDisplay() {
        String strMinute = mContext.getString(R.string.date_minute);
        mMinuteDisplay = new String[60];
        for (int i = 0; i < 60; i++) {
            mMinuteDisplay[i] = i + strMinute;
        }
    }

    public void setEnabled(boolean flag) {
        hourPicker.setEnabled(flag);
        minPicker.setEnabled(flag);

    }
}
