
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements OnClickListener,
        OnMenuItemClickListener {

    private View fragmentView = null;
    private ListMenuListView mListView;
    private Button index_a;
    private Button index_h;
    private Button index_o;
    private Button index_u;
    private ContactAdapter mContAdapter;
    private Activity mActivity;

    /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/
    private int nBtState = 0;

    private String[] indexStr1 = {
            "#", "A", "B", "C", "D", "E", "F", "G"
    };
    private String[] indexStr2 = {
            "H", "I", "J", "K", "L", "M", "N"
    };
    private String[] indexStr3 = {
            "O", "P", "Q", "R", "S", "T"
    };
    private String[] indexStr4 = {
            "U", "V", "W", "X", "Y", "Z"
    };
    private int[] index = {
            0, 0, 0, 0
    };

    private boolean bIndexSech = false;

    // 索引按钮
    int[] mButtomId = {
            R.id.bt_index_a, R.id.bt_index_h, R.id.bt_index_o, R.id.bt_index_u
    };

    public ContactFragment(Activity activity) {
        mActivity = activity;
        mContAdapter = new ContactAdapter(activity);

        new AsyncTask<Void, Void, List<ContactInfo>>() {
            @Override
            protected List<ContactInfo> doInBackground(Void... arg0) {
                return preLoadingData();
            }

            @Override
            protected void onPostExecute(List<ContactInfo> result) {
                super.onPostExecute(result);
                mContAdapter.addAll(result);
            }
        }.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private List<ContactInfo> preLoadingData() {
        LogUtil.d("ContactFragment开始时间：", System.currentTimeMillis() + "");
        // 提前加载数据
        List<ContactInfo> listContacts = new ArrayList<ContactInfo>();

        String[] projection = new String[] {
                Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID,
                Phone.SORT_KEY_PRIMARY
        };
        // 获取手机联系人
        Cursor curs = mActivity.getContentResolver().query(Phone.CONTENT_URI,
                projection, null, null, "sort_key");

        if (curs != null) {
            while (curs.moveToNext()) {
                String sName = curs.getString(0);
                String sNumber = curs.getString(1);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(sNumber))
                    continue;
                Long contactid = curs.getLong(2);
                String contactpy = curs.getString(3);

                ContactInfo recoInfo = new ContactInfo();
                recoInfo.setName(sName.isEmpty() ? "未知" : sName);
                recoInfo.setNumber(sNumber);
                recoInfo.setContactId(contactid);
                String dd = cn2FirstSpell(contactpy.subSequence(0, 1)
                        .toString());
                Log.e("wujie", "拼音" + dd);

                listContacts.add(recoInfo);

            }
            if (curs != null)
                curs.close();
        }
        LogUtil.d("ContactFragment开始时间：", System.currentTimeMillis() + "");
        return listContacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_bt_contact,
                container, false);
        initdata();
        initview();
        return fragmentView;
    }

    private void initdata() {
        mActivity.getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        RegBroadRecevier(true);
    }

    private void initview() {
        mListView = (ListMenuListView) fragmentView
                .findViewById(R.id.lv_call_contact);

        initList();
        initMenu();
        mListView.setAdapter(mContAdapter);
        index_a = (Button) fragmentView.findViewById(R.id.bt_index_a);
        index_h = (Button) fragmentView.findViewById(R.id.bt_index_h);
        index_o = (Button) fragmentView.findViewById(R.id.bt_index_o);
        index_u = (Button) fragmentView.findViewById(R.id.bt_index_u);

        // 默認選中狀態
        index_a.setBackgroundResource(R.drawable.rectangle_b_c);

        Button back = (Button) fragmentView.findViewById(R.id.btn_contact_back);
        back.setOnClickListener(this);

        index_a.setOnClickListener(this);
        index_h.setOnClickListener(this);
        index_o.setOnClickListener(this);
        index_u.setOnClickListener(this);

    }

    private void initList() {

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

    /**
     * 汉字转拼音首字母
     * 
     * @param chinese
     * @return
     */
    public static String cn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i],
                            defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
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

                // 设置删除按钮的宽高
                deleteItem.setIcon(R.drawable.number_user_del_n);

                menu.addMenuItem(deleteItem);
            }

        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(this);

    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 当联系人表发生变化时进行相应的操作
            // initList();
            if (mContAdapter != null) {
                mContAdapter.notifyDataSetChanged();
            }
            index[0] = 0;
            index[1] = 0;
            index[2] = 0;
            index[3] = 0;

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mActivity.getContentResolver().unregisterContentObserver(mObserver);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onMenuItemClick(int position, ListMenu menu, int index) {

        final ContactInfo item = mContAdapter.getItem(position);
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
                                        deleteContact(item.getContactId());

                                    }
                                }).setNegativeButton("取消", null).show();

            }
                break;
            default:
                break;
        }
    }

    public void deleteContact(long rawContactId) {

        mActivity.getContentResolver().delete(
                ContentUris.withAppendedId(RawContacts.CONTENT_URI,
                        rawContactId), null, null);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();

        // 设置按钮的选中状态
        selectButtonById(id);

        switch (id) {
            case R.id.bt_index_a: {
                if (mContAdapter.getCount() > 0)
                    mListView.setSelection(0);
            }
                break;
            case R.id.bt_index_h: {
                for (int i = 0; i < indexStr2.length; i++) {
                    if (index[1] < index[0])
                        index[1] = index[0];
                    int size = mContAdapter.getCount();
                    for (int j = index[1]; j < size; j++) {
                        String ch = cn2FirstSpell(mContAdapter.getItem(j)
                                .getName().subSequence(0, 1).toString());
                        if (indexStr2[i].equalsIgnoreCase(ch)) {
                            mListView.setSelection(j);
                            index[1] = j;
                            bIndexSech = true;
                            break;
                        }
                    }
                    if (bIndexSech)
                        break;
                }
                bIndexSech = false;
            }
                break;
            case R.id.bt_index_o: {
                for (int i = 0; i < indexStr3.length; i++) {
                    if (index[2] < index[1])
                        index[2] = index[1];
                    int size = mContAdapter.getCount();
                    for (int j = index[2]; j < size; j++) {
                        String ch = cn2FirstSpell(mContAdapter.getItem(j)
                                .getName().subSequence(0, 1).toString());
                        if (indexStr3[i].equalsIgnoreCase(ch)) {
                            mListView.setSelection(j);
                            index[2] = j;
                            bIndexSech = true;
                            break;
                        }
                    }
                    if (bIndexSech)
                        break;
                }
                bIndexSech = false;
            }
                break;
            case R.id.bt_index_u: {
                for (int i = 0; i < indexStr4.length; i++) {
                    if (index[3] < index[2])
                        index[3] = index[2];
                    int size = mContAdapter.getCount();
                    for (int j = index[3]; j < size; j++) {
                        String ch = cn2FirstSpell(mContAdapter.getItem(j)
                                .getName().subSequence(0, 1).toString());
                        if (indexStr4[i].equalsIgnoreCase(ch)) {
                            mListView.setSelection(j);
                            index[3] = j;
                            bIndexSech = true;
                            break;
                        }
                    }
                    if (bIndexSech)
                        break;
                }
                bIndexSech = false;
            }
                break;
            case R.id.btn_contact_back: {
                EventBus.getDefault().post(Constant.MODULE_TYPE_HOME);
            }
                break;
            default:
                break;
        }
    }

    private void selectButtonById(int id) {

        if (index_a.getId() == id) {
            index_a.setBackgroundResource(R.drawable.rectangle_b_c);
        } else {
            index_a.setBackgroundResource(R.drawable.call_cantact_shape_r);
        }
        if (index_h.getId() == id) {
            index_h.setBackgroundResource(R.drawable.rectangle_b_c);
        } else {
            index_h.setBackgroundResource(R.drawable.call_cantact_shape_r);
        }
        if (index_o.getId() == id) {
            index_o.setBackgroundResource(R.drawable.rectangle_b_c);
        } else {
            index_o.setBackgroundResource(R.drawable.call_cantact_shape_r);
        }
        if (index_u.getId() == id) {
            index_u.setBackgroundResource(R.drawable.rectangle_b_c);
        } else {
            index_u.setBackgroundResource(R.drawable.call_cantact_shape_r);
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
