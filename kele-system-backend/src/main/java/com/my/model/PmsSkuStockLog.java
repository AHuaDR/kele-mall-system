package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品库存变动流水表
 * pms_sku_stock_log
 */
@Data
public class PmsSkuStockLog implements Serializable {
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID (pms_sku_stock的主键)
     */
    private Long skuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 变动数量 (正数=入库/回补，负数=出库/扣减)
     */
    private Integer changeCount;

    /**
     * 变动后的剩余库存 (快照)
     */
    private Integer finalStock;

    /**
     * 变动类型：1->订单扣减；2->退货回补；3->管理员修改；4->补货
     */
    private Integer type;

    /**
     * 关联的订单号 (如果是订单导致)
     */
    private String orderSn;

    /**
     * 操作人 (管理员用户名 或 "用户下单")
     */
    private String operator;

    /**
     * 备注
     */
    private String note;

    /**
     * 发生时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}