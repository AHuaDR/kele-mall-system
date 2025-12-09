package com.my.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 商品分类表
 * pms_category
 */
@Data
public class PmsCategory implements Serializable {
    private Long id;

    /**
     * 上级分类的编号：0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类级别：0->1级；1->2级
     */
    private Integer level;

    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Boolean showStatus;

    private static final long serialVersionUID = 1L;

    private List<PmsCategory> children;
}