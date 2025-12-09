package com.my.service;

import com.my.model.MktFlashSale;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 22:00
 * @description
 */
public interface AdminFlashSalesService {
    List<MktFlashSale> getFlashSalesList();

    int createFlashSales(MktFlashSale mktFlashSale);
}
