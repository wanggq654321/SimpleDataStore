package com.mapping.db.entity;


/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:47.
 */

public class DBMasterEntity extends BaseEntity {
    private static final long serialVersionUID = 4511697615195446516L;
    private String type;
    private String name;
    private String tbl_name;
    private String sql;
    private int rootpage;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTbl_name() {
        return tbl_name;
    }

    public void setTbl_name(String tbl_name) {
        this.tbl_name = tbl_name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getRootpage() {
        return rootpage;
    }

    public void setRootpage(int rootpage) {
        this.rootpage = rootpage;
    }

}
