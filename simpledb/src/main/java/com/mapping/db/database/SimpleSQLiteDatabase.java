package com.mapping.db.database;/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:37.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

import com.mapping.db.bean.ArrayListPair;
import com.mapping.db.bean.HashMapping;
import com.mapping.db.bean.MappingArrayList;
import com.mapping.db.builder.SqlBuilder;
import com.mapping.db.builder.SqlBuilderFactory;
import com.mapping.db.cache.TableInfoCacheFactory;
import com.mapping.db.entity.DBMasterEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.exception.DBException;
import com.mapping.db.log.Log;
import com.mapping.db.utils.DBUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
     *
     * @param sql           sql语句
     * @param selectionArgs
     * @return
     */
    public ArrayList<HashMapping<String>> query(String sql, String[] selectionArgs) {
//		TALogger.i(TASQLiteDatabase.this, sql);
        if (testSQLiteDatabase()) {
            if (sql != null && !sql.equalsIgnoreCase("")) {
                this.queryStr = sql;
            }
            free();
            this.queryCursor = mSQLiteDatabase.rawQuery(sql, selectionArgs);
            if (queryCursor != null) {
                return getQueryCursorData();
            } else {
                Log.e("执行" + sql + "错误");
            }
        } else {
            Log.e("数据库未打开！");
        }
        return null;
    }

    public <T> List<T> query(Class<?> clazz) {
        return query(clazz, true, " 1=1", null, null, null, null, -1);
    }

    public <T> List<T> query(Class<?> clazz, String where, String[] selectionArgs) {
        return query(clazz, true, where, selectionArgs, null, null, null, -1);
    }

    /**
     * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
     *
     * @param clazz
     * @param distinct 限制重复，如过为true则限制,false则不用管
     * @param where    where语句
     * @param groupBy  groupBy语句
     * @param having   having语句
     * @param orderBy  orderBy语句
     * @param limit    limit语句
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<?> clazz, boolean distinct, String where, String[] selectionArgs,
                             String groupBy, String having, String orderBy, int limit) {
        if (!hasTable(clazz)) {
            creatTable(clazz);
        }
        if (testSQLiteDatabase()) {
            List<T> list = null;
            SqlBuilder getSqlBuilder = SqlBuilderFactory.getInstance()
                    .getSqlBuilder(SqlBuilderFactory.SELECT);
            getSqlBuilder.setClazz(clazz);
            getSqlBuilder.setCondition(distinct, where, groupBy, having,
                    orderBy, -1);
            Cursor cursor = null;
            try {
                String sqlString = getSqlBuilder.getSqlStatement();

                SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
                try {
                    TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(clazz);
                    sqLiteQueryBuilder.setTables(tableInfoEntity.getTableName());
                } catch (DBException e) {
                    e.printStackTrace();
                }
                sqLiteQueryBuilder.setDistinct(distinct);
                sqlString = sqLiteQueryBuilder.buildQuery(null, where,
                        groupBy, having, orderBy, 100 + "");

                Log.d("查询:" + sqlString);
                cursor = mSQLiteDatabase.rawQuery(sqlString, selectionArgs);

                if (cursor != null)
                    list = (List<T>) DBUtils.getListEntity(clazz, cursor);
                else
                    return null;
            } catch (IllegalArgumentException e) {
                Log.e(e.getMessage());

            } catch (Exception e) {
                if (e.getMessage().contains("no such column:")) {
                    try {
                        Log.w(e.getMessage());
                        String c = e.getMessage().substring(e.getMessage().indexOf("no such column:") + 15).trim();
                        String[] c1 = c.split(" ");
                        if (c1.length > 0) {
                            String lie = c1[0].trim();
                            if (lie.contains("("))
                                lie = lie.substring(0, lie.indexOf("(")).trim();
                            Log.w("该列(" + lie + ")不存在:" + e.getMessage());
                            if (!e.getMessage().contains("WHERE"))
                                addColumn(getSqlBuilder, e.getMessage().substring(e.getMessage().indexOf("FROM") + 4).trim(), lie);
                            else
                                addColumn(getSqlBuilder, e.getMessage().substring(e.getMessage().indexOf("FROM") + 4, e.getMessage().indexOf("WHERE")).trim(), lie);
                        }
                        String sqlString = getSqlBuilder.getSqlStatement();
                        Log.d("查询:" + sqlString);
                        cursor = mSQLiteDatabase.rawQuery(sqlString, selectionArgs);
                        if (cursor != null)
                            list = (List<T>) DBUtils.getListEntity(clazz, cursor);
                        else
                            return null;
                    } catch (Exception e1) {
                        Log.w(e1.getMessage());
                    }
                } else {
                    Log.e(e.getMessage());
                }

                Log.e(e.getMessage());
                Log.e(e.getMessage());
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        } else {
            return null;
        }

    }

    /**
     * 查询记录
     *
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @return
     */
    public ArrayList<HashMapping<String>> query(String table, String[] columns,
                                                String selection, String[] selectionArgs, String groupBy,
                                                String having, String orderBy) {
        if (testSQLiteDatabase()) {
            this.queryCursor = mSQLiteDatabase.query(table, columns, selection,
                    selectionArgs, groupBy, having, orderBy);
            if (queryCursor != null) {
                return getQueryCursorData();
            } else {
                Log.e("查询" + table + "错误");
            }
        } else {
            Log.e("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param distinct      限制重复，如过为true则限制,false则不用管
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return
     */
    public ArrayList<HashMapping<String>> query(String table, boolean distinct,
                                                String[] columns, String selection, String[] selectionArgs,
                                                String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            free();
            this.queryCursor = mSQLiteDatabase.query(distinct, table, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit);
            if (queryCursor != null) {
                return getQueryCursorData();
            } else {
                Log.e("查询" + table + "错误");
            }
        } else {
            Log.e("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return
     */
    public ArrayList<HashMapping<String>> query(String table, String[] columns,
                                                String selection, String[] selectionArgs, String groupBy,
                                                String having, String orderBy, String limit) {

        if (testSQLiteDatabase()) {
            free();
            this.queryCursor = mSQLiteDatabase.query(table, columns, selection,
                    selectionArgs, groupBy, having, orderBy, limit);
            if (queryCursor != null) {
                return getQueryCursorData();
            } else {
                Log.e("查询" + table + "错误");
            }
        } else {
            Log.e("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param cursorFactory
     * @param distinct      限制重复，如过为true则限制,false则不用管
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return
     */
    public ArrayList<HashMapping<String>> queryWithFactory(
            android.database.sqlite.SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table,
            String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            free();
            this.queryCursor = mSQLiteDatabase.queryWithFactory(cursorFactory,
                    distinct, table, columns, selection, selectionArgs,
                    groupBy, having, orderBy, limit);
            if (queryCursor != null) {
                return getQueryCursorData();
            } else {
                Log.e("查询" + table + "错误");
            }
        } else {
            Log.e("数据库未打开！");
        }
        return null;

    }

    /**
     * INSERT, UPDATE 以及DELETE
     *
     * @param sql      语句
     * @param bindArgs
     */
    public void execute(String sql, String[] bindArgs) {
//        Log.e("准备执行SQL[" + sql + "]语句");
        if (testSQLiteDatabase()) {
            if (sql != null && !sql.equalsIgnoreCase("")) {
                this.queryStr = sql;
                if (bindArgs != null) {
                    mSQLiteDatabase.execSQL(sql, bindArgs);
                } else {
                    mSQLiteDatabase.execSQL(sql);
                }
            }
        } else {
            Log.e("数据库未打开！");
        }

    }

    public Boolean execute(SqlBuilder getSqlBuilder) {
        return execute(getSqlBuilder, null);
    }

    /**
     * 执行INSERT, UPDATE 以及DELETE操作
     *
     * @param getSqlBuilder Sql语句构建器
     * @return
     */
    public Boolean execute(SqlBuilder getSqlBuilder, Object entity) {
        Boolean isSuccess = false;
        String sqlString = null;
        try {
            sqlString = getSqlBuilder.getSqlStatement();
            Log.e("sql:" + sqlString);
            execute(sqlString, null);
            isSuccess = true;
            Log.e("sql:" + sqlString + " 执行成功");
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.e(sqlString);
            isSuccess = false;
            Log.e(e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(sqlString);
            if (e.getMessage().contains("UNIQUE constraint failed") || e.getMessage().contains("is not unique")) {
                Log.e("该主键ID已存在");
                if (entity != null)
                    return update(entity);
            }
            if (e.getMessage().contains("has no column named")) {
                try {
                    Log.w(e.getMessage());
                    String c = e.getMessage().substring(e.getMessage().indexOf("no column named") + 15).trim();
                    String[] c1 = c.split(" ");
                    if (c1.length > 0) {
                        Log.w("该列(" + c1[0].replace(":", "") + ")不存在");
                        isSuccess = addColumn(getSqlBuilder, e.getMessage().substring(e.getMessage().indexOf("table") + 5, e.getMessage().indexOf("has no column")).trim(), c1[0].trim());
                    }
                } catch (Exception e1) {
                    Log.e(e1.getMessage());
                }
            } else {
                isSuccess = false;
                Log.e(e.getMessage());
            }
        }
        return isSuccess;
    }

    public Boolean addColumn(SqlBuilder getSqlBuilder, String tableName, String columnName) {
        Boolean isSuccess = false;
        Log.w("tableName:" + tableName + " columnName:" + columnName.replace(":", ""));
        try {
            mSQLiteDatabase.execSQL("ALTER TABLE '" + tableName + "' ADD  '" + columnName.replace(":", "") + "' varchar");
            String sqlString = getSqlBuilder.getSqlStatement();
            execute(sqlString, null);
            isSuccess = true;
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return isSuccess;
    }

    /**
     * 获得所有的查询数据集中的数据
     *
     * @return
     */
    public MappingArrayList<String> getQueryCursorData() {
        MappingArrayList<String> arrayList = null;
        if (queryCursor != null) {
            try {
                arrayList = new MappingArrayList<String>();
                queryCursor.moveToFirst();
                while (queryCursor.moveToNext()) {
                    arrayList.add(DBUtils.getRowData(queryCursor));
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                Log.e("当前数据集获取失败！");
            }
        } else {
            Log.e("当前数据集不存在！");
        }
        return arrayList;
    }

    /**
     * 取得数据库的表信息
     *
     * @return
     */
    public ArrayList<DBMasterEntity> getTables() {
        ArrayList<DBMasterEntity> tadbMasterArrayList = new ArrayList<DBMasterEntity>();
        String sql = "select * from sqlite_master where type='table' order by name";
        if (testSQLiteDatabase()) {
            if (sql != null && !sql.equalsIgnoreCase("")) {
                this.queryStr = sql;
                free();
                queryCursor = mSQLiteDatabase
                        .rawQuery(
                                "select * from sqlite_master where type='table' order by name",
                                null);

                if (queryCursor != null) {
                    while (queryCursor.moveToNext()) {
                        if (queryCursor != null
                                && queryCursor.getColumnCount() > 0) {
                            DBMasterEntity tadbMasterEntity = new DBMasterEntity();
                            tadbMasterEntity.setType(queryCursor.getString(0));
                            tadbMasterEntity.setName(queryCursor.getString(1));
                            tadbMasterEntity.setTbl_name(queryCursor
                                    .getString(2));
                            tadbMasterEntity.setRootpage(queryCursor.getInt(3));
                            tadbMasterEntity.setSql(queryCursor.getString(4));
                            tadbMasterArrayList.add(tadbMasterEntity);
                        }
                    }
                } else {
                    Log.e("数据库未打开！");
                }
            }
        } else {
            Log.e("数据库未打开！");
        }
        return tadbMasterArrayList;
    }

    /**
     * 判断是否存在某个表,为true则存在，否则不存在
     *
     * @param clazz
     * @return true则存在，否则不存在
     */
    public boolean hasTable(Class<?> clazz) {
        String tableName = DBUtils.getTableName(clazz);
        return hasTable(tableName);
    }

    /**
     * 判断是否存在某个表,为true则存在，否则不存在
     *
     * @param tableName 需要判断的表名
     * @return true则存在，否则不存在
     */
    public boolean hasTable(String tableName) {
        if (tableName != null && !tableName.equalsIgnoreCase("")) {
            if (testSQLiteDatabase()) {
                try {
                    tableName = tableName.trim();
                    String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
                            + tableName + "' ";
                    if (sql != null && !sql.equalsIgnoreCase("")) {
                        this.queryStr = sql;
                    }
                    free();
                    queryCursor = mSQLiteDatabase.rawQuery(sql, null);
                    if (queryCursor.moveToNext()) {
                        int count = queryCursor.getInt(0);
                        if (count > 0) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                }
            } else {
                Log.e("数据库未打开:" + tableName);
            }
        } else {
            Log.e("判断数据表名不能为空！");
        }
        return false;
    }

    /**
     * 创建表
     *
     * @param clazz
     * @return 为true创建成功，为false创建失败
     */
    public Boolean creatTable(Class<?> clazz) {
        Boolean isSuccess = false;
        if (testSQLiteDatabase()) {
            try {
                String sqlString = DBUtils.creatTableSql(clazz);
                execute(sqlString, null);
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
                Log.e(e.getMessage());
            }
        } else {
            Log.e("数据库未打开！");
            return false;
        }
        return isSuccess;
    }

    public Boolean dropTable(Class<?> clazz) {
        String tableName = DBUtils.getTableName(clazz);
        if (hasTable(clazz))
            return dropTable(tableName);
        else
            return true;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return 为true创建成功，为false创建失败
     */
    public Boolean dropTable(String tableName) {
        Boolean isSuccess = false;
        if (tableName != null && !tableName.equalsIgnoreCase("")) {
            if (testSQLiteDatabase()) {
                try {
                    String sqlString = "DROP TABLE " + tableName;
                    execute(sqlString, null);
                    isSuccess = true;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    isSuccess = false;
                    Log.exceptionLog(e);
                }
            } else {
                Log.local("数据库未打开！");
                return false;
            }
        } else {
            Log.local("删除数据表名不能为空！");
        }
        return isSuccess;
    }

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
        Log.e(error);
        return error;
    }

    /**
     * 插入记录
     *
     * @param entity 插入的实体
     * @return
     */
    public Boolean insert(Object entity) {
//        if (!hasTable(entity.getClass())) {
//            creatTable(entity.getClass());
//        }
        return insert(entity, null);
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
            Log.e("数据库未打开！");
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
            Log.e("数据库未打开！");
            return false;
        }
    }

    /**
     * 插入记录
     *
     * @param entity       传入数据实体
     * @param updateFields 插入到的字段,可设置为空
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean insert(Object entity, ArrayListPair updateFields) {
//        if (!hasTable(entity.getClass())) {
//            creatTable(entity.getClass());
//        }
        SqlBuilder sqlBuilderq = SqlBuilderFactory.getInstance().getSqlBuilder(SqlBuilderFactory.INSERT);
        sqlBuilderq.setEntity(entity);
        sqlBuilderq.setUpdateFields(updateFields);
        try {
            Log.d("插入:" + sqlBuilderq.getSqlStatement());
        } catch (DBException e) {
            Log.e(e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(e.getMessage());
        }
//        return true;
        return execute(sqlBuilderq, entity);
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
            Log.e("数据库未打开！");
            return false;
        }
    }

    /**
     * 删除记录
     *
     * @param clazz
     * @param where where语句
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(Class<?> clazz, String where, String[] bindArgs) {
        if (!hasTable(clazz)) {
            creatTable(clazz);
        }
        if (testSQLiteDatabase()) {
            SqlBuilder getSqlBuilder = SqlBuilderFactory.getInstance()
                    .getSqlBuilder(SqlBuilderFactory.DELETE);
            getSqlBuilder.setClazz(clazz);
            getSqlBuilder.setCondition(false, where, null, null, null, -1);

            String sql = null;
            try {
                sql = getSqlBuilder.getSqlStatement();
                Log.d("deletesql: " + sql);
                execute(sql, bindArgs);
            } catch (DBException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * 删除记录
     *
     * @param entity
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(Object entity) {
        if (!hasTable(entity.getClass())) {
            creatTable(entity.getClass());
        }
        if (testSQLiteDatabase()) {
            SqlBuilder getSqlBuilder = SqlBuilderFactory.getInstance()
                    .getSqlBuilder(SqlBuilderFactory.DELETE);
            getSqlBuilder.setEntity(entity);
            try {
                Log.d("删除:" + getSqlBuilder.getSqlStatement());
            } catch (DBException e) {
                Log.e(e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(e.getMessage());
            }
            return execute(getSqlBuilder);
        } else {
            return false;
        }

    }

    public Boolean needUpgrade(int version) {
        if (testSQLiteDatabase())
            return mSQLiteDatabase.needUpgrade(version);
        return false;
    }

    /**
     * 更新记录
     *
     * @param table       表名字
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean update(String table, ContentValues values,
                          String whereClause, String[] whereArgs) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase
                    .update(table, values, whereClause, whereArgs) > 0;
        } else {
            Log.e("数据库未打开！");
            return false;
        }
    }

    /**
     * 更新记录 这种更新方式只有才主键不是自增的情况下可用
     *
     * @param entity 更新的数据
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean update(Object entity) {
        if (!hasTable(entity.getClass())) {
            creatTable(entity.getClass());
        }
        return update(entity, null);
    }

    /**
     * 更新记录
     *
     * @param entity 更新的数据
     * @param where  where语句
     * @return
     */
    public Boolean update(Object entity, String where) {
        if (!hasTable(entity.getClass())) {
            creatTable(entity.getClass());
        }
        if (testSQLiteDatabase()) {
            SqlBuilder getSqlBuilder = SqlBuilderFactory.getInstance()
                    .getSqlBuilder(SqlBuilderFactory.UPDATE);
            getSqlBuilder.setEntity(entity);
            getSqlBuilder.setCondition(false, where, null, null, null, -1);
            try {
                Log.d("更新:" + getSqlBuilder.getSqlStatement());
            } catch (DBException e) {
                Log.e(e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(e.getMessage());
            }
            return execute(getSqlBuilder);
        } else {
            return false;
        }

    }

    /**
     * 获取最近一次查询的sql语句
     *
     * @return sql 语句
     */
    public String getLastSql() {
        return queryStr;
    }

    /**
     * 获得当前查询数据集合
     *
     * @return
     */
    public Cursor getQueryCursor() {
        return queryCursor;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mSQLiteDatabase.close();
    }

    /**
     * 释放查询结果
     */
    public void free() {
        if (queryCursor != null) {
            try {
                this.queryCursor.close();
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }

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
