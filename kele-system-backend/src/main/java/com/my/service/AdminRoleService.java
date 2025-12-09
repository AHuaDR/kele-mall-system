package com.my.service;

import com.my.model.SysRole;
import com.my.model.SysUserRoleRelation;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 17:51
 * @description
 */
public interface AdminRoleService {
    List<SysRole> getAdminRoleList();

    int updateAdminRoleRelation(SysUserRoleRelation sysUserRoleRelatio);

    int addAdminRole(SysRole sysRole);

    int updateAdminRole(Long id, SysRole sysRole);
}
