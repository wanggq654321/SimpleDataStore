package com.mapping.db.cache;

import java.util.Objects;

class CacheWeakKey {
    private String name;

    public CacheWeakKey(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        return (obj instanceof CacheWeakKey) && Objects.equals(((CacheWeakKey) obj).name, this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
