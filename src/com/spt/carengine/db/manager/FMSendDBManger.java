
package com.spt.carengine.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.spt.carengine.db.SptDabaseHelper;
import com.spt.carengine.db.bean.FmSendBean;

//FM发射信息
public class FMSendDBManger {

    SptDabaseHelper m_dabaseHelp = null;

    public FMSendDBManger(Context context) {
        m_dabaseHelp = new SptDabaseHelper(context);
    }

    public void UpdataFMSendInfo(FmSendBean m_stu_fmsendInfo) {

        boolean bInsert = false;
        SQLiteDatabase db = m_dabaseHelp.getReadableDatabase();
        if (db == null || m_stu_fmsendInfo.currfreq == null) {
            return;
        }

        Cursor cursor = db.rawQuery("select * from fmsend", null);
        if (cursor == null || cursor.moveToFirst() == false
                || cursor.getCount() <= 0) {
            bInsert = true;
        }

        db = m_dabaseHelp.getWritableDatabase();
        if (db == null) {
            if (cursor != null)
                cursor.close();
            return;
        }

        if (bInsert) {
            try {
                String sql = String.format(
                        "insert into fmsend values('%s','%s')",
                        m_stu_fmsendInfo.fmswitch, m_stu_fmsendInfo.currfreq);
                db.execSQL(sql);
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                if (cursor != null)
                    cursor.close();
                if (db != null)
                    db.close();
            }

        } else {
            ContentValues value = new ContentValues();
            value.put("fmswitch", m_stu_fmsendInfo.fmswitch);
            value.put("currfreq", m_stu_fmsendInfo.currfreq);
            try {
                db.update("fmsend", value, null, null);
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                if (cursor != null)
                    cursor.close();
                if (db != null)
                    db.close();
            }
        }
    }

    public FmSendBean GetFMSendInfo() {
        SQLiteDatabase db = m_dabaseHelp.getReadableDatabase();
        if (db == null) {
            Log.e("wujie", "数据库为空");
            return null;
        }

        FmSendBean m_fmsendInfo = new FmSendBean();

        Cursor cursor = db.rawQuery("select * from fmsend", null);
        if (cursor == null || cursor.moveToFirst() == false
                || cursor.getCount() <= 0) {

            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();

            return m_fmsendInfo;
        }

        m_fmsendInfo.fmswitch = cursor.getString(cursor
                .getColumnIndex("fmswitch"));
        m_fmsendInfo.currfreq = cursor.getString(cursor
                .getColumnIndex("currfreq"));

        return m_fmsendInfo;
    }
}
