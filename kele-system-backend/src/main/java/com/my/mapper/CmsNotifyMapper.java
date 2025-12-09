package com.my.mapper;

import com.my.model.CmsNotify;

import java.util.List;

public interface CmsNotifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsNotify record);

    int insertSelective(CmsNotify record);

    CmsNotify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsNotify record);

    int updateByPrimaryKey(CmsNotify record);

    List<CmsNotify> selectAllNotify();
}