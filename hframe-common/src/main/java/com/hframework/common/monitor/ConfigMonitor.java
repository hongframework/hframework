package com.hframework.common.monitor;

import com.hframework.common.util.message.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.math3.analysis.function.Abs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangquanhong on 2016/9/26.
 */
public abstract class ConfigMonitor<T> extends AbstractMonitor<T> implements Monitor<T>{
    private static final Logger logger = LoggerFactory.getLogger(ConfigMonitor.class);

    protected T object;

    protected String objectString;

    public ConfigMonitor(long refreshSeconds) {
        super(refreshSeconds);
    }

    public ConfigMonitor(Class invokeClass, long refreshSeconds) {
        super(invokeClass, refreshSeconds);
    }


    public synchronized void reload() {
        try {
            T newObject = fetch();
            if(isDiff(objectString, newObject)) {
                object = newObject;
                objectString = JsonUtils.writeValueAsString(newObject);
                notifyListener(this, getObject());
            }
        } catch (Exception e) {
            logger.error("config fetch error ! {}", ExceptionUtils.getFullStackTrace(e));
        }
    }

    public abstract T fetch() throws Exception;

    public  T getObject() {
        synchronized (this){
            return object;
        }
    }
}
