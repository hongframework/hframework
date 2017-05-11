package com.hframework.common.frame.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.hframework.common.frame.cache.AbstractCache;
import com.hframework.common.frame.cache.ICache;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cache实例
 */
public class GuavaCache extends AbstractCache implements ICache {

	Cache<String, Object> cache;

	public GuavaCache(String cacheName, Long duration,TimeUnit unit,  long maximumSize) {
		super(cacheName);
		cache = CacheBuilder.newBuilder()
				.expireAfterWrite(duration, unit)
				.maximumSize(maximumSize)
				.build();
	}

	public void put(String key, Object value) {
		cache.put(key, value);
	}

	public Object get(String key) {
		return cache.getIfPresent(key);
	}

	public void remove(String key) {
		cache.invalidate(key);
	}

	public List<String> getKeys() {
		return Lists.newArrayList(cache.asMap().keySet());
	}

	public void removeAll() {
		cache.cleanUp();
	}
}
