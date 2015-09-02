
package com.spt.carengine.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * CommProvider
 * 
 * @author Administrator
 */
public class CommProvider extends ContentProvider {

    private static HashMap<String, String> sMap;

    private static final int USERS = 1;
    private static final int USERS_ID = 2;

    private static final int EDOGS = 3;
    private static final int EDOGS_ID = 4;

    private static final int BLUETOOTHS = 5;
    private static final int BLUETOOTH_ID = 6;

    private static final int FMSENDS = 7;
    private static final int FMSEND_ID = 8;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理
            case EDOGS:
            case EDOGS_ID:
                qb.setTables(Provider.Edog.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Provider.Edog.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case USERS:
            case USERS_ID:
                qb.setTables(Provider.UserInfo.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Provider.UserInfo.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case BLUETOOTHS:
            case BLUETOOTH_ID:
                qb.setTables(Provider.Bluetooth.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Provider.Bluetooth.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case FMSENDS:
            case FMSEND_ID:
                qb.setTables(Provider.FMSend.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Provider.FMSend.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        switch (sUriMatcher.match(uri)) {
            case EDOGS:
            case USERS:
            case BLUETOOTHS:
            case FMSENDS:
                qb.setProjectionMap(sMap);
                break;

            case USERS_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(Provider.UserInfo._ID + "="
                        + uri.getPathSegments().get(1));
                break;

            case EDOGS_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(Provider.Edog._ID + "="
                        + uri.getPathSegments().get(1));
                break;

            case BLUETOOTH_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(Provider.Bluetooth._ID + "="
                        + uri.getPathSegments().get(1));
                break;

            case FMSEND_ID:
                qb.setProjectionMap(sMap);
                qb.appendWhere(Provider.FMSend._ID + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) { // 这里也要增加匹配项
            case EDOGS:
            case USERS:
            case BLUETOOTHS:
            case FMSENDS:
                return Provider.CONTENT_TYPE;
            case USERS_ID:
            case EDOGS_ID:
            case BLUETOOTH_ID:
            case FMSEND_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        String tableName = "";
        String nullColumn = "";
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理
            case EDOGS:
                tableName = Provider.Edog.TABLE_NAME;
                nullColumn = Provider.Edog.NAME;
                // Make sure that the fields are all set
                if (values.containsKey(Provider.Edog.NAME) == false) {
                    values.put(Provider.Edog.NAME, "");
                }

                if (values.containsKey(Provider.Edog.TITLE) == false) {
                    values.put(Provider.Edog.TITLE, "");
                }

                if (values.containsKey(Provider.Edog.LEVEL) == false) {
                    values.put(Provider.Edog.LEVEL, 0);
                }
                break;
            case USERS:
                tableName = Provider.UserInfo.TABLE_NAME;
                nullColumn = Provider.UserInfo._ID;
                // Make sure that the fields are all set
                if (values.containsKey(Provider.UserInfo.ACTIVATE) == false) {
                    values.put(Provider.UserInfo.ACTIVATE, "");
                }

                if (values.containsKey(Provider.UserInfo.DEVICEID) == false) {
                    values.put(Provider.UserInfo.DEVICEID, 0);
                }

                if (values.containsKey(Provider.UserInfo.IMEI) == false) {
                    values.put(Provider.UserInfo.IMEI, 0);
                }

                if (values.containsKey(Provider.UserInfo.MOBILE) == false) {
                    values.put(Provider.UserInfo.MOBILE, 0);
                }

                if (values.containsKey(Provider.UserInfo.DVERSION) == false) {
                    values.put(Provider.UserInfo.DVERSION, 0);
                }

                if (values.containsKey(Provider.UserInfo.SVERSION) == false) {
                    values.put(Provider.UserInfo.SVERSION, 0);
                }

                if (values.containsKey(Provider.UserInfo.DTOKEN) == false) {
                    values.put(Provider.UserInfo.DTOKEN, 0);
                }
                break;

            case BLUETOOTHS:
                tableName = Provider.Bluetooth.TABLE_NAME;
                nullColumn = Provider.Bluetooth._ID;
                // Make sure that the fields are all set
                if (values.containsKey(Provider.Bluetooth.DEVICEID) == false) {
                    values.put(Provider.Bluetooth.DEVICEID, "");
                }

                if (values.containsKey(Provider.Bluetooth.BDADDR) == false) {
                    values.put(Provider.Bluetooth.BDADDR, "");
                }

                if (values.containsKey(Provider.Bluetooth.DEVNAME) == false) {
                    values.put(Provider.Bluetooth.DEVNAME, "");
                }

                break;

            case FMSENDS:
                tableName = Provider.FMSend.TABLE_NAME;
                nullColumn = Provider.FMSend._ID;
                // Make sure that the fields are all set
                if (values.containsKey(Provider.FMSend.SWITCH) == false) {
                    values.put(Provider.FMSend.SWITCH, "");
                }

                if (values.containsKey(Provider.FMSend.CURRFREQ) == false) {
                    values.put(Provider.FMSend.CURRFREQ, "");
                }

                break;
            default:
                // Validate the requested uri
                throw new IllegalArgumentException("Unknown URI " + uri);

        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(tableName, nullColumn, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理，注意下面用到的表名不要弄错了
            case USERS:
                count = db.delete(Provider.UserInfo.TABLE_NAME, where,
                        whereArgs);
                break;

            case USERS_ID:
                String userId = uri.getPathSegments().get(1);
                count = db.delete(
                        Provider.UserInfo.TABLE_NAME,
                        Provider.UserInfo._ID
                                + "="
                                + userId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            case EDOGS:
                count = db.delete(Provider.Edog.TABLE_NAME, where, whereArgs);
                break;

            case EDOGS_ID:
                String edogId = uri.getPathSegments().get(1);
                count = db.delete(Provider.Edog.TABLE_NAME, Provider.Edog._ID
                        + "="
                        + edogId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                : ""), whereArgs);
                break;

            case BLUETOOTHS:
                count = db.delete(Provider.Bluetooth.TABLE_NAME, where,
                        whereArgs);
                break;

            case BLUETOOTH_ID:
                String devivesId = uri.getPathSegments().get(1);
                count = db.delete(
                        Provider.Bluetooth.TABLE_NAME,
                        Provider.Bluetooth._ID
                                + "="
                                + devivesId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            case FMSENDS:
                count = db.delete(Provider.FMSend.TABLE_NAME, where, whereArgs);
                break;

            case FMSEND_ID:
                String fmsendId = uri.getPathSegments().get(1);
                count = db.delete(
                        Provider.FMSend.TABLE_NAME,
                        Provider.FMSend._ID
                                + "="
                                + fmsendId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) { // 这里要对不同表的匹配结果做不同处理，注意下面用到的表名不要弄错了
            case USERS:
                count = db.update(Provider.UserInfo.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case USERS_ID:
                String userId = uri.getPathSegments().get(1);
                count = db.update(
                        Provider.UserInfo.TABLE_NAME,
                        values,
                        Provider.UserInfo._ID
                                + "="
                                + userId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            case EDOGS:
                count = db.update(Provider.Edog.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case EDOGS_ID:
                String edogId = uri.getPathSegments().get(1);
                count = db.update(
                        Provider.Edog.TABLE_NAME,
                        values,
                        Provider.Edog._ID
                                + "="
                                + edogId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            case BLUETOOTHS:
                count = db.update(Provider.Bluetooth.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case BLUETOOTH_ID:
                String devicesId = uri.getPathSegments().get(1);
                count = db.update(
                        Provider.Bluetooth.TABLE_NAME,
                        values,
                        Provider.Bluetooth._ID
                                + "="
                                + devicesId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            case FMSENDS:
                count = db.update(Provider.FMSend.TABLE_NAME, values, where,
                        whereArgs);
                break;

            case FMSEND_ID:
                String fmsendId = uri.getPathSegments().get(1);
                count = db.update(
                        Provider.FMSend.TABLE_NAME,
                        values,
                        Provider.Bluetooth._ID
                                + "="
                                + fmsendId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(Provider.AUTHORITY, "userinfos", USERS);
        sUriMatcher.addURI(Provider.AUTHORITY, "userinfos/#", USERS_ID);

        // 这里要增加另一张表的匹配项
        sUriMatcher.addURI(Provider.AUTHORITY, "edogs", EDOGS);
        sUriMatcher.addURI(Provider.AUTHORITY, "edogs/#", EDOGS_ID);

        sUriMatcher.addURI(Provider.AUTHORITY, "btdevices", BLUETOOTHS);
        sUriMatcher.addURI(Provider.AUTHORITY, "btdevices/#", BLUETOOTH_ID);

        sUriMatcher.addURI(Provider.AUTHORITY, "fmsends", FMSENDS);
        sUriMatcher.addURI(Provider.AUTHORITY, "fmsends/#", FMSEND_ID);

        // 保存所有表用到的字段
        sMap = new HashMap<String, String>();
        sMap.put(Provider.UserInfo._ID, Provider.UserInfo._ID);
        sMap.put(Provider.UserInfo.ACTIVATE, Provider.UserInfo.ACTIVATE);
        sMap.put(Provider.UserInfo.DEVICEID, Provider.UserInfo.DEVICEID);
        sMap.put(Provider.UserInfo.IMEI, Provider.UserInfo.IMEI);
        sMap.put(Provider.UserInfo.MOBILE, Provider.UserInfo.MOBILE);
        sMap.put(Provider.UserInfo.DVERSION, Provider.UserInfo.DVERSION);
        sMap.put(Provider.UserInfo.SVERSION, Provider.UserInfo.SVERSION);
        sMap.put(Provider.UserInfo.DTOKEN, Provider.UserInfo.DTOKEN);

        sMap.put(Provider.Edog._ID, Provider.Edog._ID);
        sMap.put(Provider.Edog.NAME, Provider.Edog.NAME);
        sMap.put(Provider.Edog.TITLE, Provider.Edog.TITLE);
        sMap.put(Provider.Edog.LEVEL, Provider.Edog.LEVEL);

        sMap.put(Provider.Bluetooth._ID, Provider.Bluetooth._ID);
        sMap.put(Provider.Bluetooth.DEVICEID, Provider.Bluetooth.DEVICEID);
        sMap.put(Provider.Bluetooth.BDADDR, Provider.Bluetooth.BDADDR);
        sMap.put(Provider.Bluetooth.DEVNAME, Provider.Bluetooth.DEVNAME);

        sMap.put(Provider.FMSend._ID, Provider.FMSend._ID);
        sMap.put(Provider.FMSend.SWITCH, Provider.FMSend.SWITCH);
        sMap.put(Provider.FMSend.CURRFREQ, Provider.FMSend.CURRFREQ);

    }
}
