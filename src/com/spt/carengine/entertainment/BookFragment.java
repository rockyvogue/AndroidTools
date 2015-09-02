
package com.spt.carengine.entertainment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spt.carengine.R;
import com.spt.carengine.entertainment.interfaces.IPlayControl;
import com.spt.carengine.service.MusicService;

public class BookFragment extends Fragment {

    private Activity mActivity;
    private View rootView;
    private IPlayControl mPlayControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        
        Intent service=new Intent(mActivity.getApplicationContext(),MusicService.class);
        mActivity.startService(service);
        mActivity.bindService(service, conn, Context.BIND_AUTO_CREATE);
    }
    
    private ServiceConnection conn=new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            
            
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
         // 控制服务的对象
            mPlayControl = (IPlayControl) service;
            
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_et_book, null);

        initView();
        
        return rootView;
    }

    private void initView() {

    }
}
