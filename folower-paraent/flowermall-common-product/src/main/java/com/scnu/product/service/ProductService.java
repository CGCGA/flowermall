package com.scnu.product.service;

import com.scnu.pojo.Product;
import com.scnu.product.mapper.ProductMapper;
import com.scnu.utils.MapperUtil;
import com.scnu.vo.EasyUIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private JedisCluster jedis;

    public EasyUIResult productPageQuery(Integer page, Integer rows) {
        EasyUIResult result = new EasyUIResult();

        Integer total = productMapper.queryTotal();

        Integer start = (page - 1) * rows;
        List<Product> pList = productMapper.queryByPage(start, rows);

        result.setTotal(total);
        result.setRows(pList);
        return result;
    }

    public Product queryById(String productId) {
        String productKey = "product_query_" + productId;
        String lock = "product_update_"+productId+".lock";
//        try {
//            if(jedis.exists("gender"))
//            System.out.println(jedis.get("gender"));
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        try{

            if(jedis.exists(lock)) {
                return productMapper.queryById(productId);
            }

            if(jedis.exists(productKey)) {
                String productJson = jedis.get(productKey);
                System.out.println("获取缓存中的数据: "+productKey);
                Product product = MapperUtil.MP.readValue(productJson, Product.class);
                return product;
            } else {
                Product product = productMapper.queryById(productId);
                System.out.println("到数据库查询数据，productId="+productId);
                String productJson = MapperUtil.MP.writeValueAsString(product);
                jedis.setex(productKey, 60*60*24*2, productJson);
                System.out.println("将数据存入缓存，商品信息为："+productId);
                return product;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void productSave(Product product) {
        product.setProductId(UUID.randomUUID().toString());
        productMapper.productSave(product);
    }

    public void productUpdate(Product product) {

        String lock = "product_update_"+product.getProductId()+".lock";

        Long leftTime = jedis.ttl("product_query_" + product.getProductId());
        jedis.setex(lock, Integer.parseInt(leftTime+""), "");
        jedis.del("product_query_"+product.getProductId());
        productMapper.productUpdate(product);

        jedis.del(lock);
    }

}
