package com.my.controller;

import com.my.model.CmsChatMessage;
import com.my.model.CmsChatSession;
import com.my.result.R;
import com.my.service.AdminChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 13:29
 * @description 客服对话
 */
@RestController
@RequestMapping("/admin/chat")
public class AdminChatController {

    @Resource
    private AdminChatService adminChatService;

    /**
     * 获取客服消息列表
     * @return
     */
    @GetMapping("/session/list")
    public R getSessionList(){
        List<CmsChatSession> cmsChatMessageList = adminChatService.getSessionList();
        return R.ok(cmsChatMessageList);
    }

    /**
     * 根据会话id获取历史消息
     * @param sessionId
     * @return
     */
    @GetMapping("/history")
    public R getHistoryList(@RequestParam Long sessionId){
        List<CmsChatMessage> chatMessageList = adminChatService.getMessageHistoryList(sessionId);
        return R.ok(chatMessageList);
    }

    /**
     * 发送消息
     * @param cmsChatMessage
     * @return
     */
    @PostMapping("reply")
    public R reply(@RequestBody CmsChatMessage cmsChatMessage){
        int result = adminChatService.replyMessage(cmsChatMessage);
        return result >=1 ? R.ok() : R.error();
    }

    /**
     * 更新已读状态
     * @param sessionId
     * @return
     */
    @PostMapping("/session/markRead")
    public R markRead(@RequestBody Map<String, Long> sessionId){
        Long sessionId1 = sessionId.get("sessionId");
        int result = adminChatService.updateMarkRead(sessionId1);
        return result >=0 ? R.ok() : R.error();
    }

}
