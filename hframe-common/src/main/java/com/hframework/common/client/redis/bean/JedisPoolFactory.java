package com.hframework.common.client.redis.bean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolFactory {
	private static JedisPool jedisPool;

	private static JedisPoolConfig initPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 控制一个pool最多有多少个状态为idle的jedis实例
		// jedisPoolConfig.setMaxActive(1000);
		jedisPoolConfig.setMaxTotal(Integer.parseInt(RedisConfig.REDIS_POOL_MAXACTIVE));
		// 最大能够保持空闲状态的对象数
		jedisPoolConfig.setMaxIdle(Integer.parseInt(RedisConfig.REDIS_POOL_MAXIDLE));
		// 超时时间
		// jedisPoolConfig.setMaxWait(1000);
		jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(RedisConfig.REDIS_POOL_MAXWAIT) * 1000);
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
		jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(RedisConfig.REDIS_POOL_TESTONBORROW));
		// 在还会给pool时，是否提前进行validate操作
		jedisPoolConfig.setTestOnReturn(Boolean.parseBoolean(RedisConfig.REDIS_POOL_TESTONRETURN));
		return jedisPoolConfig;
	}

	/** 初始化jedis连接池 */
	// @BeforeClass
	public static void before() {
		JedisPoolConfig jedisPoolConfig = initPoolConfig();
		// 属性文件读取参数信息

		String host = RedisConfig.REDIS_IP;
		int port = Integer.valueOf(RedisConfig.REDIS_PORT);
		int timeout = Integer.valueOf(RedisConfig.REDIS_DATA_TIMEOUT);
		String pwd = RedisConfig.REDIS_PWD;
		int database = Integer.valueOf(RedisConfig.REDIS_DATABASE);

		// 构造连接池
		jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, pwd, database);
	}

	public static Jedis getInstance() {
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}

	public static JedisPool getJedisPool() {
		if (jedisPool == null) {
			before();
		}

		return jedisPool;
	}
}
