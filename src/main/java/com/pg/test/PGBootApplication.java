package com.pg.test;

import com.pg.service.UserService;
import com.pg.spring.ApplicationContext;

public class PGBootApplication {

    // 启动方法
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        System.out.println(userService);
        userService.test();


    }

}
