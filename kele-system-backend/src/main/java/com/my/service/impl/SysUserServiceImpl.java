package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.dto.AdminInfoParam;
import com.my.mapper.SysUserMapper;
import com.my.mapper.SysUserRoleRelationMapper;
import com.my.model.SysUser;
import com.my.query.AdminPageInfoQuery;
import com.my.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 15:51
 * @description
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserRoleRelationMapper sysUserRoleRelationMapper;

    @Override
    public SysUser getAdminInfoByUsername(String name) {
        SysUser sysUser = sysUserMapper.selectByUsername(name);
        return sysUser;
    }

    @Override
    public int updateAdminInfo(Long id, AdminInfoParam adminInfoParam) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(adminInfoParam,sysUser);
        sysUser.setId(id);
        return sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    public PageInfo<SysUser> selectAdminUserList(AdminPageInfoQuery adminPageInfoQuery) {
        PageHelper.startPage(adminPageInfoQuery.getPageNum(), adminPageInfoQuery.getPageSize());
        List<SysUser> sysUserList = sysUserMapper.selectAdminUserByUsername(adminPageInfoQuery.getKeyword());
        PageInfo<SysUser> sysUserPageInfo = new PageInfo(sysUserList);
        return sysUserPageInfo;
    }

    @Override
    public Long addAdminUser(SysUser sysUser) {
        //加密,并添加管理员
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUserMapper.insertSelective(sysUser);
        //返回新管理员的id
        Long id = sysUser.getId();
        return id;
    }

    @Override
    @Transactional
    public int deleteAdmin(Long id) {
        try{
            sysUserMapper.deleteByPrimaryKey(id);
            sysUserRoleRelationMapper.deleteByAdminId(id);
            return 1;
        } catch (Exception e) {
            throw new RuntimeException("删除管理员失败,已回滚:",e);
        }
    }

    @Override
    public int updateAdminStatus(Long id, Integer status) {
        return sysUserMapper.updateStatusById(id,status);
    }
}
