package com.ccyang.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.redis.AccessKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.MiaoshaUserService;
import com.ccyang.miaosha.util.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod)handler;
            MiaoshaUser user = getMiaoshaUser(request,response);
            UserContext.setUser(user);

            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();


            if(needLogin){  // need user login
                if(user == null) {
                    WebUtil.render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key +="_" + user.getId();
            } else {
                // do nothing
            }

            AccessKey ak = AccessKey.newAccessKey(seconds);
            Integer count = redisService.get(ak,key,Integer.class);
            if(count == null){
                redisService.set(ak,key,1);
            }else if(count < maxCount){
                redisService.incr(ak,key);
            }else{
                WebUtil.render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest request, HttpServletResponse response){
        // 手机端传输使用 RequestParameter
        String requestToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        // 浏览器传输
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(requestToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(requestToken) ? cookieToken : requestToken;
        return miaoshaUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request , String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for (Cookie cookie : cookies){
            if(cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
