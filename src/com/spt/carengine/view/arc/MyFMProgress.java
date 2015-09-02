
package com.spt.carengine.view.arc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class MyFMProgress extends ProgressBar {

    @SuppressLint("NewApi")
    public MyFMProgress(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyFMProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyFMProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFMProgress(Context context) {
        super(context);
    }

}
