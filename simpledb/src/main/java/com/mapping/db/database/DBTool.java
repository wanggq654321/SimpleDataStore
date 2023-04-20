package com.mapping.db.database;

import android.content.Context;


/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2016/7/6 12:06.
 */

public class DBTool {
    // 数据库默认设置
    public static String DB_NAME = "simpledb.db"; // 默认数据库名字
    private int DB_VERSION = 1;// 默认数据库版本
    private Context mContext;
    private static DBTool utilInstance;
    private SimpleSQLiteDatabase db;
    private SimpleSQLiteDatabasePool mSQLiteDatabasePool;


    public static synchronized DBTool getInstance() {
        if (utilInstance == null) {
            utilInstance = new DBTool();
        }
        return utilInstance;
    }
    public void initDB(Context context, int v,String n, SimpleSQLiteDatabase.DBUpdateListener tadbUpdateListener) {
        mContext = context;
        DB_NAME = n;
        DB_VERSION = v;
        setDb(getSQLiteDatabasePool(tadbUpdateListener).getSQLiteDatabase());
    }

    private SimpleSQLiteDatabasePool getSQLiteDatabasePool(SimpleSQLiteDatabase.DBUpdateListener tadbUpdateListener) {
        if (mSQLiteDatabasePool == null) {
            mSQLiteDatabasePool = SimpleSQLiteDatabasePool.getInstance(mContext, DB_NAME, DB_VERSION, true);
            mSQLiteDatabasePool.setOnDbUpdateListener(tadbUpdateListener);
            mSQLiteDatabasePool.createPool();
        }
        return mSQLiteDatabasePool;
    }

    public SimpleSQLiteDatabase getDb() {
        if (db == null)
            db = getSQLiteDatabasePool(null).getSQLiteDatabase();
        return db;
    }

    public void setDb(SimpleSQLiteDatabase db) {
        this.db = db;
    }

}
