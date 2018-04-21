package com.ccyang.miaosha.controller;


import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 控制器有两种返回结果： 1.Rest api Json输出  2.页面
 */
@Controller
public class SimpleController {

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
        model.addAttribute("name","atyang1");
        return "hello";
    }


}
