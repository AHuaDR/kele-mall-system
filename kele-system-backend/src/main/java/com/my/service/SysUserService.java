package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.dto.AdminInfoParam;
import com.my.model.SysUser;
import com.my.query.AdminPageInfoQuery;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 15:38
 * @description
 */
public interface SysUserService {
    SysUser getAdminInfoByUsername(String name);

    int updateAdminInfo(Long id, AdminInfoParam adminInfoParam);

    PageInfo<SysUser> selectAdminUserList(AdminPageInfoQuery adminPageInfoQuery);

    Long addAdminUser(SysUser sysUser);

    int deleteAdmin(Long id);

    int updateAdminStatus(Long id, Integer status);
}
