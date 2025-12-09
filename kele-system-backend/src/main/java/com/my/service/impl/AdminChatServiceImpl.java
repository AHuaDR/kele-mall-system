package com.my.service.impl;

import com.my.mapper.CmsChatMessageMapper;
import com.my.mapper.CmsChatSessionMapper;
import com.my.model.CmsChatMessage;
import com.my.model.CmsChatSession;
import com.my.service.AdminChatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 13:34
 * @description
 */
@Service
public class AdminChatServiceImpl implements AdminChatService {

    @Resource
    private CmsChatSessionMapper cmsChatSessionMapper;

    @Resource
    private CmsChatMessageMapper cmsChatMessageMapper;

    @Override
    public List<CmsChatSession> getSessionList() {
        List<CmsChatSession> cmsChatSessionList = cmsChatSessionMapper.selectAllSessionList();
        return cmsChatSessionList;
    }

    @Override
    public List<CmsChatMessage> getMessageHistoryList(Long sessionId) {
        List<CmsChatMessage> cmsChatMessageList = cmsChatMessageMapper.selectBySessionId(sessionId);

        return cmsChatMessageList;
    }

    @Override
    public int replyMessage(CmsChatMessage cmsChatMessage) {
        return cmsChatMessageMapper.insertSelective(cmsChatMessage);
    }

    @Override
    public int updateMarkRead(Long sessionId) {
        return cmsChatMessageMapper.updateIsReadBySessionId(sessionId);
    }
}
