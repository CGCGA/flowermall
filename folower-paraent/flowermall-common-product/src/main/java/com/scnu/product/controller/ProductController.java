package com.scnu.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnu.pojo.Product;
import com.scnu.product.service.ProductService;
import com.scnu.vo.EasyUIResult;
import com.scnu.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    @RequestMapping("/product/manage/pageManage")
    public EasyUIResult productPageQuery(Integer page, Integer rows){
        EasyUIResult result = productService.productPageQuery(page, rows);
        return result;
    }

    @RequestMapping("/product/manage/search")
    public EasyUIResult productProdQuery(String prodName){
        System.out.println(prodName);
        EasyUIResult result = productService.productProdQuery(prodName);
        return result;
    }

    @RequestMapping("/product/manage/item/{prodId}")
    public String queryById(@PathVariable String prodId) {
        String productKey = "product_query_"+prodId;


        String prodmes;
        Product product = productService.queryById(prodId);
        // jackson代码.将product对象转化成json
        ObjectMapper mapper = new ObjectMapper();
        try {
            prodmes = mapper.writeValueAsString(product);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return prodmes;
    }
    @RequestMapping("/product/manage/save")
    public SysResult productSave(Product product) {
        try {
            productService.productSave(product);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }
    }

    @RequestMapping("/product/manage/update")
    public SysResult productUpdate(Product product) {
        try {
            productService.productUpdate(product);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }
    }

}
