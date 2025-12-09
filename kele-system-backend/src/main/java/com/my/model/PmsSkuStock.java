package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 商品SKU库存表
 * pms_sku_stock
 */
@Data
public class PmsSkuStock implements Serializable {
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 预警库存
     */
    private Integer lowStock;

    /**
     * 销售属性1 (如颜色)
     */
    private String sp1;

    /**
     * 销售属性2 (如容量)
     */
    private String sp2;

    /**
     * 销售属性3
     */
    private String sp3;

    /**
     * 展示图片
     */
    private String pic;

    /**
     * 一对一关联
     */
    private PmsProduct productDO;

    private static final long serialVersionUID = 1L;
}