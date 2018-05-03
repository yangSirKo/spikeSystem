package com.ccyang.miaosha.util;

import com.alibaba.fastjson.JSON;
import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class WebUtil {

    public static void render(HttpServletResponse response, CodeMsg cm) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();

    }
}
