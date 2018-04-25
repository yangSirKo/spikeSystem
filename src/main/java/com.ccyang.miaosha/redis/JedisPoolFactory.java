package com.ccyang.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class JedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean("jedisPool")
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxldle());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);   // 秒转化为毫秒

        JedisPool jp = new JedisPool(jedisPoolConfig,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);
        return jp;
    }

}
