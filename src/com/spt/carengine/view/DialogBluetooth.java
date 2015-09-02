
package com.spt.carengine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spt.carengine.R;

public class DialogBluetooth extends PercentRelativeLayout {

    private TextView mOk;
    private TextView mCancel;
    private EditText mEncod;
    private OnCustomDialogListener customDialogListener;

    public interface OnDialogListener {
        public void onCloseDialog();

        public void onExecutive();
    }

    /*
     * public DialogBluetooth(Context context,OnCustomDialogListener
     * customListener) { //OnCancelListener cancelListener) { super(context); //
     * TODO Auto-generated constructor stub customDialogListener =
     * customListener; }
     */
    // 定义dialog的回调事件
    public interface OnCustomDialogListener {
        void back(String str);

        void onCloseDialog();
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ok:

                    customDialogListener.back(mEncod.getText().toString());

                    break;
                case R.id.tv_cancle:

                    customDialogListener.onCloseDialog();

                    break;
            }
        }
    };

    public void setOnDialogListener(OnCustomDialogListener onDialogListener) {
        customDialogListener = onDialogListener;
    }

    public DialogBluetooth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DialogBluetooth(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public DialogBluetooth(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOk = (TextView) findViewById(R.id.tv_ok);
        mCancel = (TextView) findViewById(R.id.tv_cancle);
        mEncod = (EditText) findViewById(R.id.et_encod);
        if (mEncod == null || mOk == null || mCancel == null) {
            return;
        }
        mOk.setOnClickListener(mOnClickListener);
        mCancel.setOnClickListener(mOnClickListener);

    }

}
