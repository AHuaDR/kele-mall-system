package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消息阅读记录表
 * cms_notify_record
 */
@Data
public class CmsNotifyRecord implements Serializable {
    private Long id;

    /**
     * 对应消息ID
     */
    private Long notifyId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 是否已读：1->已读
     */
    private Integer isRead;

    private static final long serialVersionUID = 1L;
}