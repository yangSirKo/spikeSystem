package com.ccyang.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableAutoConfiguration
@SpringBootApplication // 比上面的注解更常用
public class MainApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MainApplication.class, args);
    }


}
