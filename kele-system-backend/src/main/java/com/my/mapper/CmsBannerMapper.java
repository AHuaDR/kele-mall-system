package com.my.mapper;

import com.my.model.CmsBanner;

import java.util.List;

public interface CmsBannerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsBanner record);

    int insertSelective(CmsBanner record);

    CmsBanner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsBanner record);

    int updateByPrimaryKey(CmsBanner record);

    List<CmsBanner> selectAllAdvertise();
}