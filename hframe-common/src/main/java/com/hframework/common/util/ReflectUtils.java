package com.hframework.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;


public class ReflectUtils {

    private static Log logger = LogFactory.getLog(BeanUtils.class);

    private static Object operate(Object obj, String fieldName, Object fieldVal, String type) {
        Object ret = null;
        try {
            // 获得对象类型
            Class<? extends Object> classType = obj.getClass();
            // 获得对象的所有属性
            Field fields[] = classType.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getName().equals(fieldName)) {
                    // 获得和属性对应的getXXX()方法的名字
                    String firstLetter = fieldName.substring(0, 1).toUpperCase();
                    if ("set".equals(type)) {
                        // 获得和属性对应的getXXX()方法
                        String setMethodName = "set" + firstLetter + fieldName.substring(1);
                        // 调用原对象的getXXX()方法
                        Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});
                        ret = setMethod.invoke(obj, new Object[]{fieldVal});
                    }
                    if ("get".equals(type)) {
                        // 获得和属性对应的setXXX()方法的名字
                        String getMethodName = "get" + firstLetter + fieldName.substring(1);
                        Method getMethod = classType.getMethod(getMethodName, new Class[]{});
                        ret = getMethod.invoke(obj, new Object[]{});
                    }
                    return ret;
                }
            }
        } catch (Exception e) {
            logger.warn("reflect error:" + fieldName, e);
        }
        return ret;
    }

    public static Object getVal(Object obj, String fieldName) {
        return operate(obj, fieldName, null, "get");
    }

    public static void setVal(Object obj, String fieldName, Object fieldVal) {
        operate(obj, fieldName, fieldVal, "set");
    }

    public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                // superClass.getMethod(methodName, parameterTypes);
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                // Method 不在当前类定义, 继续向上转型
            }
        }

        return null;
    }

    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    private static Field getDeclaredField(Object object, String filedName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
                // Field 不在当前类定义, 继续向上转型
            }
        }
        return null;
    }

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters)
            throws InvocationTargetException {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        }

        method.setAccessible(true);

        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException e) {

        }

        return null;
    }

    public static void setFieldValue(Object object, Map<String, String> propertyMap) {
        if(propertyMap != null) {
            for (String propertyName : propertyMap.keySet()) {
                String propertyValue = propertyMap.get(propertyName);
                PropertyDescriptor propertyDescriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(object.getClass(), propertyName);
                if(propertyDescriptor != null && StringUtils.isNotBlank(propertyValue)) {
                    setFieldValue(object, propertyName, propertyValue);
                }
            }
        }
    }
    public static void setFieldValue(Object object, String fieldName, String value) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");

        makeAccessible(field);


        try {
            if(Long.class == field.getType()) {
                field.set(object, Long.valueOf(value));
            }else if(Integer.class == field.getType()) {
                field.set(object, Integer.valueOf(value));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");

        makeAccessible(field);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field ["
                    + fieldName + "] on target [" + object + "]");

        makeAccessible(field);

        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

}
