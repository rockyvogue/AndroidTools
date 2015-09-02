
package com.spt.carengine.voice.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * <功能描述> 将dp转换为px，或者将dip转换为px
 * 
 * @author Administrator
 */
public class VoiceUtils {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());

        return (int) scale;
    }

}
