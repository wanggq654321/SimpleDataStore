package cn.mappingdb.database;
/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:35.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.mappingdb.log.DaoLog;


public class DBHelper extends SQLiteOpenHelper {
    /**
     * 数据库更新监听器
     */
    private SimpleSQLiteDatabase.DBUpdateListener mTadbUpdateListener;

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param name    数据库名字
     * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
     * @param version 数据库版本
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    /**
     * 构造函数
     *
     * @param context            上下文
     * @param name               数据库名字
     * @param factory            可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
     * @param version            数据库版本
     * @param tadbUpdateListener 数据库更新监听器
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version, SimpleSQLiteDatabase.DBUpdateListener tadbUpdateListener) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        this.mTadbUpdateListener = tadbUpdateListener;
        DaoLog.w("version:"+version);
    }

    /**
     * 设置数据库更新监听器
     */
    public void setOndbUpdateListener(SimpleSQLiteDatabase.DBUpdateListener tadbUpdateListener) {
        this.mTadbUpdateListener = tadbUpdateListener;
    }

    public void onCreate(SQLiteDatabase db) {

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DaoLog.w("oldVersion:"+oldVersion+"   newVersion:"+newVersion);
        if (mTadbUpdateListener != null) {
            mTadbUpdateListener.onUpgrade(db, oldVersion, newVersion);
            mTadbUpdateListener.upgraded();
        }
    }

}