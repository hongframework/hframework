package com.hframework.common.monitor;

import com.hframework.common.util.message.JsonUtils;
import net.sf.ehcache.util.NamedThreadFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangquanhong on 2016/9/26.
 */
public abstract class ConfigMonitor<T> implements Monitor<T>{
    private static final Logger logger = LoggerFactory.getLogger(ConfigMonitor.class);

    private ScheduledExecutorService scheduler;

    protected boolean running = false;

    protected boolean loadFinish = false;

    private long refreshSeconds;

    private Class invokeClass;

    private T object;

    private String objectString;

    private List<MonitorListener> listeners;

    public ConfigMonitor(long refreshSeconds) {
        this.refreshSeconds = refreshSeconds;
        scheduler = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("config-monitor-thread"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ConfigMonitor(Class invokeClass, long refreshSeconds) {
        this.refreshSeconds = refreshSeconds;
        this.invokeClass = invokeClass;
        scheduler = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("config-monitor-thread for "
                + invokeClass.getSimpleName()), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void reload() {
        try {
            T newObject = fetch();
            if(object == null ||objectString == null || isDiff(newObject)) {
                object = newObject;
                objectString = JsonUtils.writeValueAsString(newObject);
                notifyListener();
            }
        } catch (Exception e) {
            logger.error("config fetch error ! {}", ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void notifyListener() throws Exception {
        if(listeners != null) {
            for (MonitorListener listener : listeners) {
                listener.onEvent(this);
            }
        }
    }

    private boolean isDiff(T newObject) throws IOException {
        String newObjectJson = JsonUtils.writeValueAsString(newObject);
        if(!objectString.equals(newObjectJson)) {
            logger.info("config is change ,old config is [{}], new config is [{}]", objectString, newObjectJson);
            return true;
        }
        return false;
    }

    public void addListener(MonitorListener listener) {
        if(listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(listener);
    }

    public abstract T fetch() throws Exception;

    public void start() throws Exception {
        if(!running) {
            synchronized (this) {
                if(!running) {
                    running = true;
                    startInternal();
                    loadFinish = true;
                }
            }
        }
    };

    public ConfigMonitor ok() throws Exception {
        start();
        return this;
    }

    public  void startInternal() throws Exception {
        logger.debug("start internal...");
        reload();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                logger.debug(" reload config begin !");
                reload();
                logger.debug(" reload config finish !");
            }
        }, refreshSeconds, refreshSeconds, TimeUnit.SECONDS);
        logger.info("start internal success!");
    }

    public void destroy() {
        destroyInternal();
        running = false;
    }

    public void destroyInternal(){
        logger.info("destroy internal...");
        logger.info("destroy internal success!");
    }

    public  T getObject() {
        synchronized (this){
            return object;
        }
//        while (!loadFinish) {
//            try {
//                logger.info("load un finish, wait 100 ms !");
//                wait(100L);
//            } catch (InterruptedException e) {
//                break;
//            }
//        }

    }
}
