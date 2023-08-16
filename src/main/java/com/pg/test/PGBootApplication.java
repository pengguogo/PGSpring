package com.pg.test;

import com.pg.spring.ApplicationContext;

public class PGBootApplication {

    // 启动方法
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("orderService"));
        System.out.println(applicationContext.getBean("orderService"));
    }

}
