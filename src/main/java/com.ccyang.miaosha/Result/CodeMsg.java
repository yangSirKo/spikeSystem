package com.ccyang.miaosha.Result;

/**
 * 封装错误信息
 */
public class CodeMsg {

    private int code;
    private String msg;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"SUCCESS");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务器异常");

    // 登录模块异常 5002xx

    // 商品模块异常 5003xx

    // 订单模块异常 5004xx

    // 秒杀模块异常 5005xx


    // 自己 create对象
    private CodeMsg(int code, String message) {
        this.code = code;
        this.msg = message;
    }


    public int getCode() {
        return code;
    }



    public String getMsg() {
        return msg;
    }


}
