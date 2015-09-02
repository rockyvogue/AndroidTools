
package com.spt.carengine.fm;

import android.content.Context;

public class FMUtil {

    private FMUtil() {
    };

    private static FMState fms;

    public static FMState getFMState(Context ct) {
        synchronized (FMUtil.class) {
            if (fms == null) {
                fms = new FMState();
            }
        }
        // TODO 更改相关值
        return fms;
    }

    public static boolean saveFMState(Context ct, FMState fms) {
        return true;
    }

}
