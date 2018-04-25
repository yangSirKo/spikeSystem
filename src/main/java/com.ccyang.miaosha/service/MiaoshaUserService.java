package com.ccyang.miaosha.service;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.dao.MiaoshaUserDao;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.exception.GlobalException;
import com.ccyang.miaosha.util.MD5Util;
import com.ccyang.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public boolean login(LoginVo loginVo){
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
        return true;
    }

}
