package com.mapping.db.bean;

import java.io.Serializable;
import java.util.Objects;

public class NameValuePairDB implements Serializable {
    public NameValuePairDB(String key, String value, Class<?> clazz) {
        this.name = key;
        this.value = value;
        this.type = clazz;
    }

    private String name;
    private String value;
    private Class<?> type = String.class; // 数据类型



    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameValuePairDB that = (NameValuePairDB) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
