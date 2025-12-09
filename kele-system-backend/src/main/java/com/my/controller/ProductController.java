package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.dto.CreateProductParam;
import com.my.model.PmsProduct;
import com.my.query.ProductListQuery;
import com.my.result.R;
import com.my.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 19:52
 * @description 商品相关
 */
@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 获取商品列表,搜索商品..
     * @param query
     * @return
     */
    @GetMapping("/list")
    public R list(ProductListQuery query){
        PageInfo<PmsProduct> pmsProductList = productService.getProductList(query);
        return R.ok(pmsProductList);
    }

    /**
     * 商品的上架下架管理
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/updateStatus/{id}")
    public R updateStatus(@PathVariable Integer id,Integer status){
        int result = productService.updateStatus(id,status);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public R deleteProduct(@PathVariable Long id){
        int result = productService.deleteProduct(id);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 快编商品(只能修改商品的价格和库存数量)
     * @param id
     * @param pmsProduct
     * @return
     */
    @PostMapping("/update/{id}")
    public R updateProduct(@PathVariable Long id,@RequestBody PmsProduct pmsProduct){
        int result = productService.updateProduct(id,pmsProduct);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 新增商品
     * @param createProductParam 包含了PmsProduct和PmsSkuStock
     * @return
     */
    @PostMapping("/create")
    public R createProduct(@RequestBody CreateProductParam createProductParam){
        int result = productService.insertProductAndSku(createProductParam);
        return result >= 1 ? R.ok() : R.error();
    }

    @GetMapping("/search")
    public R searchProduct(@RequestParam(value = "params[keyword]", required = false) String keyword){
        List<PmsProduct> pmsProductList = productService.searchProductList(keyword);
        return R.ok(pmsProductList);
    }





}
