
package com.spt.carengine.btcall;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;

import java.util.List;

import de.greenrobot.event.EventBus;

public class BtMainFragment extends Fragment implements OnClickListener {

    private static FragmentManager fMgr;
    private View fragmentView = null;
    private boolean bBack = false;
    private ImageButton ib_dial;
    private ImageButton ib_contact;
    private ImageButton ib_log;
    private LinearLayout ll_dial;
    private LinearLayout ll_contact;
    private LinearLayout ll_log;

    private Fragment mCurrFragment;
    private DialFragment dialFragment;
    private ContactFragment contactFragment;
    private LogFragment logFragment;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = (View) inflater.inflate(R.layout.fragment_bt_main, null);
        fMgr = getFragmentManager();

        initeview();
        initFragment();

        return fragmentView;
    }

    private void initeview() {
        ib_dial = (ImageButton) fragmentView.findViewById(R.id.ib_Dial);
        ib_contact = (ImageButton) fragmentView.findViewById(R.id.ib_Contact);
        ib_log = (ImageButton) fragmentView.findViewById(R.id.ib_Log);

        ll_dial = (LinearLayout) fragmentView.findViewById(R.id.ll_dial_bg);
        ll_contact = (LinearLayout) fragmentView
                .findViewById(R.id.ll_contact_bg);
        ll_log = (LinearLayout) fragmentView.findViewById(R.id.ll_log_bg);

        ib_dial.setOnClickListener(this);
        ib_contact.setOnClickListener(this);
        ib_log.setOnClickListener(this);
        ll_dial.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        ll_log.setOnClickListener(this);

    }

    /**
     * 初始化首个Fragment
     */
    private void initFragment() {

        switchImageButton(R.id.ib_Dial);
        dialFragment = new DialFragment();
        switchFragment(null, dialFragment);

        contactFragment = new ContactFragment(mActivity);
        logFragment = new LogFragment(mActivity);

    }

    private void switchFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = fMgr.beginTransaction();

        if (from == null) {
            transaction.replace(R.id.fl_fragment_root, to);
            transaction.commit();

        } else {
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from).add(R.id.fl_fragment_root, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }

        mCurrFragment = to;

    }

    /**
     * 从back stack弹出所有的fragment，保留首页的那个
     */
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
            fMgr.popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ll_dial_bg) {
            id = R.id.ib_Dial;
        }
        if (id == R.id.ll_contact_bg) {
            id = R.id.ib_Contact;
        }
        if (id == R.id.ll_log_bg) {
            id = R.id.ib_Log;
        }

        switchImageButton(id);
        switch (id) {
            case R.id.ib_Dial:// 按键1 拨号器
            {
                if (mCurrFragment == dialFragment) {
                    return;
                }
                switchFragment(mCurrFragment, dialFragment);

            }
                break;
            case R.id.ib_Contact:// 按键2 联系人
            {
                if (mCurrFragment == contactFragment) {
                    return;
                }
                switchFragment(mCurrFragment, contactFragment);
            }
                break;
            case R.id.ib_Log:// 按键3 最近通话
            {
                if (mCurrFragment == logFragment) {
                    return;
                }
                switchFragment(mCurrFragment, logFragment);
            }
                break;
            default:
                break;
        }
    }

    private void switchImageButton(int id) {

        switch (id) {

            case R.id.ib_Dial:
                ib_dial.setImageResource(R.drawable.nunber_phone_icon_p);
                ib_contact.setImageResource(R.drawable.nunber_phone_peo_n);
                ib_log.setImageResource(R.drawable.nunber_phone_time_n);
                switchIBBackgroud(R.id.ll_dial_bg);
                break;

            case R.id.ib_Contact:
                ib_dial.setImageResource(R.drawable.nunber_phone_icon_n);
                ib_contact.setImageResource(R.drawable.nunber_phone_peo_p);
                ib_log.setImageResource(R.drawable.nunber_phone_time_n);
                switchIBBackgroud(R.id.ll_contact_bg);
                break;

            case R.id.ib_Log:
                ib_dial.setImageResource(R.drawable.nunber_phone_icon_n);
                ib_contact.setImageResource(R.drawable.nunber_phone_peo_n);
                ib_log.setImageResource(R.drawable.nunber_phone_time_p);
                switchIBBackgroud(R.id.ll_log_bg);
                break;

            default:
                break;
        }

    }

    private void switchIBBackgroud(int id) {
        switch (id) {
            case R.id.ll_dial_bg:
                ll_dial.setBackgroundResource(R.drawable.backgroud_p);
                ll_contact.setBackgroundResource(R.drawable.backgroud_n);
                ll_log.setBackgroundResource(R.drawable.backgroud_n);
                break;
            case R.id.ll_contact_bg:
                ll_dial.setBackgroundResource(R.drawable.backgroud_n);
                ll_contact.setBackgroundResource(R.drawable.backgroud_p);
                ll_log.setBackgroundResource(R.drawable.backgroud_n);
                break;
            case R.id.ll_log_bg:
                ll_dial.setBackgroundResource(R.drawable.backgroud_n);
                ll_contact.setBackgroundResource(R.drawable.backgroud_n);
                ll_log.setBackgroundResource(R.drawable.backgroud_p);
                break;

            default:
                break;
        }

    }
}
