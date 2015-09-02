/*
 * 文 件 名:  Constant.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月6日
 */

package com.spt.carengine.constant;

import android.R.integer;

/**
 * 常量
 * 
 * @author 时汉文
 */
public class Constant {
    /** 导航模块 */
    public static final int MODULE_TYPE_NAV = 1;
    /** 拨号模块 */
    public static final int MODULE_TYPE_NUMBER = 2;
    /** 照片模块 */
    public static final int MODULE_TYPE_PHOTO = 3;
    /** 行车记录模块 */
    public static final int MODULE_TYPE_CAR_RECORD = 4;
    /** 个人中心模块 */
    public static final int MODULE_TYPE_USER = 5;
    /** 视频模块 */
    public static final int MODULE_TYPE_VIDEO = 6;
    /** 娱乐模块 */
    public static final int MODULE_TYPE_AMUSEMENT = 7;
    /** home模块 */
    public static final int MODULE_TYPE_HOME = 8;
    /** 照相模块 */
    public static final int MODULE_TYPE_CAMERA = 9;
    /** 语音模块 */
    public static final int MODULE_TYPE_VOICE = 10;
    /** FM模块 */
    public static final int MODULE_TYPE_FM = 11;
    /** 云狗模块** */
    public static final int MODULE_TYPE_CDOG = 12;
    /** 蓝牙模块 **/
    public static final int MODULE_TYPE_BLUETOOTH = 13;
    /** 系统设置模块 */
    public static final int MODULE_TYPE_SYSTEM_SETTINGS = 14;
    /** WIFI模块 */
    public static final int MODULE_TYPE_SYSTEM_WIFI = 15;
    /** SD卡模块 **/
    public static final int MODULE_TYPE_SDSET = 16;
    /** 流量统计模块 **/
    public static final int MODULE_TYPE_TRAFFIC = 17;

    /** 主菜单的行车记录窗口 imageview 显示 **/
    public static final int MODULE_TYPE_IMAGEVIEW = 18;

    /** 主菜单的行车记录窗口 surfaceview 显示 **/
    public static final int MODULE_TYPE_SURFACEVIEW = 19;
    /** 主菜单的行车记录窗口 surfaceview 显示 **/
    public static final int BINDSERVICE_SUCCESS = 20;

    /** 倒车消息 **/
    public static final int REBACK_CAR_STATE = 21;

    /** 日期与时间模块 **/
    public static final int MODULE_TYPE_DATE = 22;
    /** 关于模块 **/
    public static final int MOUDLE_TYPE_ABOUT = 23;
    /** 切图参照分辨率 */
    public static final int SCREEN_WIDTH = 960;
    public static final int SCREEN_HEIGHT = 540;

    /** 视频格式 **/
    public static final int VIDEO_HEIGHT = 240;
    public static final int VIDEO_WIDTH = 320;

    /** 娱乐模块 */
    /**
     * 顺序播放模式
     */
    public static final int ORDER_MODE = 1000;

    /**
     * 随机播放模式
     */
    public static final int RANDOM_MODE = 1001;

    /**
     * 单曲循环
     */
    public static final int SINGLE_MODE = 1002;
    /**
     * 列表循环
     */
    public static final int LIST_MODE = 1003;

    public static final String MUSIC_PLAY_LIST = "music_playlist";
    public static final String MUSIC_PLAY = "music_play";
    public static final String MUSIC_NEXT = "music_next";
    public static final String MUSIC_PRE = "music_pre";

    /** 第三方apk 包名 **/
    public static final String GAODE_PACKAGE = "com.autonavi.xmgd.navigator";
    public static final String KUWO_PACKAGE = "cn.kuwo.kwmusiccar";
    public static final String BAIDU_PACKAGE = "com.baidu.navi";
}
