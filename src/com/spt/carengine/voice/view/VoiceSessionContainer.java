
package com.spt.carengine.voice.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import cn.yunzhisheng.asr.utils.LogUtil;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.assistant.preference.UserPreference;
import com.spt.carengine.voice.assistant.view.GeneralAnswerView;
import com.spt.carengine.voice.assistant.view.ISessionView;

public class VoiceSessionContainer extends ScrollView {
    public static final String TAG = "VoiceSessionContainer";
    private final static int SESSION_TYPE_INVALID = -1;
    private final static int SESSION_TYPE_QUESTION = 1;
    private final static int SESSION_TYPE_ANSWER = 2;

    private int mLastestSessionType = SESSION_TYPE_INVALID;

    private LinearLayout mSessionContainer;
    private ImageView mImageViewLastTTS;
    private LayoutInflater mLayoutInflater = null;

    private boolean mRequestFullScroll = true;
    private boolean mScrollable = true;
    private String mLastestSession;

    public static final int UNKNOWN = 0;
    public static final int PLAYING = 1;
    public static final int BUFFERING = 2;
    public static final int STOPPED = 3;
    public static final int UNENABLED = 4;

    private int mTTSStatus = UNKNOWN;

    private ITTSStatusListener mTTSListener;

    private Context mContext;

