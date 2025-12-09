package com.my.mapper;

import com.my.model.CmsNotifyRecord;

public interface CmsNotifyRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsNotifyRecord record);

    int insertSelective(CmsNotifyRecord record);

    CmsNotifyRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsNotifyRecord record);

    int updateByPrimaryKey(CmsNotifyRecord record);
}