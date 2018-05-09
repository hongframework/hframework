package com.hframework.common.monitor;

import com.hframework.common.util.message.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
public abstract class AbstractMonitor<T> implements Monitor<T>{
    private static final Logger logger = LoggerFactory.getLogger(AbstractMonitor.class);

    protected ScheduledExecutorService scheduler;

    protected boolean running = false;

    protected boolean loadFinish = false;

    protected long refreshSeconds;

    protected List<MonitorListener> listeners;

    public AbstractMonitor(long refreshSeconds) {
        this.refreshSeconds = refreshSeconds;
        scheduler = MonitorExecutorFactory.getExecutor();
    }

    public AbstractMonitor(Class invokeClass, long refreshSeconds) {
        this.refreshSeconds = refreshSeconds;
        scheduler = MonitorExecutorFactory.getExecutor(invokeClass.getSimpleName());
    }


    public void notifyListener(Monitor monitor, T t) throws Exception {
        if(listeners != null) {
            for (MonitorListener listener : listeners) {
                listener.onEvent(monitor);
            }
        }
    }
    public void addListener(MonitorListener listener) {
        if(listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(listener);
    }

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

    public AbstractMonitor ok() throws Exception {
        start();
        return this;
    }

    protected void startInternal() throws Exception {
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


    protected boolean isDiff(String originObjectString, Object newObject) throws IOException {
        if(originObjectString == null && newObject != null) return true;
        String newObjectJson = JsonUtils.writeValueAsString(newObject);
        if(!originObjectString.equals(newObjectJson)) {
            logger.info("config is change ,old config is [{}], new config is [{}]", originObjectString, newObjectJson);
            return true;
        }
        return false;
    }

}
