package com.hframework.common.monitor;

/**
 * Created by zhangquanhong on 2017/11/15.
 */
public interface MonitorListener<T> {

    public void onEvent(Monitor<T> monitor) throws ClassNotFoundException, Exception;
}
