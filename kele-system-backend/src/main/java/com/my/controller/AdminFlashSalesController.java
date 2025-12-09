package com.my.controller;

import com.my.model.MktFlashSale;
import com.my.result.R;
import com.my.service.AdminFlashSalesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 21:59
 * @description
 */
@RestController
@RequestMapping("/admin/flash-sales")
public class AdminFlashSalesController {

    @Resource
    private AdminFlashSalesService adminFlashSalesService;

    /**
     * 获取秒杀列表
     * @return
     */
    @GetMapping("/list")
    public R flashSalesList(){
        List<MktFlashSale> mktFlashSaleList = adminFlashSalesService.getFlashSalesList();
        return R.ok(mktFlashSaleList);
    }

    /**
     * 创建秒杀商品
     * @param mktFlashSale
     * @return
     */
    @PostMapping("/create")
    public R createFlashSale(@RequestBody MktFlashSale mktFlashSale){
        int result = adminFlashSalesService.createFlashSales(mktFlashSale);
        return result > 0 ? R.ok() : R.error();
    }

}
