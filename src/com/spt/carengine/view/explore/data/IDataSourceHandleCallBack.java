
package com.spt.carengine.view.explore.data;

/**
 * @author Rocky
 * @Time 2015年8月5日 下午3:58:16
 * @description 请求回调数据的接口
 */
public interface IDataSourceHandleCallBack {
    public static final int RESULT_SUCCESS = 0;

    public static final int RESULT_FAIL = 1;

    public void finishAcceptDataHandle(int result);
}
