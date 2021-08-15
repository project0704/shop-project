package com.lh.order;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.lh.utils.IDWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDubboConfiguration
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public IDWorker idWorker(){
       return new IDWorker(1,1);
    }

}
