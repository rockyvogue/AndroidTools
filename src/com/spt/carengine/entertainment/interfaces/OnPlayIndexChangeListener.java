
package com.spt.carengine.entertainment.interfaces;

/**
 * 监听Service中播放配置 变化的监听器
 * 
 * @author Administrator
 */
public interface OnPlayIndexChangeListener {
    /**
     * 停止更新进度条
     */
    void onStopUpdateProgress();

    void onPlayIndexChange(int newIndex);
}
