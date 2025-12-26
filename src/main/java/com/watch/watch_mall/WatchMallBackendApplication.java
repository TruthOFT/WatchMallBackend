package com.watch.watch_mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.watch.watch_mall.mapper")
public class WatchMallBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatchMallBackendApplication.class, args);
    }

}
