
package com.spt.carengine.define;

/**
 * 常量定义
 * 
 * @author Administrator
 */
public interface BTCmd {

    public final String TAG = "wujie";

    public final int BT_CMD_LEN = 250;

    /**
     * 蓝牙模块管理
     * 
     * @author Administrator
     */
    public interface BT_MANAGE {
        /** 重启模块 **/
        final int REQ_REBOOT = 0x11;
        /** 获取蓝牙模块信息 **/
        final int REQ_GET_LOCAL_DEV_INFO = 0x12;
        /** 蓝牙模块状态 **/
        final int REQ_GET_LOCAL_DEV_STATE = 0x13;
        /** 测试命令(未使用) **/
        final int REQ_ENTER_DUT_MODE = 0x14;
        /** 设置蓝牙设备名称 **/
        final int REQ_SET_LOCAL_DEV_NAME = 0x16;
        /** 设置设备的可视性，即发现和连接 **/
        final int REQ_SET_SCAN_MODE = 0x17;
    }

    /**
     * 蓝牙模块搜索
     * 
     * @author Administrator
     */
    public interface BT_SEARCH {
        /** 搜索附近蓝牙设备 **/
        final int REQ_INQUIRY = 0x1C;
        /** 检索远程设备名称 **/
        final int REQ_REMOTE_DEVICE_NAME = 0x1E;
        /** 搜索远程设备支持的服务 **/
        final int REQ_SERVICES_SEARCH = 0x1D;
    }

    /**
     * 配对管理
     * 
     * @author Administrator
     */
    public interface BT_PAIRING {
        /** 设置蓝牙模块接收远程设备配对请求。 **/
        final int REQ_SET_PAIRING_CONTROL = 0x1F;
        /** 设置配对代码 **/
        final int REQ_SET_PIN_CODE = 0x20;
        /** 远程设备的配对请求作出应答 **/
        final int REQ_PAIR_REPLY = 0x21;
        /** 远程设备配对 **/
        final int REQ_PAIR_REMOTE_DEVICE = 0x22;
        /** 获取配对列表 **/
        final int REQ_GET_PAIRED_DEVICE = 0x23;
        /** 删除配对设备 **/
        final int REQ_DELETE_PAIRED_DEVICE = 0x24;
        /** 远程设备的配对请求作出响应 **/
        final int REQ_SSP_CONFIRMATION = 0x5F;
    }

    /**
     * 蓝牙连接
     * 
     * @author Administrator
     */
    public interface BT_CONNECT {
        /** 建立与远程设备连接。 **/
        final int REQ_ESTABLISH_SLC = 0x25;
        /** 根据参数“Option”断开连接 **/
        final int REQ_DISCONNECintLC = 0x26;
        /** 免提连接得到RSSI。 **/
        final int REQ_GET_RSSI = 0x27;
        /** 查询当前蓝牙连接 **/
        final int REQ_GET_LINK_QUALITY = 0x28;
    }

    /**
     * 蓝牙连上后操作
     * 
     * @author Administrator
     */
    public interface BT_CALL {
        /** 来电应答 **/
        final int REQ_ANSWER = 0x29;
        /** 挂断当前的通话 **/
        final int REQ_TERMINATE = 0x2A;
        /** 来电拒接 **/
        final int REQ_REJECT = 0x2B;
        /** 拨打电话 **/
        final int REQ_DIAL_NUM = 0x2C;
        /** 重拨上一次拨出的电话 **/
        final int REQ_REDIAL = 0x2E;
        /** 通话中开关声音 **/
        final int REQ_VOICE_DIAL = 0x2F;
        /** 通话中蓝牙通话和手机通话切换 **/
        final int REQ_AUDIO_TRANSFER = 0x30;
        /** 通话中设置DTMF **/
        final int REQ_SEND_DTMF = 0x31;
        /** 设置蓝牙说话音量 **/
        final int REQ_SET_HFP_VOL = 0x32;
        /** 通话中有来电操作 **/
        final int REQ_ENHANCED_CALL_CTRL = 0x35;
        /** 获取当前电话号码 **/
        final int REQ_GET_CURRENT_CALLS = 0x38;
        /** 取连接的设备名 **/
        final int REQ_GET_OPERATOR = 0x39;
        /** 取当前连蓝牙的手机号码(手机不一定支持) **/
        final int REQ_GET_SUBSCRIBER_NUM = 0x3A;
        /** 蓝牙模块MIC静音 **/
        final int REQ_MIC_MUTE_TOGGLE = 0x4F;
    }

    /**
     * 蓝牙音频操作
     * 
     * @author Administrator
     */
    public interface BT_A2DP {
        /** 设置蓝牙音乐音量 **/
        final int REQ_SET_A2DP_VOL = 0x3B;
    }

