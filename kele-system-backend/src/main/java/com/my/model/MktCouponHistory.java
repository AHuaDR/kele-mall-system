package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券领取历史表
 * mkt_coupon_history
 */
@Data
public class MktCouponHistory implements Serializable {
    private Long id;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 领取人昵称
     */
    private String memberNickname;

    /**
     * 获取类型：0->后台赠送；1->主动获取
     */
    private Integer getType;

    /**
     * 领取时间
     */
    private Date createTime;

    /**
     * 使用状态：0->未使用；1->已使用；2->已过期
     */
    private Integer useStatus;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 订单ID (使用了该券的订单)
     */
    private Long orderId;

    /**
     * 订单号码
     */
    private String orderSn;

    private static final long serialVersionUID = 1L;
}