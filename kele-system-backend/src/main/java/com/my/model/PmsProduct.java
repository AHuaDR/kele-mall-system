package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 商品信息表
 * pms_product
 */
@Data
public class PmsProduct implements Serializable {
    private Long id;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 主图
     */
    private String pic;

    /**
     * 画册图片，逗号分割
     */
    private String albumPics;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal originalPrice;

    /**
     * 总库存
     */
    private Integer stock;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 销量
     */
    private Integer sale;

    /**
     * 上架状态：0->下架；1->上架
     */
    private Boolean publishStatus;

    /**
     * 推荐状态：0->不推荐；1->推荐
     */
    private Boolean recommendStatus;

    /**
     * 详情网页内容
     */
    private String detailHtml;

    /**
     * 浏览量
     */
    private Integer browse;

    private static final long serialVersionUID = 1L;
}