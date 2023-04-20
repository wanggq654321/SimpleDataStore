/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.mappingdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import cn.mappingdb.bean.Property;
import cn.mappingdb.cache.EntifyInfoCacheFactory;
import cn.mappingdb.database.DBTool;
import cn.mappingdb.internal.EntifyConfig;
import cn.mappingdb.internal.SqlUtils;
import cn.mappingdb.log.DaoLog;
import cn.mappingdb.query.DeleteQuery;
import cn.mappingdb.query.Query;
import cn.mappingdb.query.QueryBuilder;
import cn.mappingdb.utils.DBUtils;
import cn.mappingdb.utils.DataTypeUtils;
import cn.mappingdb.utils.StringUtils;

/**
 * Base class for all DAOs: Implements entity operations like insert, load, delete, and query.
 * <p>
 * This class is thread-safe.
 */
/*
 * When operating on TX, statements, or identity scope the following locking order must be met to avoid deadlocks:
 *
 * 1.) If not inside a TX already, begin a TX to acquire a DB connection (connection is to be handled like a lock)
 *
 * 2.) The DatabaseStatement
 *
 * 3.) identityScope
 */
public class AbstractDao {

    protected SQLiteDatabase db;

    public AbstractDao() {
        // todo nothing
    }

    private QueryBuilder queryBuilder(Class<?> table) {
        return QueryBuilder.initCreate(table);
    }

    /**
     * Loads all available entities from the database.
     */
    public <T> List<T> queryBuild(QueryBuilder builder) {
        SQLiteDatabase db = DBTool.getInstance().getDb().openWritable(null);
        Query query = builder.build();
        DaoLog.d(" getSql: " + query.getSql() + " getParameters: " + query.getParameters());
        Cursor cursor = db.rawQuery(query.getSql(), query.getParameters());
        DaoLog.d(" cursor.getCount : " + cursor.getCount());
        List<T> list = setValue(builder.getTable(), cursor);
        db.close();
        return list;
    }

    /**
     * 自定义条件查询
     *
     * @param table
     * @param where
     * @param selectionArg
     * @param <T>
     * @return
     */
    public <T> List<T> queryRaw(Class<?> table, String where, String... selectionArg) {
        SQLiteDatabase db = DBTool.getInstance().getDb().openWritable(null);
        EntifyConfig config = EntifyInfoCacheFactory.getInstance().getTableInfoEntity(table.getClass());

        String sql = SqlUtils.createSqlSelect(config.tablename, "T", config.allColumns, false);
        SQLiteStatement statement = db.compileStatement(sql);

        Cursor cursor = db.rawQuery(sql + (StringUtils.isEmpty(where) ? "" : where), selectionArg);
        List<T> list = setValue(table, cursor);

        statement.close();
        db.close();
        return list;
    }

    public long insert(Object entify) {
        SQLiteDatabase db = DBTool.getInstance().getDb().openWritable(null);
        EntifyConfig config = EntifyInfoCacheFactory.getInstance().getTableInfoEntity(entify.getClass());
        String sql = SqlUtils.createSqlInsert("INSERT INTO ", config.tablename, config.allColumns);
        DaoLog.d(" getSql: " + sql);
        SQLiteStatement statement = db.compileStatement(sql);
        bindValues(config, entify, statement);
        db.beginTransaction();
        long rowId = statement.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();

        statement.close();
        db.close();
        return rowId;
    }


    /**
     * @return
     */
    public void deleteBuild(QueryBuilder builder) {
        SQLiteDatabase db = DBTool.getInstance().getDb().openWritable(null);
        DeleteQuery deleteQuery = builder.buildDelete();

        db.execSQL(deleteQuery.getSql(), deleteQuery.getParameters());

        db.close();
    }


    /***
     * 绑定 赋值
     * @param config
     * @param entify
     * @param statement
     */
    private void bindValues(EntifyConfig config, Object entify, SQLiteStatement statement) {
        for (Property property : config.properties) {
            Field field = property.field;
            // String columnName = property.columnName;
            Object value = null;
            try {
                value = field.get(entify);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            DBUtils.bindObjectToProgram(statement, property.ordinal, value);
        }
    }

    public <T> List<T> setValue(Class<?> table, Cursor cursor) {
        EntifyConfig config = EntifyInfoCacheFactory.getInstance().getTableInfoEntity(table);
        List<T> queryList = new java.util.ArrayList<T>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    T entityT = (T) table.newInstance();
                    Property[] properties = config.properties;
                    for (Property propertyEntity : properties) {
                        Field field = propertyEntity.field;
                        String columnName = propertyEntity.columnName;
                        int columnIndex = cursor.getColumnIndexOrThrow((columnName != null && !columnName.equals("")) ? columnName : field.getName());
                        Class<?> clazz = field.getType();
                        if (DataTypeUtils.isString(clazz)) {
                            field.set(entityT, cursor.getString(columnIndex));
                        } else if (DataTypeUtils.isInteger(clazz)) {
                            field.set(entityT, cursor.getInt(columnIndex));
                        } else if (DataTypeUtils.isFloat(clazz)) {
                            field.set(entityT, cursor.getFloat(columnIndex));
                        } else if (DataTypeUtils.isDouble(clazz)) {
                            field.set(entityT, cursor.getDouble(columnIndex));
                        } else if (DataTypeUtils.isShort(clazz)) {
                            field.set(entityT, cursor.getShort(columnIndex));
                        } else if (DataTypeUtils.isLong(clazz)) {
                            field.set(entityT, cursor.getLong(columnIndex));
                        } else if (DataTypeUtils.isByte(clazz)) {
                            field.set(entityT, cursor.getBlob(columnIndex));
                        } else if (DataTypeUtils.isBoolean(clazz)) {
                            Boolean testBoolean = new Boolean(cursor.getString(columnIndex));
                            field.set(entityT, testBoolean);
                        } else if (DataTypeUtils.isDate(clazz)) {
                            @SuppressWarnings("deprecation")
                            Date date = new Date(cursor.getString(columnIndex));
                            field.set(entityT, date);
                        } else if (DataTypeUtils.isCharacter(clazz)) {
                            Character c1 = cursor.getString(columnIndex).trim()
                                    .toCharArray()[0];
                            field.set(entityT, c1);
                        }
                    }
                    queryList.add(entityT);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            DaoLog.e(e.toString());
        } finally {
            cursor.close();
        }
        return queryList;
    }


}
