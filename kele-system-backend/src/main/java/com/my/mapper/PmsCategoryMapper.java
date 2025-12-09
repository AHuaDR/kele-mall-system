package com.my.mapper;

import com.my.model.PmsCategory;

import java.util.List;

public interface PmsCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsCategory record);

    int insertSelective(PmsCategory record);

    PmsCategory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsCategory record);

    int updateByPrimaryKey(PmsCategory record);

    List<PmsCategory> selectAllCategory();
}