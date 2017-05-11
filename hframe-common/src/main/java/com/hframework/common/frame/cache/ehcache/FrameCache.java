package com.hframework.common.frame.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 系统级参数缓存
 * @author new
 *
 */
public class FrameCache {

	private static Cache cache = CacheManager.getInstance().getCache(FrameCache.class.getName());
	
	public static synchronized void putCache(String key,Object obj) {
		if (key != null && !key.equals("")){
			Element element = new Element(key, obj);
			cache.put(element);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static synchronized Object getCache(String key) {
		Element element = null;
		try {
			element = cache.get(key);
		} catch (CacheException cacheException) {
			throw new DataRetrievalFailureException("ResourceCache failure: "
					+ cacheException.getMessage(), cacheException);
		}
		if (element == null)
			return null;

		return element.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized Collection<String> getAllCache() {
		Collection<String> resources;
		List<String> resclist = new ArrayList<String>();
		try {
			resources = cache.getKeys();
		} catch (IllegalStateException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (CacheException e) {
			throw new UnsupportedOperationException(e.getMessage(), e);
		}
		for (Iterator<String> localIterator = resources.iterator(); localIterator.hasNext();) {
			String key = localIterator.next();
			if (key != null){
				resclist.add(key);
			}
		}
		return resclist;
	}
	
	public static synchronized void removeCache(String key) {
		cache.remove(key);
	}

	public static synchronized void removeAllCache() {
		cache.removeAll();
		cache.clearStatistics();
		cache.flush();
	}
}