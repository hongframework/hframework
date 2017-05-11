package com.hframework.common.frame.cache;

import java.util.List;

/**
 * Cache实例
 */
public interface ICache {

	/**
	 * 放入键值对
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);

	/**
	 * 获取键值对
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * 删除键值对
	 * @param key
	 */
	public void remove(String key);

	/**
	 * 获取所有键
	 * @return
	 */
	public List<String> getKeys();

	/**
	 * 删除所有键值对
	 */
	public void  removeAll();
}
