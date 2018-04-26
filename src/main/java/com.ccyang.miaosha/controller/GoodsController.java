package com.ccyang.miaosha.controller;

import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.redis.MiaoshaUserKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/to_list")
    public String toList(Model model, MiaoshaUser user){
        model.addAttribute("user", user);
        return "goods_list";
    }


}
