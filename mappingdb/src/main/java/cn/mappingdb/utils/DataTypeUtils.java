package cn.mappingdb.utils;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

/**
 * 校验 java 基本数据类型
 * <p>
 * sqlite 有如下类型亲和进行选择：
 * *
 * * TEXT
 * *
 * * NUMBERIC
 * *
 * * INTERGER
 * *
 * * REAL
 * *
 * * BLOB
 */
public class DataTypeUtils {

    /**
     * 拼接sql加 '' 号的判定
     *
     * @param clazz
     * @return
     */
    public static boolean isText(Class<?> clazz) {
        boolean isNum = isShort(clazz) || isInteger(clazz) || isLong(clazz) || isFloat(clazz) || isDouble(clazz) || isByte(clazz);
        return !isNum;
    }

    /**
     * 拼接sql加 '' 号的判定
     *
     * @param clazz
     * @return
     */
    public static boolean isNum(Class<?> clazz) {
        boolean isNum = isShort(clazz) || isInteger(clazz) || isLong(clazz) || isFloat(clazz) || isDouble(clazz) || isByte(clazz);
        return isNum;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isString(Class<?> clazz) {
        boolean isSure = false;
        isSure = clazz.equals(String.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isInteger(Class<?> clazz) {
        boolean isSure = clazz.equals(Integer.class) || clazz.equals(int.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isFloat(Class<?> clazz) {
        boolean isSure = clazz.equals(Float.class) || clazz.equals(float.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isDouble(Class<?> clazz) {
        boolean isSure = clazz.equals(Double.class) || clazz.equals(double.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isShort(Class<?> clazz) {
        boolean isSure = clazz.equals(Short.class) || clazz.equals(short.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isLong(Class<?> clazz) {
        boolean isSure = clazz.equals(Long.class) || clazz.equals(long.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isByte(Class<?> clazz) {
        boolean isSure = clazz.equals(Byte.class) || clazz.equals(byte.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isBoolean(Class<?> clazz) {
        boolean isSure = clazz.equals(Boolean.class) || clazz.equals(boolean.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isDate(Class<?> clazz) {
        boolean isSure = clazz.equals(Date.class);
        return isSure;
    }

    /**
     * @param clazz 数据类型
     * @return
     */
    public static boolean isCharacter(Class<?> clazz) {
        boolean isSure = clazz.equals(Character.class);
        return isSure;
    }

    /**
     * 是否为基本的数据类型
     *
     * @param field
     * @return
     */
    public static boolean isBaseDateType(Field field) {
        Class<?> clazz = field.getType();
        return clazz.equals(String.class) || clazz.equals(Integer.class)
                || clazz.equals(Byte.class) || clazz.equals(Long.class)
                || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.equals(Date.class)
                || clazz.equals(java.sql.Date.class) || clazz.isPrimitive();
    }


    public static int getTypeOfObject(Object obj) {
        if (obj == null) {
            return Cursor.FIELD_TYPE_NULL;
        } else if (obj instanceof byte[]) {
            return Cursor.FIELD_TYPE_BLOB;
        } else if (obj instanceof Float || obj instanceof Double) {
            return Cursor.FIELD_TYPE_FLOAT;
        } else if (obj instanceof Long || obj instanceof Integer
                || obj instanceof Short || obj instanceof Byte) {
            return Cursor.FIELD_TYPE_INTEGER;
        } else {
            return Cursor.FIELD_TYPE_STRING;
        }
    }


    /**
     * propertyToDbType.put(PropertyType.Boolean, "INTEGER");
     * propertyToDbType.put(PropertyType.Byte, "INTEGER");
     * propertyToDbType.put(PropertyType.Short, "INTEGER");
     * propertyToDbType.put(PropertyType.Int, "INTEGER");
     * propertyToDbType.put(PropertyType.Long, "INTEGER");
     * propertyToDbType.put(PropertyType.Date, "INTEGER");
     * <p>
     * propertyToDbType.put(PropertyType.Float, "REAL");
     * propertyToDbType.put(PropertyType.Double, "REAL");
     * <p>
     * propertyToDbType.put(PropertyType.ByteArray, "BLOB");
     * <p>
     * propertyToDbType.put(PropertyType.String, "TEXT");
     *
     * @param type
     * @return
     */
    public static String getDbType(Class<?> type) {
        if (isBoolean(type) || isByte(type) || isShort(type) || isInteger(type) || isLong(type)) {
            return "INTEGER";
        } else if (isFloat(type) || isDouble(type)) {
            return "REAL";
        } else if (isCharacter(type)) {
            return "BLOB";
        } else {
            return "TEXT";
        }
    }

}
