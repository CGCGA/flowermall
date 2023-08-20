package com.scnu.order.mapper;

import com.scnu.pojo.Order;

import java.util.List;

public interface OrderMapper {
    void saveOrder(Order order);

    List<Order> queryOrder(String userId);

    void deleteOrder(String orderId);

}
