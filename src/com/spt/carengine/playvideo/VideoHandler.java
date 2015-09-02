
package com.spt.carengine.playvideo;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午3:20:22
 * @description 视频播放的接口类
 */
public interface VideoHandler {

    void start();

    void pause();

    void play();

    void stop();

    long getDuration(); // Duration

    long getCurrentPosition(); // played time

    void seekTo(int pos);

    boolean isPlaying();

    boolean canPause();

    boolean moveSeekBackward();

    boolean moveSeekForward();

    public void openVideo(String url);

    void startPlayThread();

    public void destory();
}
