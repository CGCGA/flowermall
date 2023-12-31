package com.scnu.order.controller;

import com.scnu.order.service.OrderService;
import com.scnu.pojo.Order;
import com.scnu.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order/manage")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("save")
    public SysResult saveOrder(Order order) {
        try {
            orderService.saveOrder(order);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }
    }

    @RequestMapping("query/{userId}")
    public List<Order> queryMyorder(@PathVariable String userId) {
        return  orderService.queryMyOrder(userId);
    }

    @RequestMapping("delete/{orderId}")
    public SysResult deleteOrder(@PathVariable String orderId) {
        try {
            orderService.deleteOrder(orderId);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "", null);
        }
    }
}
