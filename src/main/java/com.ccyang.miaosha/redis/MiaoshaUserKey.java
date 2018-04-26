package com.ccyang.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

    private static final int TOKEN_EXPIRE = 3600*24*2;  // two days

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");

}
