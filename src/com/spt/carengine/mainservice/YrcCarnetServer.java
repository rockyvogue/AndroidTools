
package com.spt.carengine.mainservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.spt.carengine.MyApplication;
import com.spt.carengine.btcall.BtReceiver;
import com.spt.carengine.db.Provider;
import com.spt.carengine.define.Define;
import com.spt.carengine.log.LOG;
import com.spt.carengine.log.ReportTraceFile;
import com.spt.carengine.utils.GetTokenUtils;
import com.spt.carengine.utils.UtilTools;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 车联网主服务，
 * 
 * @author Administrator
 */
public class YrcCarnetServer extends Service {

    private static final String TAG = "YrcCarnetServer";

    public final int HANDLE_TOAST_CMD = 0x01;// toast

    public final int HANDLE_WEB_CMD = 0x03;// WEB命令,

    public Thread m_send_cmd_thread = null;// 发送线程,

    public static boolean bServRuning = false;// 后台服务正在运行中

    private String m_sActivate = "0"; // activate
    private String m_sIMEI = ""; // imei
    private String m_sMobile = ""; // Mobile
    private String m_sDversion = ""; // dversion
    private String m_sAPPver = ""; // dAPPver
    private String m_sDToken = ""; // DToken
    private String m_sDeviceid = ""; // Deviceid

    private boolean bXGRegistSuc = false;// 信鸽是否注册成功
    public static OSSService ossService = null;// 阿里云

    private boolean bUPLoadGps = true; // 是否上报轨迹

    private int nRetCode = -1;// 返回码。服务器执行状态，0失败 1成功
    private String sRetDesc = "";// 执行结果描述

    private ReportGpsTrace reportGpsTrace;
    // 流量统计。
    private SharedPreferences mTrafficSP;
    private static final String TRAFFIC_CONFIG = "traffic_config";
    private static final String TRAFFIC_VALUE = "traffic_value";
    private static final String TRAFFIC_CURRENT = "traffic_current";
    private static final String TRAFFIC_BUFFER_POWER_OFF = "buffer_power_off";
    private static final String TRAFFIC_BUFFER_CLEAN_BEFORE = "buffer_clean_before";
    private static final String IS_CLEANED = "isCleaned";
    private static final String IS_POWER_OFF = "isPowerOff";
    private Timer mTrafficTimer;
    private TimerTask mTrafficTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(Define.TAG, "onStartCommand() executed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Define.TAG, "onDestroy() executed");
        bServRuning = false;
        registerBroadRecevier(false);
        reportGpsTrace.cancelReportTrace();
        mTrafficTask.cancel();
        mTrafficTimer.cancel();
        mTrafficTask = null;
        mTrafficTimer = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                "yrcCarnetServer ： onCreate() executed");

        reportGpsTrace = new ReportGpsTrace(this);// 注册GPS

        bServRuning = true;
        ossService = OSSServiceProvider.getService();

        BtReceiver.CallType(this);

        registerBroadRecevier(true); // 注册广播
        registerXG();// 注册信鸽
        registerALY();// 注册阿里云

        registDataListener();

        getXGTokenData();

        getUserData();// 取数据库数据
        // 流量统计模块
        mTrafficSP = getSharedPreferences(TRAFFIC_CONFIG, MODE_PRIVATE);
        mTrafficTimer = new Timer();
        mTrafficTask = new TimerTask() {
            @Override
            public void run() {
                getTraffic();
            }
        };
        if (isMobileNetWorkConnected()) {
            mTrafficTimer.schedule(mTrafficTask, 0, 3000);
        }
        // 发送线程,用于定时上报轨迹
        m_send_cmd_thread = new Thread(new Runnable() {

            long nLastUpCmd = System.currentTimeMillis();

            long nLastRegXg = nLastUpCmd;

            long reportTraceTime = nLastUpCmd;

            @Override
            public void run() {
                // MyApplication.getInstance().initRecordVideo();
                // testUploadFileHanlder.sendEmptyMessageDelayed(HANDLER_MSG_UPLOAD_VIDEO,
                // DEFAULT_DELAY_TIME);

                while (true) {
                    if (NetConnectState() == false) { // 没网络
                        nLastUpCmd = System.currentTimeMillis();
                        sleep(200);
                        continue;
                    }

                    // 判断信鸽注册是否成功
                    if ((System.currentTimeMillis() - nLastRegXg >= ReportGpsTrace.REGISTER_XINGE_DEFAULT_INTERNAL_TIME)
                            && bXGRegistSuc) {
                        // 如果注册失败，继续注册信鸽，
                        registerXG();//
                        nLastRegXg = System.currentTimeMillis();
                    }

                    // 未激活设备激活
                    if (m_sActivate.equals("0") && bXGRegistSuc
                            && System.currentTimeMillis() - nLastUpCmd >= 15000) {
                        sendHttpCmdbyWebCmd(Define.UP_WEB_API.API_ACTIVATE,
                                null);
                        nLastUpCmd = System.currentTimeMillis();
                    }

                    // 定时上报轨迹.
                    if (m_sActivate.equals("1")
                            && bUPLoadGps
                            && (System.currentTimeMillis() - nLastUpCmd >= ReportGpsTrace.REPORT_TRACE_INTERNAL_TIME)) {
                        sendHttpCmdbyWebCmd(
                                Define.UP_WEB_API.API_UPLOAD_LOCATION, null);
                        ReportTraceFile.getInstance().reBuildRecordTraceName();
                        nLastUpCmd = System.currentTimeMillis();
                    }

                    sleep(500);// 0.5秒
                }
            }
        });
        m_send_cmd_thread.start();
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // // 注册广播和监听器 //////
    // ////////////////////////////////////////////////////////////////////////////////////////////

