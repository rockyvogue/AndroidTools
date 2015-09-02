
package com.spt.carengine.view.arc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public interface IArcAble extends MyArcDrawValues {

    public RectF getOvalRectF();

    public double getZC();

    // 获得指定角度的点坐标
    public PointF getDegPos(float deg);

    // 获得指定y轴点的角度
    public double getPosDeg(float yyy);

    // X距离原点的距离
    // 获得指定y轴相交的点坐标，如无交点就NaN，需要判断
    public double getXAbsPos(double yyy);

    public double getXPos(float yyy);

    // 获取距离弧最远的y点，用于绘制矩形时不与弧相交。如果为NaN就是没有交点，需要注意
    public int getFarYPos(int top, int bottom);

    // 获取距离弧最远的y点的x绝对坐标，如果没有焦点就返回中心点 cx
    public float getXPosFarY(int top, int bottom);

    public void drawRing(Canvas canvas, Paint p, float cx, float cy);

}
