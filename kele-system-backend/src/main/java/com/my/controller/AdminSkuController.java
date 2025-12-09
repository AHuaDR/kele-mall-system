package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.model.PmsSkuStock;
import com.my.model.PmsSkuStockLog;
import com.my.query.AdminPageInfoQuery;
import com.my.result.CodeEnum;
import com.my.result.R;
import com.my.service.AdminSkuService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 15:08
 * @description
 */
@RestController
@RequestMapping("/admin/sku")
public class AdminSkuController {

    @Resource
    private AdminSkuService adminSkuService;

    /**
     * 获取库存列表
     * @param adminPageInfoQuery
     * @return
     */
    @GetMapping("/stock/list")
    public R getStockList(AdminPageInfoQuery adminPageInfoQuery){
        PageInfo<PmsSkuStock> pmsSkuStockPageInfo = adminSkuService.getStockList(adminPageInfoQuery);
        return R.ok(pmsSkuStockPageInfo);
    }

    /**
     *  增加库存
     * @param pmsSkuStock
     * @return
     */
    @PostMapping("/stock/update")
    public R updateStock(@RequestBody PmsSkuStock pmsSkuStock){
        int result = adminSkuService.addStock(pmsSkuStock);
        return result >= 1 ? R.ok() : R.error(CodeEnum.FALL);
    }

    /**
     * 获取流水
     * @param id
     * @return
     */
    @GetMapping("/stock/log/{skuCode}")
    public R getStockLog(@PathVariable String skuCode){
        List<PmsSkuStockLog> pmsSkuStockLogList = adminSkuService.getStockLog(skuCode);
        return R.ok(pmsSkuStockLogList);
    }



}
