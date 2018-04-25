package com.ccyang.miaosha.redis;

public class OrderKey extends BasePrefix{
    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    private OrderKey(String prefix) {
        super(prefix);
    }


}
