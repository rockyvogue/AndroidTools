
package com.spt.carengine.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.album.AlbumFragment;
import com.spt.carengine.log.LOG;
import com.spt.carengine.voice.VoiceMsgAdapter;
import com.spt.carengine.voice.VoiceViewService;
import com.spt.carengine.voice.assistant.MessageReceiver;
import com.spt.carengine.voice.assistant.baidu.BaiduNavi;
import com.spt.carengine.voice.view.VoiceBottomMicroControlBar;
import com.spt.carengine.voice.view.VoiceBottomMicroControlBar.SetMessageImpl;
import com.spt.carengine.voice.view.VoiceBottomMicroControlBar.ShowViewType;
import com.spt.carengine.voice.view.VoiceMessageItem;
import com.spt.carengine.voice.view.VoiceMessageItem.MsgSource;
import com.spt.carengine.voice.view.VoiceSessionContainer;
import com.spt.carengine.voice.view.VoiceSessionRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * <功能描述> 语音模块的Fragment 视图
 * 
 * @author Rocky
 */
public class VoiceFragment extends Fragment implements SetMessageImpl{

    public static final String TAG = "VoiceFragment";

    private Activity mActivity;
    public static VoiceSessionRelativeLayout mRootView;
    public static VoiceBottomMicroControlBar mVControlBar;
    public static VoiceSessionContainer mSessionContainer;
    
    private ListView mListView;// 展示消息的
    private VoiceMsgAdapter mMessageAdapter;
    private List<VoiceMessageItem> msgList = new ArrayList<VoiceMessageItem>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = (VoiceSessionRelativeLayout) inflater.inflate(
                R.layout.fragment_voice, container, false);
        initUI();
        registerReceiver();
        setListData(MsgSource.Rebot, getString(R.string.mic_prepare_help));
        return mRootView;
    }

    private void initUI() {
        mListView = (ListView) mRootView.findViewById(R.id.voice_message_listview);
        mVControlBar = (VoiceBottomMicroControlBar) mRootView
                .findViewById(R.id.voice_micro_control_bar);
        mSessionContainer = (VoiceSessionContainer) mRootView
                .findViewById(R.id.voice_session_container);
        mVControlBar.setTextViewAnswer((TextView) mRootView
                .findViewById(R.id.voice_process_hint_tv));
        mMessageAdapter = new VoiceMsgAdapter(getActivity(), msgList);
        mListView.setAdapter(mMessageAdapter);
        mVControlBar.setSetMessageImpl(this);
    }
    
    @Override
    public void onResume() {
        startTalk();
        super.onResume();
    }

    @Override
    public void onPause() {
        cancelSession();
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }
    
    private void startTalk() {
        Intent intent = new Intent(this.getActivity(), VoiceViewService.class);
        intent.setAction(MessageReceiver.ACTION_START_TALK);
        intent.putExtra(VoiceViewService.EXTRA_KEY_START_TALK_FROM, VoiceViewService.START_TALK_FROM_MAIN_ACTIVITY);
        this.getActivity().startService(intent);
    }

    private void cancelSession() {
        Intent intent = new Intent(this.getActivity(), VoiceViewService.class);
        intent.setAction(VoiceViewService.ACTION_CANCEL_SESSION_MANAGER);
        getActivity().startService(intent); 
    }

    /*************************** 注册和注销广播与监听器 ***********************/
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN_BAIDU_NAVI_COMMMAND);
        filter.addAction(ACTION_OPEN_HOME_COMMMAND);
        filter.addAction(ACTION_OPEN_CAR_RECORD_COMMMAND);
        filter.addAction(ACTION_OPEN_GAODE_MAP_COMMMAND);
        filter.addAction(ACTION_OPEN_USER_CENTER_COMMMAND);
        filter.addAction(ACTION_TAKING_PICTURE_COMMMAND);
        filter.addAction(ACTION_OPEN_PHOTO_COMMMAND);
        filter.addAction(ACTION_OPEN_VIDEO_COMMMAND);
        mActivity.registerReceiver(mOperationReceiver, filter);
    }

    private void unregisterReceiver() {
        mActivity.unregisterReceiver(mOperationReceiver);
    }

    /**
     * 屏幕亮和关闭的广播
     */
    private BroadcastReceiver mOperationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            
            String action = intent.getAction();
