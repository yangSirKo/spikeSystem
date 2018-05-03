package com.ccyang.miaosha.exception;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)  // value指定拦截那些异常
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();

        if(e instanceof GlobalException){
            GlobalException ge = (GlobalException)e;
            return Result.error(ge.getCm());

        }else if (e instanceof BindException) {
            BindException be = (BindException) e;
            List<ObjectError> errors = be.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));

        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
