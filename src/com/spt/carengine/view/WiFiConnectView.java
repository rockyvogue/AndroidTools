
package com.spt.carengine.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.view.DialogView.OnDialogListener;

import de.greenrobot.event.EventBus;

public class WiFiConnectView extends PercentRelativeLayout {
    private WiFiConnectAdapter mWiFiConnectAdapter;
    private LoadingView mLoading;
    private ReturnBarView mReturnBarView;
    private ListView listview;

    public WiFiConnectView(Context context) {
        super(context);
    }

    public WiFiConnectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WiFiConnectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listview = (ListView) findViewById(R.id.wifi_connect_listView);
        mLoading = (LoadingView) findViewById(R.id.loading_dialog);
        mReturnBarView = (ReturnBarView) findViewById(R.id.back_bar);
        // mReturnBarView.setOncheckBoxListener(changeListener,
        // mOnClickListener);
        if (listview == null || mLoading == null || mReturnBarView == null) {
            throw new InflateException("Miss a child?");
        }
        mReturnBarView.init(ReturnBarView.TYPE_WIFI_CONNECT);
        // mReturnBarView.setcheckBoxState(fmswitch.equals("1") ? false : true);
        mReturnBarView.setListener(mOnClickListener);
        mReturnBarView.setBackListener(mOnClickListener);
        init();
    }

    private void init() {
        listview.setCacheColorHint(Color.TRANSPARENT);
        mWiFiConnectAdapter = new WiFiConnectAdapter();
        listview.setAdapter(mWiFiConnectAdapter);
        listview.setOnItemLongClickListener(mOnItemLongClickListener);
        listview.setOnScrollListener(mOnScrollListener);
        // new MyTask().execute();
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
                    break;
                case R.id.wifi_more:

                    break;
                case R.id.wifi_refresh:

                    break;
                case R.id.ck_clouds_dog_switch:

                    break;

                default:
                    break;
            }

        }
    };
    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int arg2, long arg3) {
            // -------------------
         /*   String wifiItem = mWiFiConnectAdapter.getItem(arg2);// 获得选中的设备
            showDeleteDialog(wifiItem);*/
            return true;
        }

    };

    private void showDeleteDialog(String wifiItem) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final Dialog dialog = new Dialog(getContext(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_album_delete, null);
        dialog.addContentView(view, lp);
        DialogView dialogView = (DialogView) dialog
                .findViewById(R.id.album_delete_dialog);
        dialogView.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onExecutive() {
                // deleteAlbumDir(wifiItem);
                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        // dialogView.setMessage(getMsg(info.title));
        dialog.show();
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    // mImageLoader.resume();
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    // mImageLoader.pause();
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    // mImageLoader.pause();
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

        }

    };

    public void showConnect() {
        // TODO Auto-generated method stub

    }

}
