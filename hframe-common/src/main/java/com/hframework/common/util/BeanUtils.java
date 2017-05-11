package com.hframework.common.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BeanUtils {

    private static Log log = LogFactory.getLog(BeanUtils.class);

    static {
        //cause by : org.apache.commons.beanutils.ConversionException: No value specified for 'Date'
        ConvertUtils.register(new DateConverter(null), java.util.Date.class);
    }

    /**
     * 得到fields的属性
     *
     * @param objClass 当前对象的Class对象
     * @return Map 对象属性地图(属性名称，属性类型)
     */
    public static Map<String, Class> getFilds(Class objClass) {
        Map<String, Class> map = null;
        try {
            // 得到所有的属性
            Field[] fields = objClass.getDeclaredFields();
            int size = fields.length;
            if (size > 0) {
                map = new HashMap<String, Class>();
                for (int i = 0; i < size; i++) {
                    map.put(fields[i].getName(), fields[i].getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 得到所有Method对照Map
     *
     * @param objClass 当前对象的Class对象
     * @return Map 对象方法地图(方法名，方法)
     */
    public static Map<String, Method> getMethods(Class objClass) {
        Map<String, Method> map = null;
        try {
            // 得到所有的方法
            Method[] methods = objClass.getDeclaredMethods();
            int size = methods.length;
            if (size > 0) {
                map = new HashMap<String, Method>();
                for (int i = 0; i < size; i++) {
                    map.put(methods[i].getName(), methods[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static <T> T convertObject(Class<T> objClass, Object obj1) {
        try {
            Class cla = Class.forName(objClass.getName());
            Object obj = cla.newInstance();
            org.apache.commons.beanutils.BeanUtils.copyProperties(obj, obj1);
            return (T) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> parsePropertiesMap(Object obj) {
        Map<String, Object> parmMap = new HashMap<String, Object>();
        // 属性的名称及类型
        Field[] fileds = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fileds) {
                // 属性名称
                String filedName = field.getName();
                field.setAccessible(true);
                ;
                parmMap.put(filedName,field.get(obj) != null ? field.get(obj) : null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parmMap;
    }


    public static void convertObject(Object obj, Object obj1) {
        // 属性的名称及类型
        Map<String, Class> fileds = getFilds(obj1.getClass());
        // 方法名称及方法
        Map<String, Method> methods = getMethods(obj1.getClass());

        try {
            for (Iterator it = fileds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                // 属性名称
                String filed = (String) entry.getKey();
                // 转换成SET方法(首字母大写)
                StringBuffer sub = new StringBuffer("set");
                sub.append(StringUtils.upperCaseFirstChar(filed));
                // 获取SET方法
                Method setMethod = (Method) methods.get(sub.toString());
                // 转换成GET方法(首字母大写)
                StringBuffer gub = new StringBuffer("get");
                gub.append(StringUtils.upperCaseFirstChar(filed));
                // 获取GET方法
                Method getMethod = (Method) methods.get(gub.toString());

                if (setMethod != null && getMethod != null) {
                    // 从baseForm中取出对应的值
                    Object temp = getMethod.invoke(obj1);
                    // 注入到对象中相应的属性
                    if (temp != null) {
                        setMethod.invoke(obj, new Object[]{temp});
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> convertMap(Object obj, boolean bool) {
        Map<String, String> parmMap = new HashMap<String, String>();
        // 属性的名称及类型
        Field[] fileds = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fileds) {
                // 属性名称
                String filedName = field.getName();
                field.setAccessible(true);
                ;
                parmMap.put(filedName,field.get(obj) != null ? String.valueOf(field.get(obj)) : null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parmMap;
    }

    public static Map<String, String> convertMapAndFormat(Object obj, boolean bool) {
        Map<String, String> parmMap = new HashMap<String, String>();
        // 属性的名称及类型
        Field[] fileds = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fileds) {
                // 属性名称
                String filedName = field.getName();
                field.setAccessible(true);
                if(field.getType() == Date.class) {
                    parmMap.put(filedName,field.get(obj) != null ? DateUtils.getDateYYYYMMDDHHMMSS((Date) field.get(obj)) : null);
                }else {
                    parmMap.put(filedName,field.get(obj) != null ? String.valueOf(field.get(obj)) : null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parmMap;
    }

    public static String[] getPropertiesArray(Object obj) {
        List<String> result= new ArrayList();
        Field[] fileds = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fileds) {
                // 属性名称
                field.setAccessible(true);
                result.add(String.valueOf(field.get(obj)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toArray(new String[0]);
    }

    /**
     * 得到所有Method及返回值对照Map
     *
     * @param objClass 当前对象的Class对象
     * @return Map 对象方法地图(返回类型,方法)
     */
    public static Map<Class, Method> getMethodsReturn(Class objClass) {
        Map<Class, Method> map = null;
        try {
            // 得到所有的方法
            Method[] methods = objClass.getDeclaredMethods();
            int size = methods.length;
            if (size > 0) {
                map = new HashMap<Class, Method>();
                for (int i = 0; i < size; i++) {
                    map.put(methods[i].getReturnType(), methods[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将对象的属性转换成为对象相应方法的名称(转换在get方法名，类型)
     */
    public static Map getFildsToSetName(Class objClass) {
        Map<String, Object> maps = null;
        Map<String, Class> map = getFilds(objClass);
        if (map != null) {
            maps = new HashMap<String, Object>();
            for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                StringBuffer sub = new StringBuffer("set");
                String str = (String) entry.getKey();
                str = StringUtils.upperCaseFirstChar(str);
                sub.append(str);
                maps.put(sub.toString(), entry.getValue());
            }
        }
        return maps;
    }
}
