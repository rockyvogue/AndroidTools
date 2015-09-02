
package com.spt.carengine.btcall;

public interface ICountService {
    /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/
    public int getBTState();
}
