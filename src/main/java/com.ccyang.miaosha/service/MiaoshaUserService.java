package com.ccyang.miaosha.service;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.dao.MiaoshaUserDao;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.exception.GlobalException;
import com.ccyang.miaosha.redis.MiaoshaUserKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.util.MD5Util;
import com.ccyang.miaosha.util.UUIDUtil;
import com.ccyang.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     * based on MiaoshaUser id get MiaoshaUser Object
     * @param id
     * @return
     */
    public MiaoshaUser getById(long id){
        // get cache MiaoshaUser
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user != null){
            return user;
        }
        // get mysql MiaoshaUser
        user = miaoshaUserDao.getById(id);
        if(user != null){
            // write redis cache
            redisService.set(MiaoshaUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePass(long id, String token, String formPass ){
        // get user
        MiaoshaUser user = getById(id);
        if(user == null){  // user not exist
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 只是设置要修改的字段的值
        MiaoshaUser newUser = new MiaoshaUser();
        user.setId(id);
        user.setPassword(MD5Util.formPassToDbPass(formPass,user.getSalt()));
        miaoshaUserDao.updatePass(newUser);

        // 处理缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        user.setPassword(newUser.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    /**
     * miaoshaUser login
     * @param response
     * @param loginVo
     * @return
     */
    public boolean login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // 判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // validator password
        String dbPassword = miaoshaUser.getPassword();
        String dbSalt = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDbPass(password, dbSalt);
        if(!calcPass.equals(dbPassword)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        // produce Cookie
        return addCookie(response, token, miaoshaUser);
    }


    /**
     * 根据 token 取对象
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        // 延迟 cookie 有效期
        if( user!= null){
            addCookie(response, token, user);
        }
        return user;
    }


    private boolean addCookie(HttpServletResponse response, String token, MiaoshaUser miaoshaUser){
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }
}
