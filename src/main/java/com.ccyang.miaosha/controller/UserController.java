package com.ccyang.miaosha.controller;

import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.service.GoodsService;
import com.ccyang.miaosha.service.MiaoshaUserService;
import com.ccyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user){
        return Result.success(user);
    }
}
