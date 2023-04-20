package com.mapping.db.bean;

import android.text.TextUtils;

import com.mapping.db.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 类描述
 * 创建人 Ryan
 * 创建时间 2015/6/16 17:46.
 */

public class ArrayListPair extends ArrayList<NameValuePairDB> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(NameValuePairDB nameValuePair) {
        if (!StringUtils.isEmpty(nameValuePair.getValue())) {
            return super.add(nameValuePair);
        } else {
            return false;
        }
    }

    /**
     * 添加数据
     *
     * @param key
     * @param value
     * @return
     */
    public boolean add(String key, String value,Class<?> clazz) {
        return add(new NameValuePairDB(key, value,clazz));
    }


}