
package com.spt.carengine.entertainment.interfaces;

import java.io.File;
import java.util.ArrayList;

/**
 * 服务扫描完成之后通知Activity的接口
 * 
 * @author Administrator
 */
public interface OnScanFinishListener {

    void scanFinish(ArrayList<File> data);
}
