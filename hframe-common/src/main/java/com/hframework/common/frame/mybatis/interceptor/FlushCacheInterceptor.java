package com.hframework.common.frame.mybatis.interceptor;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Intercepts( {
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class FlushCacheInterceptor implements Interceptor {
	
	private String property;

	private Properties properties;

	private ConcurrentMap<String, Set<CacheKey>> keyMap = new ConcurrentHashMap<String, Set<CacheKey>>();

	public Object intercept(Invocation invocation) throws Throwable {

		MappedStatement mappedStatement = (MappedStatement) invocation
				.getArgs()[0];
        RoutingStatementHandler statement = (RoutingStatementHandler) invocation.getTarget();
        BoundSql boundSql = statement.getBoundSql();

		if (!mappedStatement.getConfiguration().isCacheEnabled())
			return invocation.proceed();

		String sqlId = mappedStatement.getId();
		String nameSpace = sqlId.substring(0, sqlId.indexOf('.'));
		Executor exe = (Executor) invocation.getTarget();
		String methodName = invocation.getMethod().getName();
		if (methodName.equals("query")) {
			for (Object key : properties.keySet()) {
//				if (key.equals(sqlId)) {
				if (true) {
					Object parameter = invocation.getArgs()[1];
					RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
					Cache cache = mappedStatement.getConfiguration().getCache(
							nameSpace);
					cache.getReadWriteLock().readLock().lock();
					CacheKey cacheKey = exe.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
					try {
						if (cache.getObject(cacheKey) == null) {
							if (keyMap.get(sqlId) == null) {
								Set<CacheKey> cacheSet = new HashSet<CacheKey>();
								cacheSet.add(cacheKey);
								keyMap.put(sqlId, cacheSet);
							} else {
								keyMap.get(sqlId).add(cacheKey);
							}
						}
					} finally {
						cache.getReadWriteLock().readLock().unlock();
					}
					break;
				}
			}
		} else if (methodName.equals("update")) {
			for (Enumeration e = properties.propertyNames(); e
					.hasMoreElements();) {
				String cacheSqlId = (String) e.nextElement();
				String updateNameSpace = properties.getProperty(cacheSqlId);
				if (updateNameSpace.equals(nameSpace)) {
					String cacheNamespace = cacheSqlId.substring(0, cacheSqlId
							.indexOf('.'));
					Cache cache = mappedStatement.getConfiguration().getCache(
							cacheNamespace);
					Set<CacheKey> cacheSet = keyMap.get(cacheSqlId);
					cache.getReadWriteLock().writeLock().lock();
					try {
						for (Iterator it = cacheSet.iterator(); it.hasNext();) {
							cache.removeObject(it.next());
						}
					} finally {
						cache.getReadWriteLock().writeLock().unlock();
						keyMap.remove(cacheSqlId);
					}

				}
			}
		}

		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
