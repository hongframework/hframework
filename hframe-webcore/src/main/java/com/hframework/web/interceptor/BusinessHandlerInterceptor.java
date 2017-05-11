package com.hframework.web.interceptor;

import com.hframework.common.util.JavaUtil;
import com.hframework.web.extension.BusinessHandlerFactory;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.beans.exceptions.BusinessException;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.extension.annotation.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据集连带规则拦截器
 * Created by zhangquanhong on 2016/8/28.
 */
@Component
@Aspect
public class BusinessHandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BusinessHandlerInterceptor.class);

    private static final long ENUM_CLASS_DEFAULT_HOLDER = 2;

    @Pointcut("execution(* com..service.impl.*.create(..))")
    private void createMethod(){ }

    @Pointcut("execution(int com..service.impl.*.update*(..))")
    private void updateMethod(){ }

    @Pointcut("execution(int com..service.impl.*.batchOperate*(..))")
    private void batchOperateMethod(){ }

    @Pointcut("execution(int com..service.impl.*.delete*(..))")
    private void deleteMethod(){ }

    private Set<Object> createObject = new HashSet<Object>();

    @Before(value = "batchOperateMethod()")
    public void batchOperateMethodBefore(JoinPoint joinPoint) throws Throwable {
        Object[] targetObjects = (Object[]) joinPoint.getArgs()[0];
        if(targetObjects != null && targetObjects.length > 0) {
            createObject.clear();
            for (Object targetObject : targetObjects) {
                Class<?> targetClass = targetObject.getClass();
                DataSetDescriptor dataSet = WebContext.get().getDataSet(targetClass);
                Field keyField = dataSet.getKeyField();
                Object fieldValue = ReflectUtils.getFieldValue(targetObject, JavaUtil.getJavaVarName(keyField.getCode()));
                if(fieldValue != null && Long.valueOf(String.valueOf(fieldValue)) > 0L) {
                    updateBefore(targetObject, joinPoint.getSourceLocation().getWithinType());
                }else {
                    createBefore(targetObject);
                    createObject.add(targetObject);
                }
            }
        }
    }

    @AfterReturning(pointcut = "batchOperateMethod()", returning = "retVal")
    public void batchOperateMethodAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        Object[] targetObjects = (Object[]) joinPoint.getArgs()[0];
        if(targetObjects != null && targetObjects.length > 0) {
            for (Object targetObject : targetObjects) {
//                Class<?> targetClass = targetObject.getClass();
//                DataSetDescriptor dataSet = WebContext.get().getDataSet(targetClass);
//                Field keyField = dataSet.getKeyField();
//                Object fieldValue = ReflectUtils.getFieldValue(targetObject, JavaUtil.getJavaVarName(keyField.getCode()));
                if(createObject.contains(targetObject)) {
                    createAfter(targetObject);
                }else {
                    updateAfter(targetObject, joinPoint.getSourceLocation().getWithinType());
                }
            }
        }
    }


    @Before(value = "createMethod()")
    public void createBefore(JoinPoint joinPoint) throws Throwable {
        Object targetObject = joinPoint.getArgs()[0];
        createBefore(targetObject);
    }

    public void createBefore(Object targetObject) throws Throwable {
        Map<BeforeCreateHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), BeforeCreateHandler.class);
        for (BeforeCreateHandler annotation : handlers.keySet()) {
            List<Method> methods = handlers.get(annotation);
            for (Method method : methods) {
                checkAndInvokeHandler(targetObject, annotation.attr(), null, annotation.target(), method, null);
            }

        }
    }

    @AfterReturning(pointcut = "createMethod()", returning = "retVal")
    public void createAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        if(retVal > 0) {
            Object targetObject = joinPoint.getArgs()[0];
            createAfter(targetObject);
        }
    }

    private void createAfter(Object targetObject) throws Throwable {
        Map<AfterCreateHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), AfterCreateHandler.class);
        for (AfterCreateHandler annotation : handlers.keySet()) {
            List<Method> methods = handlers.get(annotation);
            for (Method method : methods) {
                checkAndInvokeHandler(targetObject, annotation.attr(), null, annotation.target(), method, null);
            }
        }
    }


    @Before(value = "updateMethod()")
    public void updateBefore(JoinPoint joinPoint) throws Throwable {
        if(joinPoint.getArgs().length == 1) {//update方法
            Object targetObject = joinPoint.getArgs()[0];
            Class curServiceClass = joinPoint.getSourceLocation().getWithinType();
            updateBefore(targetObject, curServiceClass);
        }/*else {//updateByExample方法
            Object targetObject = joinPoint.getArgs()[0];
        }*/
    }

    private void updateBefore(Object targetObject, Class curServiceClass) throws Throwable {
        Map<BeforeUpdateHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), BeforeUpdateHandler.class);
        for (BeforeUpdateHandler annotation : handlers.keySet()) {
            List<Method> methods = handlers.get(annotation);
            for (Method method : methods) {
                checkAndInvokeHandler(targetObject, annotation.attr(), annotation.orig(), annotation.target(), method, curServiceClass);
            }
        }
    }

    @AfterReturning(value = "updateMethod()")
    public void updateAfter(JoinPoint joinPoint) throws Throwable {
        if(joinPoint.getArgs().length == 1) {//update方法
            Object targetObject = joinPoint.getArgs()[0];
            Class curServiceClass = joinPoint.getSourceLocation().getWithinType();
            updateAfter(targetObject, curServiceClass);
        }/*else {//updateByExample方法
            Object targetObject = joinPoint.getArgs()[0];
        }*/
    }

    private void updateAfter(Object targetObject, Class curServiceClass) throws Throwable {
        Map<AfterUpdateHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), AfterUpdateHandler.class);
        for (AfterUpdateHandler annotation : handlers.keySet()) {
            List<Method> methods = handlers.get(annotation);
            for (Method method : methods) {
                checkAndInvokeHandler(targetObject, annotation.attr(), annotation.orig(), annotation.target(), method, curServiceClass);
            }

        }
    }

    @Before(value = "deleteMethod()")
    public void deleteBefore(JoinPoint joinPoint) throws Throwable {
        Object targetObject = joinPoint.getArgs()[0];
        if (targetObject instanceof Long) {
        } else {
            Map<BeforeDeleteHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), BeforeDeleteHandler.class);
            for (BeforeDeleteHandler annotation : handlers.keySet()) {
                List<Method> methods = handlers.get(annotation);
                for (Method method : methods) {
                    checkAndInvokeHandler(targetObject, annotation.attr(), null, annotation.orig(), method, null);
                }

            }
        }
    }

    @AfterReturning(pointcut = "deleteMethod()", returning = "retVal")
    public void deleteAfter(JoinPoint joinPoint, int retVal) throws Throwable {
        Object targetObject = joinPoint.getArgs()[0];
        if (targetObject instanceof Long) {
        }else {
            Map<AfterDeleteHandler, List<Method>> handlers = BusinessHandlerFactory.getHandler(targetObject.getClass(), AfterDeleteHandler.class);
            for (AfterDeleteHandler annotation : handlers.keySet()) {
                List<Method> methods = handlers.get(annotation);
                for (Method method : methods) {
                    checkAndInvokeHandler(targetObject, annotation.attr(), null, annotation.orig(), method, null);
                }

            }
        }
    }

    private void checkAndInvokeHandler(Object targetObject, String attr, String orig, String target,
                                       Method method, Class curServiceClass) throws Throwable {
        if(StringUtils.isNotBlank(attr)) {
            String propertyName = attr.trim();
            String targetPropertyValue = BeanUtils.getProperty(targetObject, propertyName);
            if(StringUtils.isNotBlank(target) && !checkValuePass(target, targetPropertyValue)) {
                return;
            }
            if(StringUtils.isNotBlank(orig)) {
                Object originObject = getOriginObject(targetObject, curServiceClass);
                String originPropertyValue = BeanUtils.getProperty(originObject, propertyName);
                if(!checkValuePass(orig, originPropertyValue)) {
                    return;
                }else {
                    invokeHandler(method, targetObject, originObject);
                }
            }else {
                invokeHandler(method, targetObject);
            }
        }else {
            invokeHandler(method, targetObject);
        }
    }

    private boolean checkValuePass(String request, String value) {
        request = request.trim();
        if(request.contains(",")) {
            request = "," + request + ",";
            return request.contains("," + value + ",");
        }else if(request.startsWith("!")) {
            return !request.equals(value);
        }else {
            return request.equals(value);
        }



    }


    private void invokeHandler(Method method, Object... targetObject) throws Throwable {
        Object handler = ServiceFactory.getService(method.getDeclaringClass());
        if(targetObject == null) targetObject = new Object[0];

        try {
            Object[] args = new Object[method.getParameterTypes().length];
            for (int i = 0; i < targetObject.length; i++) {
                args[i] = targetObject[i];
            }
            method.invoke(handler, args);
        } catch (Exception e) {
            if(((InvocationTargetException) e).getTargetException() instanceof BusinessException) {
                throw ((InvocationTargetException) e).getTargetException();
            }else if(((InvocationTargetException) e).getTargetException() instanceof IllegalArgumentException){
                try{
                    Object[] args = new Object[method.getParameterTypes().length +1];
                    for (int i = 0; i < targetObject.length; i++) {
                        args[i] = targetObject[i];
                    }
                    args[method.getParameterTypes().length] = null;
                    method.invoke(handler, args);
                }catch (Exception e1){
                    logger.error("class[{}], method[{}], error={}",method.getDeclaringClass().getName(), method.getName(), ExceptionUtils.getFullStackTrace(e1));
                    e1.printStackTrace();
                    throw e1;
                }
            }else {
                logger.error("class[{}], method[{}], error={}",method.getDeclaringClass().getName(), method.getName(), ExceptionUtils.getFullStackTrace(e));
                throw ((InvocationTargetException) e).getTargetException();
            }
        }
    }

    private Object getOriginObject(Object targetObject, Class curServiceClass) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Object service = ServiceFactory.getService(curServiceClass);
        String keyPropertyGetMethod = "get" + targetObject.getClass().getSimpleName() + "Id";
        String keyPropertyValue = String.valueOf(ReflectUtils.invokeMethod(targetObject, keyPropertyGetMethod, new Class[0], new Object[0]));

        String methodName = "get" + targetObject.getClass().getSimpleName() + "ByPK";

        return ReflectUtils.invokeMethod(service, methodName, new Class[]{long.class}, new Object[]{Long.valueOf(keyPropertyValue)});
    }
}
