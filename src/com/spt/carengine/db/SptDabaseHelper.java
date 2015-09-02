
package com.spt.carengine.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SptDabaseHelper extends SQLiteOpenHelper {

    private final static int VERSION = 6;// 版本
    private final static String DB_NAME = "yrcCarNet.db";// 数据库名

    public SptDabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建用户信息表结构
        final String CREATE_USERINFO = "create table userinfo "
                + "(activate text DEFAULT ''," + "token text," + "imei text,"
                + "mobile text," + "dversion text," + "sversion text,"
                + "dtoken text);";
        db.execSQL(CREATE_USERINFO);

        // 创建FM发射信息表结构
        final String CREATE_FMSENDINFO = "create table fmsend "
                + "(fmswitch text ," + "currfreq text );";
        db.execSQL(CREATE_FMSENDINFO);

        // 创建程序列表信息表结构
        // final String CREATE_APPINFO ="create table applist " +
        // "(pakagename text ''," +
        // "appname text);";
        // db.execSQL(CREATE_APPINFO);

        // 创建电子狗
        final String CREATE_EDOG = "create table edog " + "(off text '',"
                + "mode text," + "clouddog text);";
        db.execSQL(CREATE_EDOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 版本更新
        if (newVersion > oldVersion) {
            Cursor cursor = db.rawQuery("select * from userinfo", null);
            if (cursor != null) {
                if (cursor.getColumnIndex("mobile") == -1) {
                }
                cursor.close();
            }
        }
    }

    // 清空用户信息
    public void ClearUserInfo() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete("userinfo", null, null);

            String sactivate = "insert into userinfo values('0','','','','','','');";
            db.execSQL(sactivate);

            db.close();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
