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
    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数效验异常：%s");

    // 登录模块异常 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210,"session 不存在或者已失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214,"手机号未注册");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"密码错误");

    // 商品模块异常 5003xx

    // 订单模块异常 5004xx

    // 秒杀模块异常 5005xx


    // 自己 create对象
    private CodeMsg(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{code=" + code + ", msg='" + msg + '\'' + '}';
    }
}
