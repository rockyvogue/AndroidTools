
package com.spt.carengine.view.arc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

public class ArcHelp implements IArcAble, MyArcDrawValues {

    static final String tag = ArcHelp.class.getSimpleName();

    /** 主菜单用，弧上无环，选择器带环，环沿着弧绘制 */
    public static final int MODEL_MAIN = 0;
    /** 子菜单用，弧中间有环，选择器带环，环隔弧一段距离绘制，不重叠 */
    public static final int MODEL_SEC = 1;

    RectF rf;
    float jd;
    double rx; // x半轴长 即x轴半径
    double ry; // y半轴长 即y轴半径
    double rx2;
    double ry2;
    double cx; // 原点
    double cy;
    // 偏移量
    int mOffset;

    public ArcHelp() {
    }

    public ArcHelp(RectF oval) {
        setOval(oval);
    }

    public void setModel(int model) {
        switch (model) {
            case MODEL_MAIN:
                mOffset = 30; // ?
                break;
            case MODEL_SEC:
                break;
            default:
                break;
        }
        // invalidateSelf();
    }

    public void setOval(RectF oval) {
        this.rf = new RectF(oval);
        // 2*pi*r 2*100*pi
        rx = oval.width() * 0.5f;
        ry = oval.height() * 0.5f;
        rx2 = rx * rx;
        ry2 = ry * ry;
        double rxH = rx * 0.5f;
        double ryH = ry * 0.5f;
        cx = oval.centerX();
        cy = oval.centerY();
        jd = (float) Math.sqrt(rxH * rxH + ryH * ryH);
        // System.out.println( rx+","+ry+"||"+jd+"||"+cx+","+cy );
    }

    public RectF getOvalRectF() {
        return rf;
    }

    public double getZC() {
        double rs = Math.min(rx, ry); // 短半轴
        double cdc = Math.abs(rx - ry); // 长短轴差
        // double r = ( 2.0*Math.PI*dbz+ 4.0 * cdc );
        // System.out.println( dbz+","+cdc+","+r );
        return (2.0 * Math.PI * rs + 4.0 * cdc); // 周长
    }

    // 获得指定角度的点坐标
    public PointF getDegPos(float deg) {
        double ppp = deg % 360.0 / 360.0 * (2.0 * Math.PI); // 转换成弧度
        // 长轴*cos角度。 角度需转换成 pi 值的N分之几
        double xx = (cx + rx * Math.cos(ppp));
        double yy = (cy + ry * Math.sin(ppp));
        return new PointF((float) xx, (float) yy);
    }

    // 获得指定y轴点的角度
    public double getPosDeg(float yyy) {
        double ax = getXAbsPos(yyy); // x距离
        if (!Double.isNaN(ax)) {

            // 椭圆不能用tan算，要算离心角
            // double syd= Math.abs( cy - yyy ); //y距离
            // double tanA = syd / ax ;
            // double piA = Math.atan( tanA );

            double s_r = Math.min(rx, ry); //
            double cosB = ax / s_r;
            double piA = Math.acos(cosB);
            // TODO 需要确定正负
            return piA / (Math.PI * 2.0) * 360.0;
        }
        return ax;
    }

    // X距离原点的距离
    // 获得指定y轴相交的点坐标，如无交点就NaN
    public double getXAbsPos(double yy) {
        // 以cx,cy 为原点
        yy -= cy; // 0-> -50, 50->0 // 703 - 305 = 398
        // (x*x) / (a*a) + (y*y) / (b*b) =1 // 求 x*x = ( 1- (y*y) / (b*b) ) *
        // (a*a)
        // absyyy = ( 1.0 - ( yyy*yyy) /( ry*ry) ) * (rx*rx ) ;
        yy = (1.0 - (yy * yy) / ry2) * rx2;
        // println( "yy:"+yy+",cy:"+cy+","+ry2+","+rx2 );
        // 负数不可以开方，如果是负数就会得到 NaN ，需要判断
        return (Math.sqrt(yy));
    }

    public double getXPos(float yyy) {
        return cx - getXAbsPos(yyy); // TODO 正负两个点
    }

    // 获取距离弧最远的y点，用于绘制矩形时不与弧相交
    public int getFarYPos(int top, int bottom) {
        // 看矩形相当原点的位置
        int yPos = 0;
        if (bottom < cy) { // 底点没到以顶为准
            yPos = top;
        } else if (top > cy) { // 顶点过了以底为准
            yPos = bottom;
        } else { // 中间就看谁离原点远
            if (Math.abs(cy - top) > Math.abs(bottom - cy)) {
                yPos = top;
            } else {
                yPos = bottom;
            }
        }
        // println( "top:"+top+",bottom:"+bottom+",y:"+yPos );
        return yPos;
    }

    public float getXPosFarY(int top, int bottom) {
        int farYPos = getFarYPos(top, bottom);
        float x = (float) getXPos(farYPos);
        if (Float.isNaN(x)) {
            return (float) cx; // 如果没有交点，就以中心点为基准，弧形滚动的时候视觉比较合理
        } else {
            return x;
        }
    }

