package com.mapping.db.entity;
/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 18:06.
 */


import java.util.ArrayList;
import java.util.List;

public class TableInfoEntity extends BaseEntity {
    private static long serialVersionUID = 488168612576359150L;
    private String tableName = "";
    private String className = "";
    private PKProperyEntity pkProperyEntity = null;

    ArrayList<PropertyEntity> propertieArrayList = new ArrayList<PropertyEntity>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<PropertyEntity> getPropertieArrayList() {
        return propertieArrayList;
    }

    public void setPropertieArrayList(List<PropertyEntity> propertyList) {
        this.propertieArrayList = (ArrayList<PropertyEntity>) propertyList;
    }

    public PKProperyEntity getPkProperyEntity() {
        return pkProperyEntity;
    }

    public void setPkProperyEntity(PKProperyEntity pkProperyEntity) {
        this.pkProperyEntity = pkProperyEntity;
    }



}