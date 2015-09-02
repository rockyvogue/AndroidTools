
package com.spt.carengine.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.db.Provider;
import com.spt.carengine.define.Define;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

public class FMFragment extends Fragment implements
        SeekBar.OnSeekBarChangeListener {

    private View fragmentView = null;
    private TextView currentfreq = null;// 当前频率
    private SeekBar fmseekBar = null;// 标尺

    private String fmswitch = "0";
    private String currfreq = "8950";
    private ReturnBarView mReturnBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentView = inflater.inflate(
                R.layout.user_frequency_modulation_main, container, false);
        initdata(false);
        initeview(fragmentView);
        return fragmentView;

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        writetoFile(Define.DEVICE_NODES.SYS_KT0806_FM_SWITCH, fmswitch);
        if (fmswitch.equals("1")) {
            writetoFile(Define.DEVICE_NODES.SYS_KT0806_FM_FREQUENCY, currfreq);
        }
    }

    private void initdata(boolean bUpdata) {
        // TODO Auto-generated method stub
        String[] stringArray = new String[] {
                Provider.FMSend.SWITCH, Provider.FMSend.CURRFREQ
        };

        Cursor c = getActivity().getContentResolver().query(
                Provider.FMSend.CONTENT_URI, stringArray,
                Provider.FMSend._ID + "=?", new String[] {
                    "1"
                }, null);

        boolean bInsert = false;

        if (c == null || c.moveToFirst() == false || c.getCount() <= 0) {
            bInsert = true;
        }
        if (bInsert) {// 为空时插入一条

            ContentValues values = new ContentValues();
            values.put(Provider.FMSend.SWITCH, fmswitch);
            values.put(Provider.FMSend.CURRFREQ, currfreq);

            Uri uri = getActivity().getContentResolver().insert(
                    Provider.FMSend.CONTENT_URI, values);
            String lastPath = uri.getLastPathSegment();
            if (TextUtils.isEmpty(lastPath)) {
                Log.i(Define.TAG, "insert failure!");
            } else {
                Log.i(Define.TAG, "insert success! the id is " + lastPath);
            }
        } else {

            if (c != null && c.moveToFirst()) {

                if (bUpdata) {

                    ContentValues values = new ContentValues();
                    values.clear();

                    values.put("switch", fmswitch);
                    values.put("currfreq", currfreq);

                    int ret = getActivity().getContentResolver().update(
                            Provider.FMSend.CONTENT_URI, values, "_id=1", null);

                } else {
                    fmswitch = c.getString(c
                            .getColumnIndexOrThrow(Provider.FMSend.SWITCH));
                    currfreq = c.getString(c
                            .getColumnIndexOrThrow(Provider.FMSend.CURRFREQ));
                }

                Log.e(Define.TAG, "FM：fmswitch:" + fmswitch + " currfreq:"
                        + currfreq);

            } else {
                Log.e(Define.TAG, "query failure!");
            }
        }

        if (c != null) {
            c.close();
        }

    }

    private void initeview(View fragmentView) {

        fmseekBar = (SeekBar) fragmentView.findViewById(R.id.fm_seekbar_def);
        currentfreq = (TextView) fragmentView
                .findViewById(R.id.fm_change_textView);
        ImageView leftbtn = (ImageView) fragmentView
                .findViewById(R.id.fm_left_imageView);
        ImageView rightbtn = (ImageView) fragmentView
                .findViewById(R.id.fm_right_imageView);
        initShowReturnBarView(fragmentView);
        TextPaint tp = currentfreq.getPaint();
        tp.setFakeBoldText(true);

        fmseekBar.setOnSeekBarChangeListener(this);

        leftbtn.setOnClickListener(mOnClickListener);
        rightbtn.setOnClickListener(mOnClickListener);

        setCurrFreq(currfreq);
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_FM);
        mReturnBarView.setBackListener(mOnClickListener);
        mReturnBarView.setcheckBoxState(fmswitch.equals("0") ? false : true);
        mReturnBarView.setOncheckBoxListener(mOnCheckedChangeListener,
                mOnClickListener);
    }

    /**
     * 设置频率
     * 
     * @param CurrFreq
     * @return null
     */
    private void setCurrFreq(String curFreq) {
        // TODO Auto-generated method stub

        int position = Integer.parseInt(curFreq) - 8750;
        if (position < 0 || position > 2050) {
            position = 0;
        }

        fmseekBar.setProgress(position);

        StringBuffer str = new StringBuffer(curFreq);
        str.insert(str.length() - 2, ".");

        currentfreq.setText(str);

        writetoFile(Define.DEVICE_NODES.SYS_KT0806_FM_FREQUENCY, curFreq);

        currfreq = curFreq;

        initdata(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean bChecked) {
            // TODO Auto-generated method stub
            fmswitch = bChecked ? "1" : "0";
            initdata(true);
            writetoFile(Define.DEVICE_NODES.SYS_KT0806_FM_SWITCH, fmswitch);
            if (bChecked) {
                writetoFile(Define.DEVICE_NODES.SYS_KT0806_FM_FREQUENCY,
                        currfreq);
            }
        }

    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int nCurrFreq;
            switch (v.getId()) {
                case R.id.fm_left_imageView:// 左步进
                    nCurrFreq = Integer.parseInt(currfreq);
                    if (nCurrFreq > 8750) {
                        nCurrFreq = nCurrFreq - 5;
                        setCurrFreq(Integer.toString(nCurrFreq));
                    }
                    break;
                case R.id.fm_right_imageView:// 右步进
                    nCurrFreq = Integer.parseInt(currfreq);
                    if (nCurrFreq < 10800) {
                        nCurrFreq = nCurrFreq + 5;
                        setCurrFreq(Integer.toString(nCurrFreq));
                    }
                    break;
                case R.id.back:
                    EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
                    break;
                default:
                    break;
            }

        }
    };

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

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.fm_seekbar_def:
                int nCurrFreq = arg1 + 8750;
                int residue = nCurrFreq % 5;
                if (residue != 0) {
                    nCurrFreq = nCurrFreq - residue;
                }
                Log.d(Define.TAG, "余数：" + residue);
                StringBuffer str = new StringBuffer("" + nCurrFreq);
                str.insert(str.length() - 2, ".");

                currentfreq.setText(str);

                currfreq = Integer.toString(nCurrFreq);

                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        Log.d(Define.TAG, "点击：" + arg0.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        Log.d(Define.TAG, "停止：" + arg0.getProgress());
        setCurrFreq(currfreq);
    }

    /***
     * 写文件
     * 
     * @param fileName 文件路径
     * @param write_str 写入值
     */
    public void writetoFile(String fileName, String write_str) {

        File file = new File(fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        byte[] bytes = write_str.getBytes();

        try {
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
