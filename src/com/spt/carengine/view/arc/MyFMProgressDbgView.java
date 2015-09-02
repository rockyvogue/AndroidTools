
package com.spt.carengine.view.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MyFMProgressDbgView extends View {

    /*
     * @SuppressLint("NewApi") public MyFMProgressDbgView(Context context,
     * AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context,
     * attrs, defStyleAttr, defStyleRes); }
     */

    public MyFMProgressDbgView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyFMProgressDbgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFMProgressDbgView(Context context) {
        super(context);
    }

    boolean autoAjust = true;
    // 小刻度
    int xkd = 20;
    int dkd = 10;
    int xkdH = 10;
    int dkdH = 20;

    // 从多少Hz到多少Hz
    long fromHz = 1;
    long toHz = 1;
    long jgHz = 1; // 间隔

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
         * md YD1305S-X 1380 hr YD1385 1099 JR1255S 1489
         */

    }

}