    public void drawRing(Canvas canvas, PointF point) {
        drawRing(canvas, new Paint(), point);
    }

    public void drawRing(Canvas canvas, Paint p, PointF point) {
        drawRing(canvas, p, point.x, point.y);
    }

    public void drawRing(Canvas canvas, float cx, float cy) {
        drawRing(canvas, new Paint(), cx, cy);
    }

    public void drawRing(Canvas canvas, Paint p, float cx, float cy) {
        // p.setColor( nColor );
        p.setStyle(Style.FILL);
        canvas.drawCircle(cx, cy, hr - hjx - hhd * 0.5f, p); // thumb内框
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(hhd);
        canvas.drawCircle(cx, cy, hr, p); // thumb外框
    }

    public void drawRing(Canvas canvas, CharSequence txt, PointF point) {
        drawRing(canvas, new Paint(), txt, point);
    }

    public void drawRing(Canvas canvas, Paint p, CharSequence txt, PointF point) {
        drawRing(canvas, p, txt, point.x, point.y);
    }

    public void drawRing(Canvas canvas, CharSequence txt, float cx, float cy) {
        drawRing(canvas, new Paint(), txt, cx, cy);
    }

    public void drawRing(Canvas canvas, Paint p, CharSequence txt, float cx,
            float cy) {
        // < 需要设置得大一点，数字则需要小一点
        // 如果含有, 则又不一样，目前只考虑数字和符号<
        // txt ="8";

        boolean isDigOnly = !"<".equals(txt);
        // p.setColor( nColor );
        p.setAntiAlias(true);
        p.setStyle(Style.STROKE);

        p.setStrokeWidth(hhd);
        canvas.drawCircle(cx, cy, hr, p); // thumb外框
        p.setTypeface(Typeface.DEFAULT_BOLD);

        if (isDigOnly) {
            p.setTextSize(35); // 个位数字目前默认最佳字体是35
        } else {
            p.setTextSize(50); // 50
        }

        float w = p.measureText(txt, 0, txt.length());
        // FontMetricsInt fmi = p.getFontMetricsInt();
        FontMetrics fm = p.getFontMetrics();

        /*
         * p.setStrokeWidth( 1 ); p.setTextSize(15); canvas.drawLine( cx, cy,
         * getWidth(), cy, p ); canvas.drawText("e", cx+140, cy, p );
         * canvas.drawLine( cx, cy+fm.top , getWidth(), cy+fm.top , p );
         * canvas.drawText("p", cx+140, cy+fm.top , p ); canvas.drawLine( cx,
         * cy+fm.bottom , getWidth(), cy+fm.bottom , p ); canvas.drawText("m",
         * cx+140, cy+fm.bottom , p ); canvas.drawLine( cx, cy+fm.ascent ,
         * getWidth(), cy+fm.ascent , p ); canvas.drawText("a", cx+150,
         * cy+fm.ascent , p ); canvas.drawLine( cx, cy+fm.descent , getWidth(),
         * cy+fm.descent , p ); canvas.drawText("d", cx+150, cy+fm.descent , p
         * ); canvas.drawLine( cx, cy+fm.ascent+fm.descent , getWidth(),
         * cy+fm.ascent+fm.descent , p ); //数字顶点 canvas.drawText("-e-", cx+140,
         * cy+fm.ascent+fm.descent , p ); float fff= fm.ascent * 0.5f ;
         * canvas.drawLine( cx, cy+ fff , getWidth(), cy+ fff , p ); //
         * canvas.drawText("-bam-", cx+120, cy+ fff , p ); fff= ( fm.ascent
         * +fm.descent) * 0.5f ; canvas.drawLine( cx, cy+ fff , getWidth(), cy+
         * fff , p ); //符号的中点 canvas.drawText("-em-", cx+110, cy+ fff , p );
         * p.setTextSize(95);
         */

        // 改用填充，把字体调大，效果好点
        p.setStyle(Style.FILL);
        // 字符不能太厚，不然就叠在一起了
        // p.setStrokeWidth( hhd-5 );
        // canvas.drawText( "96,<顶"+fm.ascent , cx , cy , p );

        double h = 0;
        h = -fm.ascent - fm.descent + 1; // baseline - asc 只算之间的距离
        if (!isDigOnly) {
            h -= 4;
        }
        cy += h * 0.5;
        cx -= w * 0.5;
        // canvas.drawText(
        // fm.descent+","+fm.ascent+","+fm.bottom+","+fm.top+","+h , cx, cy, p
        // );
        // canvas.drawText( w+","+h+"<"+ (h*0.5) , cx, cy, p );
        canvas.drawText(txt, 0, txt.length(), cx, cy, p);
    }

    void println(Object o) {
        // System.out.println( String.valueOf( o ) );
        Log.d(tag, String.valueOf(o));
    }

}
