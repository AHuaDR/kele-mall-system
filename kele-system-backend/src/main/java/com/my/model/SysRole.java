package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 后台用户角色表
 * sys_role
 */
@Data
public class SysRole implements Serializable {
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 后台用户数量
     */
    private Integer adminCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 启用状态：0->禁用；1->启用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}