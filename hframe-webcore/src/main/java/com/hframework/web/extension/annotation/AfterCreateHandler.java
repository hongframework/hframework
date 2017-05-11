package com.hframework.web.extension.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by zqh on 2016/4/16.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD,})
public @interface AfterCreateHandler {
    String attr() default "";
    String target() default "";
}
