package com.my.mapper;

import com.my.model.PmsSkuStock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsSkuStockMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuStock record);

    int insertSelective(PmsSkuStock record);

    PmsSkuStock selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsSkuStock record);

    int updateByPrimaryKey(PmsSkuStock record);

    List<PmsSkuStock> selectAllListByKeyword(@Param("keyword") String keyword, @Param("isSkuCodeFlag") Boolean isSkuCodeFlag);
}