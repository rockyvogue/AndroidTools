
package com.spt.carengine.fragment;

import com.spt.carengine.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FMReplaceSeekbarView extends View {
    public FMReplaceSeekbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FMReplaceSeekbarView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff00ff00);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        int heightPX = dm.heightPixels;
        widthPX = dm.widthPixels;

        bitmap = Bitmap.createBitmap(widthPX, heightPX, Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    private PointF point = new PointF();
    private PointF endprint = new PointF();
    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private int widthPX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Float ex = event.getX();
        Float ey = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                point.x = 100;
                point.y = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                endprint.x = ex;
                endprint.y = 0;
                // canvas.drawLine(point.x, point.y, endprint.x, endprint.y,
                // paint);
                point.x = endprint.x;
                point.y = endprint.y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, 30, 500, paint);
        Bitmap mbitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.fm_channel_but_n);
        if(point.x <100){
            point.x = 100;
        }
        if(point.x> 574){
            point.x = 574;
        }
        
        canvas.drawBitmap(mbitmap, point.x, point.y, paint);
        

    }

}
