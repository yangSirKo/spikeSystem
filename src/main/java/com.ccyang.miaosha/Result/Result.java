package com.ccyang.miaosha.Result;

/**
 * 封装返回的 REST api JSON 结果
 */
public class Result<T> {

    // 代表一个状态码
    private int code;
    // 传递的失败或成功的消息
    private String msg;
    // 传递的数据
    private T data;

    /**
     * 成功时调用
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result(data);
    }

    /**
     * 失败时调用
     * @param codeMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    private Result(T data){
        this.code = 0;
        this.msg = "Success";
        this.data = data;
    }

    private Result(CodeMsg cm){
        if(cm == null){
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }


    public T getData() {
        return data;
    }

}
