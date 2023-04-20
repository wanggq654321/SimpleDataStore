package com.mapping.db.builder;
/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 18:04.
 */


import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.bean.ArrayListPair;
import com.mapping.db.bean.NameValuePairDB;
import com.mapping.db.exception.DBException;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.DataTypeUtils;
import com.mapping.db.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class UpdateSqlBuilder extends SqlBuilder {

    @Override
    public void onPreGetStatement() throws DBException,
            IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        if (getUpdateFields() == null) {
            setUpdateFields(getFieldsAndValue(entity));
        }
        super.onPreGetStatement();
    }

    @Override
    public String buildSql() throws DBException, IllegalArgumentException,
            IllegalAccessException {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder(256);
        stringBuilder.append("UPDATE ");
        stringBuilder.append(tableName).append(" SET ");

        ArrayListPair needUpdate = getUpdateFields();
        for (int i = 0; i < needUpdate.size(); i++) {
            NameValuePairDB nameValuePair = needUpdate.get(i);
            String value = nameValuePair.getValue().toString();
            value = !"".equals(value) ? value.replace("'", "''") : "";
            String vv = "";
            if (!StringUtils.isEmptyNull(value)) {
                vv = (String.class.equals(nameValuePair.getType())) ? "'" + value + "'" : value;
            }
            stringBuilder.append(nameValuePair.getName()).append(" = ").append(vv);
            if (i + 1 < needUpdate.size()) {
                stringBuilder.append(", ");
            }
        }
        if (!StringUtils.isEmpty(this.where)) {
            stringBuilder.append(buildConditionString());
        } else {
            stringBuilder.append(buildWhere(buildWhere(this.entity)));
        }
        return stringBuilder.toString();
    }

    /**
     * 创建Where语句
     *
     * @param  entity
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws DBException
     */
    public ArrayListPair buildWhere(Object entity)
            throws IllegalArgumentException, IllegalAccessException,
            DBException {
        Class<?> clazz = entity.getClass();
        ArrayListPair whereArrayList = new ArrayListPair();
        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            if (!DBUtils.isTransient(field)) {
//                if (DBUtils.isBaseDateType(field)) {
//                    Annotation annotation = field.getAnnotation(PrimaryKey.class);
//                    if (annotation != null) {
//                        String columnName = DBUtils.getColumnByField(field);
//                        whereArrayList.add((columnName != null && !columnName
//                                        .equals("")) ? columnName : field.getName(),
//                                field.get(entity).toString());
//                    }
//                }
//            }
//        }
        if (whereArrayList.isEmpty()) {
            throw new DBException("不能创建Where条件，语句");
        }
        return whereArrayList;
    }



//    /**
//     * 从实体加载,更新的数据
//     *
//     * @return
//     * @throws DBException
//     * @throws IllegalArgumentException
//     * @throws IllegalAccessException
//     */
//    public static ArrayListPair getFieldsAndValue(Object entity) throws DBException, IllegalArgumentException,
//            IllegalAccessException {
//        // TODO Auto-generated method stub
//        ArrayListPair arrayList = new ArrayListPair();
//        if (entity == null) {
//            throw new DBException("没有加载实体类！");
//        }
//        Class<?> clazz = entity.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//
//            if (!DBUtils.isTransient(field)) {
//                if (DataTypeUtils.isBaseDateType(field)) {
//                    PrimaryKey annotation = field.getAnnotation(PrimaryKey.class);
//                    if (annotation == null || !annotation.autoIncrement()) {
//                        String columnName = DBUtils.getColumnByField(field);
//                        field.setAccessible(true);
////                        arrayList.add((columnName != null && !columnName
////                                                .equals("")) ? columnName : field.getName(),
////                                        field.get(entity) == null ? null : field.get(entity).toString());
//                    }
//                }
//            }
//        }
//        return arrayList;
//    }

}

