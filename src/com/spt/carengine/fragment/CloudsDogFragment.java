
package com.spt.carengine.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.view.ReturnBarView;

import de.greenrobot.event.EventBus;

/** 云狗界面 **/
public class CloudsDogFragment extends Fragment implements OnClickListener {
    private TextView tv_new_hand, tv_old_hand, tv_expert;
    private ImageView img_new_hand, img_old_hand, img_expert;
    private PercentRelativeLayout pray1, pray2, pray3;
    private ReturnBarView mReturnBarView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloudsdog, container,
                false);

        initView(view);

        return view;
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TYPE_CLOUDS);
        mReturnBarView.setBackListener(this);
        mReturnBarView.setOncheckBoxListener(new OnCheckedChangeListener() {// 云狗开关
        
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {

                    }
                }, this);
    }

    private void initView(View view) {
        if (null == view)
            return;
        tv_new_hand = (TextView) view.findViewById(R.id.tv_new_hand);
        tv_old_hand = (TextView) view.findViewById(R.id.tv_old_hand);
        tv_expert = (TextView) view.findViewById(R.id.tv_expert);
        img_new_hand = (ImageView) view.findViewById(R.id.img_new_hand);
        img_old_hand = (ImageView) view.findViewById(R.id.img_old_hand);
        img_expert = (ImageView) view.findViewById(R.id.img_expert);
        pray1 = (PercentRelativeLayout) view.findViewById(R.id.pray1);
        pray1.setOnClickListener(this);
        pray2 = (PercentRelativeLayout) view.findViewById(R.id.pray2);
        pray2.setOnClickListener(this);
        pray3 = (PercentRelativeLayout) view.findViewById(R.id.pray3);
        pray3.setOnClickListener(this);
        chooseArecision(pray1);
        initShowReturnBarView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EventBus.getDefault().post(Constant.MODULE_TYPE_USER);
                break;

            case R.id.pray1:
                chooseArecision(pray1);
                break;
            case R.id.pray2:
                chooseArecision(pray2);
                break;
            case R.id.pray3:
                chooseArecision(pray3);
                break;

            default:
                break;
        }

    }

    /****
     * 选择精度提醒
     */
    private void chooseArecision(PercentRelativeLayout pray) {
        if (null == pray)
            return;
        if (pray == pray1) {
            tv_new_hand.setTextColor(Color.GREEN);
            tv_old_hand.setTextColor(Color.WHITE);
            tv_expert.setTextColor(Color.WHITE);
            img_new_hand.setVisibility(View.VISIBLE);
            img_old_hand.setVisibility(View.GONE);
            img_expert.setVisibility(View.GONE);

        } else if (pray == pray2) {
            tv_new_hand.setTextColor(Color.WHITE);
            tv_old_hand.setTextColor(Color.GREEN);
            tv_expert.setTextColor(Color.WHITE);
            img_new_hand.setVisibility(View.GONE);
            img_old_hand.setVisibility(View.VISIBLE);
            img_expert.setVisibility(View.GONE);

        } else if (pray == pray3) {
            tv_new_hand.setTextColor(Color.WHITE);
            tv_old_hand.setTextColor(Color.WHITE);
            tv_expert.setTextColor(Color.GREEN);
            img_new_hand.setVisibility(View.GONE);
            img_old_hand.setVisibility(View.GONE);
            img_expert.setVisibility(View.VISIBLE);

        }

    }

}
