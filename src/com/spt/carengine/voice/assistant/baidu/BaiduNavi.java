/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BaiduMap.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.baidu
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */

package com.spt.carengine.voice.assistant.baidu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.spt.carengine.log.LOG;

/**
 * 
 * <功能描述> 百度导航类
 * @author  Administrator
 */
public class BaiduNavi {
    public static final String TAG = "BaiduMap";
    public static final String BAIDU_NAVI_APP_PACKAGE_NAME = "com.baidu.navi";
    public static final String BAIDU_MAP_APP_PACKAGE_NAME = "com.baidu.BaiduMap";

    private static BaiduNavi instance = new BaiduNavi();
    
    private BaiduNavi() {
    }
    
    public static BaiduNavi getIntance() {
        return instance;
    }
    
    /**
     * <功能描述>通过路线规划启动百度导航
     * @param mContext 上下文对象
     * @param url 导航路线规划的参数
     * @return [参数说明] 是否启动成功
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToRoutePlan(Context mContext, String url) {
        if(hasBaiduMapClient(mContext)) {
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 通过搜索地名来导航
     * @param mContext
     * @param placeNameKey 地名
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToPlaceSearch(Context mContext, String placeNameKey) {
        if(hasBaiduMapClient(mContext)) {
            String url =  "bdnavi://query?name=" + placeNameKey;
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 启动导航
     * @param mContext
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviApp(Context mContext) {
        if(hasBaiduMapClient(mContext)) {
            String url = "bdnavi://launch";
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 离线数据管理
     * @param mContext
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToOfflineDataManager(Context mContext) {
        if(hasBaiduMapClient(mContext)) {
            String url = "bdnavi://data";
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 回家
     * @param mContext
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToGohome(Context mContext) {
        if(hasBaiduMapClient(mContext)) {
            String url = "bdnavi://gohome";
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 去公司服务
     * @param mContext
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToGoCompany(Context mContext) {
        if(hasBaiduMapClient(mContext)) {
            String url = "bdnavi://gocompany";
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 我的位置(调用后在地图页面显示系统的当前地理位置)
     * @param mContext
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToWhere(Context mContext) {
        if(hasBaiduMapClient(mContext)) {
            String url = "bdnavi://gowhere";
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * <功能描述> 周边服务场所搜索(1、加油站,2、停车场,3、汽车服务,4、银行,5、餐饮,6、酒店,7、厕所,8、医疗)
     * @param mContext
     * @param url
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean startBaiduNaviToSearchNearbyServer(Context mContext, String url) {
        if(hasBaiduMapClient(mContext)) {
            Intent intent = new Intent("com.baidu.navi.action.START");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
    
    private boolean hasBaiduMapClient(Context context) {
        PackageInfo packageInfo = null;
        try {
            if(context != null) {
                packageInfo = context.getPackageManager().getPackageInfo(BAIDU_NAVI_APP_PACKAGE_NAME,  0);
            }
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

}
