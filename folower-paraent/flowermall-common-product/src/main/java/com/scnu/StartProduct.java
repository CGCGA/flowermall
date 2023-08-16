package com.scnu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.scnu.product.mapper")
public class StartProduct
{
    public static void main( String[] args )
    {
        SpringApplication.run(StartProduct.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate initRestTemplateProduct(){
        return new RestTemplate();
    }
}
