package com.my.service.impl;

import com.my.mapper.SysRoleMapper;
import com.my.mapper.SysUserRoleRelationMapper;
import com.my.model.SysRole;
import com.my.model.SysUserRoleRelation;
import com.my.service.AdminRoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 17:51
 * @description
 */
@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleRelationMapper sysUserRoleRelationMapper;

    @Override
    public List<SysRole> getAdminRoleList() {
        List<SysRole> roleList = sysRoleMapper.selectAdminRoleList();
        return roleList;
    }

    @Override
    @Transactional
    public int updateAdminRoleRelation(SysUserRoleRelation sysUserRoleRelation) {
        try{
            //添加管理员与角色对应
            sysUserRoleRelationMapper.insertSelective(sysUserRoleRelation);
            //相关角色统计加一
            SysRole sysRole = sysRoleMapper.selectAdminRoleById(sysUserRoleRelation.getRoleId());
            sysRole.setAdminCount(sysRole.getAdminCount() + 1);
            sysRoleMapper.updateByPrimaryKeySelective(sysRole);
            return 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addAdminRole(SysRole sysRole) {
        sysRole.setCreateTime(new Date());
        return sysRoleMapper.insertSelective(sysRole);
    }

    @Override
    public int updateAdminRole(Long id, SysRole sysRole) {
        sysRole.setId(id);
        return sysRoleMapper.updateByPrimaryKeySelective(sysRole);
    }
}
