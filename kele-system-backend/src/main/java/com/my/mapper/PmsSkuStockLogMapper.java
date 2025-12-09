package com.my.mapper;

import com.my.model.PmsSkuStockLog;

import java.util.List;

public interface PmsSkuStockLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuStockLog record);

    int insertSelective(PmsSkuStockLog record);

    PmsSkuStockLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsSkuStockLog record);

    int updateByPrimaryKey(PmsSkuStockLog record);

    List<PmsSkuStockLog> selectBySkuCode(String skuCode);
}