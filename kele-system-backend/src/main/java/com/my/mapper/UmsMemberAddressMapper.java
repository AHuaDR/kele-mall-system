package com.my.mapper;

import com.my.model.UmsMemberAddress;

public interface UmsMemberAddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberAddress record);

    int insertSelective(UmsMemberAddress record);

    UmsMemberAddress selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMemberAddress record);

    int updateByPrimaryKey(UmsMemberAddress record);
}