
package com.spt.carengine.mainservice;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

import com.spt.carengine.log.LOG;
import com.spt.carengine.log.ReportTraceFile;

import java.util.Iterator;

/**
 * @author Rocky
 * @Time 2015年7月31日 下午4:37:37
 * @description 上报GPS轨迹的类
 */
public class ReportGpsTrace {

    public static final String TAG = "ReportGpsTrace";

    /**
     * 检测信鸽注册是否成功的时间间隔
     */
    public static final int REGISTER_XINGE_DEFAULT_INTERNAL_TIME = 5 * 1000;

    /**
     * 上报行车轨迹的时间间隔 ,每隔5分钟上传一次
     */
    public static final int REPORT_TRACE_INTERNAL_TIME = 5 * 60 * 1000;

    /**
     * 定时更新GPS位置的时间间隔
     */
    private static final int UPDATE_TRACE_INTERNAL_TIME = 5 * 1000;
    /**
     * 最小距离
     */
    private static final int MIN_DISTANCE = 1;

    /**
     * Default Longitude (经度)
     */
    private String mLongitude = "00.0000000000";

    /**
     * Defualt Latitude (纬度)
     */
    private String mLatitude = "00.0000000000";

    /**
     * 位置管理
     */
    private LocationManager locationManager;

    private Context mContext;

    public ReportGpsTrace(Context context) {
        this.mContext = context;
        registGPSListener();
    }

    /**
     * 取消上报轨迹
     */
    public void cancelReportTrace() {
        unRegisterGpsListener();
    }

    /**
     * 注册GPS监听器
     */
    private void registGPSListener() {
        locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);

        // 判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mContext, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            return;
        }

        // 写入location信息到本地
        writeLocationToFile(getLocation());

        // 监听状态，
        locationManager.addGpsStatusListener(gpsStatuslistener);

        // 注册更新gps信息的接口 ,注册位置更新监听(最小时间间隔为5秒,最小距离间隔为5米)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                UPDATE_TRACE_INTERNAL_TIME, MIN_DISTANCE, locationListener);
    }

    /**
     * 注销GPS监听器
     */
    private void unRegisterGpsListener() {
        locationManager.removeGpsStatusListener(gpsStatuslistener);
        locationManager.removeUpdates(locationListener);
    }

    /**
     * 获取地理位置
     * 
     * @return
     */
    private Location getLocation() {
        // 为获取地理位置信息时设置查询条件
        String bestProvider = locationManager.getBestProvider(getCriteria(),
                true);
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = locationManager.getLastKnownLocation(bestProvider);
        return location;
    }

    /**
     * 将位置信息写入本地文件
     * 
     * @param location
     */
    private void writeLocationToFile(Location location) {
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            mLongitude = String.valueOf(longitude);// 经度
            mLatitude = String.valueOf(latitude);// 纬度

            ReportTraceFile.getInstance()
                    .writeMsg(mLongitude + "," + mLatitude);
        }
    }

    /**
     * GPS位置监听
     */
    private LocationListener locationListener = new LocationListener() {

        // GPS禁用时触发
        @Override
        public void onProviderDisabled(String provider) {
            LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "onProviderDisabled.-->>"
                    + provider);
        }

        // GPS开启时触发
        @Override
        public void onProviderEnabled(String provider) {
            Location location = locationManager.getLastKnownLocation(provider);
            LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "onProviderEnabled.-->>"
                    + location);
        }

        // 位置信息变化
        @Override
        public void onLocationChanged(Location location) {
            LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "onLocationChanged.-->>"
                    + location);
            if (location == null)
                return;
            LOG.writeMsg(
                    this,
                    LOG.MODE_MAIN_SERVER,
                    "onLocationChanged.-->>" + ", 时间 :" + location.getTime()
                            + ", 经度 :" + location.getLongitude() + ", 纬度 :"
                            + location.getLatitude() + ", 海拔 :"
                            + location.getAltitude());
            // 写入location信息到本地
            writeLocationToFile(location);
        }

        // GPS状态变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub，
            switch (status) {
            // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                            "onStatusChanged.-->>" + "当前GPS状态为可见状态");
                    break;

                // GPS状态为服务区外时，
                case LocationProvider.OUT_OF_SERVICE:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                            "onStatusChanged.-->>" + "当前GPS状态为服务区外状态");
                    break;

                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                            "onStatusChanged.-->>" + "当前GPS状态为暂停服务状态");
                    break;
            }
        }
    };

    /**
     * GPS状态监听
     */
    private GpsStatus.Listener gpsStatuslistener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
            // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "第一次定位");
                    break;

                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "搜索到：" + count
                            + "颗卫星");
                    break;

                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "定位启动");
                    break;

                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    LOG.writeMsg(this, LOG.MODE_MAIN_SERVER, "定位结束");
                    break;
            }
        };
    };

    /**
     * 返回查询条件
     * 
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public String getmLatitude() {
        return mLatitude;
    }

}
