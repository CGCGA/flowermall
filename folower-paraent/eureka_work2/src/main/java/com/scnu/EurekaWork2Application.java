package com.scnu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaWork2Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaWork2Application.class, args);
    }

}
