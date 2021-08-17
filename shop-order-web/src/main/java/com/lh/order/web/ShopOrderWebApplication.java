package com.lh.order.web;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
public class ShopOrderWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopOrderWebApplication.class, args);
    }

}
