package com.scnu.order.service;

import com.scnu.order.mapper.OrderMapper;
import com.scnu.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    public void saveOrder(Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderTime(new Date());
        orderMapper.saveOrder(order);
    }

    public List<Order> queryMyOrder(String userId) {
        return orderMapper.queryOrder(userId);
    }

    public void deleteOrder(String orderId) {
        orderMapper.deleteOrder(orderId);
    }

}