    /**
     * 蓝牙播放
     * 
     * @author Administrator
     */
    public interface BT_AVRCP {
        /** 设置蓝牙音乐播放控制 **/
        final int REQ_AVRCP_CMD = 0x3D;
        /** 播放控制参数-播放 **/
        final int REQ_PLAY_CMD = 0x01;
        /** 播放控制参数-暂停 **/
        final int REQ_PAUSE_CMD = 0x02;
        /** 播放控制参数-停止 **/
        final int REQ_STOP_CMD = 0x03;
        /** 播放控制参数-下一曲 **/
        final int REQ_FORWARD_CMD = 0x04;
        /** 播放控制参数-上一曲 **/
        final int REQ_BACKWARD_CMD = 0x05;
        /** 播放控制参数-快退，暂未使用 **/
        final int REQ_FAST_FORWARD_CMD = 0x08;
        /** 播放控制参数-快退，暂未使用 **/
        final int REQ_BACK_FORWARD_CMD = 0x09;
        /** 未知，暂未使用 **/
        final int REQ_GET_CAPABILITY_CMD = 0x10;
        /** 播放状态 **/
        final int REQ_GET_PLAY_STATUS_CMD = 0x11;
        /** 取艺术家信息，暂未使用 **/
        final int REQ_GET_ELEMENT_ATTR_CMD = 0x12;
        /** 未知，暂未使用 **/
        final int REQ_SET_NOTIFICATION_CMD = 0x13;
        /** 未知，暂未使用 **/
        final int REQ_LIST_APP_SETTING_ATTR_CMD = 0x14;
        /** 未知，暂未使用 **/
        final int REQ_LIST_APP_SETTING_VALUE_CMD = 0x15;
        /** 未知，暂未使用 **/
        final int REQ_GET_APP_CURRENT_VALUE_CMD = 0x16;
        /** 未知，暂未使用 **/
        final int REQ_SET_APP_VALUE_CMD = 0x17;
        /** 未知，暂未使用 **/
        final int REQ_ABORT_DATA_TRANSFER_CMD = 0x18;
        /** 未知，暂未使用 **/
        final int REQ_GROUP_CMD = 0x19;
        /** 未知，暂未使用 **/
        final int REQ_EXTENSION_SUPPORT_CMD = 0x1A;
        /** 未知，暂未使用 **/
        final int REQ_SET_ABSOLUTE_VOLUME_CMD = 0x1B;
    }

    /**
     * 电话本操作
     * 
     * @author Administrator
     */
    public interface BT_PBOOK {
        /** 下载电话本 **/
        final int REQ_PB_DOWNLOAD = 0x3E;
    }

    /**
     * MISC操作
     * 
     * @author Administrator
     */
    public interface BT_MISC {

        final int REQ_MISC_OPERATIONS = 0x60;
        /** A2DP服务 **/
        final int REQ_SHOW_A2DP_SERVICE = 0x50;
        /** AVRCP服务 **/
        final int REQ_SHOW_AVRCP_SERVICE = 0x51;
    }

    /**
     * 连接管理
     * 
     * @author Administrator
     */
    public interface BT_CONNECTION {
        /** 新连接 **/
        final int IND_NEW_CONNECTION = 0xAC;
        /** 本地或远程设备连接断开,此消息通知主机配置断开连接 **/
        final int IND_DISCONNECT_EVENT = 0xBB;
        /** 当连接断开连接时，此消息通知主机配置断开连接。主机决定重新连接设备或忽略 **/
        final int IND_LINK_LOSS = 0xBE;
    }

    /**
     * 手机上操作，蓝牙收到消息
     * 
     * @author Administrator
     */
    public interface IND_HFP {
        /** 蓝牙状态，主机操作结果 **/
        final int IND_HFP_STATE = 0x84;
        /** 手机调整音量，蓝牙这边收到通知 **/
        final int IND_UPDATE_HFP_VOL = 0x86;
        /** 手机来电 **/
        final int IND_CALLER_ID = 0x89;
        /** 手机通话中还有来电 **/
        final int IND_CALL_WAITING = 0x8A;
        /** 手机支持语音识别 **/
        final int IND_VOICE_RECOG = 0x8B;
    }

    /**
     * 手机上操作，声音
     * 
     * @author Administrator
     */
    public interface IND_A2DP {
        /** 手机连接时会收到此消息 **/
        final int IND_A2DP_SINK_STATE = 0x95;
        /** 蓝牙编码开关，通知使用流 **/
        final int IND_CODEC_STATE = 0xD0;
        /** A2DP源装置流状态，是否有声音,开始计时 **/
        final int IND_A2DP_STREAM_STATE = 0x96;
    }

