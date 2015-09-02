
package com.spt.carengine.mainservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.spt.carengine.log.LOG;

/**
 * @author Rocky
 * @Time 2015年7月21日 上午10:26:08
 * @descrition 系统广播
 */
public class SystemBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                "SystemBroadcastReceiver-->>onReceive");
    }

}
