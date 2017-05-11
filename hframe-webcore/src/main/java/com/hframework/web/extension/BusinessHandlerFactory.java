package com.hframework.web.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/9/22.
 */
public class BusinessHandlerFactory {
//    private static Map<String, BusinessHandler> handlers = new HashMap<String, BusinessHandler>();
    private static Map<String, Map<Annotation, List<Method>>> handlers = new HashMap<String, Map<Annotation, List<Method>>>();

    /**
     * 添加handler
     * @param entryClass
     * @param eventType
     * @param handlerMethod
     * @param <T>
     * @param <V>
     */
    public static <T, V extends Annotation> void addHandler(Class<T> entryClass, V eventType, Method handlerMethod) {

        String key = entryClass.getSimpleName()+ "|" + eventType.annotationType().getSimpleName();

        if(!handlers.containsKey(key)) handlers.put(key, new HashMap<Annotation, List<Method>>());
        Map<Annotation, List<Method>> annotationListMap = handlers.get(key);
        if(!annotationListMap.containsKey(eventType)) annotationListMap.put(eventType, new ArrayList<Method>());
        annotationListMap.get(eventType).add(handlerMethod);
    }

    /**
     * 获取handler
     * @param entryClass
     * @param eventTypeClass
     * @param <T>
     * @return
     */
    public static <T, V extends Annotation> Map<V, List<Method>> getHandler(Class<T> entryClass, Class<V> eventTypeClass) {
        String key = entryClass.getSimpleName()+ "|" + eventTypeClass.getSimpleName();
        return handlers.get(key) == null ? new HashMap<V, List<Method>>() : (Map<V, List<Method>>) handlers.get(key);
    }


    public static  <T, V extends Annotation>  boolean contain(Class<T> entryClass, Class<V> eventTypeClass) {
        String key = entryClass.getSimpleName()+ "|" + eventTypeClass.getSimpleName();
        return handlers.containsKey(key);
    }
}
