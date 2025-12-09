package com.my.service;

import com.my.model.CmsChatMessage;
import com.my.model.CmsChatSession;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 13:34
 * @description
 */
public interface AdminChatService {
    List<CmsChatSession> getSessionList();

    List<CmsChatMessage> getMessageHistoryList(Long sessionId);

    int replyMessage(CmsChatMessage cmsChatMessage);

    int updateMarkRead(Long sessionId);
}
