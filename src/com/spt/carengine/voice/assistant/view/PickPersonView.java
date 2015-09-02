
package com.spt.carengine.voice.assistant.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.yunzhisheng.vui.modes.ContactInfo;

import com.spt.carengine.R;

import java.util.ArrayList;

public class PickPersonView extends PickBaseView {

    public PickPersonView(Context context) {
        super(context);
    }

    public void initView(ArrayList<ContactInfo> contactInfos) {
        Context context = getContext();

        for (int i = 0; i < contactInfos.size(); i++) {
            ContactInfo contactInfo = contactInfos.get(i);

            View view = mLayoutInflater.inflate(R.layout.pickview_item_contact,
                    mContainer, false);

            TextView tvName = (TextView) view.findViewById(R.id.textViewName);
            tvName.setText(contactInfo.getDisplayName());

            TextView noText = (TextView) view.findViewById(R.id.textViewNo);
            noText.setText((i + 1) + "");

            View divider = view.findViewById(R.id.divider);
            if (getItemCount() == contactInfos.size() - 1) {
                divider.setVisibility(View.GONE);
            }
            addItem(view);
        }
    }
}
