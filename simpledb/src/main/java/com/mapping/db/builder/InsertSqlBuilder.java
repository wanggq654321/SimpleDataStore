package com.mapping.db.builder;
/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 18:01.
 */

import android.os.Build;

import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.bean.ArrayListPair;
import com.mapping.db.bean.NameValuePairDB;
import com.mapping.db.cache.TableInfoCacheFactory;
import com.mapping.db.entity.PropertyEntity;
import com.mapping.db.entity.TableInfoEntity;
import com.mapping.db.exception.DBException;
import com.mapping.db.utils.DBUtils;
import com.mapping.db.utils.DataTypeUtils;
import com.mapping.db.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class InsertSqlBuilder extends SqlBuilder {

    @Override
    public void onPreGetStatement() throws DBException, IllegalArgumentException, IllegalAccessException {
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
        StringBuilder columns = new StringBuilder(256);
        StringBuilder values = new StringBuilder(256);
        columns.append("INSERT INTO ");
        columns.append(tableName).append(" (");
        values.append("(");
        ArrayListPair updateFields = getUpdateFields();
        if (updateFields != null) {
            for (int i = 0; i < updateFields.size(); i++) {
                NameValuePairDB nameValuePair = updateFields.get(i);
                columns.append(nameValuePair.getName());
                String value = nameValuePair.getValue();
                value = !"".equals(value) ? value.replace("'", "''") : "";
                if (!StringUtils.isEmptyNull(value)) {
                    values.append(DataTypeUtils.isText(nameValuePair.getType()) ? "'" + value + "'" : value);
                }
                if (i + 1 < updateFields.size()) {
                    columns.append(", ");
                    values.append(", ");
                }
            }
        } else {
            throw new DBException("插入数据有误！");
        }
        columns.append(") values ");
        values.append(")");
        columns.append(values);
        return columns.toString();
    }

//    /**
//     * 从实体加载,更新的数据
//     *
//     * @return
//     * @throws DBException
//     * @throws IllegalArgumentException
//     * @throws IllegalAccessException
//     */
//    public ArrayListPair getFieldsAndValue(Object entity)
//            throws DBException, IllegalArgumentException, IllegalAccessException {
//        // TODO Auto-generated method stub
//        ArrayListPair arrayList = new ArrayListPair();
//        if (entity == null) {
//            throw new DBException("没有加载实体类！");
//        }
//        TableInfoEntity tableInfoEntity = TableInfoCacheFactory.getInstance().getTableInfoEntity(entity.getClass());
//        setTableName(tableInfoEntity.getTableName());
//        ArrayList<PropertyEntity> propertyEntityArrayList = tableInfoEntity.getPropertieArrayList();
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            propertyEntityArrayList.forEach(propertyEntity -> {
//                if (propertyEntity.isPrimaryKey()) {
//                    // TODO NOTHING
//                } else {
//                    Field field = propertyEntity.getField();
//                    String columnName = propertyEntity.getColumnName();
//                    Object value = null;
//                    try {
//                        value = field.get(entity);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                    arrayList.add(StringUtils.isEmptyNull(columnName) ? propertyEntity.getName() : columnName,
//                            value == null ? null : value.toString(), propertyEntity.getType());
//                }
//            });
//        } else {
//            for (PropertyEntity propertyEntity : propertyEntityArrayList) {
//                if (propertyEntity.isPrimaryKey()) {
//                    // TODO NOTHING
//                } else {
//                    Field field = propertyEntity.getField();
//                    String columnName = propertyEntity.getColumnName();
//                    Object value = field.get(entity);
//                    arrayList.add(StringUtils.isEmptyNull(columnName) ? propertyEntity.getName() : columnName,
//                            value == null ? null : value.toString(), propertyEntity.getType());
//                }
//            }
//        }
//        return arrayList;
//    }


}
