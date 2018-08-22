package com.hframework.base.bean;

import java.util.Map;

public class MapWrapper<K, V> {
    private Map<K, V> map;
    private MapWrapper(Map<K, V> map) {
        this.map = map;
    }
    public static MapWrapper warp(Map map) {
        return new MapWrapper(map);
    }

    public Map<K, V> map(){
        return map;
    }
}
