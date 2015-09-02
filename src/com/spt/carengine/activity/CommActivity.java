
package com.spt.carengine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.spt.carengine.btcall.BtReceiver;
import com.spt.carengine.define.CALLTYPE;

// * 此类实现一些Activity的公共类
public class CommActivity extends Activity {

    public boolean bMainAty = false;


    public Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        m_context = this;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    // 控制视图的显示/隐藏
    public void ShowView(int nViewId, int visibility) {
        View view = findViewById(nViewId);
        if (view == null) {
            return;
        }
        view.setVisibility(visibility);
    }

    // 设置试图的点击事件监听
    public void SetOnClicLister(Context content, int nViewId,
            OnClickListener onclickListener) {
        try {
            View view = findViewById(nViewId);
            if (view == null) {
                return;
            }
            view.setOnClickListener(onclickListener);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // 网络连接状态
    public boolean NetConnectState() {
        return isNetworkAvailable(this);
    }

    public boolean isNetworkAvailable(Context context) {
        /** 获取网络系统服务 **/
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        } else {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 显示Toast提示信息
    public void ShowToast(String sText, int nduration) {
        // Toast.makeText(this, sText, nduration).show();
    }

    // 获取连接状态
    private int GetNetConnectState() {
        int nState = NetConnectState() ? 1 : 0;
        return nState;

    }

    public void NetonClick() {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        }
    }

    public void BtCall(String sPhoneNum) {

        BtReceiver.CallType(this);

        if (CALLTYPE.BTCALL) {
            if (BtReceiver.GetBtState() != 2) {
                Toast.makeText(this, "无法通话", Toast.LENGTH_SHORT).show();
            } else {
                BtReceiver.Call(this, sPhoneNum);
            }
        } else {
            BtReceiver.Call(this, sPhoneNum);
        }
    }

}
