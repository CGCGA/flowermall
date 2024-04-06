package com.example.test;

import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws Exception {
        test();
    }

    //连接 redisCluster（集群模式）
    @Autowired
    static JedisCluster cluster ;

    public static void test(){
        if(!cluster.exists("gender")) {
            cluster.set("gender", "male");
        }
        System.out.println(cluster.get("gender"));
    }

    public static void testRedisCluster( ApplicationContext app  ) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(10000);
        // 最大空闲数
        poolConfig.setMaxIdle(1000);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        poolConfig.setMaxWaitMillis(3000);
        poolConfig.setTestOnBorrow(true);
        Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
        nodes.add(new HostAndPort("192.168.243.133" ,8000 ));//换自己的内网IP
        nodes.add(new HostAndPort("192.168.243.133" ,8001 ));
        nodes.add(new HostAndPort("192.168.243.133" ,8002 ));
        nodes.add(new HostAndPort("192.168.243.133" ,8003 ));
        nodes.add(new HostAndPort("192.168.243.133" ,8004 ));
        nodes.add(new HostAndPort("192.168.243.133" ,8005 ));
//		cluster = new JedisCluster(nodes, poolConfig  );//		cluster = new JedisCluster(nodes, 5000 , 1000);  //		cluster = new JedisCluster( nodes, 2000, 5, 8, "redis@123", new GenericObjectPoolConfig() );//		cluster.auth("redis@123") ;
//		cluster = app.getBean(JedisCluster.class) ;
        cluster = new JedisCluster( nodes, 3000, 3000, 8, "redis@123", poolConfig );
        String name = cluster.get("name");
        System.out.println(name);
        cluster.set("age", "18");
        System.out.println(cluster.get("age"));
        try {
            cluster.close();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}

