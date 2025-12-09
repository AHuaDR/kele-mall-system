package com.my.service.impl;

import com.my.mapper.SysUserMapper;
import com.my.model.SysRole;
import com.my.model.SysUser;
import com.my.security.AdminLoginUser;
import jakarta.annotation.Resource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 13:13
 * @description
 */
@Service("adminUserServiceImpl")
public class AdminUserServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser =  sysUserMapper.selectByLogin(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("管理员账户不存在");
        }
        List<SysRole> roleList = sysUserMapper.selectRolesByAdminId(sysUser.getId());

        return new AdminLoginUser(sysUser, roleList);
    }
}
