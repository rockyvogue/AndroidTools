
package com.spt.carengine.utils;

import android.content.Context;

/**
 * @author Rocky
 * @Time 2015年8月5日 上午11:18:36
 * @description 像素转换
 */
public class PixelUtils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float px2dp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        float dp = px / density;

        return dp;
    }
}
