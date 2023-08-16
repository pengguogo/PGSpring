package com.pg.service;

import com.pg.spring.Autowired;
import com.pg.spring.Component;

@Component
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println("UserService test");
        orderService.test();
    }
}
