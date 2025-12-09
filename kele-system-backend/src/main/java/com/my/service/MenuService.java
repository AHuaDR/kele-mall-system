package com.my.service;

import com.my.model.SysMenu;
import com.my.query.UpdateRoleMenuQuery;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 23:02
 * @description
 */
public interface MenuService {
    List<SysMenu> getMenuTreeList();

    List<SysMenu> getAdminMenuList(Long id);

    int updateAllocMenu(UpdateRoleMenuQuery updateRoleMenuQuery);
}
