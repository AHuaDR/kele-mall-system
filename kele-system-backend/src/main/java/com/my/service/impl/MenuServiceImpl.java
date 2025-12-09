package com.my.service.impl;

import com.my.mapper.SysMenuMapper;
import com.my.mapper.SysRoleMenuRelationMapper;
import com.my.model.SysMenu;
import com.my.model.SysRoleMenuRelation;
import com.my.query.UpdateRoleMenuQuery;
import com.my.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 23:02
 * @description
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuRelationMapper sysRoleMenuRelationMapper;

    @Override
    public List<SysMenu> getMenuTreeList() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectAllMenu();
        return sysMenuList;
    }

    @Override
    public List<SysMenu> getAdminMenuList(Long id) {
        List<SysMenu> sysMenuList = sysRoleMenuRelationMapper.selectMenuListByAdminId(id);
        return sysMenuList;
    }

    @Override
    @Transactional
    public int updateAllocMenu(UpdateRoleMenuQuery updateRoleMenuQuery) {
        try{
            Long roleId = updateRoleMenuQuery.getRoleId();
            List<Long> menuIds = updateRoleMenuQuery.getMenuIds();
            //删除原先所有的权限
            sysRoleMenuRelationMapper.deleteByRoleId(roleId);

            //添加新的权限
            if (menuIds != null && !menuIds.isEmpty()) {
                // 将菜单ID列表转换为RoleMenu实体列表
                List<SysRoleMenuRelation> roleMenus = new ArrayList<>();
                for (Long menuId : menuIds) {
                    roleMenus.add(new SysRoleMenuRelation(roleId, menuId));
                }
                // 执行批量插入（只发生1次数据库交互）
                sysRoleMenuRelationMapper.batchInsert(roleMenus);
            }
            return 1;
        } catch (RuntimeException e) {
            throw new RuntimeException("更改用户权限失败:",e);
        }
    }
}