    public void registerBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Define.ACTION_XG_REGSTATE);// 信鸽激活状态
            filter.addAction(Define.ACTION_ACTIVATE_UPLOAD);// 激活
            filter.addAction(Define.ACTION_GPS_UPLOAD);// 上传GSP信息
            filter.addAction(Define.ACTION_PHOTO_UPLOAD);// 上传图片
            filter.addAction(Define.ACTION_VIDEO_UPLOAD);// 上传视频
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 网络状态变化
            filter.addAction(Define.ACTION_SHOW_DIALOG);// 提示框
            filter.addAction(Define.ACTION_UPLOADFILE_RESULT);// 文件上传结果
            filter.addAction(Intent.ACTION_SHUTDOWN);// 关机广播
            registerReceiver(ServCmdReceiver, filter);

        } else {
            unregisterReceiver(ServCmdReceiver);
        }
    }

    /**
     * 注册信鸽
     */
    @SuppressLint("NewApi")
    private void registerXG() {

        if (m_sIMEI.isEmpty()) {
            getIMIE();
        }
        XGPushConfig.enableDebug(this, false);
        XGPushManager.registerPush(getApplicationContext(), m_sIMEI);// 信鸽注册,IMEI作为帐号注册信鸽
    }

    /**
     * 注册阿里云
     */
    @SuppressLint("NewApi")
    private void registerALY() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // 初始化设置
        ossService.setApplicationContext(this.getApplicationContext());
        ossService.setGlobalDefaultHostId("oss-cn-hangzhou.aliyuncs.com"); // 设置region
                                                                           // host,
                                                                           // 即
                                                                           // endpoint
        ossService.setGlobalDefaultACL(AccessControlList.PRIVATE); // 默认为private
        ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK); // 设置加签类型为原始AK/SK加签
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
                    @Override
                    public String generateToken(String httpMethod, String md5,
                            String type, String date, String ossHeaders,
                            String resource) {

                        String content = httpMethod + "\n" + md5 + "\n" + type
                                + "\n" + date + "\n" + ossHeaders + resource;

                        return OSSToolKit.generateToken(
                                Define.ALIYUN_ACCESSKEY,
                                Define.ALIYUN_SCRECTKEY, content);
                    }
                });
        ossService
                .setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
        conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
        conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
        ossService.setClientConfiguration(conf);
    }

    /**
     * 注册数据监听
     */
    private void registDataListener() {
        getContentResolver().registerContentObserver(
                Uri.parse("content://" + Provider.AUTHORITY + "/userinfos"),
                true, new UserInfoObserver(new Handler()));
    }

    /**
     * 获取手机的IMEI和XG的token值
     */
    private void getXGTokenData() {
        // 信鸽注册成功后的token才是正确的token
        if (!bXGRegistSuc)
            return;
        m_sIMEI = getIMIE();
        m_sDToken = XGPushConfig.getToken(this);
    }

    /**
     * 从数据库获取用户信息数据
     */
    private void getUserData() {

        String[] stringArray = new String[] {
                Provider.UserInfo.ACTIVATE, Provider.UserInfo.DEVICEID,
                Provider.UserInfo.IMEI, Provider.UserInfo.MOBILE,
                Provider.UserInfo.DVERSION, Provider.UserInfo.SVERSION,
                Provider.UserInfo.DTOKEN
        };

        String[] selectionArgs = new String[] {
            "1"
        };

        Cursor c = getContentResolver().query(Provider.UserInfo.CONTENT_URI,
                stringArray, Provider.UserInfo._ID + "=?", selectionArgs, null);

        boolean bInsert = false;

        if (c == null || c.moveToFirst() == false || c.getCount() <= 0) {
            bInsert = true;
        }
        if (bInsert) {// 为空时插入一条

            ContentValues values = new ContentValues();
            values.put(Provider.UserInfo.ACTIVATE, m_sActivate);
            values.put(Provider.UserInfo.DEVICEID, getIMIE());
            values.put(Provider.UserInfo.IMEI, getIMIE());
            values.put(Provider.UserInfo.MOBILE, getMobile());
            values.put(Provider.UserInfo.DVERSION, getDeviceVer());
            values.put(Provider.UserInfo.SVERSION, getAPPVer());
            values.put(Provider.UserInfo.DTOKEN, m_sDToken);

            Uri uri = getContentResolver().insert(
                    Provider.UserInfo.CONTENT_URI, values);
            String lastPath = uri.getLastPathSegment();
            if (TextUtils.isEmpty(lastPath)) {
                Log.i(Define.TAG, "insert failure!");

            } else {
                Log.i(Define.TAG, "insert success! the id is " + lastPath);
            }
        } else {

            if (c != null && c.moveToFirst()) {

                m_sActivate = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.ACTIVATE));
                m_sDeviceid = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DEVICEID));
                m_sIMEI = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.IMEI));
                m_sMobile = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.MOBILE));
                m_sDversion = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DVERSION));
                m_sAPPver = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.SVERSION));
                m_sDToken = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DTOKEN));

                Log.e(Define.TAG, "用户表：m_sActivate:" + m_sActivate + "m_sIMEI:"
                        + m_sIMEI + "m_sMobile:" + m_sMobile + "m_sDversion:"
                        + m_sDversion + "m_sAPPver:" + m_sAPPver + "m_sDToken:"
                        + m_sDToken);

            } else {
                Log.e(Define.TAG, "query failure!");
            }
        }

        if (c != null) {
            c.close();
        }
    }

    // LOG.writeMsg(this, LOG.MODE_IFLYTEK, "当前正在说话，音量大小：" + volume);
    private static final int DEFAULT_DELAY_TIME = 5 * 1000;

    private static final int HANDLER_MSG_UPLOAD_PHOTO = 0;

    private static final int HANDLER_MSG_UPLOAD_VIDEO = 1;

    // Test upload photo file's local file
    private static final String testLocalPhotoFile = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "Movies/CarCameraApp/20150713" + "/123456.jpg";

    /**
     * 上传文件的Handler
     */
    private Handler testUploadFileHanlder = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_UPLOAD_PHOTO:
                    UploadFileToALY.upLoadPhoto2ALY(getApplicationContext(),
                            "1" /* m_sDeviceid */, testLocalPhotoFile);
                    break;

                case HANDLER_MSG_UPLOAD_VIDEO:
                    String sFilePath = Environment
                            .getExternalStorageDirectory()
                            + "/Movies/CarCameraApp/20150713"
                            + "/VID_154532_403.mp4";
                    UploadFileToALY.upLoadVideoToALY(getApplicationContext(),
                            m_sDeviceid, sFilePath);
                    break;

                default:
                    break;
            }
        };
    };

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    Handler m_handle = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HANDLE_TOAST_CMD:
                    Toast.makeText(YrcCarnetServer.this, "出错了！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_WEB_CMD:
                    retWebCmd(msg);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 向网站请求数据
     * 
     * @param msg命令消息
     */
    private void retWebCmd(Message msg) {
        Bundle bundle = msg.getData();
        if (bundle == null)
            return;

        int nCmd = bundle.getInt("cmd");// 命令
        String sData = bundle.getString("data");// 数据(暂未用到)

        if (nCmd == Define.UP_WEB_API.API_ACTIVATE) {
            asyncPostActivate();

        } else if (nCmd == Define.UP_WEB_API.API_UPLOAD_PHOTO) {// 照片信息，
            UploadFileToALY.upLoadPhoto2ALY(getApplicationContext(),
                    m_sDeviceid, testLocalPhotoFile);

        } else if (nCmd == Define.UP_WEB_API.API_UPLOAD_VIDEO) {// 视频信息
            String sFilePath = Environment.getExternalStorageDirectory()
                    + "/Movies/CarCameraApp/20150713" + "/VID_154532_403.mp4";
            UploadFileToALY.upLoadVideoToALY(getApplicationContext(),
                    m_sDeviceid, sFilePath);

        } else if (nCmd == Define.UP_WEB_API.API_UPLOAD_LOCATION) {// 位置信息
            LOG.writeMsg(this, LOG.MODE_MAIN_SERVER,
                    "receive infomation of getting location, start upload to ALY server");
            asyncPostLocation();
        }
    }

    /**
     * 根据Web命令编号发送http命令
     * 
     * @param nCmd
     * @param sData
     * @return
     */
    private boolean sendHttpCmdbyWebCmd(int nCmd, String sData) {
        Message msg = new Message();
        msg.what = HANDLE_WEB_CMD;
        Bundle bundle = new Bundle();
        bundle.putInt("cmd", nCmd);
        bundle.putString("data", sData);
        msg.setData(bundle);
        m_handle.sendMessage(msg);
        return true;
    }

    /**
     * 发送Toast的消息
     * 
     * @param sMess
     */
    private void showThreadToast(String sMess) {
        Message msg = new Message();
        msg.what = HANDLE_TOAST_CMD;
        Bundle bundle = new Bundle();
        bundle.putString("data", sMess);
        msg.setData(bundle);
        m_handle.sendMessage(msg);
    }

    /**
     * 收到后台数据后接收到广播消息
     */
    public BroadcastReceiver ServCmdReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;

            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(Define.ACTION_XG_REGSTATE)) { // 信鸽注册状态
                bXGRegistSuc = intent.getBooleanExtra("data", false);
                if (bXGRegistSuc) {// 信鸽注册成功后初始化一下
                    getXGTokenData();
                }
            }

            if (action.equals(Define.ACTION_ACTIVATE_UPLOAD)) { // 激活
                if (!bServRuning || !bXGRegistSuc) {
                    Log.e(Define.TAG, !bServRuning ? "yrc服务未运行" : "信鸽未注册成功");
                    return;
                }
                Log.e(Define.TAG, "激活操作");
                sendHttpCmdbyWebCmd(Define.UP_WEB_API.API_ACTIVATE, null);

            } else if (action.equals(Define.ACTION_GPS_UPLOAD)) {// 上传经纬度
                if (!bServRuning || !bXGRegistSuc) {
                    Log.e(Define.TAG, !bServRuning ? "yrc服务未运行" : "信鸽未注册成功");
                    return;
                }
                sendHttpCmdbyWebCmd(Define.UP_WEB_API.API_UPLOAD_LOCATION, null);

            } else if (action.equals(Define.ACTION_PHOTO_UPLOAD)) {// 上传图片
                if (!bServRuning || !bXGRegistSuc) {
                    Log.e(Define.TAG, !bServRuning ? "yrc服务未运行" : "信鸽未注册成功");
                    return;
                }
                sendHttpCmdbyWebCmd(Define.UP_WEB_API.API_UPLOAD_PHOTO, null);

            } else if (action.equals(Define.ACTION_VIDEO_UPLOAD)) {// 上传视频
                if (!bServRuning || !bXGRegistSuc) {
                    Log.e(Define.TAG, !bServRuning ? "yrc服务未运行" : "信鸽未注册成功");
                    return;
                }
                sendHttpCmdbyWebCmd(Define.UP_WEB_API.API_UPLOAD_VIDEO, null);

            } else if (action.equals(Define.ACTION_UPLOADFILE_RESULT)) {
                // 上传图片和视频成功之后，通知公司的服务器
                Bundle bundle = intent.getExtras(); // 获取intent里面的bundle对象
                String spath = bundle.getString("photo"); // 获取Bundle里面的字符串

                Log.e(Define.TAG, "上传成功，准备发送：");

                notificationServer(spath);

            } else if (action.equals(Define.ACTION_SHOW_DIALOG)) {
                showThreadToast("收到显示提示框消息");

            } else if (Intent.ACTION_SHUTDOWN.equals(action)) {// 关机广播，关机前将流量值存到一个缓冲区buffer中
                saveTrafficBeforePowerOff();
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (manager == null) {
                    Log.d(Define.TAG, "没有可用网络");

                } else {
                    NetworkInfo[] info = manager.getAllNetworkInfo();
                    if (info != null) {
                        for (int i = 0; i < info.length; i++) {
                            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                                String name = info[i].getTypeName();
                                Log.d(Define.TAG, "当前网络名称：" + name);
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 网络连接状态
     * 
     * @return 是否连接
     */
    public boolean NetConnectState() {
        return isNetworkAvailable(this);
    }

    public boolean isNetworkAvailable(Context context) {
        /** 获取网络系统服务 **/
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;

        } else {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** 移动网络连接状态，获取移动数据流量的判断条件 **/
    public boolean isMobileNetWorkConnected() {
        ConnectivityManager conntManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        State state = conntManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        if (State.CONNECTED == state) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机号
     * 
     * @return 手机号
     */
    private String getMobile() {
        String sMobile = UtilTools.getLocalTelPhoneNumber(this);
        return sMobile;
    }

    /**
     * 监听用户表数据发生变化
     * 
     * @author Administrator
     */
    public class UserInfoObserver extends ContentObserver {

        public UserInfoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // 只查询一条，程序后续可拓展多用户
            String[] stringArray = new String[] {
                    Provider.UserInfo.ACTIVATE, Provider.UserInfo.DEVICEID,
                    Provider.UserInfo.IMEI, Provider.UserInfo.MOBILE,
                    Provider.UserInfo.DVERSION, Provider.UserInfo.SVERSION,
                    Provider.UserInfo.DTOKEN
            };

            String[] selectionArgs = new String[] {
                "1"
            };

            Cursor c = getContentResolver().query(
                    Provider.UserInfo.CONTENT_URI, stringArray,
                    Provider.UserInfo._ID + "=?", selectionArgs, null);

            if (c != null && c.moveToFirst()) {

                m_sActivate = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.ACTIVATE));
                m_sDeviceid = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DEVICEID));
                m_sIMEI = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.IMEI));
                m_sMobile = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.MOBILE));
                m_sDversion = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DVERSION));
                m_sAPPver = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.SVERSION));
                m_sDToken = c.getString(c
                        .getColumnIndexOrThrow(Provider.UserInfo.DTOKEN));

                Log.e(Define.TAG, "用户表：m_sActivate:" + m_sActivate
                        + "m_sDeviceid:" + m_sDeviceid + "m_sIMEI:" + m_sIMEI
                        + "m_sMobile:" + m_sMobile + "m_sDversion:"
                        + m_sDversion + "m_sAPPver:" + m_sAPPver + "m_sDToken:"
                        + m_sDToken);

            } else {
                Log.e(Define.TAG, "query failure!");
            }

            if (c != null) {
                c.close();
            }
            Log.e(Define.TAG, "有数据发送变化");
        }
    }

    /**
     * 设备激活
     */
    private synchronized void asyncPostActivate() {
        // 请求的地址
        String url = String.format("%s%s", Define.WEB_IP_HTTP_URL,
                Define.API_FUNC_CAR_TO_SERVER_ACTIVATE);

        // 创建请求参数的封装的对象
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("token", GetTokenUtils.getToken());
        params.put("imei", m_sIMEI);
        params.put("mobile", m_sMobile);
        params.put("dversion", m_sDversion);
        params.put("sversion", m_sAPPver);
        params.put("dtoken", m_sDToken);

        // 执行post方法
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    byte[] responseBody) {
                String sResult = new String(responseBody);
                Log.e(Define.TAG, "---send sucs,statusCode=" + statusCode
                        + "sResult:" + sResult);
                SetPostResult(sResult, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    byte[] responseBody, Throwable error) {
                Log.e(Define.TAG, "---send fail,statusCode=" + statusCode);
                error.printStackTrace();

            }
        });

    }

    /**
     * 上传轨迹
     */
    private synchronized void asyncPostLocation() {
        UploadFileToALY
                .uploadDrivingTrace(getApplicationContext(), m_sDeviceid);
    }

    /**
     * 通知服务器
     */
    private void notificationServer(String ossFILEpath) {
        // 请求的地址
        String url = String.format("%s%s", Define.WEB_IP_HTTP_URL,
                Define.API_FUNC_CAR_TO_SERVER_PHOTO);

        // 创建请求参数的封装的对象
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("token", GetTokenUtils.getToken());
        params.put("imei", m_sIMEI);
        params.put("fileurl", ossFILEpath);
        params.put("longitude", reportGpsTrace.getmLongitude());
        params.put("latitude", reportGpsTrace.getmLatitude());

        // 执行post方法
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    byte[] responseBody) {
                String sResult = new String(responseBody);
                Log.e(Define.TAG, "---send sucs,statusCode=" + statusCode
                        + "sResult:" + sResult);
                SetPostResult(sResult, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    byte[] responseBody, Throwable error) {
                Log.e(Define.TAG, "---send fail,statusCode=" + statusCode);
                error.printStackTrace();

            }
        });

    }

    /**
     * 上传视频
     */
    private synchronized void AsyncPostVideo() {

    }

    /**
     * 设置post结果
     * 
     * @param sAnalyData 网站返回的JSON串
     * @param bSave 是否保存数据
     * @return 保存成功或失败
     */
    private boolean SetPostResult(String sAnalyData, boolean bSave) {
        if (sAnalyData == null || sAnalyData.length() == 0) {
            return false;
        }
        JSONObject userJson = null;
        try {
            userJson = new JSONObject(sAnalyData);

            if (hasMustParam(userJson) == false) {
                sRetDesc = "请求到的数据缺少必填项";
                nRetCode = -1;
                return false;
            }
            if (userJson.has("msg")) {
                sRetDesc = userJson.getString("msg");
            } else {
                sRetDesc = "";
            }

            if (userJson.has("status")) {
                nRetCode = userJson.getInt("status");
            } else {
                nRetCode = -1;
            }

            Log.e(Define.TAG, "step1:nRetCode:" + nRetCode + " sRetDesc:"
                    + sRetDesc);

            if (bSave) {// 只有激活时bSave才等于true
                Log.e(Define.TAG, "step2:nRetCode:" + nRetCode + " sRetDesc:"
                        + sRetDesc);

                if (userJson.has("device_id")) {// device_id在注册时才返回
                    m_sDeviceid = userJson.getString("device_id");

                } else {
                    Log.e(Define.TAG, "注册失败，返回的device_id不存在");

                    return false;
                }

                ContentValues values = new ContentValues();
                values.clear();

                values.put("activate", nRetCode);
                values.put("deviceid", m_sDeviceid);
                values.put("imei", m_sIMEI);
                values.put("mobile", m_sMobile);
                values.put("dversion", m_sDversion);
                values.put("sversion", m_sAPPver);
                values.put("dtoken", m_sDToken);

                int ret = getContentResolver().update(
                        Provider.UserInfo.CONTENT_URI, values, "_id=1", null);
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /***
     * 返回必填项是否存在
     * 
     * @param json
     * @return 返回值是否正确
     */
    private boolean hasMustParam(JSONObject json) {
        if (json == null) {
            return false;
        }

        if (json.has("status") == false // 状态
                || json.has("msg") == false) {// 状态代码描述
            return false;
        }
        return true;
    }

    /**
     * 线程休眠
     * 
     * @param time
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取IMEI
     * 
     * @return
     */

	private String getIMIE() {
		String simei = UtilTools.getDevicesID(getApplicationContext());
		m_sIMEI = (simei == null) ? "415555555555" : simei;
		return m_sIMEI;
	}

    /**
     * 获取软件版本
     * 
     * @return 软件版本
     */
    private String getAPPVer() {
        String sAPPver = "1.0.1";
        return sAPPver;
    }

    /**
     * 获取硬件版本
     * 
     * @return 获取硬件版本号
     */
    private String getDeviceVer() {
        String sDversion = "1.0.1";
        return sDversion;
    }

    /**
     * 更新流量，每3秒获取一次。
     */
    private void getTraffic() {
        long up = TrafficStats.getMobileRxBytes();
        long load = TrafficStats.getMobileTxBytes();
        long currentTraffic = up + load;
        mTrafficSP.edit().putLong(TRAFFIC_CURRENT, currentTraffic).commit();
    }

    /**
     * 关机前保存流量到一个缓冲区
     */
    private void saveTrafficBeforePowerOff() {
        if (null != mTrafficSP) {
            long offBefore = mTrafficSP.getLong(TRAFFIC_VALUE, 0);
            mTrafficSP.edit().putLong(TRAFFIC_BUFFER_POWER_OFF, offBefore).commit();
            mTrafficSP.edit().putLong(TRAFFIC_BUFFER_CLEAN_BEFORE, 0).commit();
            mTrafficSP.edit().putBoolean(IS_CLEANED, false).commit();
            mTrafficSP.edit().putBoolean(IS_POWER_OFF, true).commit();
        }
    }
}
