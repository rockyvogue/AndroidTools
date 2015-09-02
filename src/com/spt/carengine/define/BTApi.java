
package com.spt.carengine.define;

/**
 * 接口常量定义
 * 
 * @author Administrator
 */
public interface BTApi {

    /********** 发给蓝牙SERVER的广播 *************/
    public final String ACTION_YRC_BT = "com.yrc.car.bt";

    /******** 拨打电话 ***********/
    public final int BT_CMD_DIAL = 1;
    /******** 挂机，挂断正进行的通话 ***********/
    public final int BT_CMD_HANG = 2;
    /******** 接电话 ***********/
    public final int BT_CMD_ANSWER = 3;
    /******** 小键盘 ***********/
    public final int BT_CMD_KEYPADS = 4;
    /******** 重拨 ***********/
    public final int BT_CMD_REDIAL = 5;
    /******** 通话时在手机和蓝牙之间切换 ***********/
    public final int BT_CMD_TRANSFER = 6;
    /******** 蓝牙音乐播放 ***********/
    public final int BT_CMD_PLAY = 7;
    /******** 蓝牙音乐暂停 ***********/
    public final int BT_CMD_PAUSE = 8;
    /******** 蓝牙音乐下一首 ***********/
    public final int BT_CMD_FORWARD = 9;
    /******** 蓝牙音乐上一首 ***********/
    public final int BT_CMD_BACKWARD = 10;
    /******** 设置蓝牙名称 ***********/
    public final int BT_CMD_DEV_NAME = 11;
    /******** 下载电话本 ***********/
    public final int BT_CMD_DOWNLOAD = 12;
    /******** 蓝牙mic mute ***********/
    public final int BT_CMD_MIC_MUTE = 13;
    /******** 取手机名称 ***********/
    public final int BT_CMD_PHONENAME = 14;
    /******** 取配对列表 ***********/
    public final int BT_CMD_PAIRED_LIST = 15;
    /******** 删除配对设备 ***********/
    public final int BT_CMD_DELETE_PAIRED = 16;

    /********** 蓝牙SERVER发出的广播 *************/
    public final String ACTION_YRC_BD = "com.yrc.car.bd";
    /******** 蓝牙状态 ***********/
    public final int BT_STATE = 1;
    /******** 电话号码 ***********/
    public final int BT_CALL_NUM = 2;
    /******** 下载电话本 ***********/
    public final int BT_CALL_PBDOWN = 3;
}
