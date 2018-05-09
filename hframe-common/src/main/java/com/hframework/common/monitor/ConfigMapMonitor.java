package com.hframework.common.monitor;

import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.message.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangquanhong on 2016/9/26.
 */
public abstract class ConfigMapMonitor<T> extends AbstractMonitor<T> implements Monitor<T>{
    private static final Logger logger = LoggerFactory.getLogger(ConfigMapMonitor.class);

    protected Map<Class, Fetcher<T, String>> fetchers;

    protected List<T> objectList;
    protected String objectString;

    private Map<String, T> objectMap = new HashMap<String, T>();
    private Map<String, String> objectJsonMap = new HashMap<String, String>();

    private Map<String, T> addObjects, modObjects, delObjects;

    //最后更新时间
    public Date lastDateTime = null;


    public ConfigMapMonitor(long refreshSeconds) {
        super(refreshSeconds);
    }

    public ConfigMapMonitor(Class invokeClass, long refreshSeconds) {
        super(invokeClass, refreshSeconds);
    }

    public ConfigMapMonitor<T> addFetcher(Class clazz, Fetcher<T, String> fetcher){
        if(fetchers == null) {
            fetchers = new HashMap<Class, Fetcher<T, String>>();
        }
        fetchers.put(clazz, fetcher);
        return this;
    }

    public Map<Class, Fetcher<T, String>> getFetchers() {
        return fetchers;
    }

    //    public abstract Mapper<String, T> mapper();

    protected abstract String keyProperty(T t);

    public void reload() {
        reload(true);
    }
    public boolean reload(boolean whetherNotifyListener) {
        try {
            List<T> newObjectList = fetch();
            if(isDiff(objectString, newObjectList)) {
                Map<String, T> newObjectMap = CollectionUtils.convert(newObjectList,
                        new Mapper<String, T>() {
                            public <K> K getKey(T t) {
                                return (K) keyProperty(t);
                            }
                });
                Map<String, String> newObjectJsonMap = new HashMap<String, String>();
                addObjects = new HashMap<String, T>();
                modObjects = new HashMap<String, T>();
                delObjects = new HashMap<String, T>();
                for (String keyProperty : newObjectMap.keySet()) {
                    T newObject = newObjectMap.get(keyProperty);
                    String newObjectJsonString = JsonUtils.writeValueAsString(newObject);
                    newObjectJsonMap.put(keyProperty, newObjectJsonString);
                    if(!objectMap.containsKey(keyProperty)) {
                        addObjects.put(keyProperty, newObject);
                    }else if(!newObjectJsonString.equals(objectJsonMap.get(keyProperty))){
                        modObjects.put(keyProperty, newObject);
                    }
                }
                for (String keyProperty : objectMap.keySet()) {
                    if(!newObjectMap.containsKey(keyProperty)) {
                        delObjects.put(keyProperty, objectMap.get(keyProperty));
                    }
                }

                objectMap = newObjectMap;
                objectJsonMap = newObjectJsonMap;
                objectList = newObjectList;
                objectString = JsonUtils.writeValueAsString(newObjectList);
                synchronized (this) {
                    lastDateTime = new Date();
                    if(whetherNotifyListener) {
                        notifyListener(this, getObject());
                    }else {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("config fetch error ! {}", ExceptionUtils.getFullStackTrace(e));
        }
        return false;
    }
    public abstract List<T> fetch() throws Exception;

    public Map<String, T> getObjectMap(){
        return objectMap;
    }

    public Map<String, T> getAddObjectMap(){
        return addObjects;
    }

    public Map<String, T> getModObjectMap(){
        return modObjects;
    }

    public Map<String, T> getDelObjectMap(){
        return delObjects;
    }
    public T getObject() {
        synchronized (this){
            return (T) objectList;
        }
    }
}
