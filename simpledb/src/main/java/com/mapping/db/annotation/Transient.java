package com.mapping.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 非数据库字段标识
 * 转瞬即逝的;短暂的;倏忽;暂住的;过往的;临时的
 * 创建人 wanggaoqiang
 * 创建时间 2015/6/16 18:09.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
public @interface Transient {

}