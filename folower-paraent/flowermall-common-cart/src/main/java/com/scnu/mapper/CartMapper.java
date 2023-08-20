package com.scnu.mapper;

import com.scnu.pojo.Cart;

import java.util.List;

public interface CartMapper {

    List<Cart> queryMyCart(String userId);
    void updateNum(Cart exist);
    void saveCart(Cart cart);
    Cart queryOne(Cart cart);
    void deleteCart(Cart cart);

}
