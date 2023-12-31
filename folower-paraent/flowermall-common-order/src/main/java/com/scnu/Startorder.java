package com.scnu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.scnu.order.mapper")
public class Startorder
{
    public static void main( String[] args )
    {
        SpringApplication.run(Startorder.class, args);
    }
}
