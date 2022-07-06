package com.tn.bean;

import com.tn.annotation.Bean;

@Bean(name = "Laptop1")
public class Laptop implements Item {
    @Override
    public String name() {
        return "laptop";
    }
}
