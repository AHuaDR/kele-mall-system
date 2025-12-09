package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 限时秒杀活动表
 * mkt_flash_sale
 */
@Data
public class MktFlashSale implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动名称，如：双12整点秒杀
     */
    private String name;

    /**
     * 秒杀开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date endTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 状态：0-未开始, 1-进行中, 2-已结束, 3-已关闭
     */
    private Byte status;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 商品名称快照
     */
    private String productName;

    /**
     * 原价（快照）
     */
    private BigDecimal originalPrice;

    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀总库存
     */
    private Integer stockCount;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 每人限购数量
     */
    private Integer limitPerUser;


    /**
     * 创建人
     */
    private String creator;

    private static final long serialVersionUID = 1L;
}