//            if(action != null) return;
            LOG.writeMsg(this, LOG.MODE_VOICE, "onReceive:intent " + intent
                    + ", action : " + action);
            if (ACTION_OPEN_HOME_COMMMAND.equals(action)) {
                startFragment(new MainContentFragment());
                
            } else if (ACTION_OPEN_CAR_RECORD_COMMMAND.equals(action)) {
                startFragment(new RecordVideoFragment());

            } else if (ACTION_OPEN_BAIDU_NAVI_COMMMAND.equals(action)) {
                // 高德导航
                BaiduNavi.getIntance().startBaiduNaviApp(context);
                return;

            } else if (ACTION_OPEN_USER_CENTER_COMMMAND.equals(action)) {
                startFragment(new UserFragment());

            } else if (ACTION_TAKING_PICTURE_COMMMAND.equals(action)) {
                if (MyApplication.getInstance().getBindService() != null) {
                    MyApplication.getInstance().getBindService().doTakePicture();
                } else {
                }

            } else if (ACTION_OPEN_PHOTO_COMMMAND.equals(action)) {
                startFragment(new AlbumFragment());

            } else if (ACTION_OPEN_VIDEO_COMMMAND.equals(action)) {
                startFragment(new ExploreVideoFragment());
            } 
        }
    };
    
    private void startFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public static final String ACTION_OPEN_HOME_COMMMAND = "com.spt.carengine.open.home";
    public static final String ACTION_OPEN_BAIDU_NAVI_COMMMAND = "com.spt.carengine.open.baidu.navi";
    public static final String ACTION_OPEN_CAR_RECORD_COMMMAND = "com.spt.carengine.open.car.record";
    public static final String ACTION_OPEN_GAODE_MAP_COMMMAND = "com.spt.carengine.open.gaode.map";
    public static final String ACTION_OPEN_USER_CENTER_COMMMAND = "com.spt.carengine.open.user_center";
    public static final String ACTION_TAKING_PICTURE_COMMMAND = "com.spt.carengine.taking.picture";
    public static final String ACTION_OPEN_PHOTO_COMMMAND = "com.spt.carengine.open.photo";
    public static final String ACTION_OPEN_VIDEO_COMMMAND = "com.spt.carengine.open.video";

    @Override
    public void setMessage(MsgSource msgSource, String message) {
        if(!"".equals(message)) {
            setListData(msgSource, message);
        }
    }

    @Override
    public void showRelativedView(ShowViewType viewType) {
        if(viewType == ShowViewType.HelpView) {
            mVControlBar.getmTextViewAnswer().setVisibility(View.GONE);
            mSessionContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            
        } else if(viewType == ShowViewType.ListView) {
            mVControlBar.getmTextViewAnswer().setVisibility(View.GONE);
            mSessionContainer.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            
        } else if(viewType == ShowViewType.Recognition) {
            mVControlBar.getmTextViewAnswer().setVisibility(View.VISIBLE);
            mSessionContainer.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
    }
    
    private void setListData(MsgSource msgSource, String message) {
        VoiceMessageItem voiceMessageItem = new VoiceMessageItem();
        voiceMessageItem.setMessage(message);
        voiceMessageItem.setMessageFromWhere(msgSource);
        msgList.add(voiceMessageItem);
        mMessageAdapter.notifyDataSetChanged();
        mListView.setSelection(mMessageAdapter.getCount() - 1);
    }
    
    public boolean onBackPressed() {  
        Intent intent = new Intent(VoiceFragment.ACTION_OPEN_HOME_COMMMAND);
        this.getActivity().sendBroadcast(intent);
        return false;  
    }
    
}
