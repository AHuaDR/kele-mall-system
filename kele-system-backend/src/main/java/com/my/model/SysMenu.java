package com.my.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 后台菜单表
 * sys_menu
 */
@Data
public class SysMenu implements Serializable {
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 菜单级数
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 前端路由名称
     */
    private String name;

    /**
     * 前端图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Integer hidden;

    private static final long serialVersionUID = 1L;
}