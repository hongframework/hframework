package com.hframework.web.interceptor;

import com.hframework.beans.exceptions.BusinessException;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.frame.cache.CacheFactory;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.PathMatcherUtils;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.JsonUtils;
import com.hframework.common.util.message.PropertyReader;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.extension.BusinessHandlerFactory;
import com.hframework.web.extension.annotation.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 缓冲处理拦截器
 * Created by zhangquanhong on 2017/4/27.
 */
@Component
@Aspect
public class CacheHandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CacheHandlerInterceptor.class);




    @Pointcut("execution(* com..service.impl.*.create(..))")
    private void createMethod(){ }

    @Pointcut("execution(int com..service.impl.*.update*(..))")
    private void updateMethod(){ }

    @Pointcut("execution(int com..service.impl.*.batchOperate*(..))")
    private void batchOperateMethod(){ }

    @Pointcut("execution(int com..service.impl.*.delete*(..))")
    private void deleteMethod(){ }

    @Pointcut("execution(* com..service.impl.*.get*List*(..))")
    private void getMethod(){ }


    @Around(value = "getMethod()")
    public Object getMethodBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        Object queryObject =  joinPoint.getArgs()[0];
        Class curServiceClass = joinPoint.getSourceLocation().getWithinType();
        if(CacheFactory.cacheRequired(curServiceClass)) {
            if(queryObject != null) {
                String queryObjectString = JsonUtils.writeValueAsString(queryObject);
                Object cacheObject = CacheFactory.get(curServiceClass, queryObjectString);
                if(cacheObject != null) {
                    return cacheObject;
                }else {
                    Object result = joinPoint.proceed();
                    CacheFactory.put(curServiceClass, queryObjectString, result);
                    return result;
                }
            }
        }
        return joinPoint.proceed();
    }

    @AfterReturning(pointcut = "batchOperateMethod()", returning = "retVal")
    public void batchOperateMethodAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        CacheFactory.removeAll(joinPoint.getSourceLocation().getWithinType());

    }

    @AfterReturning(pointcut = "createMethod()", returning = "retVal")
    public void createAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        CacheFactory.removeAll(joinPoint.getSourceLocation().getWithinType());
    }


    @AfterReturning(value = "updateMethod()")
    public void updateAfter(JoinPoint joinPoint) throws Throwable {
        CacheFactory.removeAll(joinPoint.getSourceLocation().getWithinType());
    }

    @AfterReturning(pointcut = "deleteMethod()", returning = "retVal")
    public void deleteAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        CacheFactory.removeAll(joinPoint.getSourceLocation().getWithinType());
    }
}
