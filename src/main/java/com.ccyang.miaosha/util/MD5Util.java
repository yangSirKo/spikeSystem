package com.ccyang.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Md5 加密
 */
public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt= "1a2b3c4d";

    /**
     * 第一次 md5, 使用户输入的密码加密为 form 表单传输的密码
     * 使用固定的salt, 防止用户输入的明文密码在网络上传输时被拦截
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String src = "" +salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(src);
    }

    /**
     * 将 form表单传过来的密码，再次通过动态的salt进行拼接，加密存储。这个动态的 salt 需要存储在DB
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDbPass(String formPass , String salt){
        String src = "" +salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(src);
    }

    /**
     * 两次 MD5 加密
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String salt){
        return formPassToDbPass(inputPassToFormPass(inputPass),salt);
    }

    public static void main(String[] args) {

        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDbPass(inputPassToFormPass("123456"),"1a2b3c4d"));

        System.out.println(inputPassToDbPass("123456","1a2b3c4d"));
    }


}
