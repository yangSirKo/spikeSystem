package com.ccyang.miaosha.controller;


import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.User;
import com.ccyang.miaosha.rabbitmq.MQSender;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.redis.UserKey;
import com.ccyang.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 控制器有两种返回结果： 1.Rest api Json输出  2.页面
 */
@Controller
public class SimpleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    /**
     * Direct Pattern Test
     * @return
     */
    @RequestMapping("/mq/direct")
    @ResponseBody
    public Result<String> directMQ(){
        sender.sendDirect("hello Direct");
        return Result.success("ok Direct");
    }

    /**
     * Fanout Pattern Test
     * @return
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanoutMQ(){
        sender.sendFanout("hello Fanout");
        return Result.success("ok Fanout");
    }

    /**
     * Topic Pattern Test
     * @return
     */
    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topicMQ(){
        sender.sendTopic("hello Topic");
        return Result.success("ok Topic");
    }

    /**
     * Header Pattern Test
     * @return
     */
    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> headerMQ(){
        sender.sendHeader("hello Header");
        return Result.success("ok Header");
    }


    /**
     * JSON 输出
     * @return
     */
    @RequestMapping("/successDemo")
    @ResponseBody
    public Result<String> successDemo(){
        return Result.success("ccyang");
        //return new Result<String>(0,"成功","ccyang");
    }

    /**
     * JSON 输出
     * @return
     */
    @RequestMapping("/errorDemo")
    @ResponseBody
    public Result<String> errorDemo(){
        return Result.error(CodeMsg.SERVER_ERROR);
        //return new Result<String>(500XXX,"error");
    }

    /**
     * 返回 Thymeleaf 页面,返回值为字符串
     */
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","atyang");
        return "hello";
    }

    /**
     * Json 输出
     * @return
     */
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    /**
     * Json 输出
     * @return
     */
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User v1 = redisService.get(UserKey.getById,""+1, User.class );
        System.out.println(v1);
        return Result.success(v1);
    }

    /**
     * Json 输出
     * @return
     */
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("111111111");
        boolean v1 = redisService.set(UserKey.getById,""+1, user);
        return Result.success(v1);
    }
}
