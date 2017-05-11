package com.hframework.common.client.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hframework.common.util.message.JsonUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/2/18 15:36:36
 */
public class RedisService {
    private RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }


    /**
     * 添加对象
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> Boolean add(final String key, final T t) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(String.valueOf(key));
                String value = null;
                try {
                    value = JsonUtils.writeValueAsString(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] json = serializer.serialize(value);
                return connection.setNX(keybt, json);
            }
        });
    }

    /**
     * 修改
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> boolean update(final String key, final T t) {
        if (!contains(key)) {
            throw new NullPointerException("数据行不存在, key = " + key);
        }
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keyTemp = serializer.serialize(key);
                String value = null;
                try {
                    value = JsonUtils.writeValueAsString(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] json = serializer.serialize(value);
                connection.set(keyTemp, json);
                return true;
            }
        });
    }

    /**
     * 保存或更新
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> boolean saveOrUpdate(final String key, final T t) {
        if (!contains(key)) {
            //如果值不存在，则添加入库
            return this.add(key, t);
        }
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keyTemp = serializer.serialize(key);
                String value = null;
                try {
                    value = JsonUtils.writeValueAsString(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] json = serializer.serialize(value);
                connection.set(keyTemp, json);
                return true;
            }
        });
    }

    /**
     * 保存或更新
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public <T> boolean saveOrUpdate(final String key, final T t, final Long seconds) {
        if (!contains(key)) {
            //如果值不存在，则添加入库
            return this.add(key, t);
        }
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keyTemp = serializer.serialize(key);
                String value = null;
                try {
                    value = JsonUtils.writeValueAsString(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] json = serializer.serialize(value);
                connection.setEx(keyTemp, seconds, json);
                return true;
            }
        });
    }

    /**
     * map， 值递增
     * @param key
     * @param field
     * @param delta
     * @param <T>
     * @return
     */
    public <T> boolean hIncrBy(final String key, final T field,final Long delta) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keyTemp = serializer.serialize(key);
                byte[] fieldTemp = serializer.serialize(String.valueOf(field));
                connection.hIncrBy(keyTemp, fieldTemp, delta);
                return true;
            }
        });
    }

    /**
     * 删除
     * @param key
     */
    public void delete(String key) {
        List<String> list = new ArrayList<String>();
        list.add(key);
        delete(list);
    }
    /**
     * 批量删除
     * @param keys
     */
    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    public Boolean contains(final String key) {
        Boolean result = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return false;
                }
                return true;
            }
        });
        return result;
    }
    public <T> T get(final String key, final Class... cls) {
        T result = (T) redisTemplate.execute(new RedisCallback<T>() {
            public T doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                try {
                    return (T) JsonUtils.readValue(serializer.deserialize(value),cls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return result;
    }

    public <T> T get(final String key, final Class<T> cls) {
        T result = (T) redisTemplate.execute(new RedisCallback<T>() {
            public T doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                try {
                    return JsonUtils.readValue(serializer.deserialize(value),cls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return result;
    }

    /**
     * 查询字符串
     * @param keyId
     * @return
     */
    public String getAsString(final String keyId) {
        String result = (String) redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(keyId);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                String mjson = serializer.deserialize(value);
                return mjson;
            }
        });
        return result;
    }

    /**
     * 查询Long对象
     * @param keyId
     * @return
     */
    public long getAsLong(final String keyId) {
        Long result = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(keyId);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                return Long.valueOf(serializer.deserialize(value));
            }
        });
        return result;
    }


    public <T> List<T> getList(final String key, final Class<T> cls) {
        List<T> result = (List<T>) redisTemplate.execute(new RedisCallback<List<T>>() {
            public List<T> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                String mjson = serializer.deserialize(value);
                return JSON.parseArray(mjson, cls);
            }
        });
        return result;
    }


    public <K,T> Map<K,T> getMap(final String key, final Class<T> cls) {
        Map<K,T> result = (Map<K, T>) redisTemplate.execute(new RedisCallback<Map<K, T>>() {
            public Map<K, T> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                Map<K, T> resultMap = new HashMap<K, T>();
                String mjson = serializer.deserialize(value);
                if (mjson != null) {
                    JSONObject jsonObject = JSON.parseObject(mjson);
                    Iterator<String> iterator = jsonObject.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String val = jsonObject.getString(key);
                        K k = (K) key;
                        T t = (T) JSON.parseObject(val, cls);
                        resultMap.put(k, t);
                    }
                }
                return resultMap;
            }
        });
        return result;
    }

    public <K,T> Map<K,List<T>> getMapList(final String key, final Class<T> cls) {
        Map<K,List<T>> result = (Map<K, List<T>>) redisTemplate.execute(new RedisCallback<Map<K, List<T>>>() {
            @SuppressWarnings("unchecked")
            public Map<K, List<T>> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                Map<K, List<T>> resultMap = new HashMap<K, List<T>>();
                String mjson = serializer.deserialize(value);
                if (mjson != null) {
                    JSONObject jsonObject = JSON.parseObject(mjson);
                    Iterator<String> iterator = jsonObject.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String val = jsonObject.getString(key);
                        K k = (K) key;
                        List<T> t = (List<T>) JSON.parseArray(val, cls);
                        resultMap.put(k, t);
                    }
                }
                return resultMap;
            }
        });
        return result;
    }

    public <K,T> Map<K,Map<K,T>> getMapTree(final String key, final Class<T> cls) {
        Map<K,Map<K,T>> result = (Map<K, Map<K, T>>) redisTemplate.execute(new RedisCallback<Map<K, Map<K, T>>>() {
            @SuppressWarnings("unchecked")
            public Map<K, Map<K, T>> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keybt = serializer.serialize(key);
                byte[] value = connection.get(keybt);
                if (value == null) {
                    return null;
                }
                Map<K, Map<K, T>> resultMap = new HashMap<K, Map<K, T>>();
                String mjson = serializer.deserialize(value);
                if (mjson != null) {
                    JSONObject jsonObject = JSON.parseObject(mjson);
                    Iterator<String> iterator = jsonObject.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String val = jsonObject.getString(key);
                        K k = (K) key;
                        JSONObject jsonObjectTemp = JSON.parseObject(val);
                        Iterator<String> iteratorTemp = jsonObjectTemp.keySet().iterator();
                        Map<K, T> map = new HashMap<K, T>();
                        while (iteratorTemp.hasNext()) {
                            String keyTemp = iteratorTemp.next();
                            String valTemp = jsonObjectTemp.getString(keyTemp);
                            K ktemp = (K) keyTemp;
                            T ttemp = JSON.parseObject(valTemp, cls);
                            map.put(ktemp, ttemp);
                        }
                        resultMap.put(k, map);
                    }
                }
                return resultMap;
            }
        });
        return result;
    }

    /**
     * 获取redis map key
     * @param cls
     * @return
     */
    public String getRedisMapKey(Class<?> cls){
        return cls.asSubclass(cls).getSimpleName() + "_map";
    }
}
