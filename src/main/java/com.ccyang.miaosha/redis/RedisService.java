package com.ccyang.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取值
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = StringToBean(str, clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置值
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = BeanToString(value);
            if(str == null || str.length() <= 0){
                return false;
            }
            // 获得真实的 key
            String realKey = keyPrefix.getPrefix()+key;
            if(keyPrefix.expireSeconds() <= 0){ // 不过期
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey,keyPrefix.expireSeconds(),str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 键对应的值是否存在
     */
    public <T> boolean exists(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // 获得真实的 key
            String realKey = keyPrefix.getPrefix()+key;
            Boolean b = jedis.exists(realKey);
            return b;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 键对应的值自增
     */
    public <T> long incr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // 获得真实的 key
            String realKey = keyPrefix.getPrefix()+key;
            Long l = jedis.incr(realKey);
            return l;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 键对应的值自减
     */
    public <T> long decr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // 获得真实的 key
            String realKey = keyPrefix.getPrefix()+key;
            Long l = jedis.decr(realKey);
            return l;
        }finally {
            returnToPool(jedis);
        }
    }


    private <T> String BeanToString(T value) {
        if(value == null){
            return null;
        }
        Class clazz = value.getClass();
        if( clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if(clazz == long.class || clazz == Long.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String)value;
        }else{
            return JSON.toJSONString(value);
        }
    }

    private <T> T StringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if( clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

}
