package com.my.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 21:56
 * @description
 */
@Data
public class CreateProductParam {
    //基本商品信息
    private Long id;
    private Long brandId;
    private Long categoryId;
    private String name;
    private String subTitle;
    private String pic;
    private String albumPics;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sort;
    private Integer sale;
    private Boolean publishStatus;
    private Boolean recommendStatus;
    private String detailHtml;

    //SKU列表
    private List<SkuStockParam> skuStockList;
}
