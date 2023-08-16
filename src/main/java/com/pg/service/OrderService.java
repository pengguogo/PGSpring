package com.pg.service;

import com.pg.spring.Component;
import com.pg.spring.Scope;

@Component
@Scope("singleton")
public class OrderService {

    public void test(){
        System.out.println("OrderService test");
    }
}
