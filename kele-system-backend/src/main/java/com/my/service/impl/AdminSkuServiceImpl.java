package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.PmsProductMapper;
import com.my.mapper.PmsSkuStockLogMapper;
import com.my.mapper.PmsSkuStockMapper;
import com.my.model.PmsProduct;
import com.my.model.PmsSkuStock;
import com.my.model.PmsSkuStockLog;
import com.my.query.AdminPageInfoQuery;
import com.my.service.AdminSkuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 15:09
 * @description
 */
@Service
public class AdminSkuServiceImpl implements AdminSkuService {

    @Resource
    private PmsSkuStockMapper pmsSkuStockMapper;
    @Resource
    private PmsProductMapper pmsProductMapper;
    @Resource
    private PmsSkuStockLogMapper pmsSkuStockLogMapper;


    @Override
    public PageInfo<PmsSkuStock> getStockList(AdminPageInfoQuery adminPageInfoQuery) {
        PageHelper.startPage(adminPageInfoQuery.getPageNum(), adminPageInfoQuery.getPageSize());

        String keyword = adminPageInfoQuery.getKeyword();
        Boolean isSkuCodeFlag = false;
        //判断是否是商品名
        if(keyword != null && Pattern.compile("-").matcher(keyword).find()) {
            //代表是SKUcode
            isSkuCodeFlag = true;
        }
        List<PmsSkuStock> pmsSkuStockList = pmsSkuStockMapper.selectAllListByKeyword(keyword,isSkuCodeFlag);
        PageInfo<PmsSkuStock> pageInfo = new PageInfo<>(pmsSkuStockList);
        return pageInfo;
    }

    @Override
    @Transactional
    public int addStock(PmsSkuStock pmsSkuStock) {
        try{
            Long productId = pmsSkuStock.getProductId();
            Integer stock = pmsSkuStock.getStock();
            PmsProduct pmsProduct = pmsProductMapper.selectByPrimaryKey(productId);
            pmsProduct.setStock(pmsProduct.getStock() + stock);
            pmsProductMapper.updateByPrimaryKeySelective(pmsProduct);
            PmsSkuStock pmsSkuStock1 = pmsSkuStockMapper.selectByPrimaryKey(pmsSkuStock.getId());
            pmsSkuStock1.setStock(pmsSkuStock1.getStock() + stock);
            pmsSkuStockMapper.updateByPrimaryKeySelective(pmsSkuStock1);
            return 1;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PmsSkuStockLog> getStockLog(String skuCode) {
        List<PmsSkuStockLog> pmsSkuStockLogList = pmsSkuStockLogMapper.selectBySkuCode(skuCode);
        return pmsSkuStockLogList;
    }
}
