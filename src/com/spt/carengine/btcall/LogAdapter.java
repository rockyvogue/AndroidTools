
package com.spt.carengine.btcall;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.define.BTApi;
import com.spt.carengine.utils.ScreenUtils;

import java.util.List;

//自定义适配器类，提供给listView的自定义view  
public class LogAdapter extends ArrayAdapter<ContactInfo> {

    private List<ContactInfo> mlistRecordInfo = null;

    LayoutInflater infater = null;
    View rootView;
    private int mItemHeight;
    private static final int LOG_ITEM_HEIGHT = 90;
    private static final int LOG_TITLE_HEIGHT = 92;
    private Context mContext;
    private OnClickListener mOnClickListener=new OnClickListener(){
        @Override
        public void onClick(View v) {
            String number=(String) v.getTag();
            
            Intent intent = new Intent();
            intent.setAction(BTApi.ACTION_YRC_BT);
            intent.putExtra("cmd", BTApi.BT_CMD_DIAL);
            intent.putExtra("tel", number);
            mContext.sendOrderedBroadcast(intent, null);
        }
    };

    public LogAdapter(Context context) {
        super(context, 0);
        infater =LayoutInflater.from(context);
        mContext = context;
        // 标题栏的适配
        titleAdapter();
        mItemHeight=ScreenUtils.getRealHeightValue(mContext,
                LOG_ITEM_HEIGHT);
    }

    private void titleAdapter() {
        rootView = infater.inflate(R.layout.fragment_bt_log, null);

        // 拿到标题
        LinearLayout titleView = (LinearLayout) rootView
                .findViewById(R.id.ll_number_log_title);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleView
                .getLayoutParams();

        // 使用的是整个屏幕的适配
        int itemHeight = ScreenUtils.getRealHeightValue(mContext,
                LOG_TITLE_HEIGHT);
        layoutParams.height = itemHeight;

        titleView.setLayoutParams(layoutParams);

    }

    @Override
    public View getView(int position, View convertview, ViewGroup group) {

        final ContactInfo recordInfo = getItem(position);

        ViewHolder holder = null;
        if (convertview == null ) {

            convertview = infater.inflate(R.layout.list_item_log, null);
            holder = new ViewHolder(convertview);

            LinearLayout subject_ll = (LinearLayout) convertview
                    .findViewById(R.id.subject_ll);
            // 父布局参数
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) subject_ll
                    .getLayoutParams();
            linearParams.height = mItemHeight;

            subject_ll.setLayoutParams(linearParams);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        String sType = recordInfo.getCalltype();
        if (sType.equals("未接")) {
            holder.tvName.setTextColor(android.graphics.Color.RED);
            holder.tvName.setText(recordInfo.getName());
        } else {
            if (sType.equals("呼出")) {
                holder.tvName.setText(recordInfo.getName() + "↗");
            } else {
                holder.tvName.setText(recordInfo.getName() + "↙");
            }
            holder.tvName.setTextColor(android.graphics.Color.WHITE);
        }
        holder.tvNumber.setText(recordInfo.getNumber());
        holder.tvDate.setText(recordInfo.getDate());
        holder.btnDial.setTag(recordInfo.getNumber());
        holder.btnDial.setOnClickListener(mOnClickListener);

        return convertview;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvNumber;
        TextView tvDate;
        Button btnDial;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvNumber = (TextView) view.findViewById(R.id.tv_num);
            this.tvDate = (TextView) view.findViewById(R.id.tv_date);
            this.btnDial = (Button) view.findViewById(R.id.btn_item_dial);
        }
    }
}
