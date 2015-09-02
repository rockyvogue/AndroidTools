
package com.spt.carengine.view.arc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyArcViewDbg extends View implements MyArcDrawValues {

    static final String tag = MyArcViewDbg.class.getSimpleName();

    /*
     * @SuppressLint("NewApi") public MyArcViewDbg(Context context, AttributeSet
     * attrs, int defStyleAttr, int defStyleRes) { super(context, attrs,
     * defStyleAttr, defStyleRes); init(); }
     */

    public MyArcViewDbg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyArcViewDbg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyArcViewDbg(Context context) {
        super(context);
        init();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    };

    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    };

    Path pt;
    Path ptW;
    Paint p;
    PathMeasure pm;
    // 动画起始点
    PointF startP;
    PointF endP;
    //
    int hd = 8; // 厚度
    int jx = 5; // 间隙宽
    int wk = 2; // 外边框宽
    // 正负角度
    float startAngle = 0;
    float sweepAngle = 360;
    int wColor = 0xFF1A429C; // 外边框颜色
    int nColor = 0xFF5892FF;// 内框颜色

    int lineHeight = 40;
    float distance = 0;

    void init() {
        pt = new Path();
        ptW = new Path();
        pm = new PathMeasure();
        startP = new PointF();
        endP = new PointF();
        p = new Paint();
        p.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        p.setStyle(Style.STROKE);
        p.setStrokeWidth(1);

        String dbgStr = "";

        // 椭圆最佳尺寸 高708(即中心为 354)，宽410，y上偏116，容器高508,中心254。中心上偏 113 ，即实际上偏3个像素
        final int C_W = 410;
        final int C_H = 708;
        float offset = 0;
        float cH = getHeight() * 0.5f;
        canvas.drawLine(0, cH, getWidth(), cH, p);
        canvas.drawText("C_line", 50, cH + 20, p);
        cH -= (C_H * 0.5 + 15); // 116); //椭圆中心点偏差

        RectF oval = new RectF(0, 0, 400, 600);
        // new RectF( offset , cH , C_W+offset , cH+C_H ); //椭圆的尺寸定长/高，高度可忽略边距
        pt.addArc(oval, startAngle, sweepAngle);
        // canvas.drawArc(oval, startAngle, sweepAngle, false , p );
        canvas.drawPath(pt, p);

        dbgStr += oval + ",{" + oval.centerX() + "," + oval.centerY() + "}\n";

        pm.setPath(pt, false);
        // 2*pi*r 2*100*pi
        double rx = oval.width() * 0.5f;
        double ry = oval.height() * 0.5f;
        // canvas.drawLine( rx , 0 , rx, getHeight() , p );
        // canvas.drawLine( 0 , ry , getWidth() , ry , p );
        double rxH = rx * 0.5f;
        double ryH = ry * 0.5f;
        float jd = (float) Math.sqrt(rxH * rxH + ryH * ryH);
        float cx = oval.centerX();
        float cy = oval.centerY();
        canvas.drawCircle(cx, cy, 2, p);
        canvas.drawText("原", cx - 10, cy + 20, p);
        canvas.drawCircle(cx - jd, cy, 2, p);
        canvas.drawCircle(cx + jd, cy, 2, p); // 高中心轴的两个交点
        canvas.drawText("短焦", cx + jd - 10, cy + 20, p);
        dbgStr += rx + "," + ry + "||" + jd + "||" + cx + "," + cy + "\n";

        ArcHelp ah = new ArcHelp(oval);

        double dbz = Math.min(rx, ry); // 短半轴
        double cdc = Math.abs(rx - ry); // 长短轴差
        float zc = (float) (2.0 * Math.PI * dbz + 4.0 * cdc); // 周长

        dbgStr += "zc:" + zc + "||" + ah.getZC() + "\n";

        int ccc = 3;
        double ppp = (2.0 * Math.PI) / ccc;
        float xx = 0;
        float yy = 0;
        /*
         * for (int i = 0; i < ccc ; i++) { // 长轴*cos角度。 角度需转换成 pi 值的N分之几 xx=
         * (float)(cx+ rx* Math.cos( i*ppp ) ) ; yy= (float)(cy+ ry* Math.sin(
         * i*ppp ) ); canvas.drawCircle( xx , yy, 2 , p); canvas.drawText( ""+i
         * , xx, yy , p ); }
         */
        /*
         * for (int i = 1; i <= ccc; i++) { PointF pf =ah.getDegPos( 360.0f/i );
         * canvas.drawCircle( pf.x , pf.y , 2 , p); canvas.drawText( ""+i , pf.x
         * , pf.y , p ); }
         */

        // 以cx,cy 为原点
        int yyy = -50; // y轴上取20，算直角 //XXX不一定有交点
        // (x*x) / (a*a) + (y*y) / (b*b) =1 // 求 x*x = ( 1- (y*y) / (b*b) ) *
        // (a*a)
        double dd = (100 * 100) / (rx * rx) + (yyy * yyy) / (ry * ry);
        float xxx = (float) (Math.sqrt((1.0 - (yyy * yyy) / (ry * ry))
                * (rx * rx)));
        if (!Double.isNaN(xxx)) {
            canvas.drawCircle(cx + xxx, cy + yyy, 2, p);
            canvas.drawText("交_中-50", cx + xxx, cy + yyy + 10, p);
            if (xxx != 0) {
                canvas.drawCircle(cx - xxx, cy + yyy, 2, p);
            }
        }

        int ay = 210; // XXX 不一定都有交点
        float ax = (float) ah.getXAbsPos(ay);
        if (!Double.isNaN(ax)) {
            canvas.drawCircle(cx + ax, ay, 2, p);
            canvas.drawText("交_210", cx + ax, ay + 10, p);
            if (ax != 0) {
                canvas.drawCircle(cx - ax, ay, 2, p);
            }
        }

        // 假定高矩形高宽，计算切弧角度
        int vh = 200;
        int bd = 0; // 20; //上偏
        float hhh = 53; // cy - vh *0.5f - bd ;
        ax = (float) ah.getXAbsPos(hhh); // x距离
        canvas.drawLine(0, (float) hhh, getWidth(), (float) hhh, p);
        canvas.drawCircle(cx - ax, hhh, 3, p);//
        canvas.drawText("求_200", cx - ax, hhh - 5, p);//

        double s_r = Math.min(oval.width(), oval.height()) * 0.5; //
        double cosB = ax / s_r;
        double piB = Math.acos(cosB);
        double degB = piB / (Math.PI * 2.0) * 360.0;

        double syd = cy - hhh; // y距离
        double tanA = syd / ax;
        double piA = Math.atan(tanA);
        double degA = piA / (Math.PI * 2.0) * 360.0;
        // 圆心的角度
        dbgStr += "x:" + (cx - ax) + ",y:" + hhh + "\n";
        dbgStr += "dx:" + ax + ",dy:" + syd + "\n";
        dbgStr += "cosB:" + cosB + ",degB:" + degB + "\n";
        dbgStr += "tanA:" + tanA + ",degA" + degA + "\n";
        canvas.drawArc(oval, 0, 180 + (float) degB, true, p);

        canvas.drawLine(oval.centerX(), oval.centerY(), oval.centerX(),
                oval.centerY() - (float) syd, p);
        canvas.drawLine(oval.centerX(), oval.centerY(), oval.centerX() - ax,
                oval.centerY(), p);

        float w_h = (oval.height() - oval.width()) * 0.5f;
        RectF rrr = new RectF(oval.left, oval.top + w_h, oval.right, oval.right
                + w_h);
        // canvas.drawCircle( oval.centerX() , oval.centerY() , radius, paint);
        canvas.drawArc(rrr, 0, 180 + (float) degA, true, p);

        double absyyy = Math.abs(yyy);
        // 得到交点坐标后算角度
        // double cosX = xxx / rx;
        double sinX = absyyy / ry;
        // double pp1=Math.acos( cosX );
        double pp = Math.asin(sinX);
        double deg = ((2.0 * Math.PI) / pp); // 角度

        p.setTextSize(15);
        p.setColor(Color.BLACK);
        float txtX = 20;
        float txtY = 70;
        canvas.drawText("len:" + pm.getLength() + "," + "," + degA // +jd
        , txtX, txtY, p);
        canvas.drawText("wh:{" + xxx + "," + yyy + "},", txtX, txtY += 20, p);
        canvas.drawText("ce{" + cx + "," + cy + "}," + zc, txtX, txtY += 20, p);
        String[] str = dbgStr.split("\n");
        if (str != null) {
            for (String string : str) {
                canvas.drawText(string, txtX, txtY += 20, p);
            }
        }

    }

    public boolean onTouchEvent(android.view.MotionEvent event) {
        // System.out.println( "-onTouchEvent-"+event );
        boolean r = super.onTouchEvent(event);
        if (!r && event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("-onTouchEvent-DOWN-");
            postInvalidateDelayed(100);
            return true;
        }
        return r;
    };

    String getTxt(int... id) {
        String txt = "{";
        for (int i = 0; i < id.length; i++) {
            txt += id[i] + ",";
        }
        txt += "}";
        return txt;
    }

    void pirnt(Object o) {
        // System.out.println( String.valueOf( o ) );
        Log.d(tag, String.valueOf(o));
    }
}
