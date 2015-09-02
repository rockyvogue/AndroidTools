
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
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyArcViewE extends View implements MyArcDrawValues {

    static final String tag = MyArcViewE.class.getSimpleName();

    /*
     * @SuppressLint("NewApi") public MyArcViewE(Context context, AttributeSet
     * attrs, int defStyleAttr, int defStyleRes) { super(context, attrs,
     * defStyleAttr, defStyleRes); init(); }
     */

    public MyArcViewE(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyArcViewE(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyArcViewE(Context context) {
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

    /** 运动路径 */
    Path pt;
    /** 外边框路径 */
    Path ptW;
    Paint p;
    PathMeasure pm;
    /** 路径动画起始点（弧的启止点） */
    PointF startP;
    /** 路径动画止始点（弧的启止点） */
    PointF endP;
    /** 中心点 */
    PointF centerP;
    // 正负角度
    float startAngle = 45;
    float sweepAngle = 275;

    float lineHeight = defLineHeight;
    float distance = 0;

    ArcHelp ah; //
    /** 当前位于第几行 */
    int countLine;

    String dbgStr = "";

    /** 是否绘制底部环 */
    boolean drawDownCircle = false;
    /** 是否绘制中间环 */
    boolean drawCentreCircle = true;
    /** 是否自动调整角度以自适应屏幕 */
    boolean autoAjustAngle = true;

    void init() {
        pt = new Path();
        ptW = new Path();
        pm = new PathMeasure();
        startP = new PointF();
        endP = new PointF();
        centerP = new PointF();
        p = new Paint();
        p.setAntiAlias(true);

    }

    // 初始化测量
    void doInitVal(int w, int h, Canvas canvas) {
        pirnt("-onBoundsChange-:" + w + "," + h);

        // 极值点要算上环的最外边
        float offset = wk * 0.5f;
        // 环最外边也要偏移掉，避免出现环被吃掉
        offset = (int) Math.max(offset, hr + hhd * 0.5);

        final int C_W = 410;
        final int C_H = 708;
        // 椭圆最佳尺寸 高707(即中心为 354)，宽410，y上偏116，容器高508,中心254。中心上偏 113 ，即实际上偏3个像素
        float cH = h * 0.5f - (C_H * 0.5f);
        cH += -15; // 暂时偏移多点看效果

        RectF oval = isDebug ? new RectF(offset, offset, w - offset, w - offset)// 圆
                : new RectF(offset, cH, offset + C_W, cH + C_H);// 偏移外边框

        offset = jx + hd * 0.5f; // 缩小或偏移掉
        // 测试用缩小，正式的用偏移，因为椭圆大小固定了
        if (isDebug) {
            dbgStr += "inset:" + offset + "\n";
            oval.inset(offset, offset); // 缩小掉外边框
        } else { // 椭圆是在最内边的测绘的
            dbgStr += "offset:" + offset + "\n";
            oval.offset(offset * 2, 0); // 偏移到最内边
            oval.inset(-offset, -offset); // 再扩大一圈
        }

        ah = new ArcHelp(oval);

        if (autoAjustAngle) {
            // 自动最大化满角度
            int bd = hr + (int) (hhd * 0.5);
            bd += 5; // XXX 稍微偏点像素，或者用边框

            int y = bd; // 计算角度
            double posDeg = ah.getPosDeg(y); // y上轴角度
            dbgStr += "startY:" + y + ",deg:" + posDeg + "\n";
            y = h - bd;
            double posDeg1 = ah.getPosDeg(y); // y下轴角度
            dbgStr += "endY:" + y + ",deg:" + posDeg1 + "\n";
            startAngle = (float) (90.0 + (90.0 - posDeg1));
            sweepAngle = (float) (posDeg1 + posDeg);
            dbgStr += "from:" + startAngle + ",+:" + sweepAngle + "\n";
            // 调试用辅助线
            // canvas.drawArc(oval, 180.0f , (float)posDeg , true , p );
        }

        // pt.addOval(oval, Direction.CW );
        pt.addArc(oval, startAngle, sweepAngle); // 移动路径

        dbgStr += "-oval-:" + oval + "\n";

        oval.inset(-offset, -offset); // 恢复到外边框，先绘制外边框

        // ptW.addOval(oval, Direction.CW );
        ptW.addArc(oval, startAngle, sweepAngle);

        // 计算最高点和最低点，并得出其最大容纳的角度
        pm.setPath(pt, false);

        float wr = (hd + jx * 2) * 0.5f;
        // 最后一个点
        float dis = pm.getLength(); // pes * (startAngle+sweepAngle); //终点
        RectF rf = new RectF();

        float[] pos = new float[2];
        float[] tan = new float[2];

        if (pm.getPosTan(dis, pos, tan)) {
            endP.set(pos[0], pos[1]);
            rf.set(pos[0] - wr, pos[1] - wr, pos[0] + wr, pos[1] + wr);
            ptW.arcTo(rf, startAngle + sweepAngle, 180);
        } else {
            endP = ah.getDegPos(startAngle + sweepAngle);
        }

        oval.inset(offset * 2, offset * 2); // 再绘制内边框

        ptW.arcTo(oval, startAngle + sweepAngle, -sweepAngle);

        dis = 0; // pes * startAngle ; //起点
        if (pm.getPosTan(dis, pos, tan)) {
            startP.set(pos[0], pos[1]);
            rf.set(pos[0] - wr, pos[1] - wr, pos[0] + wr, pos[1] + wr);
            ptW.arcTo(rf, startAngle + 180, 180);
            // 裁掉重叠部分
        } else {
            startP = ah.getDegPos(startAngle);
        }

        dis = pm.getLength() * 0.5f;
        if (dis != 0) {
            if (pm.getPosTan(dis, pos, tan)) {
                centerP.set(pos[0], pos[1]);
            }
        } else {
            // 取一半角度
            centerP = ah.getDegPos(startAngle + sweepAngle * 0.5f);
        }
        // 行高动态计算或者指定的，目前动态计算出来
        // lineHeight = ;
        //
        ptW.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int h = getHeight();

        int left = getLeft();
        int top = getTop();
        int right = getRight();
        int bottom = getBottom();

        if (ptW.isEmpty()) {

            p.setStyle(Style.STROKE);
            p.setStrokeWidth(2);

            doInitVal(getWidth(), h, canvas);
        }

        p.setStyle(Style.STROKE);
        p.setStrokeWidth(1);
        for (float i = endP.y; i < startP.y; i += lineHeight) {
            canvas.drawLine(0, i, h, i, p);
        }

        float pes = pm.getLength() / sweepAngle; // 每一片
        String ap = pm.getLength() + "," + pes + ":";

        float[] pos = new float[2];
        float[] tan = new float[2];
        if (pm.getPosTan(distance, pos, tan)) {
            ap += "[" + (int) pos[0] + "," + (int) pos[1] + "," + tan[0] + ","
                    + tan[1] + "]";
            canvas.drawCircle(pos[0], pos[1], 9, p); // 绘制环
            // drawRing(canvas, pos[0] , pos[1] );
        }

        if (ah != null) {
            float ynow = countLine * lineHeight + lineHeight * 0.5f + endP.y;
            float xnow = (float) ah.getXPos(ynow);
            // canvas.drawCircle( xnow , ynow , 11 , p);
            ah.drawRing(canvas, p, xnow, ynow);
            countLine++;
            if ((ynow + lineHeight) > startP.y) {
                countLine = 0;
            }
        }

        boolean dwCircle = drawDownCircle | drawCentreCircle;
        if (dwCircle) {
            canvas.save();
            // 先裁减掉圆弧覆盖部分
            Path ppp = new Path();
            int hd = hr/* +hhd*0.5f */; // 目前可以先不考虑厚度，因为反正被绘制后看不见
            if (drawDownCircle) {
                ppp.addCircle(startP.x, startP.y, hd, Path.Direction.CW);
            }
            if (drawCentreCircle) {
                ppp.addCircle(centerP.x, centerP.y, hd, Path.Direction.CW);
            }
            // rg.setPath(ppp, new Region( (int)(startP.x-hr) ,
            // (int)(startP.y-hr) , (int)(startP.x+hr), (int)(startP.y+hr) ) );
            canvas.clipPath(ppp, Region.Op.DIFFERENCE);
        }

        p.setStyle(Style.STROKE);
        p.setColor(wColor);
        p.setStrokeWidth(wk);
        canvas.drawPath(ptW, p); // 边框

        p.setColor(nColor);
        p.setStrokeWidth(hd);
        canvas.drawPath(pt, p); // 路径
        p.setStyle(Style.FILL);
        // 为了路径平滑加个圆角
        canvas.drawCircle(startP.x, startP.y, hd * 0.5f, p);
        canvas.drawCircle(endP.x, endP.y, hd * 0.5f, p);

        if (dwCircle) {
            canvas.restore();
            if (drawDownCircle) {
                ah.drawRing(canvas, p, startP);
            }
            if (drawCentreCircle) {
                ah.drawRing(canvas, p, "<", centerP);
            }
        }

        // drawRing(canvas, getWidth()/2 , getHeight()/2 , "<" );
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(1);
        p.setTextSize(15);
        p.setTypeface(Typeface.DEFAULT);
        p.setColor(Color.BLACK);

        // canvas.drawCircle( centerP.x , centerP.y , 10 , p );

        float txtX = wk * 2 + jx * 2 + hd + 10;
        float txtY = getHeight() * 0.5f - 10;
        canvas.drawText(ap + getTxt(left, top, right, bottom), txtX, txtY, p);
        String[] str = dbgStr.split("\n");
        if (str != null) {
            for (String string : str) {
                canvas.drawText(string, txtX, txtY += 20, p);
            }
        }
        /*
         * if( pm.getLength()>0 ){ postInvalidateDelayed(100); }
         */

        distance += lineHeight;
        if (distance >= pm.getLength()) {
            distance = 0;
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
