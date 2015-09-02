
package com.spt.carengine.view.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MyArcListView extends ListView {

    static final String tag = MyArcListView.class.getSimpleName();

    public MyArcListView(Context context) {
        super(context);
    }

    /*
     * @SuppressLint("NewApi") public MyArcListView(Context context,
     * AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context,
     * attrs, defStyleAttr, defStyleRes); init(); }
     */

    public MyArcListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyArcListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
        // setDivider(divider); //null
        // setSelector(sel); // MyArcSelDrawable
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // pirnt("-dispatchDraw-");
        super.dispatchDraw(canvas);
    }

    IArcAble aa;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // pirnt("-onLayout-"+l+","+t+","+r+","+b +","+this );
        // pirnt("-onLayout-"+getLeft()+","+t+","+r+","+b );
        super.onLayout(changed, l, t, r, b);
        // 只要背景图支持弧度，就可以，和选择器无关
        Drawable bg = getBackground();
        if (bg != null && bg instanceof MyArcDrawable) {
            aa = ((MyArcDrawable) bg).getArcAble();
            // pirnt("-aa-"+aa );
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        float xPos = 0; // 有可能无焦点，如果不想交就忽略，否则会出现上下翻到时候消失不见的情况
        if (child != null && aa != null) {
            // int idx = indexOfChild(child);
            // 目前图标都是在前的，暂时只需要移动就可以满足需求
            xPos = aa.getXPosFarY(child.getTop(), child.getBottom());
            xPos -= child.getLeft();
            int cylThin = 30; // 环的厚度也要挪掉
            xPos += cylThin;
            // child.setPadding(left, top, right, bottom);
            // CheckedTextView
            // pirnt("-drawChild-"+idx
            // +","+child.getLeft()+","+child.getTop()+","+child.getRight()+","+getBottom()+",offset:"+xPos
            // );
            if (!Float.isNaN(xPos)) {
                // canvas.save();
                canvas.translate(xPos, 0);
                // canvas.scale( 0.9f , 1.0f );
            }
        }
        boolean b = super.drawChild(canvas, child, drawingTime);
        if (!Float.isNaN(xPos)) {
            // canvas.scale( 1/1.1f , 0f );
            canvas.translate(-xPos, 0);
            // canvas.restore();
        }
        return b;
    }

    void pirnt(Object o) {
        // System.out.println( String.valueOf( o ) );
        Log.d(tag, String.valueOf(o));
    }

}
