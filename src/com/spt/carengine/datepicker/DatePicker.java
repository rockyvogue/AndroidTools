
package com.spt.carengine.datepicker;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.spt.carengine.R;
import com.spt.carengine.datepicker.NumberPicker.OnValueChangeListener;

@SuppressLint("NewApi")
public class DatePicker extends FrameLayout {
    public static final String TAG = "DATETIMEFRAGMENT";

    private Context mContext;
    private NumberPicker mDayPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mYearPicker;
    private Calendar mCalendar;
    private String[] mYearDisplay;
    private String[] mMonthDisplay;
    private String[] mDayDisplay;

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCalendar = Calendar.getInstance();
        initMonthDisplay();
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.date_picker, this, true);
        mDayPicker = (NumberPicker) findViewById(R.id.date_day);
        mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
        mYearPicker = (NumberPicker) findViewById(R.id.date_year);

        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(31);
        mDayPicker.setValue(20);
        initDayDisplay();
        mDayPicker.setDisplayedValues(mDayDisplay);
        mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        mDayPicker.setX(10.0f);

        mMonthPicker.setMinValue(0);
        mMonthPicker.setMaxValue(11);
        mMonthPicker.setDisplayedValues(mMonthDisplay);
        mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

        mYearPicker.setMinValue(1950);
        mYearPicker.setMaxValue(2100);
        initYearDisplay(151);
        mYearPicker.setDisplayedValues(mYearDisplay);
        mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

        mMonthPicker.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                    int newVal) {
                mCalendar.set(Calendar.MONTH, newVal);
                updateDate();
            }
        });
        mDayPicker.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                    int newVal) {

                mCalendar.set(Calendar.DATE, newVal);
                updateDate();
            }
        });
        mYearPicker.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                    int newVal) {
                mCalendar.set(Calendar.YEAR, newVal);
                updateDate();
            }
        });

        updateDate();

    }

    private void updateDate() {
        mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
        mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
        int day = mCalendar.get(Calendar.DATE);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        mDayPicker.setValue(day);
        mMonthPicker.setValue(month);
        mYearPicker.setValue(year);
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public String getDate() {
        String date = mYearPicker.getValue() + "-"
                + (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();
        return date;

    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public void setCalendar(Calendar calendar) {
        if (null != calendar) {
            mCalendar = calendar;
        }

        updateDate();
    }

    /***
     * 设置年
     */
    private void initYearDisplay(int inLongYear) {
        String strYear = mContext.getString(R.string.date_year);
        mYearDisplay = new String[inLongYear];
        for (int indexYear = 0; indexYear < inLongYear; indexYear++) {
            int year = 1950 + indexYear;
            mYearDisplay[indexYear] = year + strYear;
        }
    }

    /***
     * 设置月
     */
    private void initMonthDisplay() {
        mMonthDisplay = new String[12];
        mMonthDisplay[0] = mContext.getString(R.string.Jan);
        mMonthDisplay[1] = mContext.getString(R.string.Feb);
        mMonthDisplay[2] = mContext.getString(R.string.Mar);
        mMonthDisplay[3] = mContext.getString(R.string.Apr);
        mMonthDisplay[4] = mContext.getString(R.string.May);
        mMonthDisplay[5] = mContext.getString(R.string.Jun);
        mMonthDisplay[6] = mContext.getString(R.string.Jul);
        mMonthDisplay[7] = mContext.getString(R.string.Aug);
        mMonthDisplay[8] = mContext.getString(R.string.Sep);
        mMonthDisplay[9] = mContext.getString(R.string.Oct);
        mMonthDisplay[10] = mContext.getString(R.string.Nov);
        mMonthDisplay[11] = mContext.getString(R.string.Dec);
    }

    /***
     * 设置天
     */
    private void initDayDisplay() {
        String strday = mContext.getString(R.string.date_day);
        mDayDisplay = new String[31];

        for (int index = 0; index < 31; index++) {
            int day = 1 + index;
            mDayDisplay[index] = day + strday;
        }
    }

    public void setEnabled(boolean flag) {
        mYearPicker.setEnabled(flag);
        mMonthPicker.setEnabled(flag);
        mDayPicker.setEnabled(flag);

    }
}
