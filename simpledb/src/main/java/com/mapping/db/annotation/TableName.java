package com.mapping.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名字
 * 创建人 wanggaoqiang
 * 创建时间 2015/6/16 18:09.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
    /**
     * 设置表名字
     *
     * @return
     */
    String name() default "";
}