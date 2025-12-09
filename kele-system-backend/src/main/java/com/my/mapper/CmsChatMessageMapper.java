package com.my.mapper;

import com.my.model.CmsChatMessage;

import java.util.List;

public interface CmsChatMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CmsChatMessage record);

    int insertSelective(CmsChatMessage record);

    CmsChatMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsChatMessage record);

    int updateByPrimaryKey(CmsChatMessage record);

    List<CmsChatMessage> selectBySessionId(Long sessionId);

    int updateIsReadBySessionId(Long sessionId);
}