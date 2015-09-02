
package com.spt.carengine.view.arc;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

/*
 *  随弧度动态改变绘制位置的环（目前只是x轴）.
 *  环的位置通过设置边距来完成
 */
public class MyArcSelDrawable extends LayerDrawable implements MyArcDrawValues {

    static final String tag = MyArcSelDrawable.class.getSimpleName();
    IArcAble aa;

    public MyArcSelDrawable(Drawable[] layers, IArcAble aa) {
        super(layers);
        this.aa = aa;
        // TODO 以默认 R.drawable.list_selector 为原型拷贝创建
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        // println( this+"--onBoundsChange--"+bounds );
        // Log.d(tag, "--onBoundsChange--"+bounds+","+this , new
        // Throwable("onBoundsChange") );
        Rect r = new Rect(bounds);

        // 不采用不过弧的方式，目前采用过弧
        // int yPos = aa.getFarYPos( bounds.top, bounds.bottom );
        // double xPos = aa.getXPos( yPos );
        // r.left = (int)xPos + 12 ; //还需要偏移环厚度

        float cy = bounds.centerY();
        double xPoint = aa.getXPos(cy);
        // x点坐标 加/减环半径和 一半厚度为其区域
        int cylThin = 30;
        int ld = (int) xPoint - bounds.left - cylThin;
        int rd = bounds.right - (int) xPoint - cylThin;
        // TODO，目前暂定序号为0的，可修改成按ID索引
        setLayerInset(0, ld, 0, rd, 0);
        // 外框为保持平衡用高一半与环半径的差
        int wld = (int) (bounds.height() * 0.5 - cylThin);
        setLayerInset(1, ld - wld, 0, 0, 0);

        super.onBoundsChange(bounds);

        Drawable dd = findDrawableByLayerId(android.R.id.icon2);
        // getId(0);
        // bounds.left = (int)xPos;
        // dd.setBounds(bounds);
        /*
         * println( this+"--onBoundsChange--"+bounds +",x->" + xPoint +","+dd );
         * println(
         * this+"{"+dd.getMinimumHeight()+","+dd.getMinimumWidth()+"},{"
         * +dd.getIntrinsicWidth()+","+dd.getIntrinsicHeight()+"}"
         * +","+ld+","+rd+","+wld );
         */
        // dd.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        // println( "--setBounds--"+left+","+top+","+right+","+bottom );
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        // println( "--draw--" );
        super.draw(canvas);
    }

    // 递归寻找 StateListDrawable 和 LayerDrawable 中的 本对象
    public static IArcAble findArcAble(Drawable dw) {
        if (dw == null)
            return null;
        if (dw instanceof MyArcSelDrawable) {
            return ((MyArcSelDrawable) dw).aa;
        }
        if (dw instanceof StateListDrawable) {
            println("--StateListDrawable--");
            StateListDrawable sld = (StateListDrawable) dw;
            int stateCount = sld.getStateCount();
            for (int i = 0; i < stateCount; i++) {
                IArcAble ia = findArcAble(sld.getStateDrawable(i));
                if (ia != null)
                    return ia;
            }
        }
        if ((dw instanceof LayerDrawable)) {
            println("--LayerDrawable--");
            LayerDrawable ld = (LayerDrawable) dw;
            int size = ld.getNumberOfLayers();
            Drawable[] layers = new Drawable[size];
            for (int i = 0; i < layers.length; i++) {
                IArcAble ia = findArcAble(ld.getDrawable(i));
                if (ia != null)
                    return ia;
            }
        }
        return null; //
    }

