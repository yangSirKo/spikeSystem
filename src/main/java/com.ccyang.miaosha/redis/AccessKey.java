package com.ccyang.miaosha.redis;

public class AccessKey extends BasePrefix{


    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey newAccessKey(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }
}
