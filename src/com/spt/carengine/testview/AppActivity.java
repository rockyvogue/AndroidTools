
package com.spt.carengine.testview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class AppActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction bt = fm.beginTransaction();

        bt.add(android.R.id.content, new AppListFragment(), "AppListFragment");
        bt.commit();

        Log.d("Test", "enter");
    }

    ViewGroup v;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                && event.getRepeatCount() == 0) {
            turnOnOrOffKey();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // System.out.println( "-dispatchTouchEvent-"+ev );
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            turnOnOrOffKey();
        }
        return super.dispatchTouchEvent(ev);
    }

    void turnOnOrOffKey() {
        // boolean bol= !v.isFocusable();
        // v.setFocusable( bol );
        // v.setFocusableInTouchMode( bol );
        /*
         * int descendantFocusability = v.getDescendantFocusability(); if(
         * descendantFocusability==ViewGroup.FOCUS_BLOCK_DESCENDANTS ){
         * System.out.println( "setFocusable: FOCUS_AFTER_DESCENDANTS" );
         * v.setDescendantFocusability( ViewGroup.FOCUS_AFTER_DESCENDANTS );
         * }else{ System.out.println( "setFocusable: FOCUS_BLOCK_DESCENDANTS" );
         * v.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS ); }
         */
    }

    protected void println(Object o) {
        System.out.println(String.valueOf(o));
    }

}
