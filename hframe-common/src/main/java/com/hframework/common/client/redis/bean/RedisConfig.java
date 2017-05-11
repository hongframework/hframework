package com.hframework.common.client.redis.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisConfig {

	public static String REDIS_PORT;

	@Value("${redis_port}")
	public void setRECOMMEND_PORT(String redis_port) {
		REDIS_PORT = redis_port;
	}

	public static String REDIS_IP;

	@Value("${redis_ip}")
	public void setRECOMMEND_IP(String redis_ip) {
		REDIS_IP = redis_ip;
	}

	public static String REDIS_POOL_TESTONRETURN;

	@Value("${redis_pool_testOnReturn}")
	public void setREDIS_POOL_TESTONRETURN(String redis_pool_testOnReturn) {
		REDIS_POOL_TESTONRETURN = redis_pool_testOnReturn;
	}

	public static String REDIS_POOL_TESTONBORROW;

	@Value("${redis_pool_testOnBorrow}")
	public void setREDIS_POOL_TESTONBORROW(String redis_pool_testOnBorrow) {
		REDIS_POOL_TESTONBORROW = redis_pool_testOnBorrow;
	}

	public static String REDIS_POOL_MAXWAIT;

	@Value("${redis_pool_maxWait}")
	public void setREDIS_POOL_MAXWAIT(String redis_pool_maxWait) {
		REDIS_POOL_MAXWAIT = redis_pool_maxWait;
	}

	public static String REDIS_POOL_MAXIDLE;

	@Value("${redis_pool_maxIdle}")
	public void setREDIS_POOL_MAXIDLE(String redis_pool_maxIdle) {
		REDIS_POOL_MAXIDLE = redis_pool_maxIdle;
	}

	public static String REDIS_POOL_MAXACTIVE;

	@Value("${redis_pool_maxActive}")
	public void setREDIS_POOL_MAXACTIVE(String redis_pool_maxActive) {
		REDIS_POOL_MAXACTIVE = redis_pool_maxActive;
	}

	// redis_data_timeout

	public static String REDIS_DATA_TIMEOUT;

	@Value("${redis_data_timeout}")
	public void setREDIS_DATA_TIMEOUT(String redis_data_timeout) {
		REDIS_DATA_TIMEOUT = redis_data_timeout;
	}

	public static String REDIS_PWD;

	@Value("${redis_pwd}")
	public void setREDIS_PWD(String redis_pwd) {
		REDIS_PWD = redis_pwd;
	}

	public static String REDIS_DATABASE;

	@Value("${redis_database}")
	public void   setREDIS_DATABASE(String redisDatabase) {
		REDIS_DATABASE = redisDatabase;
	}
}
