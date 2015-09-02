
package com.spt.carengine.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import com.spt.carengine.R;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.view.BaikeTextView;
import com.spt.carengine.view.ReturnBarView;
import de.greenrobot.event.EventBus;

/****
 * 关于
 */
public class AboutFragment extends Fragment implements OnClickListener {
    private ReturnBarView mReturnBarView;

    StringBuffer sb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initShowReturnBarView(view);
        BaikeTextView tv_breif = (BaikeTextView) view
                .findViewById(R.id.tv_breif);
        sb = new StringBuffer();
        sb.append(getString(R.string.tv_brief1));
        sb.append("\n");
        sb.append(getString(R.string.tv_brief2));
        sb.append("\n");
        sb.append(getString(R.string.tv_brief3));
        sb.append("\n");
        sb.append(getString(R.string.tv_brief4));
       
        tv_breif.setText(sb.toString());
        tv_breif.setMovementMethod(ScrollingMovementMethod.getInstance());

        return view;
    }

    private void initShowReturnBarView(View view) {
        mReturnBarView = (ReturnBarView) view.findViewById(R.id.back_bar);
        mReturnBarView.init(ReturnBarView.TPYE_ABOUT);
        mReturnBarView.setBackListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EventBus.getDefault()
                        .post(Constant.MODULE_TYPE_SYSTEM_SETTINGS);
                break;
            default:
                break;
        }

    }

}
