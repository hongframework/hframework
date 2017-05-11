package com.hframework.common.resource;

import com.hframework.common.resource.annotation.Key;
import com.hframework.common.resource.annotation.Source;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/4/16.
 */
public class ResourceWrapper {

    public static <T> T getResourceBean(Class<T> clazz, String[] resourceFileName)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        Constructor constructor=clazz.getDeclaredConstructor();
        constructor.setAccessible(true);

        T instance =(T) constructor.newInstance();

        Source source = clazz.getAnnotation(Source.class);
        String[] resourcePaths = source.value();
        if(resourcePaths == null || resourcePaths.length == 0) {
            resourcePaths = source.classpath();
        }

        if((resourcePaths == null || resourcePaths.length == 0)
                && (resourceFileName != null && resourceFileName.length != 0)) {
            resourcePaths = resourceFileName;
        }


        if((resourcePaths == null || resourcePaths.length == 0)) {
            System.out.println("resource not configure ！");
            return instance;
        }

        String ignore = source.ignore();
        boolean format = source.formatKey();

        List<ResourceBundle> resourceBundles = new ArrayList<ResourceBundle>();
        for (String resourcePath : resourcePaths) {
            ResourceBundle bundle = ResourceBundle.getBundle(resourcePath.replace(".properties",""));
            if(bundle != null) {
                resourceBundles.add(bundle);
            }
        }

        for (ResourceBundle resourceBundle : resourceBundles) {
            Enumeration<String> keys = resourceBundle.getKeys();
            while(keys.hasMoreElements()) {
                String element = keys.nextElement();
                String elementValue = resourceBundle.getString(element);
                System.out.println(element + "value");
                org.apache.commons.beanutils.BeanUtils.setProperty(
                        instance, getFieldName(element, ignore, format), elementValue);
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(field.getAnnotation(Key.class) != null) {
                Key key = field.getAnnotation(Key.class);
                String value = key.value();
                if(StringUtils.isNotBlank(value)) {
                    for (ResourceBundle resourceBundle : resourceBundles) {
                        org.apache.commons.beanutils.BeanUtils.setProperty(
                                instance,field.getName(), resourceBundle.getString(value));
                    }
                }
            }
        }

        return instance;
    }

    private static String getFieldName(String element, String ignore, boolean format) {
        if(format) {
            return JavaUtil.getJavaVarName(element.replaceAll(ignore, ""));
        }
        return element.replaceAll(ignore,"");
    }

    public static <T> T getResourceBean(Class<T> clazz)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        return getResourceBean(clazz, null);
    }

    public static class JavaUtil {
        private static final Map<String,String> KEYWORDS= new HashMap<String, String>() {{
            put("interface","interface1");
            put("class","clazz");
        }};


        public static String getJavaClassName(String name) {

            String returnName = "";

            String[] parts = name.split("[_\\-\\.]+");
            for (String part : parts) {
                if (!"".equals(part)) {
                    returnName += part.substring(0, 1).toUpperCase()
                            + part.substring(1);
                }
            }

            return returnName;
        }

        public static String getJavaVarName(String name) {

            String returnName="";

            String[] parts=name.split("[__\\-\\.]+");
            for (String part : parts) {
                if(!"".equals(part)){
                    returnName+=part.substring(0,1).toUpperCase()+part.substring(1);
                }
            }
            String javaVarName = null;
            if(StringUtils.isBlank(returnName)) {
                System.out.println("==>" + name);
            }
            if(returnName == null ||returnName.length() < 1) {
                System.out.println();
            }
            javaVarName = returnName.substring(0, 1).toLowerCase() + returnName.substring(1);


            if(KEYWORDS.containsKey(javaVarName)) {
                return KEYWORDS.get(javaVarName);
            }

            return javaVarName;
        }
    }
}
