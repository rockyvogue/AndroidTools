
package com.spt.carengine.playvideo;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * 弱引用类
 * 
 * @author rocky
 * @param <T>
 */
public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public WeakHandler(T owner) {
        mOwner = new WeakReference<T>(owner);
    }

    public T getOwner() {
        return mOwner.get();
    }
}
