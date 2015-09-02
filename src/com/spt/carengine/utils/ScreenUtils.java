/*
 * 文 件 名:  ScreenUtils.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月11日
 */

package com.spt.carengine.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.spt.carengine.constant.Constant;

/**
 * 屏幕相关的工具类
 * 
 * @author Heaven
 */
public class ScreenUtils {

    /**
     * 计算控件实际宽度，参照屏幕分辨率：960*540
     * 
     * @param relativeValue
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    public static int getRealWidthValue(Context context, int relativeValue) {
        int realValue = 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        realValue = (width * relativeValue) / Constant.SCREEN_WIDTH;
        return realValue;
    }

    /**
     * 计算控件实际高度，参照屏幕分辨率：960*540
     * 
     * @param relativeValue
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    public static int getRealHeightValue(Context context, int relativeValue) {
        int realValue = 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        realValue = (height * relativeValue) / Constant.SCREEN_HEIGHT;
        return realValue;
    }
}
