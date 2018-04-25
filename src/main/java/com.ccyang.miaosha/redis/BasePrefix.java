package com.ccyang.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds ;   // 默认0 表示永不过期
    private String prefix;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        this(0,prefix);
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = this.getClass().getSimpleName();
        return className +":"+ prefix;
    }
}
