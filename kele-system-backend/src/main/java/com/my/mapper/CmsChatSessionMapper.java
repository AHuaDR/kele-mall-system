package com.my.mapper;

import com.my.model.CmsChatSession;

import java.util.List;

public interface CmsChatSessionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsChatSession record);

    int insertSelective(CmsChatSession record);

    CmsChatSession selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsChatSession record);

    int updateByPrimaryKey(CmsChatSession record);

    List<CmsChatSession> selectAllSessionList();
}