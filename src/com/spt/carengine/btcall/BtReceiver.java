
package com.spt.carengine.btcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.spt.carengine.define.CALLTYPE;
import com.spt.carengine.define.Define;

//蓝牙拨打电话类
public class BtReceiver extends BroadcastReceiver {

    // private Context m_context = null;
    public static int m_nBtState = 0;// 蓝牙状态

    public static void CallType(Context context) {
        CALLTYPE.BTCALL = false;
    }

    /** 电话呼叫 */
    public static boolean Call(Context context, String sPhone) {
        {
            Intent intent = new Intent();
            intent.setAction(Define.ACTION_GPS_UPLOAD);
            intent.putExtra("upload_location", false);
            context.sendOrderedBroadcast(intent, null);
        }
        if (CALLTYPE.BTCALL) {
            Intent intent = new Intent();
            intent.putExtra("phoneNum", sPhone); // String phoneNum电话号码
            intent.setAction("com.ACTION_BTCALL");
            intent.putExtra("name", "ACTION_BTCALL");
            context.sendOrderedBroadcast(intent, null);
        }
        return true;
    }

    /** 查询蓝牙状态 */
    public static boolean ReqBtState(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.ACTION_GETBTSTATE");
        context.sendOrderedBroadcast(intent, null);
        return true;
    }

    public static int GetBtState() {
        return m_nBtState;
    }

    public static String GetBtStateString() {
        String sResult[] = new String[] {
                "蓝牙连接已经断开", "蓝牙正在链接", "蓝牙已经链接", "蓝牙未开启", "正在打开蓝牙"
        };
        // Toast.makeText(context, sResult[m_nBtState],0).show();
        int nState = m_nBtState;
        if (nState >= sResult.length) {
            nState = sResult.length - 1;
        }
        return sResult[nState];
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String sAction = intent.getAction();
        if (sAction == null) {
            return;
        }

        Intent service = new Intent(context, BTService.class);
        context.startService(service);
    }

    private void PlayMusic() {

    }

    private void PauseMusic() {

    }
}
