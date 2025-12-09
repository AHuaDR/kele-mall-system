package com.my.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 22:03
 * @description
 */
@Data
public class SkuStockParam {
    private Long id;
    private Long productId;
    private String skuCode;
    private BigDecimal price;
    private Integer stock;
    private Integer lowStock;
    private String sp1;
    private String sp2;
    private String sp3;
    private String pic;
}
