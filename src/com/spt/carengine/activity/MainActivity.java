/*
 * 文 件 名:  MainActivity.java 
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Administrator
 * 创建时间:  2015年8月5日
 */

package com.spt.carengine.activity;

import java.lang.ref.WeakReference;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.navisdk.remote.BNRemoteVistor;
import com.baidu.navisdk.remote.aidl.BNEventListener;
import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.album.AlbumFragment;
import com.spt.carengine.btcall.BTService;
import com.spt.carengine.btcall.BtMainFragment;
import com.spt.carengine.btcall.CallFragment;
import com.spt.carengine.constant.CameraConstant;
import com.spt.carengine.constant.Constant;
import com.spt.carengine.define.BTApi;
import com.spt.carengine.define.Define;
import com.spt.carengine.fragment.AboutFragment;
import com.spt.carengine.fragment.BluetoothFragment;
import com.spt.carengine.fragment.CloudsDogFragment;
import com.spt.carengine.fragment.DateTimeFragment;
import com.spt.carengine.fragment.ExploreVideoFragment;
import com.spt.carengine.fragment.FMFragment;
import com.spt.carengine.fragment.MainContentFragment;
import com.spt.carengine.fragment.RecordVideoFragment;
import com.spt.carengine.fragment.SDFragment;
import com.spt.carengine.fragment.SystemSettingsFragment;
import com.spt.carengine.fragment.TrafficFragment;
import com.spt.carengine.fragment.UserFragment;
import com.spt.carengine.fragment.VoiceFragment;
import com.spt.carengine.fragment.WIFIFragment;
import com.spt.carengine.log.LOG;
import com.spt.carengine.mainservice.EDogService;
import com.spt.carengine.mainservice.YrcCarnetServer;
import com.spt.carengine.recordvideo.GetVideoOutputPath;
import com.spt.carengine.recordvideo.RecordService;
import com.spt.carengine.utils.LogUtil;
import com.spt.carengine.utils.SharePrefsUtils;
import com.spt.carengine.utils.UtilTools;
import com.spt.carengine.view.AvInVideoManager;
import com.spt.carengine.view.DialogView;
import com.spt.carengine.view.DialogView.OnDialogListener;
import com.spt.carengine.view.StatusBarView;
import com.spt.carengine.voice.VoiceViewService;
import com.spt.carengine.voice.assistant.talk.TalkService;

import de.greenrobot.event.EventBus;

/**
 * <功能描述>
 * 
 * @author Administrator
 */

public class MainActivity extends CommActivity {
    private static final String TAG = "MainActivity";
    private static final int MSG_UPDATE_GPS_OFF_ON_STATUS = 0x1000;
    private View mFragmentContainer;
    private int mFragmentContainerId;
    private EventBus mEventBus;
    private StatusBarView mStatusBarView;
    private TelephonyManager mTelephonyManager;
    private LocationManager mLocationManager;
    private PowerManager mPowerManager;
    private Handler mHandler = new MyHandler(this);
    private boolean isCalling = false;
    private static final String TRAFFIC_CONFIG = "traffic_config";
    private static final String ACTION_AAC = "com.spt.aac";

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mMain;

