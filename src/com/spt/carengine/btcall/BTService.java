
package com.spt.carengine.btcall;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.spt.carengine.MyApplication;
import com.spt.carengine.R;
import com.spt.carengine.btcall.ICountService;
import com.spt.carengine.db.Provider;
import com.spt.carengine.define.BTApi;
import com.spt.carengine.define.BTCmd;
import com.spt.carengine.define.Define;
import com.spt.carengine.port.SerialPort;
import com.tencent.android.tpush.service.channel.exception.SecurityException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class BTService extends Service implements ICountService {

    private final int CMD_OPEN_SERIAL = -1112;// 打开串口
    private final int CMD_BT_INIT = -1113;// 初始化

    static final String TAG = BTService.class.getSimpleName();

    private Thread m_send_thread = null;// 发送线程
    private Thread m_recv_thread = null;// 接收线程
    private Thread m_pbap_thread = null;// 电话本线程

    private boolean m_bPortOpen = false;// 串口是否打开
    private boolean m_bPBDownload = false;// 电话本下载
    private boolean m_bMicMute = false;// 静音
	private boolean m_bHF = false;// 免提
	private boolean m_bSetNameSucc = false;// 设置名称

    private boolean m_bSaveCallLog = false;// 保存通话记录

    // 0未初始化 1准备中 2正在连接 3活动新号 4已连接5语音流
    private int m_nA2dpState = -1;
    // 0未初始化1准备中2正在连接3连接
    private int m_nAvrcpState = -1;

    /******** m_nbtState 0未初始化，1 准备中，2正在连接，3已连接，4拨号中，5来电，6通话中 **************/
    private int m_nbtState = 0;
    private int m_nLastBTState = -1;// 上一次蓝牙状态

    private String m_sTelNumber = "";// 电话号码
    private int m_sTalkType = CallLog.Calls.OUTGOING_TYPE;// 通话类型

	private String m_sDeviceName = "CarDev1";// DeviceName
	// private String m_sPinCode = "0000";// 配对代码

    private int m_nDeviceID = -1;// 配对列表中设备ID
    private String m_sBdAddr = "";// 配对列表中蓝牙地址
    private String m_sDevName = "";// 配对列表中设备名
    private String m_sConnDevName = "";// 当前连接的手机名称

    private int m_nA2dpVol = 0x0f;// A2DP音量

    static final String DT_FMT = "yyyy-MM-dd HH:mm:ss"; // .SSS";
    static SimpleDateFormat sdf = new SimpleDateFormat(DT_FMT);
	private List<ContactInfo> listContacts = new ArrayList<ContactInfo>();

    protected MyApplication mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;

    private PipedOutputStream PipedOut = new PipedOutputStream();
    private PipedInputStream PipedIn = new PipedInputStream();

    Handler mainHandler;
    Handler workHandler;
    HandlerThread htWork;
    Messenger msger;
    Runnable runOpenSerial;

    private ServiceBinder serviceBinder = new ServiceBinder();

    public class ServiceBinder extends Binder implements ICountService {

        @Override
        public int getBTState() {

            return m_nbtState;
        }

    }

    private Messenger ensureMessenger() {
        if (msger == null) {
            if (mainHandler == null) {
                mainHandler = new Handler() {
                    @Override
                    public void dispatchMessage(Message msg) {
                        super.dispatchMessage(msg);
                        handleMyMessage(msg);
                    }
                };
            }
            msger = new Messenger(mainHandler);
        }
        return msger;
    }

    @Override
    public IBinder onBind(Intent intent) {
        println("-onBind-" + this);
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        println("-onCreate-BT--------" + this);
        super.onCreate();
        ensureMessenger();

        mApplication = (MyApplication) getApplication();
        htWork = new HandlerThread("work");
        htWork.start();
        RegBroadRecevier(true); // 注册广播
        workHandler = new Handler(htWork.getLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                handleMyMessage(msg);
            }
        };

		getContentResolver().delete(
				Uri.parse(ContactsContract.RawContacts.CONTENT_URI.toString()
						+ "?" + ContactsContract.CALLER_IS_SYNCADAPTER
						+ " = true"),
				ContactsContract.RawContacts._ID + " > 0", null);

		Date dt = new Date();

        try {
            // 连接两个管道流。
            PipedIn.connect(PipedOut);
        } catch (IOException e) {
            System.err.println("管道连接失败");
            e.printStackTrace();
        }

        m_send_thread = new Thread(new Runnable() {

            long nLastSendOpenCmd = System.currentTimeMillis();// 最后一次发送时间
            long nLastSearchCmd = nLastSendOpenCmd;// 上一次搜索时间
            long nLastQueryBtState = nLastSendOpenCmd;// 上一次查询蓝牙状态时间

            @Override
            public void run() {
                while (true) {
                    // 串口未打开
                    if (m_bPortOpen == false
                            && System.currentTimeMillis() - nLastSendOpenCmd >= 2000) {
                        nLastSendOpenCmd = System.currentTimeMillis();
                        Sleep(200);
                        sendOpenSerialPort();// 打开串口
                    }

                    // 查询设备状态
                    if (m_bPortOpen == true
                            && m_bPBDownload == false
                            && System.currentTimeMillis() - nLastQueryBtState >= 700) {
                        nLastQueryBtState = System.currentTimeMillis();
                        Sleep(100);
                        workHandler
                                .sendEmptyMessage(BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE);
                    }

//					if (m_bPortOpen == true && m_bSetNameSucc == false) {
//						nLastSendOpenCmd = System.currentTimeMillis();
//						Sleep(300);
//						RequestDatas(BTCmd.BT_MANAGE.REQ_SET_LOCAL_DEV_NAME,
//								m_sDeviceName);
//						workHandler
//								.sendEmptyMessage(BTCmd.BT_PAIRING.REQ_SET_PIN_CODE);
//
//						workHandler
//								.sendEmptyMessage(BTCmd.BT_PAIRING.REQ_SET_PAIRING_CONTROL);
//
//					}
					//
					// 未连接时15秒搜索附近设备,找到配对的设备后会自动连接
					if (m_bPortOpen == true
							&& m_nbtState == 1
							&& System.currentTimeMillis() - nLastSearchCmd >= 100000) {
						nLastSearchCmd = System.currentTimeMillis();
						Sleep(500);
						workHandler
								.sendEmptyMessage(BTCmd.BT_SEARCH.REQ_INQUIRY);
					}

                    sendBdcast(BTApi.BT_STATE, null);// 蓝牙状态

                    Sleep(200);
                }
            }

        });

        m_recv_thread = new Thread(new Runnable() {

            @Override
            public void run() {

                // 定义一个包的最大长度
                int maxLength = 2048;
                byte[] buffer = new byte[maxLength];
                // 每次收到实际长度
                int available = 0;
                // 当前已经收到包的总长度
                int currentLength = 0;
                /**
                 * 协议格式 Message type Parameter Length Parameters 1 byte 1 byte N
                 */
                // 协议头长度2个字节（消息类型1字节，内容长度1字节）
                int headerLength = 2;

                while (true) {

                    if (m_bPortOpen == false) {// 串口未打开
                        Sleep(200);
                        continue;
                    }

                    try {
                        available = mInputStream.available();
                        if (available > 0) {
                            // 防止超出数组最大长度导致溢出
                            if (available > maxLength - currentLength) {
                                available = maxLength - currentLength;
                            }
                            mInputStream.read(buffer, currentLength, available);
                            currentLength += available;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int cursor = 0;

                    // 如果当前收到包大于头的长度，则解析当前包
                    while (currentLength >= headerLength) {

                        // println("-收到数据:" + toHexString(buffer,
                        // currentLength));

                        // 取到头部第一个字节,协议头

                        int nCmd = buffer[cursor] & 0xff;

                        if (nCmd != BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_INFO
                                && nCmd != BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE
                                && nCmd != BTCmd.BT_MANAGE.REQ_SET_LOCAL_DEV_NAME
                                && nCmd != BTCmd.BT_MANAGE.REQ_SET_SCAN_MODE
                                && nCmd != BTCmd.BT_CALL.REQ_DIAL_NUM
                                && nCmd != BTCmd.BT_CALL.REQ_TERMINATE
                                && nCmd != BTCmd.BT_CALL.REQ_ANSWER
                                && nCmd != BTCmd.BT_CALL.REQ_REDIAL
                                && nCmd != BTCmd.BT_CALL.REQ_VOICE_DIAL
                                && nCmd != BTCmd.BT_CALL.REQ_AUDIO_TRANSFER
                                && nCmd != BTCmd.BT_CALL.REQ_SET_HFP_VOL
                                && nCmd != BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL
                                && nCmd != BTCmd.BT_CALL.REQ_GET_CURRENT_CALLS
                                && nCmd != BTCmd.BT_CALL.REQ_GET_OPERATOR
                                && nCmd != BTCmd.BT_CALL.REQ_MIC_MUTE_TOGGLE
                                && nCmd != BTCmd.BT_A2DP.REQ_SET_A2DP_VOL
                                && nCmd != BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD
                                && nCmd != BTCmd.IND_PHONEBOOK.IND_PBAPC_START
                                && nCmd != BTCmd.IND_PHONEBOOK.IND_PB_SESSION_STATUS
                                && nCmd != BTCmd.IND_PHONEBOOK.IND_PBAPC_DATA
                                && nCmd != BTCmd.IND_PHONEBOOK.IND_PBAPC_COMPLETE
                                && nCmd != BTCmd.IND_A2DP.IND_A2DP_SINK_STATE
                                && nCmd != BTCmd.BT_CONNECTION.IND_NEW_CONNECTION
                                && nCmd != BTCmd.REV_AVRCP.IND_AVRCP_CT_STATE
                                && nCmd != BTCmd.IND_HFP.IND_HFP_STATE
                                && nCmd != BTCmd.REV_AVRCP.IND_AG_BRSF
                                && nCmd != BTCmd.REV_OTHER.IND_HFP_SERVICE
                                && nCmd != BTCmd.REV_OTHER.IND_HFP_SIGNAL
                                && nCmd != BTCmd.REV_OTHER.IND_HFP_ROAM
                                && nCmd != BTCmd.REV_OTHER.IND_HFP_BATTCHG
                                && nCmd != BTCmd.REV_OTHER.IND_HFP_EXT_MSG
                                && nCmd != BTCmd.IND_A2DP.IND_CODEC_STATE
                                && nCmd != BTCmd.IND_A2DP.IND_A2DP_STREAM_STATE
                                && nCmd != BTCmd.BT_AVRCP.REQ_AVRCP_CMD
                                && nCmd != BTCmd.IND_NETWORK.IND_CURRENT_CALLS
                                && nCmd != BTCmd.REV_OTHER.IND_SCO_STATE
                                && nCmd != BTCmd.BT_PAIRING.REQ_SET_PAIRING_CONTROL
                                && nCmd != BTCmd.BT_PAIRING.REQ_SET_PIN_CODE
                                && nCmd != BTCmd.BT_PAIRING.REQ_PAIR_REPLY
                                && nCmd != BTCmd.BT_PAIRING.REQ_PAIR_REMOTE_DEVICE
                                && nCmd != BTCmd.BT_PAIRING.REQ_GET_PAIRED_DEVICE
                                && nCmd != BTCmd.BT_PAIRING.REQ_DELETE_PAIRED_DEVICE
                                && nCmd != BTCmd.BT_PAIRING.REQ_SSP_CONFIRMATION
                                && nCmd != BTCmd.BT_CONNECT.REQ_ESTABLISH_SLC
                                && nCmd != BTCmd.BT_CONNECT.REQ_DISCONNECintLC
                                && nCmd != BTCmd.BT_CONNECT.REQ_GET_LINK_QUALITY
                                && nCmd != BTCmd.BT_CALL.REQ_REJECT
                                && nCmd != BTCmd.IND_NETWORK.IND_NETWORK_OPERATOR
                                && nCmd != BTCmd.BT_CONNECTION.IND_DISCONNECT_EVENT
                                && nCmd != BTCmd.IND_HFP.IND_CALLER_ID
                                && nCmd != BTCmd.IND_HFP.IND_CALL_WAITING
                                && nCmd != BTCmd.BT_SEARCH.REQ_INQUIRY
                                && nCmd != BTCmd.BT_MISC.REQ_MISC_OPERATIONS
                                && nCmd != BTCmd.IND_INIT.IND_INIT_OK
                                && nCmd != BTCmd.IND_INIT.IND_PROFILES_INIT_OK
								&& nCmd != BTCmd.IND_PAIRING.IND_PAIRING_REQUEST
								&& nCmd != BTCmd.IND_PAIRING.IND_PAIRING_STATUS) {
							// println("-----nCmd" + nCmd + " cursor:" +
							// cursor);
							--currentLength;// 数据包长度-1
							++cursor;// 查找索引不断后移
							if (cursor >= maxLength) {// 遍历到最后一个数据都没找到跳出
								break;
							}
							continue;
						}

                        int contentLenght = parseLen(buffer, cursor);// 内容长度

                        // println("-----包长度" + contentLenght);
                        // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
                        if (contentLenght < 0
                                || contentLenght > maxLength - headerLength) {
                            currentLength = 0;
                            break;
                        }

                        // 如果当前buffer数据长度小于整个包的长度，则跳出循环等待继续接收数据
                        int factPackLen = contentLenght + headerLength;// 包头+内容=包长度
                        if (currentLength < factPackLen) {
                            break;
                        }

                        // 一个完整包即产生
                        onDataReceived(buffer, cursor, factPackLen);
                        currentLength -= factPackLen;
                        cursor += factPackLen;

                        // 残留字节移到缓冲区首
                        if (currentLength > 0 && cursor > 0) {

                            System.arraycopy(buffer, cursor, buffer, 0,
                                    currentLength);
                            cursor = 0;
                            // println("-缓冲区数据:"
                            // + toHexString(buffer, currentLength));
                        }

                    }

					Sleep(100);
				}
			}

        });

        m_pbap_thread = new Thread(new Runnable() {

            @Override
            public void run() {

                // 定义一个包的最大长度
                int maxLength = 2048;
                byte[] buffer = new byte[maxLength];
                // 每次收到实际长度
                int available = 0;
                // 当前已经收到包的总长度
                int currentLength = 0;

                // 协议头长度2个字节（消息类型1字节，内容长度1字节）
                int headerLength = 2;

                boolean bcontact = false;// 联系人

                while (true) {

					if (m_bPortOpen == false) {// 串口未打开
						Sleep(100);
						continue;
					}

                    try {
                        available = PipedIn.available();
                        if (available > 0) {
                            // 防止超出数组最大长度导致溢出
                            if (available > maxLength - currentLength) {
                                available = maxLength - currentLength;
                            }
                            // println("-----m_pbap_thread  读取管道数据长度" +
                            // available
                            // + " 残余数据长度" + currentLength);
                            PipedIn.read(buffer, currentLength, available);
                            currentLength += available;
                        }
                        // println("-----m_pbap_thread  currentLength"
                        // + currentLength);

                    } catch (Exception e) {

                        println("-----m_pbap_thread  读取失败");
                        e.printStackTrace();
                    }

                    int cursor = 0;

                    int cursorend = 0;// 电话本命令尾

                    // 如果当前收到包大于头的长度，则解析当前包
                    while (currentLength >= headerLength) {

                        // println("-读联系人:" + toHexString(buffer,
                        // currentLength));

                        if (bcontact != true) {
                            if (buffer[cursor] != 0x42
                                    && buffer[cursor + 1] != 0x45) {// 电话本数据头

                                println("--联系人 cursor:" + cursor);
                                --currentLength;// 数据包长度-1
                                ++cursor;// 查找索引不断后移
                                if (cursor + 1 > maxLength) {// 遍历到最后一个数据都没找到跳出
                                    break;
                                }
                                continue;
                            }
                        }

                        // 联系人协议
                        if (buffer[cursor] == 0X42
                                && buffer[cursor + 1] == 0X45) {
                            bcontact = true;// 找到了联系人包头
                            cursorend = cursor;// 寻找包尾
                            println("-----联系人开始位置" + cursor);
                        }

                        if (bcontact == true) {// 联系人数据包

                            boolean bfind = false;

                            for (int i = 0; i < currentLength; i++) {
                                if (i + 8 >= currentLength) {
                                    bfind = false;
                                    break;// 说明还没有找到包尾
                                }

                                // 45 4e 44 3a 56 43 41 52 44
                                if (buffer[cursorend] == 0x45
                                        && buffer[cursorend + 1] == 0x4E
                                        && buffer[cursorend + 2] == 0x44
                                        && buffer[cursorend + 3] == 0x3A
                                        && buffer[cursorend + 4] == 0x56
                                        && buffer[cursorend + 5] == 0x43
                                        && buffer[cursorend + 6] == 0x41
                                        && buffer[cursorend + 7] == 0x52
                                        && buffer[cursorend + 8] == 0x44) {// 找到了尾部END:VCARD
                                    println("-----找到联系人尾" + cursorend);
                                    bfind = true;
                                    break;
                                } else {
                                    cursorend++;
                                }
                            }

                            if (bfind == false) {// 一包数据解完还未找到包尾

                                println("----没找到包尾");
                                break;
                            }

                        }

                        if (bcontact == true && buffer[cursorend] == 0x45
                                && buffer[cursorend + 1] == 0x4E
                                && buffer[cursorend + 2] == 0x44
                                && buffer[cursorend + 3] == 0x3A
                                && buffer[cursorend + 4] == 0x56
                                && buffer[cursorend + 5] == 0x43
                                && buffer[cursorend + 6] == 0x41
                                && buffer[cursorend + 7] == 0x52
                                && buffer[cursorend + 8] == 0x44
                                && buffer[cursorend + 9] == 0x0D
                                && buffer[cursorend + 10] == 0x0A) {
                            println("-----联系人,结束位置" + cursorend);
                            cursorend += 10;// 到0X0A
                        }

                        int factPackLen = cursorend - cursor + 1;// 整包长度

                        println("-----pbcursor" + cursorend + " factPackLen"
                                + factPackLen);

                        // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
                        if (factPackLen <= 0 || factPackLen > maxLength) {
                            currentLength = 0;
                            break;
                        }

                        // 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
                        if (currentLength < factPackLen) {
                            break;
                        }

                        // 一个完整包即产生
                        println("-----一个完整联系人包即产生");

                        try {
                            String contacts = new String(buffer, cursor,
                                    factPackLen, "UTF-8");

                            try {
                                AnalyContactsData(contacts, contacts.length());
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        } catch (UnsupportedEncodingException e) {

                            println("-----一出错");
                            e.printStackTrace();
                        }

                        currentLength -= factPackLen;// 剩下的长度

                        cursorend += 1;// 下次查找包头开始位置
                        cursor = cursorend;

                        println("-----下一个包开始位置查找位置:" + cursor);
                        bcontact = false;

                        // 残留字节移到缓冲区首
                        if (currentLength > 0 && cursor > 0) {
                            println("-----残留字节移到缓冲区首:cursorend" + cursorend
                                    + " currentLength:" + currentLength);

                            System.arraycopy(buffer, cursor, buffer, 0,
                                    currentLength);
                            cursor = 0;
                            bcontact = false;

                            println("-缓冲数据包:"
                                    + toHexString(buffer, currentLength));
                        }

                    }

 				Sleep(50);
				}
			}

        });

        m_send_thread.start();
        m_recv_thread.start();
        m_pbap_thread.start();

        println(sdf.format(dt) + " 模块已运行。");

    }

    /**
     * 用于注册接收外部命令操作的广播
     * 
     * @param bStart
     */
    public void RegBroadRecevier(boolean bStart) {
        if (bStart) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BTApi.ACTION_YRC_BT);// BT
            registerReceiver(BTCmdReceiver, filter);
        } else {
            unregisterReceiver(BTCmdReceiver);
        }
    }

    /**
     * 接收外边广播
     */
    public BroadcastReceiver BTCmdReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null)
                return;

            String sAction = intent.getAction();
            if (sAction == null)
                return;

            if (sAction.equals(BTApi.ACTION_YRC_BT)) {
                int nCmd = intent.getIntExtra("cmd", -1);
                if (nCmd == -1)
                    return;

                if (BTApi.BT_CMD_DIAL == nCmd) {// 打电话
                    if (m_nbtState != 3)// 蓝牙不是空闲状态一律不拨打电话
                    {
                        return;
                    }

                    String sTelNumber = intent.getStringExtra("tel");
                    if (sTelNumber.length() < 2) // phone number error.
                    {
                        return;
                    }

                    m_sTelNumber = sTelNumber;
                    RequestDatas(BTCmd.BT_CALL.REQ_DIAL_NUM, m_sTelNumber);

                } else if (BTApi.BT_CMD_HANG == nCmd) {// 挂机

                    if (m_nbtState == 6 || m_nbtState == 5 || m_nbtState == 4) {
                        RequestDatan(m_nbtState == 5 ? BTCmd.BT_CALL.REQ_REJECT
                                : BTCmd.BT_CALL.REQ_TERMINATE, null);
                    }

                } else if (BTApi.BT_CMD_ANSWER == nCmd) { // 接听
                    if (m_nbtState != 5)// 不为来电一律返回
                        return;
                    RequestDatan(BTCmd.BT_CALL.REQ_ANSWER, null);

                } else if (BTApi.BT_CMD_KEYPADS == nCmd) { // 数字小键盘
                    if (m_nbtState != 6) // 通话中
                        return;

                    String sDtmfnum = intent.getStringExtra("number");
                    if (sDtmfnum.length() != 1) // number error.
                        return;

                    RequestDatas(BTCmd.BT_CALL.REQ_SEND_DTMF, sDtmfnum);

                } else if (BTApi.BT_CMD_REDIAL == nCmd) { // 重拨
                    if (m_nbtState != 3) // 蓝牙不是空闲状态一律不拨打电话
                        return;
                    RequestDatan(BTCmd.BT_CALL.REQ_REDIAL, null);
                } else if (BTApi.BT_CMD_TRANSFER == nCmd) { // 通话中蓝牙通话和手机通话切换
                    // m_bHF = !m_bHF;
                    byte[] transferAG = {
                        0x00
                    };// 手机
                    byte[] transferHF = {
                        0x01
                    };// 蓝牙
                    RequestDatan(BTCmd.BT_CALL.REQ_AUDIO_TRANSFER,
                            m_bHF ? transferHF : transferAG);

                } else if (BTApi.BT_CMD_PLAY == nCmd) { // 蓝牙播放音乐
                    byte[] avrcp_play = {
                        0x01
                    };
                    RequestDatan(BTCmd.BT_AVRCP.REQ_AVRCP_CMD, avrcp_play);
                } else if (BTApi.BT_CMD_PAUSE == nCmd) { // 蓝牙播放音乐暂停
                    byte[] avrcp_pause = {
                        0x02
                    };
                    RequestDatan(BTCmd.BT_AVRCP.REQ_AVRCP_CMD, avrcp_pause);

                } else if (BTApi.BT_CMD_FORWARD == nCmd) { // 蓝牙播放音乐下一曲
                    byte[] avrcp_forward = {
                        0x04
                    };
                    RequestDatan(BTCmd.BT_AVRCP.REQ_AVRCP_CMD, avrcp_forward);

                } else if (BTApi.BT_CMD_BACKWARD == nCmd) { // 蓝牙播放音乐上一曲
                    byte[] avrcp_backward = {
                        0x05
                    };
                    RequestDatan(BTCmd.BT_AVRCP.REQ_AVRCP_CMD, avrcp_backward);

                } else if (BTApi.BT_CMD_DEV_NAME == nCmd) { // 设置设备名
                    String sDevName = intent.getStringExtra("devname");
                    if (sDevName.isEmpty() || sDevName.length() > 32)
                        return;
                    RequestDatas(BTCmd.BT_MANAGE.REQ_SET_LOCAL_DEV_NAME,
                            sDevName);

                } else if (BTApi.BT_CMD_MIC_MUTE == nCmd) { // 蓝牙mic mute
                    if (m_nbtState != 6)
                        return;

                    m_bMicMute = false;

                    byte[] MicUnMute = {
                        0x01
                    };// unMute
                    byte[] MicMute = {
                        0x00
                    };// Mute
                    RequestDatan(BTCmd.BT_CALL.REQ_MIC_MUTE_TOGGLE,
                            m_bMicMute ? MicMute : MicUnMute);

                } else if (BTApi.BT_CMD_PHONENAME == nCmd) { // 取连接的设备名,并不是直接返回当前的设备名，通过解析其他指令
                    if (m_nbtState < 3)
                        return;

                    RequestDatan(BTCmd.BT_CALL.REQ_GET_OPERATOR, null);

                } else if (BTApi.BT_CMD_PAIRED_LIST == nCmd) { // 取配对列表
                    if (m_bPortOpen == false)
                        return;

                    RequestDatan(BTCmd.BT_PAIRING.REQ_GET_PAIRED_DEVICE, null);

				} else if (BTApi.BT_CMD_DOWNLOAD == nCmd) { // 下载电话本
					if (m_bPortOpen == false)
						return;
					workHandler
							.sendEmptyMessage(BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD);

				} else if (BTApi.BT_CMD_DELETE_PAIRED == nCmd) {
					if (m_bPortOpen == false)
						return;
					int nDeviceid = intent.getIntExtra("deviceid", -1);
					if (nDeviceid < 0 || nDeviceid > 255)
						return;
					byte[] id = { (byte) nDeviceid };
					RequestDatan(BTCmd.BT_PAIRING.REQ_DELETE_PAIRED_DEVICE, id);
				} else {
					println("未知外部命令");
				}

            }
        }

    };

    /**
     * 解析联系人并插入系统列表
     * 
     * @param contacts
     * @param length
     * @throws IOException
     */
	private synchronized void AnalyContactsData(String contacts, int length)
            throws IOException {

        if (contacts.isEmpty())
            return;

        String Name = "";
        String FName = "";
        String Tel = "";

        Scanner scanner = new Scanner(contacts);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("N")) {
                Name = line.substring(line.indexOf(":") + 1);
				if (line.indexOf("QUOTED-PRINTABLE") > 0) {
					Name = qpDecoding(FName);
				}
                println("Name:" + Name);
            } else if (line.startsWith("FN")) {
                FName = line.substring(line.indexOf(":") + 1);
				if (line.indexOf("QUOTED-PRINTABLE") > 0) {
					FName = qpDecoding(FName);
				}
                println("FName:" + FName);
            } else if (line.startsWith("TEL")) {
                Tel = line.substring(line.lastIndexOf(":") + 1);
                println("Tel:" + Tel);
            }
        }

        if (Name.isEmpty() && FName.isEmpty() && Tel.isEmpty())
            return;

		ContactInfo contact = new ContactInfo();
		contact.setName(FName.isEmpty() ? Name : FName);
		contact.setNumber(Tel);

		listContacts.add(contact);

		// ContentValues values = new ContentValues();
		//
		// // 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
		//
		// Uri rawContactUri = getApplicationContext().getContentResolver()
		// .insert(RawContacts.CONTENT_URI, values);
		//
		// long rawContactId = ContentUris.parseId(rawContactUri);
		//
		// // 往data表插入姓名数据
		//
		// values.clear();
		//
		// values.put(Data.RAW_CONTACT_ID, rawContactId);
		//
		// values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// 内容类型
		//
		// values.put(StructuredName.GIVEN_NAME, FName.isEmpty() ? Name :
		// FName);
		//
		// getApplicationContext().getContentResolver().insert(
		// ContactsContract.Data.CONTENT_URI, values);
		//
		// // 往data表插入电话数据
		//
		// values.clear();
		//
		// values.put(Data.RAW_CONTACT_ID, rawContactId);
		//
		// values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		//
		// values.put(Phone.NUMBER, Tel);
		//
		// values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		//
		// getApplicationContext().getContentResolver().insert(
		// ContactsContract.Data.CONTENT_URI, values);

    }

    /**
     * 获取协议内容长度
     * 
     * @param header
     * @return
     */
    public int parseLen(byte buffer[], int index) {
        byte a = buffer[index + 1];// 参数长度
        int rlt = a & 0xff;
        return rlt;
    }

    /**
     * 收到整包数据
     * 
     * @param buffer
     * @param index
     * @param packlen
     */
    protected void onDataReceived(final byte[] buffer, final int index,
            final int packlen) {
        // System.out.println("收到信息");
        byte[] buf = new byte[packlen];
        System.arraycopy(buffer, index, buf, 0, packlen);

        AnalyData(buf, packlen);
    }

    private byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);

        return bb.array();

    }

    /**
     * 解析数据
     * 
     * @param buf 数据
     * @param packlen 数据长度
     */
    private void AnalyData(final byte[] buf, int packlen) {

        if (buf == null || packlen < 2)
            return;
        println("-完整数据包:" + toHexString(buf, packlen));

        int nCmd = buf[0] & 0xff;
        // 数据格式：消息类型,参数长度,参数
        if (nCmd == BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_INFO) {// 蓝牙模块信息,通讯正常,初始化相关设置
            println("收到模块信息");
        } else if (nCmd == BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE) {// 蓝牙状态

            if (packlen != 15) {// 返回长度固定15位
                println("收到错误数据");
                return;
            }
            // HfpState,A2dpState,AvrcpState
            if (buf[9] < 0x01 || buf[10] < 0x01 || buf[11] < 0x01) {// 未初始化
                println("模块未初始化，正在初始化");
                // 打开后重启模块
                workHandler.sendEmptyMessage(CMD_BT_INIT);// 设置蓝牙未初始化
            }

            m_nbtState = buf[9] & 0xff;

            if ((m_nbtState == 4 || m_nbtState == 5) && m_nLastBTState == 3) {
                m_bSaveCallLog = true;
                println("保存");
            }
            
            if(m_nbtState == 6 && m_nLastBTState != 6){
				char ucmd[] = { 0x33, 0x01, 0x1f };
				try {
					mOutputStream.write(getBytes(ucmd));
					mOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	workHandler.sendEmptyMessage(BTCmd.BT_CALL.REQ_SET_HFP_VOL); 
            }
            
            if(m_nLastBTState != 3 && m_nbtState>=3){ 
            	workHandler.sendEmptyMessage(BTCmd.BT_A2DP.REQ_SET_A2DP_VOL);             	
            }

			if (m_nLastBTState < 3 && m_nbtState == 3) {
				println("请求下载电话本");
				workHandler.sendEmptyMessage(BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD);
			}

            
			m_nLastBTState = m_nbtState;
			if (m_nLastBTState < 3) {
				m_bPBDownload = false;
			}
            println("蓝牙状态:" + m_nbtState);

        } else if (nCmd == BTCmd.BT_MANAGE.REQ_SET_LOCAL_DEV_NAME) {// 设置蓝牙名称
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {// 名称设置成功
                m_sDeviceName = new String(buf, 3, packlen - 3);
                println("名称设置成功.名称为:" + m_sDeviceName);
				m_bSetNameSucc = true;
                workHandler.sendEmptyMessage(BTCmd.BT_MANAGE.REQ_REBOOT);
            } else {
				m_bSetNameSucc = false;
                println("名称设置失败");
            }
        } else if (nCmd == BTCmd.BT_MANAGE.REQ_SET_SCAN_MODE) {// 设置设备可见性
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {// 设置成功
                println("蓝牙可见设置成功");
            } else {
                println("蓝牙可见设置失败");
            }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_SET_PAIRING_CONTROL) {// 配对请求操作
			if (packlen < 4)
				return;
            if (buf[2] == 0x00) {// 配对操作成功
                if (buf[3] == 0x00) {// Reject pairing request
                    println("拒绝配对");
                } else if (buf[3] == 0x01) {// Accept pairing request
                    println("接收配对");
                } else if (buf[3] == 0x02) {// Forward pairing request to Host
                    println("正在配对");
                }
            }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_SET_PIN_CODE) {// 设置配对代码
			if (packlen < 4)
				return;
			// if (buf[2] == 0x00) {
			// m_sPinCode = new String(buf, 3, packlen - 3);
			println("设置配对代码成功.代码为:" + new String(buf, 3, packlen - 3));
			// } else {
			// println("配对代码太长");
			// }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_PAIR_REPLY) {// 远程设备的配对请求作出应答
            println("配对应答");
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_PAIR_REMOTE_DEVICE) {// 远程设备配对
            if (buf[2] == 0x00) {// 配对操作成功
                println("配对操作成功");
            }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_GET_PAIRED_DEVICE) {// 获取配对列表
            if (packlen < 11)
                return;

            if (buf[2] == 0x00) {// 获取成功

                int id = buf[3] & 0xff;
                String sbdaddr = "";
                for (int i = 0; i < 6; ++i) {
                    sbdaddr += String.format("%02X", buf[i + 4]);
                }

                int nSize = buf[10] & 0xff;
                String sdevname = "";
                try {
                    sdevname = new String(buf, 11, nSize, "UTF-16");
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

                addPairedList(Integer.toString(id), sbdaddr, sdevname);

                println("配对设备:" + "id:" + id + " bdaddr" + sbdaddr
                        + " devname:" + sdevname);
            }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_DELETE_PAIRED_DEVICE) {// 删除配对设备
			if (packlen < 4)
				return;
            if (buf[2] == 0x00) {
                int nDevID = buf[3] & 0xff;
                println("删除配对设备成功,设备ID为:" + nDevID);
            }
        } else if (nCmd == BTCmd.BT_PAIRING.REQ_SSP_CONFIRMATION) {// 远程设备的配对请求作出响应
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("远程设备的配对请求作出响应成功");
            }
        } else if (nCmd == BTCmd.BT_CONNECT.REQ_ESTABLISH_SLC) {// 建立与远程设备连接
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                int nDevID = buf[3] & 0xff;
                println("建立SLC,设备ID为:" + nDevID);
            }
        } else if (nCmd == BTCmd.BT_CONNECT.REQ_DISCONNECintLC) {// 根据参数“Option”断开连接
			if (packlen < 4)
				return;
            if (buf[2] == 0x00) {
                int nDevID = buf[3] & 0xff;
                println("断开SLC,设备ID为:" + nDevID);
            }
        } else if (nCmd == BTCmd.BT_CONNECT.REQ_GET_LINK_QUALITY) {// 连接查询
			if (packlen < 5)
				return;
            if (buf[2] == 0x00) {
                int nDevID = buf[4] & 0xff;
                println("当前连接的设备ID为:" + nDevID);
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_ANSWER) {// 来电应答
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("来电应答");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_TERMINATE) {// 挂断正在进行的通话
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("挂断成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_REJECT) {// 来电拒接
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("来电拒接成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_DIAL_NUM) {// 拨打电话
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("拨号操作成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_REDIAL) {// 重拨上一次拨出的电话
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("重拨操作成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_VOICE_DIAL) {// 语音拨号开关
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("语音拨号开关操作成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_AUDIO_TRANSFER) {// 手机和蓝牙通话语音切换
			if (packlen < 4)
				return;
            if (buf[2] == 0x00) {
                if (buf[3] == 0x00) {
                    println("切换到手机");
                } else {
                    println("切换到蓝牙");
                }
                println("手机和蓝牙通话语音切换" + buf[3]);
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_SET_HFP_VOL) {// 设置蓝牙说话音量
        	println("-蓝牙音量:" + toHexString(buf, packlen));
        } else if (nCmd == BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL) {// 通话中有来电操作(拒接)
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("通话中有来电操作(拒接)成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_GET_CURRENT_CALLS) {// 获取当前通话,手机不一定支持
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("获取当前通话成功");
            }
        } else if (nCmd == BTCmd.BT_CALL.REQ_GET_OPERATOR) {// 取连接的设备名(手机名)
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {
                println("取连接的设备名(手机名)成功");// 收到这条命令前会收到0x91这个命令
            }
        } else if (nCmd == BTCmd.IND_NETWORK.IND_NETWORK_OPERATOR) {// 当前连接的手机名称
			if (packlen < 4)
				return;
            int nLen = buf[3] & 0xff;
            if (nLen == packlen - 3)
                m_sConnDevName = new String(buf, 4, nLen);
            println("当前连接的手机名称:" + m_sConnDevName);
        } else if (nCmd == BTCmd.BT_CALL.REQ_GET_SUBSCRIBER_NUM) {// 取当前连蓝牙的手机号码(手机不一定支持)

        } else if (nCmd == BTCmd.BT_A2DP.REQ_SET_A2DP_VOL) {// 设置蓝牙音量
			if (packlen < 5)
				return;
            if (buf[2] == 0x00) {
                m_nA2dpVol = buf[3] & 0xff;
                int nDevID = buf[4] & 0xff;
                println("蓝牙音量:" + m_nA2dpVol + " 设备ID:" + nDevID);
            }
        } else if (nCmd == BTCmd.BT_AVRCP.REQ_AVRCP_CMD) {// 蓝牙音乐控制

        } else if (nCmd == BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD) {// 下载电话本
			if (packlen < 3)
				return;
            if (buf[2] == 0x00) {// 指令执行成功
                println("下载电话本操作成功");
            }
        } else if (nCmd == BTCmd.BT_CONNECTION.IND_NEW_CONNECTION) {// 新连接
			if (packlen < 3)
				return;
            String sConnType = "";
            if (buf[2] == 0x01) {
                sConnType = "HFP";
            } else if (buf[2] == 0x02) {
                sConnType = "A2DP (SNK)";
            } else if (buf[2] == 0x03) {
                sConnType = "AVRCP (CT)";
            } else if (buf[2] == 0x04) {
                sConnType = "SPP";
            } else if (buf[2] == 0x05) {
                sConnType = "A2DP (SRC)";
            } else if (buf[2] == 0x06) {
                sConnType = "AVRCP (TG)";
            }
            println("新连接,类型:" + sConnType);

        } else if (nCmd == BTCmd.BT_CONNECTION.IND_DISCONNECT_EVENT) {// 蓝牙连接断开
            // if (buf[2] == 0x01) {
			if (packlen < 4)
				return;

			m_nbtState = 0;

			sendBdcast(BTApi.BT_STATE, null);
			int nDevID = buf[3] & 0xff;
			println("断开连接的设备ID:" + nDevID);
        } else if (nCmd == BTCmd.BT_CONNECTION.IND_LINK_LOSS) {// 当连接断开连接时，此消息通知主机配置断开连接。主机决定重新连接设备或忽略
			if (packlen < 4)
				return;
            m_nbtState = 0;
            int nDevID = buf[3] & 0xff;
            println("1断开连接的设备ID:" + nDevID);
        } else if (nCmd == BTCmd.IND_HFP.IND_HFP_STATE) {// 蓝牙状态，主机操作结果，手机挂机会收到此消息

            if (packlen < 5)
                return;

            int nbtState = buf[2] & 0xff;// 蓝牙状态

            if (nbtState > 6) {// 多方通话挂掉其它电话
                workHandler
                        .sendEmptyMessage(BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL);// 通话中拒接来电
            }

        } else if (nCmd == BTCmd.IND_HFP.IND_CALLER_ID) {// 手机来电
			if (packlen < 4)
				return;
            int nDevID = buf[2] & 0xff;// 当前配对连接设备的设备ID
            int nLen = buf[3] & 0xff;
            String sNum = new String(buf, 4, nLen); // 来电号码
            m_sTelNumber = sNum;
            // println("来电号码:" + m_sTelNumber);

        } else if (nCmd == BTCmd.IND_HFP.IND_CALL_WAITING) {// 手机通话中还有来电
			if (packlen < 4)
				return;
            int nDevID = buf[2] & 0xff;// 当前配对连接设备的设备ID
            int nLen = buf[3] & 0xff;
            String sIncomingNum = new String(buf, 4, nLen); // 来电号码
            println("通话中的来电号码:" + sIncomingNum);

            workHandler.sendEmptyMessage(BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL);// 通话中拒接来电
        } else if (nCmd == BTCmd.IND_HFP.IND_VOICE_RECOG) {// 语音识别，不做，手机不一定支持
            println("语音识别");
        } else if (nCmd == BTCmd.IND_AVRCP.IND_AVRCP_PASSTHROUGH_CMD) {// 手机音乐播放操作，如上一曲，下一曲

        } else if (nCmd == BTCmd.IND_PHONEBOOK.IND_PB_SESSION_STATUS) {// 电话本下载状态
			if (packlen < 3)
				return;
            int nDown = buf[2] & 0xff;// 0下载完成，1开始下载，2下载暂停
            m_bPBDownload = (nDown == 1) ? true : false;
            sendBdcast(BTApi.BT_CALL_PBDOWN, m_bPBDownload == true ? "1" : "0");

            println(m_bPBDownload == true ? "开始下载" : "下载完成");

			if (!m_bPBDownload) {
				if (listContacts.isEmpty() == false) {
					try {
						Sleep(200);
						BatchAddContact(listContacts);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OperationApplicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
        } else if (nCmd == BTCmd.IND_PHONEBOOK.IND_PBAPC_START) {// 从手机上开始接收数据
			if (packlen < 3)
				return;
			if (buf[2] == 0x02) {
				// 从手机上下载
				m_bPBDownload = true;
			}
			println("-开始下载电话本");

			sendBdcast(BTApi.BT_CALL_PBDOWN, m_bPBDownload == true ? "1" : "0");// 开始下载电话本

        } else if (nCmd == BTCmd.IND_PHONEBOOK.IND_PBAPC_DATA) {// 这里面还要对联系人数据再解析一次才能得到联系人信息
            println("-联系人数据包:" + toHexString(buf, packlen));
            if (packlen <= 2)
                return;

            int nLen = buf[1] & 0xff;// 联系人数据实际长度
            byte[] buff = new byte[nLen];
            System.arraycopy(buf, 2, buff, 0, nLen);// 去掉包头，模块发过来的数据把联系人数据拆分了,要重新组装数据
            println("-联系人包:" + toHexString(buff, nLen));
            try {
                PipedOut.write(buff);
                PipedOut.flush();
            } catch (IOException e) {
                println("写入失败");
                e.printStackTrace();
            }

        } else if (nCmd == BTCmd.IND_PHONEBOOK.IND_PBAPC_COMPLETE) {
            m_bPBDownload = false;
            sendBdcast(BTApi.BT_CALL_PBDOWN, "0");// 开始下载电话本
            // println("-联系人数据接收完成");
        } else if (nCmd == BTCmd.IND_A2DP.IND_A2DP_SINK_STATE) {// 手机主动连接时收到
			if (packlen < 3)
				return;
			if (packlen <= 2)
				return;
			m_nA2dpState = buf[2] & 0xff;
			println("-A2DP连接状态:" + m_nA2dpState);

        } else if (nCmd == BTCmd.REV_AVRCP.IND_AVRCP_CT_STATE) {// 音频连接
            if (packlen <= 2)
                return;
            m_nAvrcpState = buf[2] & 0xff;
            println("-音频连接" + toHexString(buf, packlen));

        } else if (nCmd == BTCmd.REV_AVRCP.IND_AG_BRSF) {
            println("CMD:" + nCmd + " -数据包:" + toHexString(buf, packlen));
        } else if (nCmd == BTCmd.REV_OTHER.IND_HFP_SERVICE) {
            println("CMD:" + nCmd + " -数据包:" + toHexString(buf, packlen));
        } else if (nCmd == BTCmd.REV_OTHER.IND_HFP_SIGNAL) {
            int nSingnal = buf[2] & 0xff;
            println("信号强度:" + nSingnal);
        } else if (nCmd == BTCmd.IND_A2DP.IND_A2DP_STREAM_STATE) {// 音频播放有此消息,需要通知切声道
            if (packlen <= 2)
                return;
            int nA2dpStream = buf[2] & 0xff;// 0无声1有声
            println("a2dp流状态:" + nA2dpStream);
        } else if (nCmd == BTCmd.REV_OTHER.IND_SCO_STATE) {
            println("未知命令:" + nCmd);
        } else if (nCmd == BTCmd.BT_CALL.REQ_MIC_MUTE_TOGGLE) {// mute
            if (packlen < 4)
                return;

            if (buf[2] == 0x00) {// mute Status Successful
                if (buf[3] == 0x00) {
                    m_bMicMute = true;
                } else {
                    m_bMicMute = false;
                }
            }
            println("MUTE:" + nCmd);
        } else if (nCmd == BTCmd.IND_NETWORK.IND_CURRENT_CALLS) { // 有电话活动时会收到此消息
			if (packlen < 9)
				return;
            if (buf[4] == 0x00) {// Active
                println("接通");
            } else if (buf[4] == 0x01) {// Held
                println("Held");
            } else if (buf[4] == 0x02) {// Dialing (outgoing calls only)
                m_sTalkType = CallLog.Calls.OUTGOING_TYPE;
                println("拨号");
            } else if (buf[4] == 0x03) {// Alerting (outgoing calls only)
                println("对方响铃");
            } else if (buf[4] == 0x04) {// Incoming (incoming calls only)
                m_sTalkType = CallLog.Calls.INCOMING_TYPE;
                println("来电");
            } else if (buf[4] == 0x05) {// Waiting (incoming calls only)
                println("来电等待（来电中还有来电）");
            }

            int nLen = buf[8] & 0xff;// 电话号码长度

            m_sTelNumber = new String(buf, 9, nLen);

            // if (buf[5] == 0x00) {// voice
            //
            // }
            println("电话号码:" + m_sTelNumber);
            sendBdcast(BTApi.BT_CALL_NUM, m_sTelNumber);// 电话号码

            if (m_bSaveCallLog == true && !m_sTelNumber.isEmpty()) {// 插入通话记录
                m_bSaveCallLog = false;
                addTalkLog(m_sTelNumber, m_sTalkType);
            }

        } else if (nCmd == BTCmd.REV_OTHER.IND_HFP_BATTCHG) {// 电池等级，手机不一定支持
            int nLevel = buf[2] & 0xff;
            println(" 电池等级:" + nLevel);
        } else if (nCmd == BTCmd.BT_SEARCH.REQ_INQUIRY) {// 搜索附近设备
            if (packlen < 13)
                return;

            println("-搜索附近设备");

            byte[] addres = new byte[6];
            System.arraycopy(buf, 3, addres, 0, 6);
            analyNearbyData(addres);

        } else if (nCmd == BTCmd.BT_MISC.REQ_MISC_OPERATIONS) {// 服务初始化操作
            if (packlen < 4)
                return;
            if (buf[2] != 0x00) {// 未成功
                if (buf[3] == BTCmd.BT_MISC.REQ_SHOW_A2DP_SERVICE) {// A2DP 未成功
                    byte[] a2dp = {
                            BTCmd.BT_MISC.REQ_SHOW_A2DP_SERVICE, 0x01
                    };
                    RequestDatan(BTCmd.BT_MISC.REQ_MISC_OPERATIONS, a2dp);
                } else if (buf[3] == BTCmd.BT_MISC.REQ_SHOW_AVRCP_SERVICE) {// AVRCP
                                                                            // 未成功
                    byte[] avrcp = {
                            BTCmd.BT_MISC.REQ_SHOW_AVRCP_SERVICE, 0x01
                    };
                    RequestDatan(BTCmd.BT_MISC.REQ_MISC_OPERATIONS, avrcp);
                }
            }

        } else if (nCmd == BTCmd.IND_INIT.IND_INIT_OK) {// 模块初始化完成
			println("-模块初始化完成");
        } else if (nCmd == BTCmd.IND_INIT.IND_PROFILES_INIT_OK) {// 模块配置初始化完成
            println("-模块配置初始化完成");
        } else if(nCmd == BTCmd.IND_PAIRING.IND_PAIRING_REQUEST){//收到配对请求
			if (m_nbtState > 2)
				return;
			byte[] accept = { 0x01 };
			RequestDatan(BTCmd.BT_PAIRING.REQ_PAIR_REPLY, accept);

			println("-收到配对请求");
		} else if (nCmd == BTCmd.IND_PAIRING.IND_PAIRING_STATUS) {// 收到配对状态

		} else if (nCmd == BTCmd.IND_A2DP.IND_CODEC_STATE) {// 内部代码
			if (packlen < 3)
				return;
			int nstate = buf[2] & 0xff;
			println("-内部代码:" + nstate);
		} else {
			println("-未识别的数据包:" + toHexString(buf, packlen));
		}

    }

    /**
     * 解析附近设备数据
     * 
     * @param btAddr 附近设备蓝牙地址
     */
    private void analyNearbyData(final byte[] btAddr) {

        if (btAddr.length != 6)
            return;

        String sBdAddr = "";
        for (int i = 0; i < 6; ++i) {
            sBdAddr += String.format("%02X", btAddr[i]);
        }

        println("-sBdAddr:" + sBdAddr);

        String[] stringArray = new String[] {
                Provider.Bluetooth.DEVICEID, Provider.Bluetooth.BDADDR,
                Provider.Bluetooth.DEVNAME
        };

        Cursor c = getContentResolver().query(Provider.Bluetooth.CONTENT_URI,
                stringArray, Provider.Bluetooth.BDADDR + "=?", new String[] {
                    sBdAddr
                }, null);

        if (c == null || c.moveToFirst() == false || c.getCount() <= 0) {// 配对列表中未找到
            if (c != null) {
                c.close();
            }
            return;
        }

        if (c != null) {
            c.close();
        }

        // 找到的设备已配对，进行连接
        if (m_nbtState >= 2)// 已经连接不需要再连接
            return;

        byte[] profileID = new byte[7];
        profileID[0] = 0x01;// HFP
        System.arraycopy(btAddr, 0, profileID, 1, 6);
        RequestDatan(BTCmd.BT_CONNECT.REQ_ESTABLISH_SLC, profileID);
        Sleep(250);
        profileID[0] = 0x02;// A2DP (SNK) and AVRCP (CT)
        System.arraycopy(btAddr, 0, profileID, 1, 6);
        RequestDatan(BTCmd.BT_CONNECT.REQ_ESTABLISH_SLC, profileID);

    }

    /***
     * 模块初始化
     */
    private void InitBt() {

        try {
            char ucmd[] = {
                    0x60, 0x07, 0x11, 0x21, 0x31, 0x40, 0x70, 0xa0, 0x01
            };

            char ucmd2[] = {
                    0x33, 0x01, 0x1a
            };

            mOutputStream.write(getBytes(ucmd));
            mOutputStream.flush();

            Sleep(200);
            mOutputStream.write(getBytes(ucmd2));
            mOutputStream.flush();
            
        } catch (IOException e) {

            e.printStackTrace();
        }// 写数据到流，最终写到串口
    }

    private void Sleep(long time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        // Notification notification = new Notification(R.drawable.ic_launcher,
        // getString(R.string.app_name), System.currentTimeMillis());
        //
        // PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
        // new Intent(this, MyApplication.class), 0);
        // // notification.setLatestEventInfo(this, "BTService", "请保持程序在后台运行",
        // // pendingintent);
        // startForeground(0x111, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        println("-onDestroy-");
        stopForeground(true);
        Intent intent = new Intent("com.yrc.bt.destroy");
        sendBroadcast(intent);
        super.onDestroy();
        htWork.quit();
    }

    private SharedPreferences getSendedSharedPreferences() {
        return getSharedPreferences("sended", MODE_PRIVATE);
    }

    /**
     * 打开串口
     */
    private void sendOpenSerialPort() {
        println("-sendOpenSerialPort-");
        writetoFile(Define.DEVICE_NODES.SYS_KT0806_BT_STATUS, "1");
        workHandler.sendMessageAtFrontOfQueue(Message.obtain(workHandler,
                CMD_OPEN_SERIAL));
    }

    /**
     * 消息队列
     * 
     * @param msg
     */
    public void handleMyMessage(Message msg) {
        // println( "-handleMyMessage-"+msg.what );
        switch (msg.what) {
            case BTCmd.BT_AVRCP.REQ_STOP_CMD:// 蓝牙播放音乐停止
            {
                byte[] avrcp_stop = {
                    0x03
                };
                RequestDatan(BTCmd.BT_AVRCP.REQ_AVRCP_CMD, avrcp_stop);
            }
                break;
            case BTCmd.BT_MANAGE.REQ_REBOOT: {
                byte[] reboot = {
                    0x01
                };
                RequestDatan(BTCmd.BT_MANAGE.REQ_REBOOT, reboot);
            }
                break;
            case BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_INFO:// 取蓝牙模块信息
            {
                RequestDatan(BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_INFO, null);
            }
                break;
            case BTCmd.BT_MANAGE.REQ_SET_SCAN_MODE:// 设置蓝牙可见
            {
                byte[] scan = {
                    0x03
                };
                RequestDatan(BTCmd.BT_MANAGE.REQ_SET_SCAN_MODE, scan);
            }
                break;
            case BTCmd.BT_PAIRING.REQ_SET_PIN_CODE:// 设置配对密码
            {
                byte[] code = {
                        0x30, 0x30, 0x30, 0x30
                };
                RequestDatan(BTCmd.BT_PAIRING.REQ_SET_PIN_CODE, code);
            }
                break;
            case BTCmd.BT_PAIRING.REQ_SET_PAIRING_CONTROL:// 设置配对
            {
                byte[] pairing = {
                    0x01
                };
                RequestDatan(BTCmd.BT_PAIRING.REQ_SET_PAIRING_CONTROL, pairing);
            }
                break;
            case BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE:// 取蓝牙模块状态
            {
                println("取蓝牙状态");
                msg.getTarget().removeMessages(
                        BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE);
                if (m_bPBDownload == false)
                    RequestDatan(BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_STATE, null);
            }
                break;
            case BTCmd.BT_CALL.REQ_SET_HFP_VOL:// 设置蓝牙音量，暂时不对外提供接口
            {
                byte[] hfp_vol = {
                        0x07,0x0f
                };
                RequestDatan(BTCmd.BT_CALL.REQ_SET_HFP_VOL, hfp_vol);
            }
                break;
            case BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL:// 通话中有来电操作(一律拒接)
            {
                byte[] multi_call = {
                    0x00
                }; // 多方向通话拒接
                RequestDatan(BTCmd.BT_CALL.REQ_ENHANCED_CALL_CTRL, multi_call);
            }
                break;
            case BTCmd.BT_CALL.REQ_GET_CURRENT_CALLS:// 获取当前通话的号码，手机不一定支持
            {
                RequestDatan(BTCmd.BT_CALL.REQ_GET_CURRENT_CALLS, null);
            }
                break;

            case BTCmd.BT_A2DP.REQ_SET_A2DP_VOL:// 设置蓝牙音乐音量(0x00 – 0x0f)，暂不提供接口
            {
                byte[] a2dp_vol = {
                    0x0f
                };
                RequestDatan(BTCmd.BT_A2DP.REQ_SET_A2DP_VOL, a2dp_vol);
            }
                break;
            case BTCmd.BT_SEARCH.REQ_INQUIRY:// 搜索附近设备
            {
                if (m_nbtState >= 2)// 已经连接不需要再搜索
                    return;
                msg.getTarget().removeMessages(BTCmd.BT_SEARCH.REQ_INQUIRY); // 重复执行命令无意义
                byte[] bt_search = {
                        0x00, 0x10, 0x10
                };
                RequestDatan(BTCmd.BT_SEARCH.REQ_INQUIRY, bt_search);
            }
                break;
            case BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD:// 下载电话本
            {
                if (m_nbtState < 3)
                    return;
                if (m_bPBDownload == true)
                    return;
                msg.getTarget().removeMessages(BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD); // 重复执行命令无意义
                println("----req_pb_download");
                byte[] pbook_download = {
                        0x01, 0x02
                };
                RequestDatan(BTCmd.BT_PBOOK.REQ_PB_DOWNLOAD, pbook_download);
            }
                break;
            case CMD_OPEN_SERIAL: {
                msg.getTarget().removeMessages(CMD_OPEN_SERIAL); // 重复执行命令无意义
                try {
                    tryOpenSerialPort();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
                break;
            case CMD_BT_INIT:// 初始化
            {
                msg.getTarget().removeMessages(CMD_BT_INIT); // 重复执行命令无意义
                InitBt();
            }
                break;
            default:
                break;
        }
    }

    /**
     * 向模块发送数据请求
     * 
     * @param nCmd 命令类型
     * @param sendcmd 命令参数
     */
    private synchronized void RequestDatan(int nCmd, byte[] sendcmd) {

        int nLen = (sendcmd == null) ? 0 : sendcmd.length;

        println("-请求 nCmd:" + nCmd + " nLen:" + nLen);

        if (nLen >= BTCmd.BT_CMD_LEN)// 超过最大长度直接返回
            return;

        byte[] cmd = new byte[nLen + 2];
        cmd[0] = (byte) nCmd;// 命令头
        cmd[1] = (byte) nLen;// 参数长度

        if (nLen > 0) {// 带参数时，组装参数
            for (int i = 0; i < nLen; i++) {
                cmd[i + 2] = sendcmd[i];
            }
        }

        tryWriteData(cmd);
    }

    /**
     * 向模块发送数据请求
     * 
     * @param nCmd 命令类型
     * @param sParameters 命令参数
     */
    private synchronized void RequestDatas(int nCmd, String sParameters) {
        println("-requestdata nCmd:" + nCmd + "sParameters:" + sParameters);
        int nLen = sParameters.length();

        if (nLen >= BTCmd.BT_CMD_LEN)// 超过最大长度直接返回
            return;

        byte[] cmd = new byte[nLen + 2];
        cmd[0] = (byte) nCmd;// 命令头
        cmd[1] = (byte) nLen;// 参数长度

        if (nLen > 0) {// 带参数时，组装参数
            System.arraycopy(sParameters.getBytes(), 0, cmd, 2, nLen);
        }

        tryWriteData(cmd);

    }

    /**
     * 写串口数据
     * 
     * @param data 整包数据
     */
    private void tryWriteData(byte[] data) {

        if (data.length > BTCmd.BT_CMD_LEN) {// 数据长度超过最大长度
            println("data length is too long. data:" + toHexString(data));
            return;
        }

        try {

            println("-tryWriteData-" + data.length + "," + toHexString(data));
            mOutputStream.write(data);// 写数据到流，最终写到串口
            mOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            println("-write error try openSerialPort-" + this);
            sendOpenSerialPort(); // 发送失败重新打开串口？
        }
    }

    private void tryOpenSerialPort() throws SecurityException {
        println("-开始打开串口-");
        try {
            mSerialPort = mApplication.getSerialPort();
            if (mSerialPort != null) {
                m_bPortOpen = true;

                Log.e(TAG, "-串口打开成功-");

                mOutputStream = mSerialPort.getOutputStream();
                println("-mOutputStream-" + mOutputStream);

                mInputStream = mSerialPort.getInputStream();
                println("-mInputStream-" + mInputStream);

                // 打开成功后查看模块信息
                workHandler
                        .sendEmptyMessage(BTCmd.BT_MANAGE.REQ_GET_LOCAL_DEV_INFO);

            } else {
                m_bPortOpen = false;
            }

            return;
        } catch (IOException e) {
            e.printStackTrace();
            // DisplayError("R.string.error_unknown");
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            // DisplayError("R.string.error_configuration");
        }

        m_bPortOpen = false;

        SystemClock.sleep(1000);
    }

    /**
     * 对外部发送广播
     */
    private void sendBdcast(int nCmd, String sData) {
        Intent intent = new Intent();
        intent.setAction(BTApi.ACTION_YRC_BD);
        intent.putExtra("cmd", nCmd);
        intent.putExtra("btstate", m_nbtState);
        if (sData != null) {
            intent.putExtra("data", sData);
        }

        Log.d("m_nbtState:", m_nbtState + "");
        sendBroadcast(intent, null);
    }

    private void closeAndReOpen() {
        try {
            mSerialPort.close();
            mOutputStream.close();
            mInputStream.close();
            mSerialPort = null;
            mOutputStream = null;
            mInputStream = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendOpenSerialPort();
    }

    private byte readHisCount(ByteBuffer wrap) {
        return wrap.get();
    }

    private boolean isEqual(byte[][] scr, byte[] dst) {
        for (int i = 0; i < scr.length; i++) {
            if (isEqual(scr[i], dst)) {
                return true;
            }
            //
        }
        return false;
    }

    private boolean isEqual(byte[] src, byte[] dst) {
        if (dst.length == src.length) {
            for (int i = 0; i < dst.length; i++) {
                if (src[i] != dst[i]) {
                    return false;
                }
                //
            }
            return true;
        }
        return false;
    }

    private static String toHexString(byte[] data) {
        return toHexString(data, data.length);
    }

    private static String toHexString(byte[] data, int bytes) {
        String s = "";
        for (int i = 0; i < bytes; ++i) {
            s += String.format("%02X ", data[i]);
        }
        return s;
    }

    private static String toHexStr(byte[] data, int bytes) {
        String s = "";
        for (int i = 0; i < bytes; ++i) {
            s += String.format("%02X", data[i]);
        }
        return s;
    }

    private static String toString(byte[] data) {
        return toString(data, data.length);
    }

    private static String toString(byte[] data, int bytes) {
        String s = "";
        for (int i = 0; i < bytes; ++i) {
            s += (char) data[i];
        }
        return s;
    }

    private static void println(Object o) {
        // System.out.println( String.valueOf(o) );
        Log.e(TAG, String.valueOf(o));
    }

    /**
     * 添加配对设备到表
     * 
     * @param sId 配对ID
     * @param sBdAddr 配对蓝牙地址
     * @param sDevName 配对设备名
     */
    public void addPairedList(String sId, String sBdAddr, String sDevName) {
        if (sId.length() < 0 || sBdAddr.length() < 6 || sDevName.length() < 0)
            return;

        String[] stringArray = new String[] {
                Provider.Bluetooth.DEVICEID, Provider.Bluetooth.BDADDR,
                Provider.Bluetooth.DEVNAME
        };

        Cursor c = getContentResolver().query(Provider.Bluetooth.CONTENT_URI,
                stringArray, Provider.Bluetooth.DEVICEID + "=?", new String[] {
                    sId
                }, null);

        boolean bInsert = false;

        if (c == null || c.moveToFirst() == false || c.getCount() <= 0) {
            bInsert = true;
        }
        if (c != null) {
            c.close();
        }

        ContentValues values = new ContentValues();
        values.clear();
        values.put(Provider.Bluetooth.DEVICEID, sId);
        values.put(Provider.Bluetooth.BDADDR, sBdAddr);
        values.put(Provider.Bluetooth.DEVNAME, sDevName);

        if (bInsert) {// 为空时插入一条
            Uri uri = getApplicationContext().getContentResolver().insert(
                    Provider.Bluetooth.CONTENT_URI, values);
            String lastPath = uri.getLastPathSegment();
            if (TextUtils.isEmpty(lastPath)) {
                println("insert failure!");
            } else {
                println("insert success! the id is " + lastPath);
            }
        } else {
            int ret = getApplicationContext().getContentResolver().update(
                    Provider.Bluetooth.CONTENT_URI, values,
                    Provider.Bluetooth.DEVICEID + "=" + sId, null);
        }

    }

    /**
     * 插入通话记录
     * 
     * @param sTel 通话号码
     * @param nTalkType 通话类型
     */
    public void addTalkLog(String sTel, int nTalkType) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(CallLog.Calls.NUMBER, sTel);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0);
        values.put(CallLog.Calls.TYPE, nTalkType);
        values.put(CallLog.Calls.NEW, "0");// 0已看1未看 ,获取默认为已读
        getApplicationContext().getContentResolver().insert(
                CallLog.Calls.CONTENT_URI, values);
    }

    @Override
    public int getBTState() {

        return m_nbtState;
    }

    /***
     * 写文件
     * 
     * @param fileName 文件路径
     * @param write_str 写入值
     */
    public void writetoFile(String fileName, String write_str) {

        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {

            println("蓝牙打开失败 ");
            e.printStackTrace();
            return;
        }

        byte[] bytes = write_str.getBytes();

        try {
            fos.write(bytes);
            fos.flush();
            println("蓝牙打开 ");
        } catch (IOException e) {

            println("蓝牙打开失败 2");
            e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

	public final String qpDecoding(String str) {
		String sRet = "";
		if (str == null)
			return sRet;

		try {
			StringBuffer sb = new StringBuffer(str);
			for (int i = 0; i < sb.length(); i++) {
				if (sb.charAt(i) == ' ' && sb.charAt(i - 1) == '=') {
					sb.deleteCharAt(i - 1);
				}
			}
			str = sb.toString();
			byte[] bytes = str.getBytes("US-ASCII");
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				if (b != 95) {
					bytes[i] = b;
				} else {
					bytes[i] = 32;
				}
			}
			if (bytes != null) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				for (int i = 0; i < bytes.length; i++) {
					int b = bytes[i];
					if (b == '=') {
						try {
							int u = Character.digit((char) bytes[++i], 16);
							int l = Character.digit((char) bytes[++i], 16);
							if (u == -1 || l == -1) {
								continue;
							}
							buffer.write((char) ((u << 4) + l));
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					} else {
						buffer.write(b);
					}
				}
				sRet = new String(buffer.toByteArray(), "UTF-8");
			}
			return sRet;
		} catch (Exception e) {
			e.printStackTrace();
			return sRet;
		}
	}

	/**
	 * 批量添加通讯录
	 * 
	 * @throws OperationApplicationException
	 * @throws RemoteException
	 */
	public void BatchAddContact(final List<ContactInfo> list)
			throws RemoteException, OperationApplicationException {
		// 开始添加;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = 0;

		for (int i = 0; i < list.size(); i++) {
			println("getName:" + list.get(i).getName() + " getNumber:"
					+ list.get(i).getNumber());

			rawContactInsertIndex = ops.size(); // 实现批量添加
			//
			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_TYPE, null)
					.withValue(RawContacts.ACCOUNT_NAME, null)
					.withYieldAllowed(true).build());
			// 添加姓名
			ops.add(ContentProviderOperation
					.newInsert(
							android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME,
							list.get(i).getName()).withYieldAllowed(true)
					.build());
			// 添加号码
			ops.add(ContentProviderOperation
					.newInsert(
							android.provider.ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, list.get(i).getNumber())
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.withValue(Phone.LABEL, "").withYieldAllowed(true).build());

		}

		if (ops != null) {
			// 真正添加
			ContentProviderResult[] results = getApplicationContext()
					.getContentResolver().applyBatch(
							ContactsContract.AUTHORITY, ops);
		}
		listContacts.clear();
	}

}
