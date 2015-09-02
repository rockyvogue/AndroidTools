
package com.spt.carengine.define;

public interface ActivityResultListener {

    public void onNoWebResult(int nRelCode);

    public void onWebResult(int nCmdType, String sRecData);
}
