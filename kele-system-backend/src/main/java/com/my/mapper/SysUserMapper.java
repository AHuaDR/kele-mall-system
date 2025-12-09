package com.my.mapper;

import com.github.pagehelper.PageInfo;
import com.my.model.SysRole;
import com.my.model.SysUser;
import com.my.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser selectByLogin(String username);

    @Select("SELECT r.* FROM sys_role r " +
            "LEFT JOIN sys_user_role_relation ur ON r.id = ur.role_id " +
            "WHERE ur.admin_id = #{adminId}")
    List<SysRole> selectRolesByAdminId(Long id);

    SysUser selectByUsername(String name);

    List<SysUser> selectAdminUserByUsername(String keyword);

    int updateStatusById(@Param("id") Long id,@Param("status") Integer status);
}