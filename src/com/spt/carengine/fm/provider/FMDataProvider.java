
package com.spt.carengine.fm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class FMDataProvider extends ContentProvider implements FMStore {

    private static final String TAG = FMDataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, CHANNELS);
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", CHANNELS_ID);
        sUriMatcher.addURI(AUTHORITY, SAVED_TABLE_NAME, SAVED_STATE);
        sUriMatcher.addURI(AUTHORITY, SAVED_TABLE_NAME + "/#", SAVED_STATE_ID);
    }

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new FMSQLiteOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
