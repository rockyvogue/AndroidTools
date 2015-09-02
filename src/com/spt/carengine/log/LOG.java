
package com.spt.carengine.log;

/**
 * 
 * <功能描述> 模块化Log日志保存到本地
 * @author  Administrator
 */
public class LOG {

    /** 上传文件模块 */
    public static final int MODE_UPLOAD_FILE  = 1;    // 0000 0000 0000 0001
    /** 主服务模块 */
    public static final int MODE_MAIN_SERVER  = 2;    // 0000 0000 0000 0010
    /** 蓝牙电话模块 */
    public static final int MODE_BLUETOOTH    = 4;    // 0000 0000 0000 0100
    /** 讯飞语音模块 */
    public static final int MODE_VOICE        = 8;    // 0000 0000 0000 1000
    /** Camera导航模块 */
    public static final int MODE_RECORD_VIDEO = 16;   // 0000 0000 0001 0000

    /** 获取baby信息 */
    public static final int MODE_GET_BABY_INFO = 32; // 0000 0000 0010 0000
    /** 上传文件 */
    public static final int MODE_UPLOAD = 64; // 0000 0000 0100 0000
    /** HOME页面 */
    public static final int MODE_HOME = 128; // 0000 0000 1000 0000
    /** 提醒页面 */
    public static final int MODE_REMIND = 256; // 0000 0001 0000 0000
    /** share分享页面 */
    public static final int MODE_SHARE = 512; // 0000 0010 0000 0000
    /** 宝贝页面 */
    public static final int MODE_BAOBEI = 1024; // 0000 0100 0000 0000
    /** SmartConfig */
    public static final int MODE_SMARTCONFIG = 2048; // 0000 1000 0000 0000

    // /** 图片放器模块 */
    // public static final int MODE_PICTURE = 4096; // 0001 0000 0000 0000
    // /** 显示p2p模块 */
    // public static final int MODE_P2P_GETUI = 8192; // 0010 0000 0000 0000
    // /** 升级模块 */
    // public static final int MODE_UPDATE = 16384; // 0100 0000 0000 0000
    // /** 获取磁盘容量模块 */
    // public static final int MODE_GET_DISK_INFO = 32768; // 1000 0000 0000
    // 0000
    //
    // /** 打印个推模块的数据到文件 */
    // public static final int MODE_P2P_LOGFILE = 65536; // 0001 0000 0000 0000
    // 0000
    //
    // /** 优化wifi信道模块 */
    // public static final int MODE_OPTIMIZE_WIFI = 131072; // 0010 0000 0000
    // 0000 0000
    //
    // public static final int MODE_TRANSFER = 262144; // 0100 0000 0000 0000
    // 0000
    //

    /***
     * 打印日志 屏幕打印，并写入sd卡文件
     * 
     * @param obj 类实例 this
     * @param mode 当前打印的模块
     * @param msg 日志信息
     */
    public static void writeMsg(Object obj, int mode, String msg) {
        LogManager.getInstance().Log(obj, mode, msg);
    }

    /***
     * 打印日志 屏幕打印，并写入sd卡文件
     * 
     * @param obj 类实例 this
     * @param mode 当前打印的模块
     * @param voiceText 日志信息
     */
    public static void writeMsg(Throwable ex) {
        LogManager.getInstance().Log(ex);
    }
}
