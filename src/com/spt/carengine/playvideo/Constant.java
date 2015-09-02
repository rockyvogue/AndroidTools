
package com.spt.carengine.playvideo;

/**
 * 系统常量类
 * 
 * @author rocky
 */
public class Constant {
    public static final boolean DEBUG = false;

    // 视频播放地址的键
    public static final String PLAYER_URL = "play_url";

    // 自适配解码器
    public static final int DECODE_SELFADAPTATION = 0;

    // 软解码
    public static final int DECODE_SOFTWARE = 1;

    // 硬解码
    public static final int DECODE_HARDWARE = 2;

    // 网络缓冲时间
    public static final String VIDEO_NETWORKCACHING = "5000";

    // 快进快退的默认时间
    public static final long BACKWARD_DEAFAULT_TIME = 20000;

    // 播放
    public static final int VIDEO_PLAY = 0;

    // 暂停
    public static final int VIDEO_PAUSE = 1;

    // 快退
    public static final int VIDEO_BACKWARD = 2;

    // 快进
    public static final int VIDEO_FORWARD = 3;

    // 播放完成
    public static final int VIDEO_PLAY_COMPLETION = 4;

    // 播放错误
    public static final int VIDEO_PLAY_ERROR = 5;

    // ProgressBar Loading 可见
    public static final int VIDEO_PLAY_PROGRESSBAR_LOADING_VISIBLE = 8;

    // ProgressBar Loading 不可见
    public static final int VIDEO_PLAY_PROGRESSBAR_LOADING_GONE = 9;

    // 显示当前的播放时间
    public static final int HANDLER_MSG_DISPLAY_CURRENT_PLAY_TIME = 11;

    // 选择隐藏与显示的动画监听器的常量
    public static final int OVERLAY_CONTROL = 0;
    public static final int OVERLAY_TOPTOOLBAR = 1;

    // 当前播放的时间要超过10秒才记录播放时间，
    public static final int DEFAULT_RCURRENTTIME = 10 * 1000;
    // 当有播放记录的时候，弹出播放记录并让其消失的常量
    public static final int DISMISS_DIALOG = 0x10;
    // 让播放记录对话框5秒消失的时间
    public static final int DISMISS_DELAY = 5000; // 5 s
    // 跳转到播放记录的handler消息
    public static final int SEEK_DIALOG = 0x20; // 5 s

    // 百度播放器的时长以秒为单位，需要转化为毫秒，
    public static final int BAIDU_DURETION = 1000;
}
