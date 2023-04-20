package com.mapping.db.entity;


/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 18:05.
 */

public class PKProperyEntity extends PropertyEntity {

    public PKProperyEntity() {

    }

    public PKProperyEntity(String name, Class<?> type, Object defaultValue,
                           boolean primaryKey, boolean isAllowNull, boolean autoIncrement,
                           String columnName) {
        super(name, type, defaultValue, primaryKey, isAllowNull, autoIncrement,
                columnName);
    }

}