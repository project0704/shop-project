package com.lh.pay;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.lh.utils.IDWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableDubboConfiguration
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

    @Bean
    public IDWorker idWorker(){
        return new IDWorker(1,2);
    }

    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);

        executor.setMaxPoolSize(8);

        executor.setQueueCapacity(100);

        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("Pool-A");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;

    }
}