        public MyHandler(MainActivity main) {
            mMain = new WeakReference<MainActivity>(main);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity main = mMain.get();
            if (main == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_GPS_OFF_ON_STATUS:
                    main.updateGPSOffOnStatus();
                    break;
                    
                default:
                    break;
            }
        }
    }

    private void updateGPSOffOnStatus() {
        boolean enabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        mStatusBarView.showGpsStatus(enabled);
    }

    private final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mHandler.sendEmptyMessage(MSG_UPDATE_GPS_OFF_ON_STATUS);
        }
    };
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level = signalStrength.getLevel();
            mStatusBarView.showSigner(level);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (Intent.ACTION_TIME_TICK.equals(action)) {
                mStatusBarView.showTime();
            }
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {// 监听wifi开关
                mStatusBarView.showWifiOnOff(intent);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {// 监听wifi信号
                mStatusBarView.showWifiSignalLevel(intent);
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                mStatusBarView.updateWifiSingalLevel();
            } else if (BTApi.ACTION_YRC_BD.equals(action)) {// 监听蓝牙状态广播,m_nbtState
                                                            // 0未初始化，1
                                                            // 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中
                int btstate = intent.getIntExtra("btstate", -1);
                int nCmd = intent.getIntExtra("cmd", -1);
                // 拨打电话
                if (BTApi.BT_CALL_NUM == nCmd) {// 是拨打电话状态
                    String number = intent.getStringExtra("data");
                    if (number != null) {
                        if (isCalling)
                            return;
                        showCallModule(number, CallFragment.CALL_STATE);
                    }

                    isCalling = true;
                }
                if (btstate == CallFragment.RING_RECEIVER)// 来电广播状态
                {
                    String number = intent.getStringExtra("data");
                    if (number != null) {
                        showCallModule(number, CallFragment.RING_STATE);
                    }
                }

                if (btstate == 3) {
                    isCalling = false;
                }

                if (btstate < 3) {// 蓝牙未连接
                    mStatusBarView.showBTStatus(false);
                } else if (btstate >= 3) {// 蓝牙已经连接
                    mStatusBarView.showBTStatus(true);
                }

            } else if (SHOW_DIALOG_DISK_FULL_ACTION.equals(action)) {
                boolean isFlag = intent.getBooleanExtra(
                        IS_SHOW_DELETE_DIALOG_KEY, false);
                if (isFlag) {
                    showClearStorageDialog(context);

                } else {
                    dissmissDeleteDialog();
                }
            } else if (TalkService.WAKEUP_EVENT_ON_SUCCESS.equals(action)) {
                LOG.writeMsg(this, LOG.MODE_VOICE,
                        "main onWakeupSuccess, show voice module");
                showVoiceModule();
            } else if (ACTION_AAC.equals(action)) {
                boolean flag = intent.getBooleanExtra("aac", true);// 熄火：false，打火：true
                LogUtil.d(TAG, "=====>>>in ACTION_AAC,flag:" + flag);
                handleAAC(flag);
            } else if (CameraConstant.AVIN_CAMERA_ACTION.equals(action)) {
                boolean direct_flag = intent.getBooleanExtra("direct_flag",
                        true);
                LogUtil.d(TAG, "::::" + direct_flag);
                if (direct_flag) {
                    startAvInVideo();
                } else {
                    stopAvInVideo();
                }
            }

        }
    };

    // flag, 熄火：false，打火：true
    private void handleAAC(boolean flag) {
        if (flag) {
            if (!mPowerManager.isScreenOn()) {
                mPowerManager.wakeUp(SystemClock.uptimeMillis());
            }
        } else {
            mPowerManager.goToSleep(SystemClock.uptimeMillis());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);

        Intent intent = new Intent(getApplicationContext(), RecordService.class);
        startService(intent);

        initView();
        initData();
        initNavi();
    }

    private void initView() {
        mStatusBarView = (StatusBarView) findViewById(R.id.status_bar);
        mFragmentContainer = findViewById(R.id.fragment_container);
        mFragmentContainerId = R.id.fragment_container;
        SharedPreferences mTrafficSP = getSharedPreferences(TRAFFIC_CONFIG,
                MODE_PRIVATE);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(BTApi.ACTION_YRC_BD); // 监听蓝牙状态
        filter.addAction(SHOW_DIALOG_DISK_FULL_ACTION);
        filter.addAction(TalkService.WAKEUP_EVENT_ON_SUCCESS);
        filter.addAction(CameraConstant.AVIN_CAMERA_ACTION);
        filter.addAction(ACTION_AAC);
        filter.addCategory(getPackageInfo());

        registerReceiver(mReceiver, filter);
        showHomeModule();
    }

    private String getPackageInfo() {
        String mPackageName = "";
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            mPackageName = info.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            mPackageName = null;
        }
        return mPackageName;
    }

    private void initData() {
        mEventBus = EventBus.getDefault();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mEventBus.register(this);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        mStatusBarView.showGpsStatus(gpsStatus);
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().startRecordVideoServer();
        initYrcServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().unBindRecordVideoService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(getApplicationContext(), RecordService.class);
        stopService(intent);

        unregisterReceiver(mReceiver);
        getContentResolver().unregisterContentObserver(mGpsMonitor);
        BNRemoteVistor.getInstance().disconnectToBNService(
                getApplicationContext());

    }

    /**
     * eventbus监听
     */
    public void onEventMainThread(Integer moduleType) {
        switch (moduleType) {
            case Constant.MODULE_TYPE_AMUSEMENT:
                showAmusementModule();
                break;
            case Constant.MODULE_TYPE_NAV:
                showNavModule();
                break;
            case Constant.MODULE_TYPE_NUMBER:
                showNumberModule();
                break;
            case Constant.MODULE_TYPE_CAR_RECORD:
                showCarRecordModule();
                break;
            case Constant.MODULE_TYPE_USER:
                showUserModule();
                break;
            case Constant.MODULE_TYPE_VIDEO:
                showVideoModule();
                break;
            case Constant.MODULE_TYPE_PHOTO:
                showPhotoModule();
                break;
            case Constant.MODULE_TYPE_HOME:
                showHomeModule();
                break;
            case Constant.MODULE_TYPE_CAMERA:
                showCaremaModule();
                break;
            case Constant.MODULE_TYPE_VOICE:
                showVoiceModule();
                break;
            case Constant.MODULE_TYPE_FM:
                showFMModule();
                break;
            case Constant.MODULE_TYPE_CDOG:
                showCloudsDogModule();
                break;
            case Constant.MODULE_TYPE_BLUETOOTH:
                showBluetooth();
                break;
            case Constant.MODULE_TYPE_SYSTEM_SETTINGS:
                showSTModule();
                break;
            case Constant.MODULE_TYPE_SYSTEM_WIFI:
                showWIFIModule();
                break;
            case Constant.MODULE_TYPE_SDSET:
                showScard();
                break;
            case Constant.MODULE_TYPE_TRAFFIC:
                showTraffic();
                break;
            case Constant.MODULE_TYPE_DATE:
                showDateTime();
                break;
            case Constant.MOUDLE_TYPE_ABOUT:
                showAbout();
                break;
            case Constant.REBACK_CAR_STATE:
                // 启动倒车后视
                AvInVideoManager.getInstance().startAvInVideo(this);
                break;
        }

    }

    /***
     * 显示关于模块
     */
    private void showAbout() {
        if (mFragmentContainer != null) {
            AboutFragment aboutContent = new AboutFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, aboutContent)
                    .addToBackStack(null);
            transaction.commit();
        }

    }

    /***
     * 显示日期与时间模块
     */
    private void showDateTime() {
        if (mFragmentContainer != null) {
            DateTimeFragment dateContent = new DateTimeFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, dateContent)
                    .addToBackStack(null);
            transaction.commit();
        }

    }

    /***
     * 显示SD卡模块
     */
    private void showScard() {
        if (mFragmentContainer != null) {
            SDFragment sdContent = new SDFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, sdContent)
                    .addToBackStack(null);
            transaction.commit();
        }

    }

    /****
     * 显示蓝牙模块
     */
    private void showBluetooth() {
        if (mFragmentContainer != null) {
            BluetoothFragment blueFragment = new BluetoothFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, blueFragment)
                    .addToBackStack(null);
            transaction.commit();
        }

    }

    /**
     * 显示home模块
     */
    private void showHomeModule() {
        if (mFragmentContainer != null) {
            MainContentFragment mainContent = new MainContentFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, mainContent);
            transaction.commit();
        }
    }

    /**
     * 显示个人中心模块
     */

    private void showUserModule() {
        if (mFragmentContainer != null) {
            UserFragment mUser = new UserFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, mUser);
            transaction.commit();
        }
    }

    /**
     * 显示相册模块
     */
    private void showPhotoModule() {
        if (mFragmentContainer != null) {
            AlbumFragment albumFragment = new AlbumFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, albumFragment);
            transaction.commit();
        }
    }

    /**
     * 显示视频模块
     */
    private void showVideoModule() {
        if (mFragmentContainer != null) {
            ExploreVideoFragment exploreVideoFragment = new ExploreVideoFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, exploreVideoFragment)
                    .addToBackStack("video");
            transaction.commit();
        }
    }

    /**
     * 显示拨号模块
     */
    private void showNumberModule() {
        if (mFragmentContainer != null) {
            BtMainFragment btContent = new BtMainFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, btContent);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    /**
     * 显示导航模块
     */
    private void showNavModule() {
        MyApplication.getInstance().unBindRecordVideoService();
        try {
            UtilTools.doStartApplicationWithPackageName(Constant.BAIDU_PACKAGE,
                    this);

        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }
        // Intent intent = new Intent("com.baidu.navi.action.START");
        // intent.setData(Uri.parse("bdnavi://launch"));
        // startActivity(intent);
    }

    /**
     * 显示照相模块
     */
    private void showCaremaModule() {
        MyApplication.getInstance().getBindService().doTakePicture();
    }

    /**
     * 显示语音模块
     */
    private void showVoiceModule() {
        if (mFragmentContainer != null) {
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            Fragment fragment = getFragmentManager().findFragmentById(
                    mFragmentContainerId);
            if (!(fragment instanceof VoiceFragment)) {
                VoiceFragment voiceFragment = new VoiceFragment();
                transaction.replace(mFragmentContainerId, voiceFragment);
                transaction.commitAllowingStateLoss();
            }
        }
    }

    /**
     * 显示娱乐模块
     */
    private void showAmusementModule() {

        // 启动酷我音乐 cn.kuwo.kwmusiccar
        try {
            UtilTools.doStartApplicationWithPackageName(Constant.KUWO_PACKAGE,
                    this);
        } catch (NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.kuwo_apk,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 显示行车记录模块
     */
    private void showCarRecordModule() {
        if (mFragmentContainer != null) {
            RecordVideoFragment recordVideoFragment = new RecordVideoFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, recordVideoFragment);
            transaction.commit();
        }
    }

    /**
     * 显示来电和去电模块
     */
    private void showCallModule(String number, int state) {
        if (mFragmentContainer != null) {

            CallFragment callFragment = new CallFragment(
                    getApplicationContext(), number, state);
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, callFragment);
            transaction.commit();
        }
    }

    /**
     * FM模块
     */
    private void showFMModule() {
        if (mFragmentContainer != null) {
            FMFragment mFMFragment = new FMFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, mFMFragment);
            transaction.commit();
        }
    }

    /**
     * 系统设置模块
     */

    private void showSTModule() {
        if (mFragmentContainer != null) {
            SystemSettingsFragment mSystemSettings = new SystemSettingsFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, mSystemSettings);
            transaction.commit();
        }
    }

    /**
     * wifi模块
     */

    private void showWIFIModule() {
        if (mFragmentContainer != null) {
            WIFIFragment mWiFi = new WIFIFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, mWiFi);
            transaction.commit();
        }
    }

    /**
     * 云狗模块
     */
    private void showCloudsDogModule() {
        if (mFragmentContainer != null) {
            CloudsDogFragment clDogFragment = new CloudsDogFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, clDogFragment);
            transaction.commit();
        }
    }

    /**
     * 显示流量统计模块
     */

    private void showTraffic() {
        if (mFragmentContainer != null) {
            TrafficFragment trafficFragment = new TrafficFragment();
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(mFragmentContainerId, trafficFragment)
                    .addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * Start Main Server
     */
    private void initYrcServer() {
        Intent intent = new Intent();
        intent.setClass(this, YrcCarnetServer.class);
        startService(intent);

        Intent intbt = new Intent();
        intbt.setClass(this, BTService.class);
        startService(intbt);

        Intent intedog = new Intent();
        intedog.setClass(this, EDogService.class);
        startService(intedog);

        Intent voiceIntent = new Intent();
        voiceIntent.setClass(this, VoiceViewService.class);
        startService(voiceIntent);

    }

    private Dialog dialog = null;

    public void showClearStorageDialog(Context context) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                return;
            }
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_video_delete, null);
        dialog.addContentView(view, lp);
        DialogView dialogView = (DialogView) dialog
                .findViewById(R.id.album_delete_dialog);
        dialogView.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onExecutive() {
                GetVideoOutputPath getVideoOutputPath = new GetVideoOutputPath();
                getVideoOutputPath.deleteOldVideoFile();
                SharePrefsUtils.saveAutoClearRecordVideoFile(m_context, true);
                dialog.dismiss();
            }

            @Override
            public void onCloseDialog() {
                dialog.dismiss();
            }
        });
        dialogView.setMessage(getMsg());
        dialog.show();
    }

    private void dissmissDeleteDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private Spanned getMsg() {
        String result = String.format(MyApplication.getInstance().getString(
                R.string.video_storage_full_needto_clear));
        return Html.fromHtml(result);
    }

    public static final String SHOW_DIALOG_DISK_FULL_ACTION = "com.show.diaolog.disk.full.hint";
    public static final String IS_SHOW_DELETE_DIALOG_KEY = "is.show.delete.dialog";

    /***
     * 打开车后视频
     */
    private void startAvInVideo() {

        SharePrefsUtils.saveRebackCarState(MyApplication.getInstance()
                .getApplicationContext(), true);
        MyApplication.getInstance().unBindRecordVideoService();

    }

    /***
     * 关闭车后视频
     */
    private void stopAvInVideo() {
        Camera mCamera = AvInVideoManager.getInstance().stopAvInVideo();
        Log.d("zhentao", "----stop back");
        if (null == mCamera) {
            Log.d("zhentao", "----startRecordVideoServer");
            MyApplication.getInstance().startRecordVideoServer();
        }

    }

    private void sendBraocast(int flag) {
        if (MyApplication.getInstance().getBindService() != null) {

        }

    }

    private void destoryTalkSessionServer() {
        Intent intent = new Intent(this, VoiceViewService.class);
        intent.setAction(VoiceViewService.ACTION_DESTORY_SESSION_MANAGER);
        this.startService(intent);
    }

    /*
     * 初始化导航
     */
    private void initNavi() {
        BNRemoteVistor.getInstance().setOnConnectListener(mOncConnectListener);
        BNRemoteVistor.getInstance()
                .connectToBNService(getApplicationContext());
    }

    private BNEventListener.Stub mBNEventListener = new BNEventListener.Stub() {

        /**
         * 辅助诱导图标更新回调
         * 
         * @param assitantType 辅助诱导类型
         *            {@link com.baidu.navisdk.remote.BNRemoteConstants.AssitantType
         *            <code>AssitantType</code>}
         * @param limitedSpeed 当类型是SpeedCamera和IntervalCamera的时候，会带有限速的值
         * @param distance 诱导距离(以米位单位),当距离为0时，表明这个诱导丢失消失
         */
        @Override
        public void onAssistantChanged(int assistantType, int limitedSpeed,
                int distance) throws RemoteException {
            Log.e(Define.TAG, "onRoadCameraChanged: assistantType = "
                    + assistantType + " ,limitedSpeed = " + limitedSpeed
                    + " ,distance = " + distance);
        }

        /**
         * GPS速度变化，在实际应用中，在某些情况下，手机GPS速度与实际车速在相差4km/h左右
         * 
         * @param speed gps速度，单位km/h
         * @param latitude 纬度，GCJ-02坐标
         * @param longitude 经度，GCJ-02坐标
         */
        @Override
        public void onGpsChanged(int speed, double latitude, double longitude) {
            Log.e(Define.TAG, "onGpsSpeedChanged: speed = " + speed
                    + " ,latitude = " + latitude + " ,longitude = " + longitude);
        }

        /**
         * 服务区更新回调
         * 
         * @param serviceArea 服务区的名字
         * @param distance 服务区的距离，当distance为0或者serviceArea为空时，表明服务区消失
         */
        @Override
        public void onServiceAreaChanged(String serviceArea, int distance) {
            Log.e(Define.TAG, "onServiceAreaChanged: serviceArea = "
                    + serviceArea + " ,distance = " + distance);
        }

        /**
         * 导航机动点更新
         * 
         * @param maneuverName 下一个机动点名称，具体可以参考官网上，每一个机动点名称对应的图标
         * @param distance 距离下一个机动点距离（以米为单位）
         */
        @Override
        public void onManeuverChanged(String maneuverName, int distance) {
            Log.e(Define.TAG, "onManeuverChanged: maneuverName = "
                    + maneuverName + " ,distance = " + distance);
        }

        /**
         * 到达目的地的距离和时间更新
         * 
         * @param remainDistance 到达目的地的剩余距离（以米为单位）
         * @param remainTime 到达目的地的剩余时间(以秒为单位)
         */
        @Override
        public void onRemainInfoChanged(int remainDistance, int remainTime) {
            Log.e(Define.TAG, "onRemainInfoChanged: remainDistance = "
                    + remainDistance + " ,remainTime = " + remainTime);
        }

        /**
         * 当前道路名更新
         * 
         * @param currentRoadName 当前路名
         */
        @Override
        public void onCurrentRoadNameChanged(String currentRoadName) {
            Log.e(Define.TAG, "onCurrentRoadNameChanged: currentRoadName = "
                    + currentRoadName);
        }

        /**
         * 下一道路名更新
         * 
         * @param nextRoadName 下一个道路名
         */
        @Override
        public void onNextRoadNameChanged(String nextRoadName) {
            Log.e(Define.TAG, "onNextRoadNameChanged: nextRoadName = "
                    + nextRoadName);
        }

        /**
         * 开始导航
         */
        @Override
        public void onNaviStart() {
            Log.e(Define.TAG, "onNaviStart");
        }

        /**
         * 结束导航
         */
        @Override
        public void onNaviEnd() {
            Log.e(Define.TAG, "onNaviEnd");
        }

        /**
         * 电子狗模式开启
         */
        @Override
        public void onCruiseStart() {
            Log.e(Define.TAG, "onCruiseStart");
        }

        /**
         * 电子狗模式结束
         */
        @Override
        public void onCruiseEnd() {
            Log.e(Define.TAG, "onCruiseEnd");
        }

        /**
         * 导航中偏航
         */
        @Override
        public void onRoutePlanYawing() {
            Log.e(Define.TAG, "onRoutePlanYawing");
        }

        /**
         * 导航中偏航结束,重新算路成功
         */
        @Override
        public void onReRoutePlanComplete() {
            Log.e(Define.TAG, "onReRoutePlanComplete");
        }

        /**
         * gps丢失
         */
        @Override
        public void onGPSLost() {
            Log.e(Define.TAG, "onGPSLost");
        }

        /**
         * gps正常
         */
        @Override
        public void onGPSNormal() {
            Log.e(Define.TAG, "onGPSNormal");
        }

        /**
         * 扩展事件接口，现在暂时没有数据回调，用作以后扩展
         * 
         * @param eventType
         * @param data
         */
        @Override
        public void onExtendEvent(int eventType, Bundle data) {
            Log.e(Define.TAG, "onExtendEvent: eventType = " + eventType
                    + " ,data = " + data.toString());
        }

    };

    /**
     * 连接监听
     */
    private BNRemoteVistor.OnConnectListener mOncConnectListener = new BNRemoteVistor.OnConnectListener() {

        @Override
        public void onDisconnect() {
            Log.e(Define.TAG, "onDisconnect");
        }

        @Override
        public void onConnectSuccess() {
            try {
                BNRemoteVistor.getInstance().setBNEventListener(
                        mBNEventListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "connect success",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onConnectFail(final String reason) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "connect fail reason:" + reason, Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    };
}
