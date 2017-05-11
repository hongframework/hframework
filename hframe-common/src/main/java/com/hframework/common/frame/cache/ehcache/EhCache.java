package com.hframework.common.frame.cache.ehcache;

import com.google.common.collect.Lists;
import com.hframework.common.frame.cache.AbstractCache;
import com.hframework.common.frame.cache.ICache;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.List;

/**
 * Cache实例
 */
public class EhCache extends AbstractCache implements ICache {

	Cache cache;

	public EhCache(String cacheName) {
		super(cacheName);
		cache = CacheManager.getInstance().getCache(cacheName);
	}

	public void put(String key, Object value) {
		if (key != null && !key.equals("")){
			Element element = new Element(key, value);
			cache.put(element);
		}
	}

	public Object get(String key) {
		Element element;
		try {
			element = cache.get(key);
		} catch (CacheException cacheException) {
			throw new DataRetrievalFailureException("ResourceCache failure: "
					+ cacheException.getMessage(), cacheException);
		}
		if (element == null)
			return null;

		return element.getObjectValue();
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public List<String> getKeys() {
		try {
			return Lists.newArrayList(cache.getKeys());
		} catch (IllegalStateException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (CacheException e) {
			throw new UnsupportedOperationException(e.getMessage(), e);
		}
	}

	public void removeAll() {
		cache.removeAll();
		cache.clearStatistics();
		cache.flush();
	}
}
