package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 优惠券表
 * mkt_coupon
 */
@Data
public class MktCoupon implements Serializable {
    private Long id;

    /**
     * 优惠券类型；0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券
     */
    private Integer type;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 使用门槛；0表示无门槛
     */
    private BigDecimal minPoint;

    /**
     * 开始使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date endTime;

    /**
     * 发行数量
     */
    private Integer publishCount;

    /**
     * 已使用数量
     */
    private Integer useCount;

    /**
     * 领取数量
     */
    private Integer receiveCount;

    private static final long serialVersionUID = 1L;
}