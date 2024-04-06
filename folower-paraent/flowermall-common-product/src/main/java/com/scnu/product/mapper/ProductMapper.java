package com.scnu.product.mapper;

import com.scnu.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    public Integer queryTotal();

    public List<Product> queryByPage(@Param("start")Integer start, @Param("rows")Integer rows);

    public Product queryById(String prodId);

    public void productSave(Product product);

    public void productUpdate(Product product);

    public List<Product> queryByName(String prodName);

    public Integer queryprodTotal(String prodName);

    public void productDelete(String productid);


}
