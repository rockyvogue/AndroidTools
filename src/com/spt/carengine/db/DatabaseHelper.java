
package com.spt.carengine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 直接操作数据库类
 * 
 * @author Administrator
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "yrcCarDev.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Provider.UserInfo.TABLE_NAME + " ("
                + Provider.UserInfo._ID + " INTEGER PRIMARY KEY,"
                + Provider.UserInfo.ACTIVATE + " TEXT,"
                + Provider.UserInfo.DEVICEID + " TEXT,"
                + Provider.UserInfo.IMEI + " TEXT," + Provider.UserInfo.MOBILE
                + " TEXT," + Provider.UserInfo.DVERSION + " TEXT,"
                + Provider.UserInfo.SVERSION + " TEXT,"
                + Provider.UserInfo.DTOKEN + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + Provider.Edog.TABLE_NAME + " ("
                + Provider.Edog._ID + " INTEGER PRIMARY KEY,"
                + Provider.Edog.NAME + " TEXT," + Provider.Edog.TITLE
                + " TEXT," + Provider.Edog.LEVEL + " INTEGER" + ");");

        db.execSQL("CREATE TABLE " + Provider.VideoLock.TABLE_NAME + " ("
                + Provider.VideoLock._ID + " INTEGER PRIMARY KEY,"
                + Provider.VideoLock.NAME + " TEXT,"
                + Provider.VideoLock.CREATE_TIME + " TEXT,"
                + Provider.VideoLock.SIZE + " TEXT," + Provider.VideoLock.PATH
                + " TEXT," + Provider.VideoLock.DATE + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + Provider.Bluetooth.TABLE_NAME + " ("
                + Provider.Bluetooth._ID + " INTEGER PRIMARY KEY,"
                + Provider.Bluetooth.DEVICEID + " TEXT,"
                + Provider.Bluetooth.BDADDR + " TEXT,"
                + Provider.Bluetooth.DEVNAME + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + Provider.FMSend.TABLE_NAME + " ("
                + Provider.FMSend._ID + " INTEGER PRIMARY KEY,"
                + Provider.FMSend.SWITCH + " TEXT," + Provider.FMSend.CURRFREQ
                + " TEXT" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Provider.UserInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.Edog.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.VideoLock.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.Bluetooth.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.FMSend.TABLE_NAME);
        onCreate(db);
    }
}
