package cn.mappingdb.database;/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:37.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.io.Serializable;

import cn.mappingdb.log.DaoLog;


public class SimpleSQLiteDatabase implements Serializable {

    private static final long serialVersionUID = 1905122041415298207L;

    // 数据库默认设置
    private final static String DB_NAME = "simpledb.db"; // 默认数据库名字
    private final static int DB_VERSION = 1;// 默认数据库版本
    // 当前SQL指令
    private String queryStr = "";
    // 错误信息
    private String error = "";
    // 当前查询Cursor
    private Cursor queryCursor = null;
    // 是否已经连接数据库
    private Boolean isConnect = false;
    // 执行oepn打开数据库时，保存返回的数据库对象
    private android.database.sqlite.SQLiteDatabase mSQLiteDatabase = null;
    private DBHelper mDatabaseHelper = null;
    private DBUpdateListener mTadbUpdateListener;
    private Context mContext;

    public SimpleSQLiteDatabase(Context context) {
        this.mContext = context;
        DBParams params = new DBParams();
        this.mDatabaseHelper = new DBHelper(context, params.getDbName(),
                null, params.getDbVersion());
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param params  数据参数信息
     */
    public SimpleSQLiteDatabase(Context context, DBParams params) {
        this.mContext = context;
        this.mDatabaseHelper = new DBHelper(context, params.getDbName(),
                null, params.getDbVersion());
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param params  数据参数信息
     */
    public SimpleSQLiteDatabase(Context context, DBParams params, DBUpdateListener tadbUpdateListener) {
        this.mContext = context;
        this.mDatabaseHelper = new DBHelper(context, params.getDbName(),
                null, params.getDbVersion(), tadbUpdateListener);
    }

    /**
     * 设置升级的的监听器
     *
     * @param dbUpdateListener
     */
    public void setOnDbUpdateListener(DBUpdateListener dbUpdateListener) {
        this.mTadbUpdateListener = dbUpdateListener;
        if (mTadbUpdateListener != null) {
            mDatabaseHelper.setOndbUpdateListener(mTadbUpdateListener);
        }
    }

    /**
     * 打开数据库如果是 isWrite为true,则磁盘满时抛出错误
     *
     * @param isWrite
     * @return
     */
    public android.database.sqlite.SQLiteDatabase openDatabase(DBUpdateListener dbUpdateListener,
                                                               Boolean isWrite) {
        if (isWrite) {
            mSQLiteDatabase = openWritable(mTadbUpdateListener);
        } else {
            mSQLiteDatabase = openReadable(mTadbUpdateListener);
        }
        return mSQLiteDatabase;

    }

    /**
     * 以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就不能以只能读而不能写抛出错误。
     *
     * @param dbUpdateListener
     * @return
     */
    public android.database.sqlite.SQLiteDatabase openWritable(DBUpdateListener dbUpdateListener) {
        if (dbUpdateListener != null) {
            this.mTadbUpdateListener = dbUpdateListener;
        }
        if (mTadbUpdateListener != null) {
            mDatabaseHelper.setOndbUpdateListener(mTadbUpdateListener);
        }
        try {
            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
            isConnect = true;
            // 注销数据库连接配置信息
            // 暂时不写
        } catch (Exception e) {
            // TODO: handle exception
            isConnect = false;
        }

        return mSQLiteDatabase;
    }

    /**
     * @return
     */
    public Boolean testSQLiteDatabase() {
        if (isConnect) {
            if (mSQLiteDatabase.isOpen()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 以读写方式打开数据库，如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库。如果该问题成功解决，
     * 则只读数据库对象就会关闭，然后返回一个可读写的数据库对象。
     *
     * @param dbUpdateListener
     * @return
     */
    public android.database.sqlite.SQLiteDatabase openReadable(DBUpdateListener dbUpdateListener) {
        if (dbUpdateListener != null) {
            this.mTadbUpdateListener = dbUpdateListener;
        }
        if (mTadbUpdateListener != null) {
            mDatabaseHelper.setOndbUpdateListener(mTadbUpdateListener);
        }
        try {
            mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
            isConnect = true;
            // 注销数据库连接配置信息
            // 暂时不写
        } catch (Exception e) {
            // TODO: handle exception
            isConnect = false;
        }

        return mSQLiteDatabase;
    }

//    /**
//     * 判断是否存在某个表,为true则存在，否则不存在
//     *
//     * @param clazz
//     * @return true则存在，否则不存在
//     */
//    public boolean hasTable(Class<?> clazz) {
//        String tableName = DBUtils.getTableName(clazz);
//        return hasTable(tableName);
//    }
//
//    /**
//     * 判断是否存在某个表,为true则存在，否则不存在
//     *
//     * @param tableName 需要判断的表名
//     * @return true则存在，否则不存在
//     */
//    public boolean hasTable(String tableName) {
//        if (tableName != null && !tableName.equalsIgnoreCase("")) {
//            if (testSQLiteDatabase()) {
//                try {
//                    tableName = tableName.trim();
//                    String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
//                            + tableName + "' ";
//                    if (sql != null && !sql.equalsIgnoreCase("")) {
//                        this.queryStr = sql;
//                    }
//                    free();
//                    queryCursor = mSQLiteDatabase.rawQuery(sql, null);
//                    if (queryCursor.moveToNext()) {
//                        int count = queryCursor.getInt(0);
//                        if (count > 0) {
//                            return true;
//                        }
//                    }
//                } catch (Exception e) {
//                    DaoLog.e(e.getMessage());
//                }
//            } else {
//                DaoLog.e("数据库未打开:" + tableName);
//            }
//        } else {
//            DaoLog.e("判断数据表名不能为空！");
//        }
//        return false;
//    }
//
//    /**
//     * 创建表
//     *
//     * @param clazz
//     * @return 为true创建成功，为false创建失败
//     */
//    public Boolean creatTable(Class<?> clazz) {
//        Boolean isSuccess = false;
//        if (testSQLiteDatabase()) {
//            try {
//                String sqlString = DBUtils.creatTableSql(clazz);
//                execute(sqlString, null);
//                isSuccess = true;
//            } catch (Exception e) {
//                isSuccess = false;
//                DaoLog.e(e.getMessage());
//            }
//        } else {
//            DaoLog.e("数据库未打开！");
//            return false;
//        }
//        return isSuccess;
//    }
//
//    public Boolean dropTable(Class<?> clazz) {
//        String tableName = DBUtils.getTableName(clazz);
//        if (hasTable(clazz))
//            return dropTable(tableName);
//        else
//            return true;
//    }
//
//    /**
//     * 删除表
//     *
//     * @param tableName
//     * @return 为true创建成功，为false创建失败
//     */
//    public Boolean dropTable(String tableName) {
//        Boolean isSuccess = false;
//        if (tableName != null && !tableName.equalsIgnoreCase("")) {
//            if (testSQLiteDatabase()) {
//                try {
//                    String sqlString = "DROP TABLE " + tableName;
//                    execute(sqlString, null);
//                    isSuccess = true;
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    isSuccess = false;
//                    Log.exceptionLog(e);
//                }
//            } else {
//                Log.local("数据库未打开！");
//                return false;
//            }
//        } else {
//            Log.local("删除数据表名不能为空！");
//        }
//        return isSuccess;
//    }

    /**
     * 更新表用于对实体修改时，改变表 暂时不写
     *
     * @param tableName
     * @return
     */
    public Boolean alterTable(String tableName) {
        return false;
    }

    /**
     * 数据库错误信息 并显示当前的SQL语句
     *
     * @return
     */
    public String error() {
        if (this.queryStr != null && !queryStr.equalsIgnoreCase("")) {
            error = error + "\n [ SQL语句 ] : " + queryStr;
        }
        DaoLog.e(error);
        return error;
    }

    /**
     * 插入记录
     *
     * @param table          需要插入到的表
     * @param nullColumnHack 不允许为空的行
     * @param values         插入的值
     * @return
     */
    public Boolean insert(String table, String nullColumnHack,
                          ContentValues values) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.insert(table, nullColumnHack, values) > 0;
        } else {
            DaoLog.e("数据库未打开！");
            return false;
        }
    }

    /**
     * 插入记录
     *
     * @param table          需要插入到的表
     * @param nullColumnHack 不允许为空的行
     * @param values         插入的值
     * @return
     */
    public Boolean insertOrThrow(String table, String nullColumnHack,
                                 ContentValues values) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.insertOrThrow(table, nullColumnHack, values) > 0;
        } else {
            DaoLog.e("数据库未打开！");
            return false;
        }
    }

    /**
     * 删除记录
     *
     * @param table       被删除的表名
     * @param whereClause 设置的WHERE子句时，删除指定的数据 ,如果null会删除所有的行。
     * @param whereArgs
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(String table, String whereClause, String[] whereArgs) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.delete(table, whereClause, whereArgs) > 0;

        } else {
            DaoLog.e("数据库未打开！");
            return false;
        }
    }

    public Boolean needUpgrade(int version) {
        if (testSQLiteDatabase())
            return mSQLiteDatabase.needUpgrade(version);
        return false;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mSQLiteDatabase.close();
    }

    /**
     * 数据库配置参数
     */
    public static class DBParams {
        private String dbName = DB_NAME;
        private int dbVersion = DB_VERSION;

        public DBParams() {
        }

        public DBParams(String dbName, int dbVersion) {
            this.dbName = dbName;
            this.dbVersion = dbVersion;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public int getDbVersion() {
            return dbVersion;
        }

        public void setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
        }
    }

    /**
     * Interface 数据库升级回调
     */
    public interface DBUpdateListener {
        void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion);

        void upgraded();
    }
}
