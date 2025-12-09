package com.my.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 后台用户和角色关系表
 * sys_user_role_relation
 */
@Data
public class SysUserRoleRelation implements Serializable {
    private Long id;

    /**
     * 后台用户ID (sys_user.id)
     */
    private Long adminId;

    /**
     * 角色ID (sys_role.id)
     */
    private Long roleId;

    private static final long serialVersionUID = 1L;
}