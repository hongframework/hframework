package com.hframework.common.frame.cache;

import com.hframework.common.frame.cache.ehcache.EhCache;
import com.hframework.common.frame.cache.guava.GuavaCache;
import com.hframework.common.util.PathMatcherUtils;
import com.hframework.common.util.message.PropertyReader;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CacheFactory {

	private static final Object lockObject = new Object();

	private static Map<String, ICache> cacheMap = new HashMap<String, ICache>();

	private static final String CACHE_ENGINE = "hframework.cache.engine";
	private static final String CACHE_EXPIRE_TIME = "hframework.cache.expire.time";
	private static final String CACHE_MAXIMUM_SIZE = "hframework.cache.maximum.size";
	private static final String CACHE_SERVICE_EXPRESS = "hframework.cache.service.express";
	private static final String CACHE_SERVICE_METHOD = "hframework.cache.service.method";


	private static PropertyReader propertyReader =
			PropertyReader.read("properties/cache.properties")
					.merge("cache.properties")
					.addDefine(CACHE_ENGINE, CACHE_EXPIRE_TIME, CACHE_MAXIMUM_SIZE,
							CACHE_SERVICE_EXPRESS, CACHE_SERVICE_METHOD);

	private static Map<Class, Boolean> cacheRequiredInfo = new HashMap<Class, Boolean>();

	public static boolean cacheRequired(Class curServiceClass) {
		if(!cacheRequiredInfo.containsKey(curServiceClass)) {
			Map<String, String> configs = propertyReader.getAsList(CACHE_SERVICE_EXPRESS);
			boolean required  = false;
			for (Map.Entry<String, String> config : configs.entrySet()) {
				boolean matches = PathMatcherUtils.matches(config.getValue(), curServiceClass.getName());
				if(matches) {
					required = true;
					break;
				}
			}
			cacheRequiredInfo.put(curServiceClass, required);

		}
		return cacheRequiredInfo.get(curServiceClass);
	}

	public static void addRequired(Class curServiceClass) {
		cacheRequiredInfo.put(curServiceClass, true);
	}


	public static synchronized void put(Class clazz, String key,Object obj) {
		put(clazz.getName(),key,obj);
	}

	public static ICache getOrCreateCacheIfNotExists(String cacheName) {
		if(!cacheMap.containsKey(cacheName)) {
			synchronized (lockObject) {
				if(!cacheMap.containsKey(cacheName)) {
					String engine = propertyReader.get(CACHE_ENGINE);
					if("guava".equals(engine)) {
						String expireString = propertyReader.get(CACHE_EXPIRE_TIME, "3m").trim();
						Long duration = 3L;
						TimeUnit unit = TimeUnit.MINUTES;
						if(expireString.endsWith("ms")) {
							duration = Long.valueOf(expireString.substring(0, expireString.length()-2));
							unit = TimeUnit.MILLISECONDS;
						}else if(expireString.endsWith("s")) {
							duration = Long.valueOf(expireString.substring(0, expireString.length()-1));
							unit = TimeUnit.SECONDS;
						}else if(expireString.endsWith("m")) {
							duration = Long.valueOf(expireString.substring(0, expireString.length()-1));
							unit = TimeUnit.MINUTES;
						}else if(expireString.endsWith("h")) {
							duration = Long.valueOf(expireString.substring(0, expireString.length()-1));
							unit = TimeUnit.HOURS;
						}
						cacheMap.put(cacheName,new GuavaCache(cacheName, duration, unit, propertyReader.getAsLong(CACHE_MAXIMUM_SIZE, 100L)));
					}else {
						cacheMap.put(cacheName,new EhCache(cacheName));
					}

				}
			}
		}
		return cacheMap.get(cacheName);
	}

	public static boolean contain(String cacheName) {
		return cacheMap.containsKey(cacheName);
	}

	public static synchronized void put(String cacheName, String key,Object obj) {
		getOrCreateCacheIfNotExists(cacheName).put(key, obj);
	}


	public static  Object get(Class clazz, String key) {
		return get(clazz.getName(),key);
	}

	public static  Object get(String cacheName, String key) {
		return getOrCreateCacheIfNotExists(cacheName).get(key);
	}

	public static  Collection<String> getAll(Class clazz ) {
		return getAll(clazz.getName());
	}

	public static  Collection<String> getAll(String cacheName ) {
		return getOrCreateCacheIfNotExists(cacheName).getKeys();
	}

	public static synchronized void remove(Class clazz,String key) {
		remove(clazz.getName(), key);
	}

	public static synchronized void remove(String cacheName,String key) {
		getOrCreateCacheIfNotExists(cacheName).remove(key);
	}

	public static synchronized void removeAll(Class clazz) {
		removeAll(clazz.getName());
	}

	public static synchronized void removeAll(String cacheName) {
		if(contain(cacheName)) {
			getOrCreateCacheIfNotExists(cacheName).removeAll();
			cacheMap.remove(cacheName);
		}
	}

}
