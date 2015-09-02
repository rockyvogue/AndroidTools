
package com.spt.carengine.entertainment.interfaces;

import java.io.File;
import java.util.ArrayList;

public interface IPlayControl {

    /**
     * 获取当前服务中歌曲播放的索引
     * 
     * @return
     */
    int getSongIndex();

    /**
     * 设置播放位置变化的监听器
     * 
     * @param listener
     */
    void setonPlayIndexChangeListener(OnPlayIndexChangeListener listener);

    /**
     * 获取本首歌曲的总长度
     * 
     * @return
     */
    int getMax();

    /**
     * 获取当前播放器的播放进度
     * 
     * @return
     */
    int getCurProgress();

    boolean isPlaying();

    void play(String url);

    void pause();

    void resume();

    /**
     * 获取服务中的歌曲列表
     * 
     * @return
     */
    ArrayList<File> getSongList();

    /**
     * 给服务设置扫描完成的监听器
     */
    void setOnScanFinishListener(OnScanFinishListener lis);

    /**
     * 控制播放器跳转播放
     * 
     * @param progress
     */
    void seekTo(int progress);
}
