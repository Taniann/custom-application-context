package com.tn.bean;

import com.tn.annotation.Bean;

@Bean
public class OrderService implements Service{

    public void process() {
        System.out.println("Order is being fulfilled..");
    }
}
