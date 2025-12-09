package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.PmsSkuStock;
import com.my.model.PmsSkuStockLog;
import com.my.query.AdminPageInfoQuery;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 15:09
 * @description
 */
public interface AdminSkuService {
    PageInfo<PmsSkuStock> getStockList(AdminPageInfoQuery adminPageInfoQuery);

    int addStock(PmsSkuStock pmsSkuStock);

    List<PmsSkuStockLog> getStockLog(String skuCode);
}
