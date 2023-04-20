package com.mapping.db.exception;

/**
 * 类描述
 * 创建人 wanggq
 * 创建时间 2015/6/16 17:50.
 */


public class DBException extends Exception {
    private static final long serialVersionUID = 1L;

    public DBException() {
        super();
    }

    public DBException(String detailMessage) {
        super(detailMessage);
    }

}