
package com.spt.carengine.voice.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.carengine.R;
import com.spt.carengine.log.LOG;
import com.spt.carengine.view.PercentRelativeLayout;
import com.spt.carengine.voice.view.VoiceMessageItem.MsgSource;

/**
 * <功能描述> The bar of voice bottom
 * 
 * @author Administrator
 */
public class VoiceBottomMicroControlBar extends PercentRelativeLayout {

    private static final String GRAY = "#162B3C";

    private RelativeLayout mBottomBarlayout;

    private ImageView mStartSpeakBtn;

    public ImageView getmStartSpeakBtn() {
        return mStartSpeakBtn;
    }

//    private ImageView mCancelVoiceBtn;

    private ImageView mVoiceRecognition;

    private AnimationDrawable animationDrawable;

    public VoiceBottomMicroControlBar(Context context) {
        this(context, null);
    }

    public VoiceBottomMicroControlBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceBottomMicroControlBar(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        View mRootView = View.inflate(context, R.layout.voice_mic_control, this);
        mBottomBarlayout = (RelativeLayout) mRootView
                .findViewById(R.id.voice_bottome_bar_rl);
        mStartSpeakBtn = (ImageView) mRootView
                .findViewById(R.id.voice_start_speak_btn);

//        mCancelVoiceBtn = (ImageView) mRootView
//                .findViewById(R.id.voice_control_cancel_btn);
        mVoiceRecognition = (ImageView) mRootView
                .findViewById(R.id.voice_recognition_img);
        animationDrawable = new AnimationDrawable();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mStartSpeakBtn.setOnClickListener(l);
//        mCancelVoiceBtn.setOnClickListener(l);
        mVoiceRecognition.setOnClickListener(l);
    }

    public void setEnabled(boolean enable) {
        if (enable) {
            mStartSpeakBtn.setEnabled(true);
        } else {
            mStartSpeakBtn.setEnabled(false);
        }
    }

    public void onPrepare() {
        // Prepare...
        mBottomBarlayout.setBackgroundColor(Color.parseColor(GRAY));
        mStartSpeakBtn.setEnabled(false);
        mTextViewAnswer.setText(R.string.mic_prepare_help);
        if(setMessageImpl != null) {
            setMessageImpl.showRelativedView(ShowViewType.ListView);
        }
    }

    public void onIdle(boolean resetMicrophoneText) {
        mBottomBarlayout.setBackgroundColor(Color.parseColor(GRAY));
        mStartSpeakBtn.setVisibility(View.VISIBLE);
        mVoiceRecognition.setVisibility(View.GONE);
        if(setMessageImpl != null) {
            setMessageImpl.showRelativedView(ShowViewType.ListView);
        }
        stopRecognize();
        mStartSpeakBtn.setEnabled(true);
    }

    public void onRecording() {
        // start receive voice , Listening to user speaking ...
        mBottomBarlayout.setBackgroundColor(Color.parseColor(GRAY));
        mStartSpeakBtn.setVisibility(View.GONE);
        mTextViewAnswer.setText(R.string.mic_recording);
        mTextViewAnswer.setVisibility(View.GONE);
        mVoiceRecognition.setVisibility(View.VISIBLE);
        if(setMessageImpl != null) {
            setMessageImpl.showRelativedView(ShowViewType.HelpView);
        }
        startRecognize();
    }

    public void onProcess() {
        // recognition...
        mBottomBarlayout.setBackgroundColor(Color.parseColor(GRAY));
        stopRecognize();
        mStartSpeakBtn.setVisibility(View.GONE);
        mTextViewAnswer.setText(R.string.mic_processing);
        mTextViewAnswer.setVisibility(View.VISIBLE);
        if(setMessageImpl != null) {
            setMessageImpl.showRelativedView(ShowViewType.Recognition);
        }
        startRecognize();
    }

    private void startRecognize() {
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable = null;
        }
        animationDrawable = (AnimationDrawable) mVoiceRecognition.getDrawable();
        animationDrawable.start();
    }

    private void stopRecognize() {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    public void onDestroy() {
        setOnClickListener(null);
    }

    private TextView mTextViewAnswer;

    public void setAnswerText(MsgSource msgSource, String text) {
        LOG.writeMsg(this, LOG.MODE_VOICE, "AnswerText(), text-->>>" + text);
        if(mTextViewAnswer != null) {
        	if(!"".equals(text)) {
        		mTextViewAnswer.setText(text);
        	}
        }
        
        if(!isShowSessionView) {
            if(setMessageImpl != null) {
                setMessageImpl.showRelativedView(ShowViewType.ListView);
            } 
            
        } else {
            if(setMessageImpl != null) {
                setMessageImpl.showRelativedView(ShowViewType.HelpView);
            }
            isShowSessionView = false;
        }

        if(setMessageImpl != null) {
            setMessageImpl.setMessage(msgSource, text);
        }
    }
    
    private boolean isShowSessionView = false;
    public void setAnswerView(boolean isShow) {
        this.isShowSessionView = isShow;
        if(setMessageImpl != null) {
            setMessageImpl.showRelativedView(ShowViewType.HelpView);
        }
    }

    public void setTextViewAnswer(TextView tv) {
        this.mTextViewAnswer = tv;
        this.mTextViewAnswer.setSelected(true);
    }
    
    public TextView getmTextViewAnswer() {
        return mTextViewAnswer;
    }

    private SetMessageImpl setMessageImpl;
    
    
    public void setSetMessageImpl(SetMessageImpl setMessageImpl) {
        this.setMessageImpl = setMessageImpl;
    }

    public interface SetMessageImpl {
        /**
         * <功能描述>设置ListView的数据
         * @param msgSource 来源
         * @param message [参数说明] 显示数据
         * @return void [返回类型说明] 无，
         */
        void setMessage(MsgSource msgSource, String message);
        /**
         * <功能描述>
         * @param showOrHide [参数说明] true : 显示ListView, false : 隐藏，
         * @return void [返回类型说明]
         */
        void showRelativedView(ShowViewType viewType);
    }
    
    public static enum ShowViewType {
        ListView, HelpView, Recognition;
    }

}
