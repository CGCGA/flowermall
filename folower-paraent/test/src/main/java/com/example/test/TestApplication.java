package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

public class TestApplication {


    @Test
    public void redisTest() {
        Jedis jedis = new Jedis("192.168.243.133", 8003);
        String set = jedis.set("name", "itheima");
        String name = jedis.get("name");
        System.out.println(name);
        jedis.close();
    }
}
