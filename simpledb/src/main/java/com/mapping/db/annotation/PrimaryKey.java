package com.mapping.db.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 主键标志
 * 创建人 wanggaoqiang
 * 创建时间 2015/6/16 18:09.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PrimaryKey {
    /**
     * 设置主键名
     *
     * @return
     */
    public String name() default "";

    /**
     * 字段默认值
     *
     * @return
     */
    public String defaultValue() default "";

    /**
     * 是否自动自增
     *
     * @return
     */
    boolean autoIncrement() default false;
}