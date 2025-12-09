package com.my.mapper;

import com.my.model.MktVipLevel;

import java.util.List;

public interface MktVipLevelMapper {
    int deleteByPrimaryKey(Byte level);

    int insert(MktVipLevel record);

    int insertSelective(MktVipLevel record);

    MktVipLevel selectByPrimaryKey(Byte level);

    int updateByPrimaryKeySelective(MktVipLevel record);

    int updateByPrimaryKey(MktVipLevel record);

    List<MktVipLevel> selectAllVipLevel();
}