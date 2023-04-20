package com.mapping.db.builder;
/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 18:00.
 */

import com.mapping.db.bean.ArrayListPair;
import com.mapping.db.exception.DBException;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.DataTypeUtils;

import java.lang.reflect.Field;

public class DeleteSqlBuilder extends SqlBuilder {
    @Override
    public String buildSql() throws DBException, IllegalArgumentException,
            IllegalAccessException {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder(256);
        stringBuilder.append("DELETE FROM ");
        stringBuilder.append(tableName);
        if (entity == null) {
            stringBuilder.append(buildConditionString());
        } else {
            // stringBuilder.append(buildWhere(buildWhere(this.entity)));
        }
        return stringBuilder.toString();
    }



//    /**
//     * 创建Where语句
//     *
//     * @param entity
//     * @return
//     * @throws IllegalArgumentException
//     * @throws IllegalAccessException
//     * @throws DBException
//     */
//    public ArrayListPair buildWhere(Object entity)
//            throws IllegalArgumentException, IllegalAccessException,
//            DBException {
//        Class<?> clazz = entity.getClass();
//        ArrayListPair whereArrayList = new ArrayListPair();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            if (!DBUtils.isTransient(field)) {
//                if (DataTypeUtils.isBaseDateType(field)) {
//                    // 如果ID不是自动增加的
//                    if (!DBUtils.isAutoIncrement(field)) {
//                        String columnName = DBUtils.getColumnByField(field);
//                        if (null != field.get(entity)
//                                && field.get(entity).toString().length() > 0) {
//                            whereArrayList.add(
//                                    (columnName != null && !columnName
//                                            .equals("")) ? columnName : field
//                                            .getName(), field.get(entity)
//                                            .toString());
//                        }
//                    }
//                }
//            }
//        }
//        if (whereArrayList.isEmpty()) {
//            throw new DBException("不能创建Where条件，语句");
//        }
//        return whereArrayList;
//    }

}
