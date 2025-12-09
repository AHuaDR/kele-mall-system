package com.my.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 首页轮播图表
 * cms_banner
 */
@Data
public class CmsBanner implements Serializable {
    private Long id;

    /**
     * 广告名称
     */
    private String name;

    /**
     * 轮播位置：0->PC首页；1->app首页
     */
    private Integer type;

    /**
     * 图片地址
     */
    private String pic;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 上下线状态：0->下线；1->上线
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    private static final long serialVersionUID = 1L;
}