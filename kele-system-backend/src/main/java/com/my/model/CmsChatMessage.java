package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客服聊天记录表
 * cms_chat_message
 */
@Data
public class CmsChatMessage implements Serializable {
    private Long id;

    /**
     * 会话ID(通常是member_id)
     */
    private String sessionId;

    /**
     * 发送者：1->用户；2->管理员
     */
    private Boolean senderType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 发送时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}