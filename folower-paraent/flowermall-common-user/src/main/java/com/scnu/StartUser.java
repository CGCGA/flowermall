package com.scnu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Hello world!
 *
 */

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.scnu.mapper")
public class StartUser
{
    public static void main( String[] args )
    {
        SpringApplication.run(StartUser.class, args);
    }
}
