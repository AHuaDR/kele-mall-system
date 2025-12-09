package com.my.controller;

import com.my.model.SysMenu;
import com.my.model.SysRole;
import com.my.model.SysUserRoleRelation;
import com.my.query.UpdateRoleMenuQuery;
import com.my.result.R;
import com.my.service.AdminRoleService;
import com.my.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 17:50
 * @description B端角色管理
 */
@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Resource
    private AdminRoleService adminRoleService;
    @Autowired
    private MenuService menuService;

    /**
     * 获取角色列表
     * @return
     */
    @GetMapping("/listAll")
    public R AdminRoleListAll(){
        List<SysRole> sysRoleList = adminRoleService.getAdminRoleList();
        return R.ok(sysRoleList);
    }

    /**
     * B端为新增管理员添加角色
     * @param sysUserRoleRelation
     * @return
     */
    @PostMapping("/update")
    public R AdminRoleUpdate(@RequestBody SysUserRoleRelation sysUserRoleRelation){
        int result = adminRoleService.updateAdminRoleRelation(sysUserRoleRelation);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 创建管理员角色
     * @param sysRole
     * @return
     */
    @PostMapping("/create")
    public R AdminRoleCreate(@RequestBody SysRole sysRole){
        int result = adminRoleService.addAdminRole(sysRole);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 获取所选用户拥有哪些菜单权限
     * @param id
     * @return
     */
    @GetMapping("/listMenu/{id}")
    public R AdminRoleListMenu(@PathVariable Long id){
        List<SysMenu> sysMenuList = menuService.getAdminMenuList(id);
        return R.ok(sysMenuList);
    }

    /**
     * 更新所选用户拥有的菜单权限
     * @param updateRoleMenuQuery
     * @return
     */
    @PostMapping("/allocMenu")
    public R allocMenu(@RequestBody UpdateRoleMenuQuery  updateRoleMenuQuery){
        int result = menuService.updateAllocMenu(updateRoleMenuQuery);
        return result >= 1 ? R.ok():R.error();
    }

    /**
     * 更新角色信息
     * @param id
     * @param sysRole
     * @return
     */
    @PostMapping("/update/{id}")
    public R updateAdminRole(@PathVariable Long id,@RequestBody SysRole sysRole){
        int result = adminRoleService.updateAdminRole(id,sysRole);
        return result >= 1 ? R.ok():R.error();
    }


}
