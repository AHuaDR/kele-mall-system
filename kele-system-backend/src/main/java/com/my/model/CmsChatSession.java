package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客服会话列表表
 * cms_chat_session
 */
@Data
public class CmsChatSession implements Serializable {
    private Long id;

    /**
     * C端用户ID
     */
    private Long memberId;

    /**
     * 冗余用户昵称
     */
    private String memberNick;

    /**
     * 冗余用户头像
     */
    private String memberAvatar;

    /**
     * 当前接待的客服ID (0表示未分配)
     */
    private Long adminId;

    /**
     * 最后一条消息预览
     */
    private String lastContent;

    /**
     * 最后聊天时间
     */
    private Date lastTime;

    /**
     * 管理员未读消息数
     */
    private Integer unreadCount;

    /**
     * 1:进行中, 0:已结束
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}