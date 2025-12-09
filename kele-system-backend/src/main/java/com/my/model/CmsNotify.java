package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 系统消息通知表
 * cms_notify
 */
@Data
public class CmsNotify implements Serializable {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息图标/封面
     */
    private String icon;

    /**
     * 消息类型：1->活动优惠；2->系统公告；3->物流通知
     */
    private Integer type;

    /**
     * 发送范围：1->全体用户；2->指定用户
     */
    private Integer targetType;

    /**
     * 指定用户ID (当target_type=2时，存ID集合，逗号隔开)
     */
    private String targetMemberIds;

    /**
     * 发布人ID (管理员ID)
     */
    private Long publisherId;

    /**
     * 发送状态：0->未发送；1->已发送；2->已撤回
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}