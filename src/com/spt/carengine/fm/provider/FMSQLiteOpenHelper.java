
package com.spt.carengine.fm.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FMSQLiteOpenHelper extends SQLiteOpenHelper implements FMStore {

    private static final String DATABASE_NAME = "FM_RadioDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String C_SAVE_TABLE = String
            .format("CREATE TABLE FM_Radio_saved_state (Last_ChNum INTEGER,Last_Freq FLOAT,"
                    + "isFirstScaned BOOLEAN,Last_Volume INTEGER);",
                    SAVED_TABLE_NAME, Last_ChNum, Last_Freq, isFirstScaned,
                    Last_Volume);

    private static final String C_TABLE = String
            .format("CREATE TABLE FM_Radio (ID INTEGER,CH_Num TEXT,CH_Freq FLOAT,CH_Name TEXT,CH_RdsName TEXT);",
                    TABLE_NAME, CH_Num, CH_Freq, CH_Name, CH_RdsName);

    public FMSQLiteOpenHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public FMSQLiteOpenHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
