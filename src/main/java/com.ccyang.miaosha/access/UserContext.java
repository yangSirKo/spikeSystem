package com.ccyang.miaosha.access;

import com.ccyang.miaosha.domain.MiaoshaUser;

public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }
}
