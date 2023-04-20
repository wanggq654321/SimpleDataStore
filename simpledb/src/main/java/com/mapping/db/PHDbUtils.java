package com.mapping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mapping.db.database.DBTool;
import com.mapping.db.database.SimpleSQLiteDatabase;

import java.util.List;

/**
 * Created with AndroidStudio3.0.
 * Description:
 * User: wanggq
 * Date: 2018-02-11
 * Time: 下午2:03
 */
public class PHDbUtils {
    private static PHDbUtils phDbUtils;

    public PHDbUtils() {
        // TODO NOTHING

    }

    // 需要实现 安卓 自带的数据基础操作
    private void test() {
//        SQLiteDatabase db;
//        db.update(String table, ContentValues values, String whereClause, String[]whereArgs)
//        db.delete(String table, String whereClause, String[]whereArgs )
//        db.query( boolean distinct, String table, String[]columns,
//                String selection, String[]selectionArgs, String groupBy,
//                String having, String orderBy, String limit)
//        ContentValues
    }

    public static PHDbUtils getInstance() {
        if (phDbUtils == null) {
            synchronized (PHDbUtils.class) {
                if (phDbUtils == null) {
                    phDbUtils = new PHDbUtils();
                }
            }
        }
        return phDbUtils;
    }

    public void initDB(Context context, SimpleSQLiteDatabase.DBUpdateListener updateListener) {
        DBTool.getInstance().initDB(context, 1, "simpleMapping.db", updateListener);
        if (!DBTool.getInstance().getDb().needUpgrade(1)) {
            if (updateListener != null) {
                updateListener.upgraded();
            }
        }
    }

    /*
     *  各种查询
     */
    public <T> List<T> query(Class<?> clazz, String where, String[] selectionArgs) {
        return DBTool.getInstance().getDb().query(clazz, true, where, selectionArgs, null, null, null, -1);
    }

    public <T> T queryFrist(Class<?> clazz, String where, String[] selectionArgs) {
        List<T> l = DBTool.getInstance().getDb().query(clazz, true, where, selectionArgs, null, null, null, -1);
        return (l == null || l.size() == 0) ? null : l.get(0);
    }

    public <T> List<T> query(Class<?> clazz) {
        return DBTool.getInstance().getDb().query(clazz, true, " 1=1", null, null, null, null, -1);
    }

    public void execute(String sql) {
        DBTool.getInstance().getDb().execute(sql, null);
    }

    public Boolean insert(Object entity) {
        return DBTool.getInstance().getDb().insert(entity);
    }

    public Boolean delete(Class<?> clazz, String where, String[] bindArgs) {
        return DBTool.getInstance().getDb().delete(clazz, where,bindArgs);
    }

    public Boolean delete(Object entity) {
        return DBTool.getInstance().getDb().delete(entity);
    }

    public Boolean update(Object entity) {
        return DBTool.getInstance().getDb().update(entity);
    }

    public Boolean update(Object entity, String where) {
        return DBTool.getInstance().getDb().update(entity, where);
    }


}
