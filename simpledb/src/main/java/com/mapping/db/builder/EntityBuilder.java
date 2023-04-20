package com.mapping.db.builder;
/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 17:51.
 */

import android.database.Cursor;


import com.mapping.db.cache.TableInfoCacheFactory;
import com.mapping.db.database.DBTool;
import com.mapping.db.entity.PropertyEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.log.Log;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.DataTypeUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntityBuilder implements Serializable {

    private static final long serialVersionUID = 1905122041914789207L;

    /**
     * 通过Cursor获取一个实体数组
     *
     * @param clazz  实体类型
     * @param cursor 数据集合
     * @return 相应实体List数组
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> buildQueryList(Class<T> clazz, Cursor cursor) {
        List<T> queryList = new java.util.ArrayList<T>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    queryList.add((T) buildQueryOneEntity(clazz, cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(e.toString());
        } finally {
            cursor.close();
        }
        return queryList;
    }


    /**
     * 通过Cursor获取一个实体
     *
     * @param clazz  实体类型
     * @param cursor 数据集合
     * @return 相应实体
     */
    @SuppressWarnings("unchecked")
    public static <T> T buildQueryOneEntity(Class<?> clazz, Cursor cursor) {
        T entityT = null;
        try {
            TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(clazz);
            ArrayList<PropertyEntity> arrayList = tableInfoEntity.getPropertieArrayList();
            entityT = (T) clazz.newInstance();
            for (PropertyEntity propertyEntity : arrayList) {
                Field field = propertyEntity.getField();
                if (!DBUtils.isTransient(field)) {
                    if (DataTypeUtils.isBaseDateType(field)) {
                        setValue(field, propertyEntity.getColumnName(), entityT, cursor);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return entityT;
    }

    /**
     * 有如下类型亲和进行选择：
     * <p>
     * TEXT
     * <p>
     * NUMBERIC
     * <p>
     * INTERGER
     * <p>
     * REAL
     * <p>
     * BLOB
     * <p>
     * 设置值到字段
     *
     * @param field      需要设置的字段
     * @param columnName 数据库字段名
     * @param entityT    实体模版
     * @param cursor     数据集合
     */
    private static <T> void setValue(Field field, String columnName, T entityT,
                                     Cursor cursor) {
        try {
            setEntifyCourse(field, columnName, entityT, cursor);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            String sql = "ALTER TABLE '" + DBUtils.getTableName(entityT.getClass()) + "' ADD COLUMN '" +
                    field.getName() + "' '" + field.getType().getName().substring(field.getType().getName().lastIndexOf(".") + 1) + "'";
            Log.w(sql);
            DBTool.getInstance().getDb().execute(sql, null);
            try {
                setEntifyCourse(field, columnName, entityT, cursor);
            } catch (IllegalArgumentException e1) {
                Log.e(e1.toString());
            } catch (IllegalAccessException e1) {
                Log.e(e1.toString());
            }
        } catch (IllegalAccessException e) {
            Log.e(e.getMessage());
        }
    }

    /**
     * 对象映射赋值
     *
     * @param field
     * @param columnName
     * @param entityT
     * @param cursor
     * @throws IllegalAccessException
     */
    public static void setEntifyCourse(Field field, String columnName, Object entityT,
                                       Cursor cursor) throws IllegalAccessException {
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


//    /**
//     * 通过Cursor获取一个实体
//     *
//     * @param clazz  实体类型
//     * @param cursor 数据集合
//     * @return 相应实体
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T buildQueryOneEntity(Class<?> clazz, Cursor cursor) {
//        Field[] fields = clazz.getDeclaredFields();
//        T entityT = null;
//        try {
//            entityT = (T) clazz.newInstance();
//            for (Field field : fields) {
//                field.setAccessible(true);
//                if (!DBUtils.isTransient(field)) {
//                    if (DBUtils.isBaseDateType(field)) {
//
//                        String columnName = DBUtils.getColumnByField(field);
//                        field.setAccessible(true);
//                        setValue(field, columnName, entityT, cursor);
//
//                    }
//                }
//
//            }
//        } catch (InstantiationException e) {
//            Log.e(e.getMessage());
//        } catch (IllegalAccessException e) {
//            Log.e(e.getMessage());
//        }
//        return entityT;
//    }
//
//    /**
//     * 设置值到字段
//     *
//     * @param field      需要设置的字段
//     * @param columnName 数据库字段名
//     * @param entityT    实体模版
//     * @param cursor     数据集合
//     */
//    private static <T> void setValue(Field field, String columnName, T entityT,
//                                     Cursor cursor) {
//        try {
//            int columnIndex = cursor
//                    .getColumnIndexOrThrow((columnName != null && !columnName
//                            .equals("")) ? columnName : field.getName());
//            Class<?> clazz = field.getType();
//            if (clazz.equals(String.class)) {
//                field.set(entityT, cursor.getString(columnIndex));
//            } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
//                field.set(entityT, cursor.getInt(columnIndex));
//            } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
//                field.set(entityT, cursor.getFloat(columnIndex));
//            } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
//                field.set(entityT, cursor.getDouble(columnIndex));
//            } else if (clazz.equals(Short.class) || clazz.equals(Short.class)) {
//                field.set(entityT, cursor.getShort(columnIndex));
//            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
//                field.set(entityT, cursor.getLong(columnIndex));
//            } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
//                field.set(entityT, cursor.getBlob(columnIndex));
//            } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
//                Boolean testBoolean = new Boolean(cursor.getString(columnIndex));
//                field.set(entityT, testBoolean);
//            } else if (clazz.equals(Date.class)) {
//                @SuppressWarnings("deprecation")
//                Date date = new Date(cursor.getString(columnIndex));
//                field.set(entityT, date);
//            } else if (clazz.equals(Character.class)
//                    || clazz.equals(char.class)) {
//                Character c1 = cursor.getString(columnIndex).trim()
//                        .toCharArray()[0];
//                field.set(entityT, c1);
//            }
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            String sql = "ALTER TABLE '" + DBUtils.getTableName(entityT.getClass()) + "' ADD COLUMN '" +
//                    field.getName() + "' '" + field.getType().getName().substring(field.getType().getName().lastIndexOf(".") + 1) + "'";
//            Log.w(sql);
//            DBTool.getInstance().getDb().execute(sql, null);
//            try {
//                int columnIndex = cursor
//                        .getColumnIndexOrThrow((columnName != null && !columnName
//                                .equals("")) ? columnName : field.getName());
//                Class<?> clazz = field.getType();
//                if (clazz.equals(String.class)) {
//                    field.set(entityT, cursor.getString(columnIndex));
//                } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
//                    field.set(entityT, cursor.getInt(columnIndex));
//                } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
//                    field.set(entityT, cursor.getFloat(columnIndex));
//                } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
//                    field.set(entityT, cursor.getDouble(columnIndex));
//                } else if (clazz.equals(Short.class) || clazz.equals(Short.class)) {
//                    field.set(entityT, cursor.getShort(columnIndex));
//                } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
//                    field.set(entityT, cursor.getLong(columnIndex));
//                } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
//                    field.set(entityT, cursor.getBlob(columnIndex));
//                } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
//                    Boolean testBoolean = new Boolean(cursor.getString(columnIndex));
//                    field.set(entityT, testBoolean);
//                } else if (clazz.equals(Date.class)) {
//                    @SuppressWarnings("deprecation")
//                    Date date = new Date(cursor.getString(columnIndex));
//                    field.set(entityT, date);
//                } else if (clazz.equals(Character.class)
//                        || clazz.equals(char.class)) {
//                    Character c1 = cursor.getString(columnIndex).trim()
//                            .toCharArray()[0];
//                    field.set(entityT, c1);
//                }
//            } catch (IllegalArgumentException e1) {
//                Log.e(e1.toString());
//            } catch (IllegalAccessException e1) {
//                Log.e(e1.toString());
//            }
//        } catch (IllegalAccessException e) {
//            Log.e(e.getMessage());
//        }
//
//    }
}