    /**
     * 手机上操作，音频
     * 
     * @author Administrator
     */
    public interface IND_AVRCP {
        /** 手机音乐播放操作，如上一曲，下一曲 **/
        final int IND_AVRCP_PASSTHROUGH_CMD = 0xB8;
    }

    /**
     * 电话本操作回复
     * 
     * @author Administrator
     */
    public interface IND_PHONEBOOK {
        /** 从手机上开始接收数据 **/
        final int IND_PBAPC_START = 0xA3;
        /** 收到PBAP数据(电话本数据) **/
        final int IND_PBAPC_DATA = 0xA4;
        /** 收到PBAP数据完成 **/
        final int IND_PBAPC_COMPLETE = 0xA5;
        /** 电话本下载状态 **/
        final int IND_PB_SESSION_STATUS = 0xD3;
    }

    /**
     * 音频
     * 
     * @author Administrator
     */
    public interface REV_AVRCP {
        /** 手机音频连接状态 **/
        final int IND_AVRCP_CT_STATE = 0x97;
        /** 手机音频目标，同上 **/
        final int IND_AVRCP_TG_STATE = 0xB3;
        /** 手机播放音频操作，如上一曲，下一曲，播放，暂停 **/
        final int IND_AVRCP_PASSTHROUGH_CMD = 0xB8;
        /**** 功能位掩码 ****/
        final int IND_AG_BRSF = 0xD2;
    }

    /**
     * 音频
     * 
     * @author Administrator
     */
    public interface REV_OTHER {
        /** 远程手机支持HFP功能 **/
        final int IND_HFP_SERVICE = 0x90;
        /** HFP信号 **/
        final int IND_HFP_SIGNAL = 0x8D;
        /** 未知 **/
        final int IND_HFP_ROAM = 0x8E;
        /** 电池 **/
        final int IND_HFP_BATTCHG = 0x8F;
        /** 未知 **/
        final int IND_HFP_EXT_MSG = 0xCB;
        /** 未知 **/
        final int IND_SCO_STATE = 0x85;
    }

    public interface IND_NETWORK {
        /** 远程手机支持HFP功能 **/
        final int IND_NETWORK_OPERATOR = 0x91;
        /** HFP信号 **/
        final int IND_SUBSCRIBER_NUMBER = 0x92;
        /** 未知 **/
        final int IND_CURRENT_CALLS = 0x93;
    }

    /**
     * 初始化
     * 
     * @author Administrator
     */
    public interface IND_INIT {
        /** 模块重启和初始化操作成功 **/
        final int IND_INIT_OK = 0x80;
        /** 模块配置初始化完成 **/
        final int IND_PROFILES_INIT_OK = 0x81;
    }
    
    /**
     * 收到配对
     * 
     * @author Administrator
     */
    public interface IND_PAIRING {
        /** 模块重启和初始化操作成功 **/
        final int IND_PAIRING_REQUEST = 0x82;
        /** 模块配置初始化完成 **/
        final int IND_PAIRING_STATUS = 0x83;
    }    
    
}

// enum
// {
// req_success = 0x00,
// isInd = 0x01,
// rsp_avrcp_event_change = 0x02,
// req_fail = 0x80,
// error_invalid_param = 0x81,
// error_illegal_request = 0x82,
// error_request_on_wrong_state = 0x83,
// error_invalid_request_for_this_profile = 0x84,
// error_pin_length_too_big = 0x85,
// error_empty_pin = 0x86,
// error_name_length_too_big = 0x87,
// error_local_dev_name_is_empty = 0x88,
// error_tdl_is_empty = 0x89,
// error_voice_recog_not_support = 0x8A,
// error_in_inquiry_mode = 0x8B,
// error_in_service_searching_mode = 0x8C,
// error_in_get_remote_dev_name_mode = 0x8D,
// error_dut_not_allowed_now = 0x8E,
// error_profiles_initialized = 0x8F,
// error_vol_limit = 0x90,
// error_slc_not_established = 0x91,
// error_slc_already_established = 0x92,
// error_no_call = 0x93,
// error_serial_flash_bad = 0x94,
// error_no_phonebook_exist = 0x95,[1.1.99.24]
// error_proximity_pairing = 0x96,
// error_busy = 0x97,
// error_wrong_hfp_id = 0x98,
// error_no_memory = 0x99,
// rsp_tail = 0x9A
// }

//
// PairingControlSetting
// Value Description
// 0x00 Reject pairing request
// 0x01 Accept pairing request
// 0x02 Forward pairing request to host application

//
// PbapcState
// Value Description
// 0x01 Ready.
// 0x02 Connecting.
// 0x03 Connected
// 0x04 Reserved
// 0x05 SetPhonebook
// 0x06 GetVcardList
// 0x07 GetVcardEntry
// 0x08 Download phonebook
