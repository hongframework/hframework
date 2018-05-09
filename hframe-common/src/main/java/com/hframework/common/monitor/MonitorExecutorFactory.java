package com.hframework.common.monitor;

import net.sf.ehcache.util.NamedThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class MonitorExecutorFactory {

    private static Map<String, ScheduledExecutorService> executors;

    private static final String DEFAULT_EXECUTOR_NAME = "DEFAULT";

    public static ScheduledExecutorService getExecutor(){
        return getExecutor(DEFAULT_EXECUTOR_NAME);
    }

    public static ScheduledExecutorService getExecutor(String executorName){
        if(executors == null) executors = new HashMap<String, ScheduledExecutorService>();
        if(!executors.containsKey(executorName)) {
            synchronized (MonitorExecutorFactory.class) {
                if(!executors.containsKey(executorName)) {
                    executors.put(executorName, new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("config-monitor-thread["
                            + executorName + "]"), new ThreadPoolExecutor.CallerRunsPolicy()));
                }
            }
        }
        return executors.get(executorName);
    }
}
