package com.hframework.smartsql.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.hframework.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhangquanhong on 2017/8/10.
 */
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private static final Boolean REGISTER_DATABASE_IGNORE_KEY_REPEAT = true;
    private static ThreadLocal<String> currentDatabaseKey = new ThreadLocal<String>();

    private static Map<String, RedisInfo> dbInfoCache = new HashMap<String, RedisInfo>();
    private static Map<String, JedisPool> cache = new HashMap<String, JedisPool>();

    public static void registerRedis(String key, String host, Integer port, String auth, int database) {
        logger.info("register database :{},{},{},{}", key, host, port, database);
        if(dbInfoCache.containsKey(key)) {
            if(!REGISTER_DATABASE_IGNORE_KEY_REPEAT) {
                throw new RuntimeException("register database key [" + key + "] exists !");
            }
        }else {
            dbInfoCache.put(key, new RedisInfo(host, port, auth, database));
        }
        logger.info("register database success :{},{},{}", key, host, port);
        setCurrentRedisKey(key);
    }

    public static void setCurrentRedisKey(String key) {
        currentDatabaseKey.set(key);
        logger.info("set current database success :{}", key);
    }

    public static String getCurrentRedisKey() {
        String key = currentDatabaseKey.get();
        if(StringUtils.isBlank(key)) {
            throw new RuntimeException("get current database key [" + key + "] failed , not exists !");
        }
        logger.info("get current database success :{}", key);
        return key;
    }

    public static JedisPool getPool(String key) {
        if(!dbInfoCache.containsKey(key)) {
            throw new RuntimeException("get datasource failed, [" + key + "] 's not register !");
        }
        RedisInfo dbInfo = dbInfoCache.get(key);
        return getPool(dbInfo.getHost(), dbInfo.getPort(), dbInfo.getAuth(), dbInfo.getDatabase());
    }

    private static JedisPool getPool(String host, Integer port, String auth, int database) {
        String cacheKey = Joiner.on("|").join(new String[]{host, String.valueOf(port), auth, String.valueOf(database)});
        if(!cache.containsKey(cacheKey)) {
            synchronized (RedisClient.class) {
                if(!cache.containsKey(cacheKey)) {
                    try {
                        cache.put(cacheKey, getDataSourceInternal(host, port, auth, database));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException("datasource create error => " + e.getMessage());
                    }
                }
            }
        }
        return cache.get(cacheKey);
    }

    private static JedisPool getDataSourceInternal(String host, Integer port, String auth, int database) throws SQLException {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setMaxWaitMillis(1000);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setMaxTotal(2014);

        int timeout = 2000;

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, auth, database);

        return jedisPool;
    }

    public static Set<String> keys(String pattern){
        return keys(getCurrentRedisKey(), pattern);
    }

    public static Set<String> keys(String dbKey, String pattern){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.keys(pattern);
        } catch (Exception e) {
            logger.warn("keys failed.", e);
            return null;
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public static Map<String ,String> hGet(String dbKey, String key){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.warn("hget failed.key:{}", key, e);
            return Collections.emptyMap();
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public static String hGet(String dbKey, String key, String field){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.warn("hget failed.key:{}", key, e);
            return null;
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public static Boolean sIsMember(String dbKey, String key, String member){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            logger.warn("hget failed.key:{}", key, e);
            return false;
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public static Long sAdd(String dbKey, String key, String member){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.sadd(key, member);
        } catch (Exception e) {
            logger.warn("hget failed.key:{}", key, e);
            return 0L;
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public static Long setnx(String dbKey, String key, String value){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.setnx(key, value);
        } catch (Exception e) {
            logger.warn("hget failed.key:{}", key, e);
            return null;
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
    }


    public static Map<String, Object> getList(List<String> keys) {
        return getList(getCurrentRedisKey(), keys);
    }

    public static Map<String, Object> getList(String dbKey, final List<String> keys){
        logger.debug("get keys:{}",keys);
        return zip(keys, pipeline(dbKey, new PipelineExecutor() {
            public void execute(Pipeline pipeline) {
                for (String key : keys) {
                    pipeline.get(key);
                }
            }
        }));
    }

    public static Map<String, Object> hGetList(List<String> keys){
        return hGetList(getCurrentRedisKey(), keys);
    }


    public static Map<String, Object> keysList(String dbKey, final List<String> patterns){
        logger.debug("keys patterns :{}", patterns);
        return zip(patterns, pipeline(dbKey, new PipelineExecutor() {
            public void execute(Pipeline pipeline) {
                for (String pattern : patterns) {
                    pipeline.keys(pattern);
                }
            }
        }));
    }

    public static Map<String, Object> hGetList(String dbKey, final List<String> keys){
        logger.debug("hget keys:{}", keys);
        return zip(keys, pipeline(dbKey, new PipelineExecutor() {
            public void execute(Pipeline pipeline) {
                for (String key : keys) {
                    pipeline.hgetAll(key);
                }
            }
        }));
    }

    public static Map<String, Object> zip(List<String> keys, List<Object> data){
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i), data.get(i));
        }
        return result;
    }

    public static List<Object> pipeline(String dbKey, PipelineExecutor executor){
        Jedis jedis = getPool(dbKey).getResource();
        long start= System.currentTimeMillis();
        try {
            Pipeline pipeline = jedis.pipelined();
            executor.execute(pipeline);
            return pipeline.syncAndReturnAll();
        } catch (Exception e) {
            logger.warn("pipeline failed: {}", e);
            return Lists.newArrayList();
        } finally {
            long spent  = System.currentTimeMillis() - start;
            if (spent > 500){
                logger.warn("pipeline timeout({}ms)", spent);
            }
            getPool(dbKey).returnResourceObject(jedis);
        }
    }

    public interface PipelineExecutor {
        void execute(Pipeline pipeline);
    }

    public static String get(String dbKey, String key){
        Jedis jedis = getPool(dbKey).getResource();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            logger.warn("get failed.key:{}", key, e);
        } finally {
            getPool(dbKey).returnResourceObject(jedis);
        }
        return null;
    }

    public static class RedisInfo {
        private String host;
        private Integer port;
        private String auth;
        private int database;

        public RedisInfo(String host, Integer port, String auth, int database) {
            this.host = host;
            this.port = port;
            this.auth = auth;
            this.database = database;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }

    /**
     * 值递增
     * @param key 键
     * @return
     * @throws Exception
     */
    public static Long increase(String dbKey, RedisKey key) throws Exception{
        return increase(dbKey, key.getKey(), key.getInitValue(), key.getExpiredSeconds());
    }

    /**
     * 值递增
     * @param key 键
     * @return
     * @throws Exception
     */
    public static Long increase(String dbKey, RedisKey key, long step,String... keyKeyWords) throws Exception{
        return increase(dbKey, key.getKey(keyKeyWords), key.getInitValue(), key.getExpiredSeconds(), step);
    }

    /**
     * 值递增
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long increase(String dbKey, String key, Long initValue, int expiredSeconds, long step) throws Exception {
        logger.debug("param:{}",key,initValue,expiredSeconds, step);
        JedisPool jedisPool = getPool(dbKey);
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
                    if(expiredSeconds == -1) {
                        jedis.setnx(key, String.valueOf(initValue + step));
                    }else {
                        jedis.setex(key, expiredSeconds, String.valueOf(initValue + step));
                    }
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
    public static Long increase(String dbKey, String key, Long initValue, int expiredSeconds) throws Exception {
        logger.debug("param:{}",key,initValue,expiredSeconds);
        JedisPool jedisPool = getPool(dbKey);
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
                    if(expiredSeconds == -1) {
                        jedis.setnx(key, String.valueOf(++initValue));
                    }else {
                        jedis.setex(key, expiredSeconds, String.valueOf(++initValue));
                    }
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
    public static Long decrease(String dbKey, RedisKey key, long step) throws Exception{
        return decrease(dbKey, key.getKey(), key.getInitValue(), key.getExpiredSeconds(), step);
    }


    /**
     * 值递减
     * @param key 键
     * @return
     */
    public static Long decrease(String dbKey, RedisKey key) throws Exception{
        return decrease(dbKey, key.getKey(),key.getInitValue(),key.getExpiredSeconds());
    }

    /**
     * 值递减
     * @param key 键
     * @param initValue 初始值
     * @param expiredSeconds 键失效秒数
     * @return
     */
    public static Long decrease(String dbKey, String key, Long initValue, int expiredSeconds, long step) throws Exception{
        logger.debug("param:{}",key,initValue,expiredSeconds, step);
        JedisPool jedisPool = getPool(dbKey);
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
                    if(expiredSeconds == -1) {
                        jedis.setnx(key, String.valueOf(initValue - step));
                    }else {
                        jedis.setex(key, expiredSeconds, String.valueOf(initValue - step));
                    }
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
    public static Long decrease(String dbKey, String key, Long initValue, int expiredSeconds) throws Exception{
        logger.debug("param:{}",key,initValue,expiredSeconds);
        JedisPool jedisPool = getPool(dbKey);
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
                    if(expiredSeconds == -1) {
                        jedis.setnx(key, String.valueOf(--initValue));
                    }else {
                        jedis.setex(key, expiredSeconds, String.valueOf(--initValue));
                    }
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
    public static Long getValueAsLong(String dbKey, RedisKey key) throws Exception {
        return getValueAsLong(dbKey, key.getKey(), key.getInitValue());
    }

    /**
     * 值查询
     * @param key 键
     * @return
     */
    public static Long getValueAsLong(String dbKey, String key, long defaultValue) throws Exception {
        logger.debug("param:{}",key);
        JedisPool jedisPool = getPool(dbKey);;
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
}
