package com.hframework.common.util.collect.bean;


/**
 * User: zhangqh6
 * Date: 2015/12/29 10:32:32
 */
public interface Merger<K, V> {

   <K> K getKey(V v);

   <K> K groupKey(V v);
}
