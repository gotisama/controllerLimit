package com.goti.controllerLimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口访问频率限制注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    /**
     * 访问频率 默认1次
     * @return 访问频率
     */
    int limit() default 1;

    /**
     * 限制时间 单位为妙 默认1秒
     * @return 限制时间
     */
    long time() default 1L;
}
