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
package cn.mappingdb.internal;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.mappingdb.bean.Property;
import cn.mappingdb.exception.DaoException;
import cn.mappingdb.utils.DataTypeUtils;
import cn.mappingdb.utils.ReflectUtils;

/**
 * 数据表缓存 表信息
 * Internal class used by greenDAO. DaoConfig stores essential data for DAOs, and is hold by AbstractDaoMaster. This
 * class will retrieve the required information from the DAO classes.
 */
public final class EntifyConfig implements Cloneable, Serializable {

    public final String tablename;
    public final Property[] properties;
    // 所有字段 包含主键
    public String[] allColumns;
    // 主键
    public final String[] pkColumns;
    // 除了主键外，其他键
    public final String[] nonPkColumns;
    public final HashMap<String, Property> propertyHashMap = new HashMap<>();

    /**
     * Single property PK or null if there's no PK or a multi property PK.
     */
    public final Property pkProperty;
    public final boolean keyIsNumeric;


    public EntifyConfig(Class<?> tableClass) {
        try {
            this.tablename = ReflectUtils.getTableName(tableClass);
            Property[] properties = reflectProperties(tableClass);

            this.properties = properties;
            allColumns = new String[properties.length];

            List<String> pkColumnList = new ArrayList<>();
            List<String> nonPkColumnList = new ArrayList<>();
            Property lastPkProperty = null;
            for (int i = 0; i < properties.length; i++) {
                Property property = properties[i];
                propertyHashMap.put(property.name, property);
                String name = property.columnName;
                allColumns[i] = name;
                property.ordinal = (i + 1);
                if (property.primaryKey) {
                    pkColumnList.add(name);
                    lastPkProperty = property;
                } else {
                    nonPkColumnList.add(name);
                }
            }
            String[] nonPkColumnsArray = new String[nonPkColumnList.size()];
            nonPkColumns = nonPkColumnList.toArray(nonPkColumnsArray);
            String[] pkColumnsArray = new String[pkColumnList.size()];
            pkColumns = pkColumnList.toArray(pkColumnsArray);

            pkProperty = pkColumns.length == 1 ? lastPkProperty : null;

            if (pkProperty != null) {
                Class<?> type = pkProperty.type;
                keyIsNumeric = DataTypeUtils.isNum(type);
            } else {
                keyIsNumeric = false;
            }

        } catch (Exception e) {
            throw new DaoException("Could not init DAOConfig", e);
        }
    }

    private static Property[] reflectProperties(Class<?> daoClass)
            throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        Field[] fields = daoClass.getDeclaredFields();
        ArrayList<Property> propertyList = new ArrayList<Property>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (ReflectUtils.isTransient(field)) {
                // todo noithing
            } else {
                String columnName = ReflectUtils.getColumnByField(field);
                String name = field.getName();
                Class<?> type = field.getType();
                boolean primaryKey = ReflectUtils.isPk(field);
                Property property = new Property(0, type, name, primaryKey, columnName);
                property.field = field;
                propertyList.add(property);
            }
        }

        Property[] properties = new Property[propertyList.size()];
        for (int i = 0; i < propertyList.size(); i++) {
            Property property = propertyList.get(i);
            properties[i] = property;
        }
        return properties;
    }

    /**
     * Does not copy identity scope.
     */
    public EntifyConfig(EntifyConfig source) {
        tablename = source.tablename;
        properties = source.properties;
        allColumns = source.allColumns;
        pkColumns = source.pkColumns;
        nonPkColumns = source.nonPkColumns;
        pkProperty = source.pkProperty;
        keyIsNumeric = source.keyIsNumeric;
    }

    /**
     * Does not copy identity scope.
     */
    @Override
    public EntifyConfig clone() {
        return new EntifyConfig(this);
    }


}
