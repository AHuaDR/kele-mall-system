package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品评价表
 * pms_comment
 */
@Data
public class PmsComment implements Serializable {
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员昵称
     */
    private String memberNickName;

    /**
     * 会员头像
     */
    private String memberIcon;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 评价星数：0->5
     */
    private Integer star;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 上传图片地址，以逗号隔开
     */
    private String pics;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Integer showStatus;

    /**
     * 购买时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}