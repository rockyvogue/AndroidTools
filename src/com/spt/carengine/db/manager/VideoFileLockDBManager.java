
package com.spt.carengine.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spt.carengine.db.DatabaseHelper;
import com.spt.carengine.db.Provider;
import com.spt.carengine.db.bean.VideoFileLockBean;
import com.spt.carengine.recordvideo.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rocky
 * @Time 2015年8月6日 下午5:30:11
 * @description 操作视频锁文件的实体类数据表格
 */
public class VideoFileLockDBManager {

    private Context mContext;

    public VideoFileLockDBManager(Context context) {
        this.mContext = context;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    // // Query /////
    // ////////////////////////////////////////////////////////////////////////////////////////

    public VideoFileLockBean acceptVideoInfoFromPath(String path) {
        synchronized (this) {
            VideoFileLockBean videoFileLockInfo = null;
            String where = Provider.VideoLock.PATH + "='" + path + "'";
            SQLiteDatabase sqLiteDatabase = getDatabase();
            Cursor cursor = sqLiteDatabase.query(Provider.VideoLock.TABLE_NAME,
                    null, where, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    videoFileLockInfo = getInfoFromCursor(cursor);
                }
                sqLiteDatabase.close();
                cursor.close();

            } else {
                return null;
            }
            return videoFileLockInfo;
        }
    }