    private UserPreference mPreference;

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mTTSListener != null) {
                mTTSListener.onStatusChanged(mTTSStatus, mLastestSession);
            }
        }
    };

    public void setTTSListener(ITTSStatusListener l) {
        mTTSListener = l;
    }

    public VoiceSessionContainer(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSessionContainer = new LinearLayout(context);
        mSessionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mSessionContainer.setOrientation(LinearLayout.VERTICAL);
        addView(mSessionContainer);
        mContext = context;
        mPreference = new UserPreference(context);
    } 

    public VoiceSessionContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceSessionContainer(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mRequestFullScroll) {
            mRequestFullScroll = false;
            fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScrollable) {
                    return super.onTouchEvent(ev);
                }
                return false;
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScrollable) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public LinearLayout getContentView() {
        return mSessionContainer;
    }

    public void clearTemporaryViews() {
        for (int i = 0; i < mSessionContainer.getChildCount(); i++) {
            View child = mSessionContainer.getChildAt(i);
            if (child instanceof ISessionView
                    && ((ISessionView) child).isTemporary()) {
                mSessionContainer.removeViewAt(i);
            }
        }
    }

    public void removeAllSessionViews() {
        mLastestSession = "";
        mSessionContainer.removeAllViews();
    }

    public void removeSessionView(View view) {
        mSessionContainer.removeView(view);
    }

    public void addSessionView(View view) {
        addSessionView(view, true);
    }

    public void addSessionView(View view, boolean fullScroll) {
        if (view == null)
            return;
        mRequestFullScroll = fullScroll;
        mSessionContainer.addView(view);
    }

    public void addSessionViewWithoutScrolling(View view) {
        addSessionView(view, true);
        // 禁止滚动
        setScrollingEnabled(false);
        // 将视图宽设成和container一致，视图高设成与弹出框一致
        int height = getHeight();
        int width = mSessionContainer.getWidth();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,
                height);
        mSessionContainer.addView(view, params);
    }

    public void needFullScrollNextTime() {
        mRequestFullScroll = true;
    }

    public void addQustionView(String question) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "addAnswerView question = "
                + question);
        if (TextUtils.isEmpty(question)
                || (mLastestSessionType == SESSION_TYPE_QUESTION && question
                        .equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_QUESTION;
        mLastestSession = question;

        TextView tv2 = (TextView) mLayoutInflater.inflate(
                R.layout.session_question_view, mSessionContainer, false);
        tv2.setText(question);

        if (LogUtil.DEBUG) {
            tv2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString();
                    Toast.makeText(
                            getContext(),
                            getResources().getString(R.string.feedback_error_)
                                    + text, Toast.LENGTH_SHORT).show();
                }
            });
        }

        addSessionView(tv2, true);
    }

    public void addAnswerView(String answer) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "addAnswerView answer=" + answer);
        if (TextUtils.isEmpty(answer)
                || (mLastestSessionType == SESSION_TYPE_ANSWER && answer
                        .equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_ANSWER;
        mLastestSession = answer;
        hideLastTTSView();
        View v = mLayoutInflater.inflate(R.layout.session_answer_view,
                mSessionContainer, false);
        mImageViewLastTTS = (ImageView) v.findViewById(R.id.imageViewTTSStatus);
        mImageViewLastTTS.setOnClickListener(mOnClickListener);
        TextView tv1 = (TextView) v.findViewById(R.id.textViewSessionAnswer);
        tv1.setText(answer);
        addSessionView(v);
        if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
            onTTSBuffer();
        }
    }

    public void addAnswerViewEx(String text, String imgURL, String imgAlt,
            String url, String urlAlt) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "addAnswerViewEx text = " + text);
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(imgURL)
                && TextUtils.isEmpty(url)) {
            return;
        }

        if (!TextUtils.isEmpty(text) && TextUtils.isEmpty(imgURL)
                && TextUtils.isEmpty(url)) {
            addAnswerView(text);
            return;
        }

        url = TextUtils.isEmpty(url) ? null : url.replaceAll(" ", "%20");
        if (!TextUtils.isEmpty(text)
                && (mLastestSessionType == SESSION_TYPE_ANSWER && text
                        .equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_ANSWER;
        mLastestSession = text;
        hideLastTTSView();

        GeneralAnswerView v = new GeneralAnswerView(getContext());
        mImageViewLastTTS = (ImageView) v.findViewById(R.id.imageViewTTSStatus);
        mImageViewLastTTS.setOnClickListener(mOnClickListener);
        v.setGeneralData(text, imgURL, imgAlt, url, urlAlt);
        addSessionView(v);

        if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
            onTTSBuffer();
        }
    }

    private void hideLastTTSView() {
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setOnClickListener(null);
            mImageViewLastTTS.setVisibility(View.GONE);
        }
    }

    public void onTTSBuffer() {
        if (mTTSStatus == BUFFERING || mTTSStatus == PLAYING) {
            return;
        }
        mTTSStatus = BUFFERING;
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setVisibility(View.VISIBLE);
            if (mImageViewLastTTS.getDrawable() != null) {
                ((AnimationDrawable) mImageViewLastTTS.getDrawable()).start();
            }
        }
    }

    public void onTTSPlay() {
        if (mTTSStatus == PLAYING) {
            return;
        }
        mTTSStatus = PLAYING;
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setVisibility(View.VISIBLE);
            if (mImageViewLastTTS.getDrawable() != null) {
                ((AnimationDrawable) mImageViewLastTTS.getDrawable()).start();
            }
        }
    }

    public void onTTSStop() {
        if (mTTSStatus == STOPPED) {
            return;
        }
        LOG.writeMsg(this, LOG.MODE_VOICE, "onTTSStop :"
                + Build.VERSION.SDK_INT);
        mTTSStatus = STOPPED;
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setVisibility(View.VISIBLE);
        }
        boolean btOn = mPreference.getBluetoothMode();
        if (Build.VERSION.SDK_INT < 14 && Build.VERSION.SDK_INT > 7 && btOn) {
            AudioManager audioManager = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
                audioManager.setBluetoothScoOn(false);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.stopBluetoothSco();
            }
            audioManager.startBluetoothSco();
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setBluetoothScoOn(true);
        }
    }

    public void onTTSUnenabled() {
        mTTSStatus = UNENABLED;
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <功能描述> 云之声离线在线的状态监听器
     * 
     * @author Administrator
     */
    public static interface ITTSStatusListener {
        public void onStatusChanged(int oldStatus, String obj);
    }

}
