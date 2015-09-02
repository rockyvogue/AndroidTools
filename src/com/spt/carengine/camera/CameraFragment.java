
package com.spt.carengine.camera;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CameraFragment extends Fragment {

    private static SimpleDateFormat sdf = new SimpleDateFormat(
            CameraValues.DT_FMT, Locale.CHINESE);

    // 对重复的选项自动隐藏
    private static final boolean AUTO_HIDE_REPATE = true;
    private static final int LEVEL_GARY_EXPAND_STAT = 0;
    private static final int LEVEL_LIGHT_COLLAPSE_STAT = 1;

    private static final String SP_NAME = "recoder";

    private SharedPreferences sharedPreferences;
    private MyListener myListener;
    private MediaRecorderHelper mediaRecorderHelper;
    private TextToSpeech mTextToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        mediaRecorderHelper = /* MediaRecorderHelper.getInstance(); */new MediaRecorderHelper(/* sharedPreferences */);
        mediaRecorderHelper
                .setSharedPreferenceChangeListener(sharedPreferences);

        mTextToSpeech = new TextToSpeech(getActivity(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTextToSpeech
                                    .setLanguage(Locale.ENGLISH);
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                LOG.writeMsg(CameraFragment.class,
                                        LOG.MODE_RECORD_VIDEO,
                                        "error,Love is so short, forgetting is so long.");
                                mTextToSpeech
                                        .speak("error,Love is so short, forgetting is so long.​",
                                                TextToSpeech.QUEUE_FLUSH, null);

                            } else {
                                LOG.writeMsg(CameraFragment.class,
                                        LOG.MODE_RECORD_VIDEO,
                                        "onInit TextToSpeech success :"
                                                + status);
                                mediaRecorderHelper
                                        .setTextToSpeech(mTextToSpeech);
                            }
                        } else {
                            LOG.writeMsg(CameraFragment.class,
                                    LOG.MODE_RECORD_VIDEO, "onInit error:"
                                            + status);
                        }
                    }
                });
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_camera, null);

        TextView mRecordTimeTv = (TextView) view
                .findViewById(R.id.camera_record_time);
        initDisplayRecordTime(mRecordTimeTv);

        // v.getBackground().setl
        RadioGroup resolutionRatioBtn = (RadioGroup) view
                .findViewById(R.id.resolution_ratio_radiogroup);
        RadioGroup recordTimeInternalBtn = (RadioGroup) view
                .findViewById(R.id.record_time_internal_radiogroup);
        CheckBox startRecordCheckbox = (CheckBox) view
                .findViewById(R.id.right_bottom_start_record);

        myListener = new MyListener(view, resolutionRatioBtn,
                recordTimeInternalBtn);

        // int[] btnIds={R.id.radio1080p,R.id.radio720p};
        initRadioGroup(R.array.arr_str_format_label, R.array.opt_format_value,
                getKey(resolutionRatioBtn), sharedPreferences,
                resolutionRatioBtn);
        initRadioGroup(R.array.arr_str_time_label, R.array.opt_time_value,
                getKey(recordTimeInternalBtn), sharedPreferences,
                recordTimeInternalBtn);

        // 初始化的时候屏幕灰色状态,
        // setAllViewEnblestate( v , false );
        view.requestFocus();
        startRecordCheckbox.setOnCheckedChangeListener(myListener);
        // 点击或者任意键之后清除灰色状态，
        view.setOnKeyListener(myListener);
        view.setOnTouchListener(myListener);

        SurfaceView surfaceView = (SurfaceView) view
                .findViewById(R.id.surfaceView1);
        mediaRecorderHelper.setPreviewDisplay(surfaceView);
        int rotation = getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();
        mediaRecorderHelper.setRotation(rotation);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
        }
    }

    private String getKey(RadioGroup rg) {
        switch (rg.getId()) {
            case R.id.resolution_ratio_radiogroup:
                return CameraValues.KEY_OPT;

            case R.id.record_time_internal_radiogroup:
                return CameraValues.KEY_TIME;

            default:
                break;
        }
        return "key_unknow";
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // // OnTouchListener 和 OnKeyListener 用于 root view //////
    // /////////////////////////////////////////////////////////////////////////////////

    private class MyListener implements View.OnTouchListener,
            View.OnKeyListener, View.OnClickListener,
            RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener, Runnable {
        private static final long INTEVAL_TIME = 3 * 1000; // 3s
        private long lastAct;
        private Handler handler;
        private View rootView;
        private RadioGroup[] radioGroupArray;

        private MyListener(View rootView, RadioGroup... rgArr) {
            this.rootView = rootView;
            this.radioGroupArray = rgArr;
        }

        /**
         * 播放按钮事件
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            refLastAct();
            if (isChecked) {
                mediaRecorderHelper.start();
            } else {
                mediaRecorderHelper.stop();
            }
        };

        /**
         * 第一个radio的点击事件
         */
        @Override
        public void onClick(View v) {
            refLastAct();
            // 效果为点一下展开，点一下收回,
            Drawable bg = v.getBackground();
            if (bg != null) {
                int level = bg.getLevel();
                if (level == LEVEL_LIGHT_COLLAPSE_STAT) { // 点亮状态下展开
                    LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "展开灰掉并自动倒计时收缩");
                    expandOtherRadioButton((ViewGroup) v.getParent(), v);
                    bg.setLevel(LEVEL_GARY_EXPAND_STAT);

                } else { // 否则仅点亮
                    LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "收缩点亮并自动倒计时灰掉");
                    collapseOtherRadioButton((ViewGroup) v.getParent(), v);
                    bg.setLevel(LEVEL_LIGHT_COLLAPSE_STAT);
                }
            }
        }

        /**
         * 选择时间或者分辨率
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            refLastAct();
            // 效果为选中的挪到第一，并且3s后自动隐藏列表，再3s后自动灰掉
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO,
                    "-onCheckedChanged- checkedId:" + checkedId);
            RadioButton cked = (RadioButton) group.findViewById(checkedId);
            String defValue = String.valueOf(cked.getTag());
            sharedPreferences.edit().putString(getKey(group), defValue)
                    .commit();
            int[] resIds = (int[]) group.getTag();

            initRadioGroup(resIds[0], resIds[1], defValue, group);
        }

        /**
         * root view 的按键事件
         */
        @Override
        public boolean onKey(View rootView, int keyCode, KeyEvent event) {
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "-onKey-" + keyCode);
            return changeState(rootView);
        }

        /**
         * root view 的触摸事件
         */
        @Override
        public boolean onTouch(View rootView, MotionEvent event) {
            LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "-onTouch-" + rootView);
            return changeState(rootView);
        }

        private boolean changeState(View rootView) {
            refLastAct();
            this.rootView = rootView; // XXX 考虑是否后期加入
            setAllViewEnble(rootView, true); // 激活原有控件
            return true;
        }

        private boolean expandOtherRadioButton(ViewGroup vg, View v) {
            int count = setOtherRadioButtonVisibility(vg, v, View.VISIBLE);
            return count > 0;
        }

        private boolean collapseOtherRadioButton(ViewGroup vg, View v) {
            int count = setOtherRadioButtonVisibility(vg, v, View.INVISIBLE);
            return count > 0;
        }

        private int setOtherRadioButtonVisibility(ViewGroup vg, View v, int vis) {
            int count = 0;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof ViewGroup) {
                    count += setOtherRadioButtonVisibility((ViewGroup) child,
                            v, vis);

                } else if (child instanceof RadioButton) {
                    if (child != v) { // 排除自己
                        if (child.getVisibility() != vis) {
                            child.setVisibility(vis);
                            count++;
                        }
                    }
                }
            }
            return count;
        }

        private void refLastAct() {
            // if (handler == null && rootView != null) {
            // handler = rootView.getHandler();
            // LOG.writeMsg(this, LOG.MODE_CAMERA, "init myListener handler :" +
            // handler);
            // }
            //
            // lastAct = SystemClock.uptimeMillis();
            // LOG.writeMsg(this, LOG.MODE_CAMERA, "refLastAct use handler :" +
            // handler);
            // if (handler != null) {
            // handler.removeCallbacks(this);
            // }
        }

        private int countMaxVisibleRadioButton(RadioGroup... rgArr) {
            int countMax = 0;
            for (RadioGroup rg : rgArr) {
                View first = null;
                for (int i = 0; i < rg.getChildCount(); i++) {
                    View v = rg.getChildAt(i);
                    if (v instanceof RadioButton) {
                        // RadioButton rb = (RadioButton)v;
                        first = v;
                        break;
                    }
                }
                int count = setOtherRadioButtonVisibility(rg, first,
                        View.INVISIBLE);
                // 还原ImgLevel状态
                if (count > 0) {
                    // 默认其余是隐藏的，因此是收缩状态
                    setLevel(first, LEVEL_LIGHT_COLLAPSE_STAT);
                }
                countMax = Math.max(countMax, count);
            }
            return countMax;
        }

        public void run() {
            long iv = SystemClock.uptimeMillis() - lastAct;
            if (iv >= INTEVAL_TIME) {
                // 到达自动隐藏或者灰掉时间
                // 先做收缩，然后进行灰掉处理
                if (rootView != null && !rootView.isFocusable()) { // 子view在工作状态
                    if (countMaxVisibleRadioButton(radioGroupArray) < 1) { // 有超过两个，则进行隐藏
                        // 没有隐藏的，则进行锁屏
                        if (!setAllViewEnble(rootView, false)) { // 关闭原有控件
                            // 如果不在需要设置任何项，则终止任务
                            return;
                        }
                    }
                }
                iv = INTEVAL_TIME; // 下一次
            }

            //
            if (iv <= INTEVAL_TIME) { // 时间未到
                handler.removeCallbacks(this);
                handler.postDelayed(this, iv); //
            }
        }
    }

    /**
     * 设置所有View都使能
     * 
     * @param rootView
     * @param enabled
     * @return
     */
    private boolean setAllViewEnble(View rootView, boolean enabled) {
        // 激活原有控件
        boolean flag = setAllChildEnblestate(rootView, enabled);
        // 激活控件则不再监听按键，否则关闭控件就开始监听按键
        rootView.setFocusable(!enabled);
        rootView.setFocusableInTouchMode(!enabled);
        return flag;
    }

    private boolean setAllChildEnblestate(View view, boolean enabled) {
        boolean flag = false;
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int childCount = vg.getChildCount();
            for (int i = 0; i < childCount; i++) {
                flag |= setAllChildEnblestate(vg.getChildAt(i), enabled);
            }
        } else {
            if (view.isClickable()) { // 可点击的全部屏蔽
                view.setEnabled(enabled);
                flag = true;
            }
        }
        return flag;
    }

    // ///////////////////////////---END---///////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////
    // // 初始化RadioGroup的按钮， ////
    // /////////////////////////////////////////////////////////////////////////////////
    private void initRadioGroup(int resOpts, int resVals, String defKey,
            SharedPreferences sp, RadioGroup rg) {
        // RadioGroup rg = (RadioGroup) getActivity().findViewById( radioGroupId
        // );
        String[] vals = getResources().getStringArray(resVals);
        String defValue = sp.getString(defKey, vals[0]);
        int[] resIds = {
                resOpts, resVals
        };
        rg.setTag(resIds); // 临时存储
        initRadioGroup(resOpts, vals, defValue, rg);
    }

    private void initRadioGroup(int resOpts, String[] vals, String defValue,
            RadioGroup rg) {
        String[] opts = getResources().getStringArray(resOpts);
        initRadioGroup(opts, vals, defValue, rg);
    }

    private void initRadioGroup(int resOpts, int resVals, String defValue,
            RadioGroup rg) {
        // RadioGroup rg = (RadioGroup) getActivity().findViewById( radioGroupId
        // );
        String[] opts = getResources().getStringArray(resOpts);
        String[] vals = getResources().getStringArray(resVals);
        int[] resIds = {
                resOpts, resVals
        };
        rg.setTag(resIds); // 临时存储
        initRadioGroup(opts, vals, defValue, rg);
    }

    private void initRadioGroup(final String[] opts, final String[] vals,
            String defValue, final RadioGroup rg) {
        rg.setOnCheckedChangeListener(null); // 取消监听
        RadioButton first = null;

        int vCount = 0; // view 计数
        int iCount = 0; // 下标计数
        int rCount = 0; // radio 计数
        int defIdx = defIdx(vals, defValue); // 默认值的下标为0
        final int cCount = rg.getChildCount();
        for (; iCount < opts.length && iCount < vals.length; vCount++) {
            RadioButton rb = null;
            if (vCount < cCount) {
                // 更改
                View v = rg.getChildAt(vCount);
                if (!(v instanceof RadioButton)) {
                    continue;
                }
                rb = (RadioButton) v;
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "set " + vCount
                        + " RadioButton " + opts[iCount]);

            } else /* if( vCount >= cCount ) */{ // 增加新的
                //
                rb = new RadioButton(getActivity());
                rb.setId(rCount);
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, "add " + vCount
                        + " RadioButton " + opts[iCount]);
            }

            // 第一个拥有默认选中
            if (rCount == 0) {
                first = rb;
                LOG.writeMsg(this, LOG.MODE_RECORD_VIDEO, rCount + ":" + rb);
                rCount++;
                continue;
            }

            if (AUTO_HIDE_REPATE) {
                if (iCount == defIdx) {
                    iCount++;
                    if (iCount >= opts.length || iCount >= vals.length) {
                        break; //
                    }
                }
            }

            // if( rCount>0 && iCount!=defIdx ){
            // 默认选中的放到第一个，其余的要隐藏
            // 临时存储值
            if (!vals[iCount].equals(rb.getTag())) {
                rb.setTag(vals[iCount]);
            }
            if (!opts[iCount].equals(rb.getText())) {
                rb.setText(opts[iCount]);
            }
            rb.setOnClickListener(null); // 清理所有事件监听
            if (rb.isChecked()) {
                rb.setChecked(false); // 不选中
            }
            // rb.setVisibility( View.INVISIBLE ); //默认隐藏
            // rb.setText( opts[iCount] );
            if (rb.getParent() == null) {
                rg.addView(rb);
            }
            if (rb.isShown()) {
                rb.setVisibility(View.INVISIBLE);
            }
            // }

            iCount++;
            rCount++;
        }
        // 倒着往前移掉
        for (int j = rg.getChildCount(); j > vCount; j--) { // 移除未设置完的
            View v = rg.getChildAt(j);
            if (v instanceof RadioButton) {
                rg.removeViewAt(j);
            }
        }

        // 默认选中第0个
        if (first != null) {
            // first.setOnClickListener( null );
            first.setTag(vals[defIdx]);
            first.setText(opts[defIdx]);
            rg.check(first.getId());
            // 默认其余是隐藏的，因此是收缩状态
            setLevel(first, LEVEL_LIGHT_COLLAPSE_STAT);
            // 展开或者收起来
            first.setOnClickListener(myListener);
        }
        rg.setOnCheckedChangeListener(myListener);

    }

    private int defIdx(String[] vals, String defValue) {
        for (int i = 0; i < vals.length; i++) {
            if (vals[i].equals(defValue)) {
                return i;
            }
        }
        return 0;
    }

    // //////////////////////////---END---//////////////////////////////////////////////////

    private boolean setLevel(View v, int level) {
        Drawable bg = v.getBackground();
        if (bg != null) { // 默认其余是隐藏的，因此是收缩状态
            bg.setLevel(level);
            return true;
        }
        return false;
    }

    /**
     * 初始化显示记录的当前时间
     * 
     * @param tv
     */
    private void initDisplayRecordTime(final TextView tv) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) {
                    tv.removeCallbacks(this);
                    return;
                }
                Calendar c = Calendar.getInstance();
                String timeNow = sdf.format(c.getTime());
                tv.setText(timeNow);
                // c = Calendar.getInstance();
                long nowTimeInMillis = c.getTimeInMillis();
                // c.add( Calendar.MINUTE , 1 ); //1分钟后再次刷新
                // c.set( Calendar.SECOND, 0 );
                c.add(Calendar.SECOND, 1); // 1s 后刷新
                c.set(Calendar.MILLISECOND, 0);
                tv.postDelayed(this, c.getTimeInMillis() - nowTimeInMillis);
            }
        };
        run.run();
    }

    /**
     * 得到当前时间
     * 
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINESE);
        Calendar calendar = Calendar.getInstance();
        String timeNow = sdf.format(calendar.getTime());
        return timeNow;
    }

}
