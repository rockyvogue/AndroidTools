
package com.spt.carengine.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.spt.carengine.db.SptDabaseHelper;
import com.spt.carengine.db.bean.UserInfoBean;

//用户信息数据库
public class UserDabaseManger {
    private SptDabaseHelper m_dabaseHelp = null;

    public UserDabaseManger(Context context) {
        m_dabaseHelp = new SptDabaseHelper(context);
    }

    public void UpdateUserInfo(UserInfoBean m_stu_userInfo) {
        boolean bInsert = false;
        SQLiteDatabase db = m_dabaseHelp.getReadableDatabase();
        if (db == null || m_stu_userInfo == null) {
            return;
        }

        Cursor cursor = db.rawQuery("select * from userinfo", null);
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
                String sql = String
                        .format("insert into userinfo values('%s','%s','%s','%s','%s','%s','%s')",
                                m_stu_userInfo.activate, m_stu_userInfo.token,
                                m_stu_userInfo.imei, m_stu_userInfo.mobile,
                                m_stu_userInfo.dversion,
                                m_stu_userInfo.sversion, m_stu_userInfo.dtoken);
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
            value.put("activate", m_stu_userInfo.activate);
            value.put("token", m_stu_userInfo.token);
            value.put("imei", m_stu_userInfo.imei);
            value.put("mobile", m_stu_userInfo.mobile);
            value.put("dversion", m_stu_userInfo.dversion);
            value.put("sversion", m_stu_userInfo.sversion);
            value.put("dtoken", m_stu_userInfo.dtoken);
            try {
                db.update("userinfo", value, null, null);
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

    public UserInfoBean GetUserInfo() {
        SQLiteDatabase db = m_dabaseHelp.getReadableDatabase();
        if (db == null) {
            Log.e("wujie", "数据库为空");
            return null;
        }

        UserInfoBean m_fmsendInfo = new UserInfoBean();

        Cursor cursor = db.rawQuery("select * from userinfo", null);
        if (cursor == null || cursor.moveToFirst() == false
                || cursor.getCount() <= 0) {

            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();

            return m_fmsendInfo;
        }

        m_fmsendInfo.activate = cursor.getString(cursor
                .getColumnIndex("activate"));
        m_fmsendInfo.token = cursor.getString(cursor.getColumnIndex("token"));
        m_fmsendInfo.imei = cursor.getString(cursor.getColumnIndex("imei"));
        m_fmsendInfo.mobile = cursor.getString(cursor.getColumnIndex("mobile"));
        m_fmsendInfo.dversion = cursor.getString(cursor
                .getColumnIndex("dversion"));
        m_fmsendInfo.sversion = cursor.getString(cursor
                .getColumnIndex("sversion"));
        m_fmsendInfo.dtoken = cursor.getString(cursor.getColumnIndex("dtoken"));

        if (cursor != null)
            cursor.close();
        if (db != null)
            db.close();

        return m_fmsendInfo;
    }

    // 清空表
    public void ClearData() {
        SQLiteDatabase db = null;
        try {
            db = m_dabaseHelp.getWritableDatabase();
            if (db == null) {
                return;
            }
            db.delete("userinfo", null, null);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (db != null)
                db.close();
        }
    }
}
