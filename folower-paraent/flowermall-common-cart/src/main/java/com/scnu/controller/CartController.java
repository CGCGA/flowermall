package com.scnu.controller;


import com.scnu.pojo.Cart;
import com.scnu.service.CartService;
import com.scnu.vo.SysResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cart/manage")
public class CartController {
    @Autowired
    private CartService cartService;

    @RequestMapping("query")
    public List<Cart> queryMyCart(String userId) {
        if(!StringUtils.isNotEmpty(userId)) {
            return null;
        }
        return cartService.queryMyCart(userId);
    }

    @RequestMapping("save")
    public SysResult cartSave(Cart cart) {
        try {
            cartService.cartSave(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "", null);
        }
    }

    @RequestMapping("update")
    public SysResult updateNum(Cart cart) {
        try {
            cartService.updateNum(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "", null);
        }
    }

    @RequestMapping("delete")
    public SysResult deleteCart(Cart cart) {
        try {
            cartService.deleteCart(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "", null);
        }
    }
}
