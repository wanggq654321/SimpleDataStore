package com.mapping.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置字段名
 * 表字段名字，默认是 类对象字段
 * 创建人 wanggaoqiang
 * 创建时间 2023/3/16 18:08.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Column {
    /**
     * 设置字段名
     *
     * @return
     */
    String name() default "";

    /**
     * 字段默认值
     *
     * @return
     */
    public String defaultValue() default "";
}
