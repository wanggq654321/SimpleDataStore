package com.mapping.db.bean;


/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:48.
 */


public class MappingArrayList<T extends Object> extends java.util.ArrayList<HashMapping<T>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(HashMapping<T> taHashMap) {
        if (taHashMap != null) {
            return super.add(taHashMap);
        } else {
            return false;
        }
    }
}
