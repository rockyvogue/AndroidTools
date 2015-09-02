
package com.spt.carengine.btcall;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.define.BTApi;

import java.util.ArrayList;
import java.util.List;

//自定义适配器类，提供给listView的自定义view  
public class ContactAdapter extends ArrayAdapter<ContactInfo> {


    LayoutInflater infater = null;
    Context ncontext = null;

    public ContactAdapter(Context context) {
        super(context, 0);
        infater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ncontext = context;
    }
    
    public View getView( int position, View convertview, ViewGroup group) {
        final ContactInfo recordInfo=getItem(position);
        // 获取listView的高度
        int listviewHeight = group.getHeight();
        int listviewWidth = group.getWidth();
        // 每一个item的宽高
        int itemHeight = (int) ((listviewHeight * 1.0f) / 4 + 0.5);
        ViewHolder holder = null;
        if (convertview == null || convertview.getTag() == null) {
            // 屏幕適配
            convertview = infater.inflate(R.layout.list_item_contact, null);

            LinearLayout subject_ll = (LinearLayout) convertview
                    .findViewById(R.id.subject_ll);
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) subject_ll
                    .getLayoutParams();
            linearParams.height = itemHeight;
            subject_ll.setLayoutParams(linearParams);
            holder = new ViewHolder(convertview);
            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.tvName.setText(recordInfo.getName());
        holder.tvNumber.setText(recordInfo.getNumber());
        holder.viewBtn.setTag(position);

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vid = v.getId();
                if (vid == R.id.btn_item_dial) {
                    Intent intent = new Intent();
                    intent.setAction(BTApi.ACTION_YRC_BT);
                    intent.putExtra("cmd", BTApi.BT_CMD_DIAL);
                    intent.putExtra("tel", recordInfo.getNumber());
                    ncontext.sendOrderedBroadcast(intent, null);
                }
            }

        });

        return convertview;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvNumber;
        Button viewBtn;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvNumber = (TextView) view.findViewById(R.id.tv_num);
            this.viewBtn = (Button) view.findViewById(R.id.btn_item_dial);

        }
    }
}
