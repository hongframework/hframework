package com.hframework.common.resource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by zqh on 2016/4/16.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface Source {
    String[] value() default "";

    String[] classpath() default "";

    String ignore() default "";

    boolean formatKey() default true;

}
