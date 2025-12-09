package com.my.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 品牌表
 * pms_brand
 */
@Data
public class PmsBrand implements Serializable {
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 首字母
     */
    private String firstLetter;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否为品牌制造商：0->不是；1->是
     */
    private Integer factoryStatus;

    /**
     * 显示状态
     */
    private Integer showStatus;

    /**
     * 产品数量
     */
    private Integer productCount;

    /**
     * 品牌Logo
     */
    private String logo;

    /**
     * 专区大图
     */
    private String bigPic;

    /**
     * 品牌故事
     */
    private String brandStory;

    private static final long serialVersionUID = 1L;
}