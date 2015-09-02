
package com.spt.carengine.recordvideo;

import android.R.integer;
import android.view.SurfaceView;

/**
 * <功能描述> 切换SurfaceView显示对象
 * 
 * @author ymm
 */
public class SwitchSVMessage {

    private SurfaceView surfaceView;

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    private int messageType;

}
