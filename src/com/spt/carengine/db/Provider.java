
package com.spt.carengine.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 保存数据库中的常量
 * 
 * @author Administrator
 */
public class Provider {

    // 这里只有一个authority
    public static final String AUTHORITY = "com.example.testview";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yrc.carnet";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.yrc.carnet";

    /**
     * 保存UserInfo表中用到的常量
     * 
     * @author Administrator
     */
    public static final class UserInfo implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/userinfos");
        public static final String TABLE_NAME = "userinfo";
        public static final String DEFAULT_SORT_ORDER = Provider.UserInfo._ID
                + " desc";

        public static final String ACTIVATE = "activate";
        public static final String DEVICEID = "deviceid";

        public static final String IMEI = "imei";
        public static final String MOBILE = "mobile";
        public static final String DVERSION = "dversion";
        public static final String SVERSION = "sversion";
        public static final String DTOKEN = "dtoken";

    }

    /**
     * 保存Edog表中用到的常量
     * 
     * @author Administrator
     */
    public static final class Edog implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/edogs");
        public static final String TABLE_NAME = "edog";
        public static final String DEFAULT_SORT_ORDER = Provider.Edog._ID
                + "  desc";

        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String LEVEL = "level";
    }

    /**
     * @author Rocky
     * @Time 2015年8月6日 下午5:21:40
     * @description 视频锁
     */
    public static final class VideoLock implements BaseColumns {
        public static final String TABLE_NAME = "videolock";
        public static final String DEFAULT_SORT_ORDER = Provider.VideoLock._ID
                + " desc";

        public static final String NAME = "videoname";
        public static final String CREATE_TIME = "createTime";
        public static final String SIZE = "filesize";
        public static final String PATH = "filepath";
        public static final String DATE = "Date";
    }

    /**
     * 保存Edog表中用到的常量
     * 
     * @author Administrator
     */
    public static final class Bluetooth implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/btdevices");
        public static final String TABLE_NAME = "btdevice";
        public static final String DEFAULT_SORT_ORDER = Provider.Bluetooth._ID
                + "  desc";

        public static final String DEVICEID = "deviceid";
        public static final String BDADDR = "bdaddr";
        public static final String DEVNAME = "devname";

    }

    /**
     * 保存FMSend表中用到的常量
     * 
     * @author Administrator
     */
    public static final class FMSend implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/fmsends");
        public static final String TABLE_NAME = "fmsend";
        public static final String DEFAULT_SORT_ORDER = Provider.Bluetooth._ID
                + "  desc";

        public static final String SWITCH = "switch";
        public static final String CURRFREQ = "currfreq";

    }
}
