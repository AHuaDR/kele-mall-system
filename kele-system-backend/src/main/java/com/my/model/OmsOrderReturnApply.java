package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 订单退货申请
 * oms_order_return_apply
 */
@Data
public class OmsOrderReturnApply implements Serializable {
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 退货商品id
     */
    private Long productId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * 会员用户名
     */
    private String memberUsername;

    /**
     * 退款金额
     */
    private BigDecimal returnAmount;

    /**
     * 退货人姓名
     */
    private String returnName;

    /**
     * 退货人电话
     */
    private String returnPhone;

    /**
     * 申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝
     */
    private Integer status;

    /**
     * 处理时间
     */
    private Date handleTime;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品销售属性
     */
    private String productAttr;

    /**
     * 退货数量
     */
    private Integer productCount;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 原因
     */
    private String reason;

    /**
     * 描述
     */
    private String description;

    /**
     * 凭证图片，以逗号隔开
     */
    private String proofPics;

    private static final long serialVersionUID = 1L;
}