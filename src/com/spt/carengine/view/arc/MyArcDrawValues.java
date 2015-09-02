
package com.spt.carengine.view.arc;

public interface MyArcDrawValues {

    /*
     * <pre> 厚度 (移动路径 (外半径 (内半径 中心 (外边框宽 (间隙 间隙(内边框宽 ( 内 | 环 ) </pre>
     */

    /** 环厚度 */
    int hd = 8;
    /** 间隙宽 */
    int jx = 5;
    /** 外边框宽 */
    int wk = 2;
    /*
     * <pre> (半径 中心 ( 厚度 ( 间隙 ( 内 | 环 ) </pre>
     */
    /** 环间隙 */
    int hjx = 4;
    /** 环半径 */
    int hr = 20;
    /** 环厚度 */
    int hhd = hd;

    /** 外边框颜色 */
    int wColor = 0xFF1A429C;
    /** 内框颜色 */
    int nColor = 0xFF5892FF;

    float defLineHeight = 61;
    float defLineDividerHeight = 3; // 5*5 图片高

    boolean isDebug = false;

    /** 主菜单用，弧上无环，选择器带环，环沿着弧绘制 */
    public static final int MODEL_MAIN = 0;
    /** 子菜单用，弧中间有环，选择器带环，环隔弧一段距离绘制，不重叠 */
    public static final int MODEL_SEC = 1;

}
