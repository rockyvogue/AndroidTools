
package com.spt.carengine.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import com.spt.carengine.R;
import com.spt.carengine.adapter.TimeZoneAdpter;
import com.spt.carengine.db.bean.TimeZoneCity;
import com.spt.carengine.utils.ScreenUtils;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import java.util.ArrayList;
import java.util.List;

/***
 * 时区
 */
public class TimeZoneView extends PercentRelativeLayout {
    private static final int HSPACING = 40;
    private static final int VSPACING = 30;
    private ReturnBarView mReturnBarView;
    private GridView mTimeGridView;
    private TimeZoneAdpter mZoneAdpter;
    private List<TimeZoneCity> mList;

    public TimeZoneView(Context context) {
        super(context);
    }

    public TimeZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TimeZoneView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        mTimeGridView = (GridView) findViewById(R.id.gridview);
        init();
    }

    private void init() {
        setListCity();
        TimeZoneCity mCity = SharePrefsUtils.getTimeZone(getContext());
        mReturnBarView.init(ReturnBarView.TYPE_TIMEDATE);
        int hspacing = ScreenUtils.getRealHeightValue(getContext(), HSPACING);
        mTimeGridView.setHorizontalSpacing(hspacing);
        int vspacing = ScreenUtils.getRealHeightValue(getContext(), VSPACING);
        mTimeGridView.setVerticalSpacing(vspacing);
        mZoneAdpter = new TimeZoneAdpter(getContext(), mList);
        mZoneAdpter.setTimeZoneCity(mCity.mCityName);
        mTimeGridView.setAdapter(mZoneAdpter);
    }

    public void setListener(OnClickListener l) {
        mReturnBarView.setBackListener(l);
        mReturnBarView.setListener(l);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mTimeGridView.setOnItemClickListener(l);
    }

    public void updateView(String name) {
        mZoneAdpter.setTimeZoneCity(name);
        mZoneAdpter.notifyDataSetChanged();
    }

    public void setListCity() {
        mList = new ArrayList<TimeZoneCity>();
        // 先加北京时间东八区
        setTimeCity(mContext.getString(R.string.city_Beijing),
                mContext.getString(R.string.AsiaShanghai));
        setTimeCity(mContext.getString(R.string.city_Hongkong),
                mContext.getString(R.string.AsiaShanghai));
        setTimeCity(mContext.getString(R.string.city_Taipei),
                mContext.getString(R.string.AsiaShanghai));
        setTimeCity(mContext.getString(R.string.city_KualaLumpur),
                mContext.getString(R.string.AsiaShanghai));
        // 东九区
        setTimeCity(mContext.getString(R.string.city_Tokyo),
                mContext.getString(R.string.AsiaTokyo));
        setTimeCity(mContext.getString(R.string.city_Seoul),
                mContext.getString(R.string.AsiaTokyo));
        // 东一区
        setTimeCity(mContext.getString(R.string.city_Berlin),
                mContext.getString(R.string.EuropeBerlin));
        setTimeCity(mContext.getString(R.string.city_Paris),
                mContext.getString(R.string.EuropeParis));
        setTimeCity(mContext.getString(R.string.city_London),
                mContext.getString(R.string.EuropeLondon));
        setTimeCity(mContext.getString(R.string.city_Roman),
                mContext.getString(R.string.EuropeRome));

        // 东三区
        setTimeCity(mContext.getString(R.string.city_Moscow),
                mContext.getString(R.string.EuropeMoscow));

        // 东七区
        setTimeCity(mContext.getString(R.string.city_Bangkok),
                mContext.getString(R.string.AsiaBangkok));
        // 西五区
        setTimeCity(mContext.getString(R.string.city_NewYork),
                mContext.getString(R.string.AmericaNew_York));
        // 西六区
        setTimeCity(mContext.getString(R.string.city_Chicago),
                mContext.getString(R.string.AmericaChicago));
        setTimeCity(mContext.getString(R.string.city_MexicoCity),
                mContext.getString(R.string.AmericaChicago));
        // 西七区
        setTimeCity(mContext.getString(R.string.city_Phoenix),
                mContext.getString(R.string.AmericaPhoenix));
        // 西八区
        setTimeCity(mContext.getString(R.string.city_LAX),
                mContext.getString(R.string.AmericaLos_Angeles));
        // 西六区
        setTimeCity(mContext.getString(R.string.city_Hawaii),
                mContext.getString(R.string.PacificHonolulu));

        setTimeCity(mContext.getString(R.string.city_Sydne),
                mContext.getString(R.string.AustraliaSydney));
    }

    public void setTimeCity(String cityName, String timeZone) {
        TimeZoneCity city = new TimeZoneCity();
        if (null != cityName && !"".equals(cityName) && null != timeZone
                && !"".equals(timeZone)) {
            city.mCityName = cityName;
            city.mTimeZone = timeZone;
            mList.add(city);
        }
    }
}
