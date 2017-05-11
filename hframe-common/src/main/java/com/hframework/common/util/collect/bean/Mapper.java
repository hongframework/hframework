package com.hframework.common.util.collect.bean;

/**
 * User: zhangqh6
 * Date: 2016/2/18 18:11:11
 */
public interface Mapper<K, V> {
    <K> K getKey(V v);
}
