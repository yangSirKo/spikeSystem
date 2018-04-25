package com.ccyang.miaosha.redis;

public interface KeyPrefix {

    /**
     * 设置过期时间
     * @return
     */
    public int expireSeconds();

    /**
     * 获取键前缀
     * @return
     */
    public String getPrefix();

}
