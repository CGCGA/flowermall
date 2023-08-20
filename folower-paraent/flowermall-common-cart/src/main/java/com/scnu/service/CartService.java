package com.scnu.service;

import com.scnu.mapper.CartMapper;
import com.scnu.pojo.Cart;
import com.scnu.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private RestTemplate restTemplate;

    public List<Cart> queryMyCart(String userId) {
        return cartMapper.queryMyCart(userId);
    }

    public void cartSave(Cart cart) {
        Cart exist = cartMapper.queryOne(cart);
        if(exist != null) {
            exist.setNum(exist.getNum() + cart.getNum());
            cartMapper.updateNum(exist);
        } else {
            Product product = restTemplate
                    .getForObject("http://productservice/product/manage/item/"
                     + cart.getProductId(), Product.class);
            cart.setProductPrice(product.getProductPrice());
            cart.setProductName(product.getProductName());
            cart.setProductImage(product.getProductImgurl());
            cartMapper.saveCart(cart);
        }
    }

    public void updateNum(Cart cart) {
        cartMapper.updateNum(cart);
    }

    public void deleteCart(Cart cart) {
        cartMapper.deleteCart(cart);
    }

}
