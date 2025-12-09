package com.my.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台角色菜单关系表
 * sys_role_menu_relation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleMenuRelation implements Serializable {
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

    private static final long serialVersionUID = 1L;

    public SysRoleMenuRelation(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}