    public VideoFileLockBean acceptVideoInfoFromName(String name) {
        synchronized (this) {
            VideoFileLockBean videoFileLockInfo = null;
            String where = Provider.VideoLock.NAME + "='" + name + "'";
            SQLiteDatabase sqLiteDatabase = getDatabase();
            Cursor cursor = sqLiteDatabase.query(Provider.VideoLock.TABLE_NAME,
                    null, where, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    videoFileLockInfo = getInfoFromCursor(cursor);
                    ;
                }
                sqLiteDatabase.close();
                cursor.close();

            } else {
                return null;
            }
            return videoFileLockInfo;
        }
    }

    public VideoFileLockBean acceptVideoInfoFromPathAndDate(String path,
            String date) {
        synchronized (this) {
            VideoFileLockBean videoFileLockInfo = null;
            String where = Provider.VideoLock.PATH + "='" + path + "' and "
                    + Provider.VideoLock.DATE + "='" + date + "'";
            SQLiteDatabase sqLiteDatabase = getDatabase();
            Cursor cursor = sqLiteDatabase.query(Provider.VideoLock.TABLE_NAME,
                    null, where, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    videoFileLockInfo = getInfoFromCursor(cursor);
                    ;
                }
                sqLiteDatabase.close();
                cursor.close();

            } else {
                return null;
            }
            return videoFileLockInfo;
        }
    }

    public List<VideoFileLockBean> getDateVideoFilesFromDate(String date) {
        synchronized (this) {
            List<VideoFileLockBean> vLockInfos = new ArrayList<VideoFileLockBean>();
            String where = Provider.VideoLock.DATE + "='" + date + "'";
            SQLiteDatabase sqLiteDatabase = getDatabase();
            Cursor cursor = sqLiteDatabase.query(Provider.VideoLock.TABLE_NAME,
                    null, where, null, null, null, null);
            if (cursor == null) {
                return vLockInfos;
            } else {
                cursor.moveToFirst();
                VideoFileLockBean vInfo = null;
                while (!cursor.isAfterLast()) {
                    vInfo = getInfoFromCursor(cursor);
                    if (vInfo != null) {
                        vLockInfos.add(vInfo);
                    }
                    cursor.moveToNext();
                }
                sqLiteDatabase.close();
                cursor.close();
            }
            return vLockInfos;
        }
    }

    public List<VideoFileLockBean> acceptTotalVideoFiles() {
        synchronized (this) {
            List<VideoFileLockBean> vLockInfos = new ArrayList<VideoFileLockBean>();
            SQLiteDatabase sqLiteDatabase = getDatabase();
            Cursor cursor = sqLiteDatabase.query(Provider.VideoLock.TABLE_NAME,
                    null, null, null, null, null, null);
            if (cursor == null) {
                return vLockInfos;
            } else {
                cursor.moveToFirst();
                VideoFileLockBean vInfo = null;
                while (!cursor.isAfterLast()) {
                    vInfo = getInfoFromCursor(cursor);
                    if (vInfo != null) {
                        vLockInfos.add(vInfo);
                    }
                    cursor.moveToNext();
                }
                sqLiteDatabase.close();
                cursor.close();
            }
            return vLockInfos;
        }
    }

    public SQLiteDatabase getDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        return sqLiteDatabase;
    }

    public VideoFileLockBean getInfoFromCursor(Cursor cursor) {
        synchronized (this) {
            if (cursor == null) {
                throw new NullPointerException();
            }
            VideoFileLockBean videoFileLockInfo = new VideoFileLockBean();
            videoFileLockInfo.setName(cursor.getString(cursor
                    .getColumnIndex(Provider.VideoLock.NAME)));
            videoFileLockInfo.setPath(cursor.getString(cursor
                    .getColumnIndex(Provider.VideoLock.PATH)));
            videoFileLockInfo.setSize(cursor.getString(cursor
                    .getColumnIndex(Provider.VideoLock.SIZE)));
            videoFileLockInfo.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(Provider.VideoLock.CREATE_TIME)));
            videoFileLockInfo.setDate(cursor.getString(cursor
                    .getColumnIndex(Provider.VideoLock.DATE)));
            return videoFileLockInfo;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    // // Insert /////
    // ////////////////////////////////////////////////////////////////////////////////////////

    public boolean saveInfoToDatabase(VideoFileLockBean vLockInfo) {
        synchronized (this) {
            // 判断是否有已锁定的视频目录
            FileUtils.createLockVideoRecordDir();
            boolean isSuccess = false;
            if (vLockInfo == null)
                return isSuccess;
            if (!isExistRecord(vLockInfo.getPath(), vLockInfo.getDate())) {
                isSuccess = insertInfoToDatabase(vLockInfo);

            } else {
                isSuccess = updateInfoRecord(vLockInfo);
            }
            return isSuccess;
        }
    }

    public boolean isExistRecord(String path, String date) {
        synchronized (this) {
            boolean isExistRecord = false;
            VideoFileLockBean vFileLockInfo = acceptVideoInfoFromPathAndDate(
                    path, date);
            if (vFileLockInfo != null) {
                isExistRecord = true;
            }
            return isExistRecord;
        }
    }

    private boolean insertInfoToDatabase(VideoFileLockBean vLockInfo) {
        synchronized (this) {
            boolean isSuccess = false;
            ContentValues values = new ContentValues();
            values.put(Provider.VideoLock.NAME, vLockInfo.getName());
            values.put(Provider.VideoLock.PATH, vLockInfo.getPath());
            values.put(Provider.VideoLock.SIZE, vLockInfo.getSize());
            values.put(Provider.VideoLock.CREATE_TIME,
                    vLockInfo.getCreateTime());
            values.put(Provider.VideoLock.DATE, vLockInfo.getDate());

            SQLiteDatabase sqLiteDatabase = getDatabase();
            isSuccess = (sqLiteDatabase.insert(Provider.VideoLock.TABLE_NAME,
                    null, values) == -1) ? false : true;
            sqLiteDatabase.close();
            return isSuccess;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    // // Update /////
    // ////////////////////////////////////////////////////////////////////////////////////////

    public boolean updateInfoRecord(VideoFileLockBean vLockInfo) {
        synchronized (this) {
            ContentValues values = new ContentValues();
            values.put(Provider.VideoLock.NAME, vLockInfo.getName());
            values.put(Provider.VideoLock.PATH, vLockInfo.getPath());
            values.put(Provider.VideoLock.SIZE, vLockInfo.getSize());
            values.put(Provider.VideoLock.CREATE_TIME,
                    vLockInfo.getCreateTime());
            values.put(Provider.VideoLock.DATE, vLockInfo.getDate());

            String where = Provider.VideoLock.PATH + "='" + vLockInfo.getPath()
                    + "' and " + Provider.VideoLock.DATE + "='"
                    + vLockInfo.getDate() + "'";
            SQLiteDatabase sqLiteDatabase = getDatabase();
            sqLiteDatabase.update(Provider.VideoLock.TABLE_NAME, values, where,
                    null);
            sqLiteDatabase.close();
            return true;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////
    // // Delete /////
    // ////////////////////////////////////////////////////////////////////////////////////////

    public boolean deleteInfoRecordFromPath(String path) {
        synchronized (this) {
            SQLiteDatabase sqLiteDatabase = getDatabase();
            String where = Provider.VideoLock.PATH + "='" + path + "'";
            boolean isSuccess = sqLiteDatabase.delete(
                    Provider.VideoLock.TABLE_NAME, where, null) == 0 ? false
                    : true;
            return isSuccess;
        }
    }

    public boolean deleteInfoRecordFromDate(String date) {
        synchronized (this) {
            SQLiteDatabase sqLiteDatabase = getDatabase();
            String where = Provider.VideoLock.DATE + "='" + date + "'";
            boolean isSuccess = sqLiteDatabase.delete(
                    Provider.VideoLock.TABLE_NAME, where, null) == 0 ? false
                    : true;
            return isSuccess;
        }
    }

}
