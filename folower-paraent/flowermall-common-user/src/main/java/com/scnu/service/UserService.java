package com.scnu.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnu.mapper.UserMapper;
import com.scnu.pojo.User;
import com.scnu.utils.MD5Util;
import com.scnu.utils.MapperUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;


@Service
public class UserService {

     @Autowired
     private UserMapper userMapper;

//     @Autowired
//     private ShardedJedisPool pool;
     @Autowired
     private JedisCluster jedis;

     private ObjectMapper mapper = MapperUtil.MP;

     public Integer checkUserName(String userName) {
         return userMapper.queryUserName(userName);
     }

     public void userSave(User user) {
         user.setUserId(UUID.randomUUID().toString());
         user.setUserPassword(MD5Util.md5(user.getUserPassword()));

         userMapper.userSave(user);
     }

     public String doLogin(User user) {

         //ShardedJedis jedis = pool.getResource();

         try {
             user.setUserPassword(MD5Util.md5(user.getUserPassword()));
             User exist = userMapper.queryUserByNameAndPassword(user);

             if(exist == null) {
                 return "";
             } else {
                 String ticket = UUID.randomUUID().toString();
                 String userJson;
                 userJson = mapper.writeValueAsString(exist);

                 //判断当前用户是否曾经有人登录过
                 String existTicket=jedis.get("user_logined_"+exist.getUserId());
                 //顶替实现.不允许前一个登录的人ticket存在
                 if(StringUtils.isNotEmpty(existTicket)){
                     jedis.del(existTicket);
                 }

//                 //登录list最多允许一个用户登录
//                 List<String> existTicketList=jedis.lrange("user_logined_"+exist.getUserId(), 0, -1);
//                 if(existTicketList.size()>=1) {
//                     jedis.del(jedis.rpop("user_logined_"+exist.getUserId()));
//                 }

                 //定义当前客户端登录的信息 userId:ticket
                 //jedis.setex("user_logined_"+exist.getUserId(), 60*30,ticket);
//
//
                 //用户登录超时30分钟
                 jedis.setex(ticket, 60*30, userJson);
                 jedis.set(ticket, userJson);
                 return ticket;
             }
         } catch (Exception e) {
             e.printStackTrace();
             return "";
         }
//         finally {
//             pool.returnResource(jedis);
//         }

     }


     public String queryUserJson(String ticket) {

         //ShardedJedis jedis = pool.getResource();
         String userJson = "";
         try {
             //首先判断超时剩余时间
             Long leftTime = jedis.pttl(ticket);
             //少于10分钟,延长5分钟
             if(leftTime<1000*60*10l){
                 jedis.pexpire(ticket,leftTime+1000*60*5);
             }

             userJson = jedis.get(ticket);
             return userJson;
         } catch (Exception e) {
             e.printStackTrace();
             return "";
         }
//         finally {
//             pool.returnResource(jedis);
//         }
     }

}