    public static void printDW(Drawable dw, String txt) {
        if (dw == null) {
            println(txt + "--dw is null--" + dw);
            return;
        }
        if (dw instanceof StateListDrawable) {
            StateListDrawable sld = (StateListDrawable) dw;
            int stateCount = sld.getStateCount();
            println(txt + "-StateListDrawable-" + sld + ",count:" + stateCount);
            for (int i = 0; i < stateCount; i++) {
                // txt += "{"+i+"}" /*+ Arrays.toString( sld.getStateSet( i )
                // )*/ ;
                printDW(sld.getStateDrawable(i), txt + "{" + i + "}");
            }
        } else if (dw instanceof LayerDrawable) {
            LayerDrawable ld = (LayerDrawable) dw;
            int size = ld.getNumberOfLayers();
            println(txt + "-LayerDrawable-" + ld + ",size:" + size);
            Drawable[] layers = new Drawable[size];
            for (int i = 0; i < size; i++) {
                // txt += "[id:"+ ld.getId( i ) +"]";
                printDW(ld.getDrawable(i), txt + "[" + i + ":" + ld.getId(i)
                        + "]");
            }
        } else {
            println(txt + "--dw is--" + dw);
        }
    }

    // 把 LayerDrawable 替换成这个。其余原样返回
    public static Drawable copyFrom(Drawable dw, IArcAble aa) {
        if (dw == null)
            return dw;
        // TODO 每个有可能发生改变的 Drawable 都要重新拷贝，否则会造成状态或者边距重用
        Drawable dst = null;
        if (dw instanceof StateListDrawable) {
            StateListDrawable sld = (StateListDrawable) dw;
            int stateCount = sld.getStateCount();
            // println( "--StateListDrawable--"+sld+",count:"+stateCount );
            // 状态需要重新拷贝才有用，部分拷贝会出问题
            // 反射或者多套一层
            StateListDrawable sldCopy = new StateListDrawable();
            for (int i = 0; i < stateCount; i++) {
                dst = copyFrom(sld.getStateDrawable(i), aa);
                // if( dst!=null ) return dst; // ? 处理完成一个就ok？
                sldCopy.addState(sld.getStateSet(i), dst);
            }
            return sldCopy;
        }
        if (dw instanceof MyArcSelDrawable) {
            // println( "--MyArcSelDrawable--"+dw );
            return dw;
        }
        if (dw instanceof LayerDrawable) {
            LayerDrawable ld = (LayerDrawable) dw;
            int size = ld.getNumberOfLayers();
            // println( "--LayerDrawable--"+ld +",size:"+size );
            Drawable icon2 = ld.findDrawableByLayerId(android.R.id.icon2);
            // println( "icon2:"+icon2 );
            if (null != icon2) {
                int idx = 0;
                Drawable[] layers = new Drawable[size];
                for (int i = 0; i < size; i++) {
                    // dst= copyFrom( ld.getDrawable(i) , aa );
                    // XXX 目前可以不用考虑递归来完成拷贝，目前到这层就已经完结
                    layers[i] = ld.getDrawable(i); // dst
                }
                MyArcSelDrawable msd = new MyArcSelDrawable(layers, aa);
                // Log.d( tag, "--MySelDrawable--"+size , new
                // Throwable("MySelDrawable") );
                for (int i = 0; i < size; i++) {
                    msd.setId(i, ld.getId(i));
                    // println( "idx "+i+"-> id "+ld.getId(i)
                    // +","+ld.getDrawable(i) );
                }
                // 其余原样拷贝
                /*
                 * msd.setLevel( ld.getLevel() ); msd.setAlpha( ld.getAlpha() );
                 * msd.setBounds( ld.getBounds() ); msd.setColorFilter(
                 * ld.getColorFilter() ); msd.setState( ld.getState() );
                 */
                // XXX !!!! 千万不要改变原有结构，不然会导致后续使用该资源的时候乱掉
                // boolean b = ld.setDrawableByLayerId( android.R.id.icon2 , msd
                // ); // 替换
                // ld.setId( idx , idx ); //移除 android.R.id.icon2
                // println(" set "+b );

                return msd;
            } else {
                // 有这个 icon2 才替换
                // FIXME 没有还需要往下层解析添加
                return dw;
            }
        }
        // println( "--dw is--"+dw );
        return dw;
    }

    static void println(Object o) {
        // System.out.println( String.valueOf( o ) );
        println(o, null);
    }

    static void println(Object o, Throwable e) {
        Log.d(tag, String.valueOf(o), e);
    }
}
