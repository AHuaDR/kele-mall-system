package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.dto.CreateProductParam;
import com.my.model.PmsProduct;
import com.my.query.ProductListQuery;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 20:29
 * @description
 */
public interface ProductService {
    PageInfo<PmsProduct> getProductList(ProductListQuery query);

    int updateStatus(Integer id, Integer status);

    int deleteProduct(Long id);

    int updateProduct(Long id, PmsProduct pmsProduct);


    int insertProductAndSku(CreateProductParam createProductParam);

    List<PmsProduct> searchProductList(String keyword);
}
