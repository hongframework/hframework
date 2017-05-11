package com.hframework.common.client.redis;

import com.hframework.common.client.redis.bean.JedisPoolFactory;
import com.hframework.common.util.DateUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangquanhong on 2016/5/19.
 */
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private JedisPoolConfig jedisPoolConfig;
    private JedisPool jedisPool;

    public static RedisClient getInstance(String confName) throws ConfigurationException {
        RedisClient redisClient = new RedisClient();
        redisClient.init(confName);
        return redisClient;
    }

    public void init(String confName) throws ConfigurationException {
        URL url = getClass().getClassLoader().getResource(confName);
        PropertiesConfiguration configuration = new PropertiesConfiguration(url);

        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(configuration.getInt("redis.pool.maxIdle", 200));
        jedisPoolConfig.setMaxWaitMillis(configuration.getLong("redis.pool.maxWait", 1000));
        jedisPoolConfig.setTestOnBorrow(configuration.getBoolean("redis.pool.testOnBorrow", true));
        jedisPoolConfig.setTestOnReturn(configuration.getBoolean("redis.pool.testOnReturn", false));
        jedisPoolConfig.setMaxTotal(configuration.getInt("redis.pool.maxActive", 2014));

        String host = configuration.getString("redis.host", "127.0.0.1");
        int port = configuration.getInt("redis.port", 3306);
        int database = configuration.getInt("redis.database", 0);
        String auth = configuration.getString("redis.auth", "");
        int timeout = configuration.getInt("redis.timeout", 1);

        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, auth, database);
    }

    /**
     * 值递增
     * @param key 键
     * @return
     * @throws Exception
     */
    public static Long increase(RedisKey key) throws Exception{
        return increase(key.getKey(), key.getInitValue(), key.getExpiredSeconds());
    }

    /**
     * 值递增
     * @param key 键
     * @return
     * @throws Exception
     */
    public static Long increase(RedisKey key, long step,String... keyKeyWords) throws Exception{
        return increase(key.getKey(keyKeyWords), key.getInitValue(), key.getExpiredSeconds(), step);
    }

    /**
     * 值递增
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long increase(String key, Long initValue, int expiredSeconds, long step) throws Exception {
        logger.debug("param:{}",key,initValue,expiredSeconds, step);
        JedisPool jedisPool = JedisPoolFactory.getJedisPool();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                Long newValue = jedis.incrBy(key, step);
                logger.debug("return :{}",newValue);
                return newValue;
            } else {
                logger.info("lock required:{}", key + "_LOCK");
                if(acquireOnce(jedis,key+"_LOCK")){
                    logger.info("lock success:{}", key + "_LOCK");
                    jedis.setex(key, expiredSeconds, String.valueOf(initValue + step));
                    logger.debug("return :{}", initValue + step);
                    return initValue + step;
                }else {
                    logger.info("lock failed:{}", key + "_LOCK");
                    Thread.sleep(1000L);
                    logger.debug("lock failed and wait {} mill seconds :{}", key + "_LOCK");
                    if (jedis.exists(key)) {
                        Long newValue = jedis.incrBy(key, step);
                        logger.debug("return :{}",newValue);
                        return newValue;
                    }
                    logger.error("lock failed and wait {} mill seconds and failed too");
                    throw new Exception("获取失败");
                }
            }
        } catch (Exception e) {
            logger.error("longin check faield.|{}", e);
            throw e;
        } finally {
            // 释放对象池
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * 值递增
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long increase(String key, Long initValue, int expiredSeconds) throws Exception {
        logger.debug("param:{}",key,initValue,expiredSeconds);
        JedisPool jedisPool = JedisPoolFactory.getJedisPool();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                Long newValue = jedis.incr(key);
                return newValue;
            } else {
                logger.info("lock required:{}", key + "_LOCK");
                if(acquireOnce(jedis,key+"_LOCK")){
                    logger.info("lock success:{}", key + "_LOCK");
                    jedis.setex(key, expiredSeconds, String.valueOf(--initValue));
                    return initValue;
                }else {
                    logger.info("lock failed:{}", key + "_LOCK");
                    Thread.sleep(1000L);
                    logger.debug("lock failed and wait {} mill seconds :{}", key + "_LOCK");
                    if (jedis.exists(key)) {
                        Long newValue = jedis.incr(key);
                        return newValue;
                    }
                    logger.error("lock failed and wait {} mill seconds and failed too");
                    throw new Exception("获取失败");
                }
            }
        } catch (Exception e) {
            logger.error("longin check faield.|{}", e);
            throw e;
        } finally {
            // 释放对象池
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * 值递减
     * @param key 键
     * @return
     */
    public static Long decrease(RedisKey key, long step) throws Exception{
        return decrease(key.getKey(), key.getInitValue(), key.getExpiredSeconds(), step);
    }


    /**
     * 值递减
     * @param key 键
     * @return
     */
    public static Long decrease(RedisKey key) throws Exception{
        return decrease(key.getKey(),key.getInitValue(),key.getExpiredSeconds());
    }

    /**
     * 值递减
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long decrease(String key, Long initValue, int expiredSeconds, long step) throws Exception{
        logger.debug("param:{}",key,initValue,expiredSeconds, step);
        JedisPool jedisPool = JedisPoolFactory.getJedisPool();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                Long newValue = jedis.decrBy(key, step);
                logger.debug("result:{}",newValue);
                return newValue;
            } else {
                logger.info("lock required:{}", key + "_LOCK");
                if(acquireOnce(jedis,key+"_LOCK")){
                    logger.info("lock success:{}", key + "_LOCK");
                    jedis.setex(key, expiredSeconds, String.valueOf(initValue - step));
                    logger.debug("result:{}", initValue - step);
                    return initValue - step;
                }else {
                    logger.info("lock failed:{}", key + "_LOCK");
                    Thread.sleep(1000L);
                    logger.debug("lock failed and wait {} mill seconds :{}", key + "_LOCK");
                    if (jedis.exists(key)) {
                        Long newValue = jedis.decrBy(key, step);
                        logger.debug("result:{}",newValue);
                        return newValue;
                    }
                    logger.error("lock failed and wait {} mill seconds and failed too");
                    throw new Exception("获取失败");
                }
            }
        } catch (Exception e) {
            logger.error("longin check faield.|{}", e);
            throw e;
        } finally {
            // 释放对象池
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * 值递减
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long decrease(String key, Long initValue, int expiredSeconds) throws Exception{
        logger.debug("param:{}",key,initValue,expiredSeconds);
        JedisPool jedisPool = JedisPoolFactory.getJedisPool();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                Long newValue = jedis.decr(key);
                return newValue;
            } else {
                logger.info("lock required:{}", key + "_LOCK");
                if(acquireOnce(jedis,key+"_LOCK")){
                    logger.info("lock success:{}", key + "_LOCK");
                    jedis.setex(key, expiredSeconds, String.valueOf(--initValue));
                    return initValue;
                }else {
                    logger.info("lock failed:{}", key + "_LOCK");
                    Thread.sleep(1000L);
                    logger.debug("lock failed and wait {} mill seconds :{}", key + "_LOCK");
                    if (jedis.exists(key)) {
                        Long newValue = jedis.decr(key);
                        return newValue;
                    }
                    logger.error("lock failed and wait {} mill seconds and failed too");
                    throw new Exception("获取失败");
                }
            }
        } catch (Exception e) {
            logger.error("longin check faield.|{}", e);
            throw e;
        } finally {
            // 释放对象池
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * 值查询
     * @param key 键
     * @return
     */
    public static Long getValueAsLong(RedisKey key) throws Exception {
        return getValueAsLong(key.getKey(), key.getInitValue());
    }

    /**
     * 值查询
     * @param key 键
     * @return
     */
    public static Long getValueAsLong(String key, long defaultValue) throws Exception {
        logger.debug("param:{}",key);
        JedisPool jedisPool = JedisPoolFactory.getJedisPool();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)){
                logger.debug("param:{}", key, Long.valueOf(jedis.get(key)));
                return Long.valueOf(jedis.get(key));
            }
            logger.debug("param:{}",key, defaultValue);
            return defaultValue;
        } catch (Exception e) {
            logger.warn("checkImgVerifyCode.|{}|{}", key, e);
            throw e;
        } finally {
            // 释放对象池
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * 尝试占用一次
     * @param jedis
     * @param lockKey
     * @return
     */
    public static synchronized boolean acquireOnce(Jedis jedis, String lockKey) {
        try {
            return acquire(jedis,lockKey,1,100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized boolean acquire(Jedis jedis, String lockKey, int maxAttemptTimes, int attemptIntervalMillis) throws InterruptedException {
        int attemptCount = 0;
        int expiredMillis = attemptIntervalMillis * 10;
        while (attemptCount < maxAttemptTimes) {
            if(attemptCount != 0) {
                Thread.sleep(attemptIntervalMillis);
            }
            if (jedis.setnx(lockKey, String.valueOf(System.currentTimeMillis() +expiredMillis)) == 1) {
                // lock acquired
                return true;
            }

            String lockValue = jedis.get(lockKey); //redis里的时间
            if (lockValue != null && Long.parseLong(lockValue) < System.currentTimeMillis()) {
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                // lock is expired

                String oldValueStr = jedis.getSet(lockKey, String.valueOf(System.currentTimeMillis() + expiredMillis));
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(lockValue)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    return true;
                }
            }
            attemptCount ++ ;
        }
        return false;
    }

    public void pipeLine(IExecutor executor) {
        Jedis jedis = jedisPool.getResource();
        long start= System.currentTimeMillis();
        try {
            Pipeline pipeLine = jedis.pipelined();
            executor.execute(pipeLine);
            pipeLine.sync();
        } catch (Exception e) {

        } finally {
            long spent  = System.currentTimeMillis() - start;
            if (spent > 500){
                logger.warn("Huge redis pipe line({}ms)", spent);
            }
            jedisPool.returnResourceObject(jedis);
        }
    }

    public interface IExecutor {
        void execute(Pipeline pipeline);
    }

    public Set<String> keys(String v) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.keys(v);
        } catch (Exception e) {
            logger.warn("keys failed.", e);
            return null;
        } finally {
            getPool().returnResourceObject(jedis);
        }
    }

    public Map<String ,String> hGetAll(String key){
        Jedis jedis = getPool().getResource();
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.warn("hgetall failed.key:{}", key, e);
            return Collections.emptyMap();
        } finally {
            getPool().returnResourceObject(jedis);
        }

    }
    public JedisPool getPool() {
        return jedisPool;
    }

    public enum RedisKey {

        PLANT_TODAY_LUCK_MONEY_TOTAL_AMOUNT("PLANT_TODAY_LUCK_MONEY_TOTAL_AMOUNT_", "yyyyMMdd",
                10000L, 24 * 60 * 60 * 5);



        private String keyPrefix;
        private String keyId;
        private long initValue;
        private int expiredSeconds;
        RedisKey(String keyPrefix, String keyId, long initValue, int expiredSeconds) {
            this.keyPrefix = keyPrefix;
            this.keyId = keyId;
            this.initValue = initValue;
            this.expiredSeconds = expiredSeconds;
        }

        public String getKey() {
            if(keyId.equals(YYYYMMDD)) {
                return keyPrefix + DateUtils.getCurrentDate(YYYYMMDD);
            }
            return keyPrefix;
        }
        public String getKey(String[] keyKeyWords) {

            if(keyKeyWords != null) {
                String result = keyPrefix;
                for (String keyKeyWord : keyKeyWords) {
                    result += keyKeyWord;
                }
                return result;
            }
            return getKey();
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public long getInitValue() {
            return initValue;
        }

        public void setInitValue(long initValue) {
            this.initValue = initValue;
        }

        public int getExpiredSeconds() {
            return expiredSeconds;
        }

        public void setExpiredSeconds(int expiredSeconds) {
            this.expiredSeconds = expiredSeconds;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public static  final  String YYYYMMDD = "yyyyMMdd";
    }

    public static void main(String[] args) throws ConfigurationException {
        RedisClient redisClient = RedisClient.getInstance("properties/redis-user.properties");

        Set<String> keys = redisClient.keys("*");
        System.out.println(keys);
        System.out.println(redisClient.hGetAll("get_lcs_update_time"));
    }

}
