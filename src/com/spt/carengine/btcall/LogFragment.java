
package com.spt.carengine.btcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.define.BTApi;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.view.listitemmenu.ListMenu;
import com.spt.carengine.view.listitemmenu.ListMenuCreator;
import com.spt.carengine.view.listitemmenu.ListMenuItem;
import com.spt.carengine.view.listitemmenu.ListMenuListView;
import com.spt.carengine.view.listitemmenu.ListMenuListView.OnMenuItemClickListener;

import de.greenrobot.event.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogFragment extends Fragment implements OnClickListener,
        OnMenuItemClickListener {

    private ListMenuListView listview = null;
    private View fragmentView = null;
    private int nBtState = 0;
    private Activity mActivity;
    private LogAdapter mLogAdapter;

    public LogFragment(Activity activity) {
        mActivity = activity;
        mLogAdapter = new LogAdapter(activity);

        new AsyncTask<Void, Void, List<ContactInfo>>() {
            @Override
            protected List<ContactInfo> doInBackground(Void... arg0) {
                return preLoadingData();
            }

            @Override
            protected void onPostExecute(List<ContactInfo> result) {
                super.onPostExecute(result);
                mLogAdapter.addAll(result);
            }
        }.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private List<ContactInfo> preLoadingData() {
        LogUtil.d("LogFragment开始时间：", System.currentTimeMillis() + "");

        // 提前加载数据
        List<ContactInfo> mlistRecordinfo = new ArrayList<ContactInfo>();
        // 查询通话记录
        Cursor cursor = mActivity.getApplicationContext().getContentResolver()
                .query(CallLog.Calls.CONTENT_URI, // 使用系统URI，取得通话记录
                        new String[] {
                                CallLog.Calls.NUMBER, // 电话号
                                CallLog.Calls.CACHED_NAME, // 联系人
                                CallLog.Calls.TYPE, // 通话类型
                                CallLog.Calls.DATE, "_id"
                        // 通话时间
                        }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

        // 遍历每条通话记录
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String strNumber = cursor.getString(0); // 呼叫号码
            String strName = cursor.getString(1); // 联系人姓名
            int type = cursor.getInt(2);// 通话类型

            String str_type = "";
            if (type == CallLog.Calls.INCOMING_TYPE) {
                str_type = "呼入";
            } else if (type == CallLog.Calls.OUTGOING_TYPE) {
                str_type = "呼出";
            } else if (type == CallLog.Calls.MISSED_TYPE) {
                str_type = "未接";
            }

            SimpleDateFormat sfd = new SimpleDateFormat("MM-dd HH:mm");
            Date date = new Date(Long.parseLong(cursor.getString(3)));
            String time = sfd.format(date);// 通话时间
            long contactId = cursor.getLong(4);

            Cursor c = mActivity.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                            strNumber),
                    new String[] {
                            PhoneLookup._ID, PhoneLookup.NUMBER,
                            PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE,
                            PhoneLookup.LABEL
                    }, null, null, null);

            if (c.getCount() == 0) {
                // 没找到电话号码
                strName = "未知联系人";
            } else if (c.getCount() > 0) {
                c.moveToFirst();
                strName = c.getString(2); // 获取姓名
            }

            ContactInfo recoInfo = new ContactInfo();
            recoInfo.setName(strName);
            recoInfo.setNumber(strNumber);
            recoInfo.setDate(time);
            recoInfo.setCalltype(str_type);
            recoInfo.setContactId(contactId);

            if (c != null) {
                c.close();
            }

            mlistRecordinfo.add(recoInfo);
        }

        if (cursor != null) {
            cursor.close();
        }

        LogUtil.d("LogFragment开始时间：", System.currentTimeMillis() + "");

        return mlistRecordinfo;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_bt_log, container,
                false);
        initdata();
        initview();
        initMenu();
        return fragmentView;
    }

    private void initdata() {

        mActivity.getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        RegBroadRecevier(true);
    }

    private void initview() {
        listview = (ListMenuListView) fragmentView
                .findViewById(R.id.lv_call_log);
        TextView tv_none = (TextView) fragmentView.findViewById(R.id.tv_none);

        listview.setAdapter(mLogAdapter);
        if (mLogAdapter.getCount() == 0) {
            tv_none.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }

        LogUtil.d("ListView-->count", listview.getChildCount() + "");

        Button back = (Button) fragmentView.findViewById(R.id.btn_log_back);
        back.setOnClickListener(this);
    }

    private void initMenu() {

        ListMenuCreator creator = new ListMenuCreator() {

            @Override
            public void create(ListMenu menu) {

                ListMenuItem dialItem = new ListMenuItem(
                        mActivity.getApplicationContext());

                ListMenuItem deleteItem = new ListMenuItem(
                        mActivity.getApplicationContext());

                deleteItem.setBackground(R.drawable.number_user_del_n);
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.number_user_del_n);
                menu.addMenuItem(deleteItem);
            }

        };
        listview.setMenuCreator(creator);
        listview.setOnMenuItemClickListener(this);

    }

    public void RegBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BTApi.ACTION_YRC_BD);// BD
            mActivity.getApplicationContext().registerReceiver(btReceiver,
                    filter);
        } else {
            mActivity.getApplicationContext().unregisterReceiver(btReceiver);
        }

    }

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null)
                return;

            String sAction = intent.getAction();
            if (sAction == null)
                return;
            if (sAction.equals(BTApi.ACTION_YRC_BD)) {
                int nCmd = intent.getIntExtra("cmd", -1);
                if (nCmd == -1)
                    return;
                nBtState = intent.getIntExtra("btstate", -1);
            }
        }

    };

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mActivity.getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public void onMenuItemClick(int position, ListMenu menu, int index) {

        final ContactInfo item = mLogAdapter.getItem(position);
        switch (index) {
            case 0:// 删除
            {
                new AlertDialog.Builder(mActivity)
                        .setTitle("删除提示框")
                        .setMessage("确认删除该通话记录？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        if (item == null)
                                            return;
                                        deleteContact(item.getContactId());
                                    }
                                }).setNegativeButton("取消", null).show();

            }
                break;
            default:
                break;
        }
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 当联系人表发生变化时进行相应的操作
            // initList();
            mLogAdapter.notifyDataSetChanged();
        }
    };

    public void deleteContact(long rawContactId) {

        mActivity.getContentResolver().delete(
                ContentUris.withAppendedId(RawContacts.CONTENT_URI,
                        rawContactId), null, null);
    }

    @Override
    public void onClick(View arg0) {

        int vid = arg0.getId();
        if (vid == R.id.btn_log_back) {
            EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
        }
    }

    public void onBack() {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    Log.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }
}
