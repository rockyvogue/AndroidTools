
package com.spt.carengine.fm.provider;

import android.net.Uri;

public interface FMStore {

    static final int CHANNELS = 1;
    static final int CHANNELS_ID = 2;

    static final int SAVED_STATE = 3;
    static final int SAVED_STATE_ID = 4;
    static final String SAVED_TABLE_NAME = "FM_Radio_saved_state";
    static final String TABLE_NAME = "FM_Radio";

    public static final String AUTHORITY = "com.motorola.provider.fmradio";
    public static final int CLEAR_ID = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri SAVED_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + SAVED_TABLE_NAME);
    public static final int SELECT_ALL_ID = 2;

    public static final int High_frequency = 108000;
    public static final int Low_frequency = 87500;

    /***/
    static final String Last_ChNum = "Last_ChNum";
    static final String Last_Freq = "Last_Freq";
    static final String isFirstScaned = "isFirstScaned";
    static final String Last_Volume = "Last_Volume";

    static final String CH_Num = "CH_Num";
    static final String CH_Freq = "CH_Freq";
    static final String CH_Name = "CH_Name";
    static final String CH_RdsName = "CH_RdsName";

}
