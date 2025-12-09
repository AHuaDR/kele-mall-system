package com.my.mapper;

import com.my.model.MktFlashSale;

import java.util.List;

public interface MktFlashSaleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MktFlashSale record);

    int insertSelective(MktFlashSale record);

    MktFlashSale selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MktFlashSale record);

    int updateByPrimaryKey(MktFlashSale record);

    List<MktFlashSale> selectAllFlashSales();
}