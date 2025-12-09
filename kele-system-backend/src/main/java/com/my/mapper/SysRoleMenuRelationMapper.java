package com.my.mapper;

import com.my.model.SysMenu;
import com.my.model.SysRoleMenuRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMenuRelationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysRoleMenuRelation record);

    int insertSelective(SysRoleMenuRelation record);

    SysRoleMenuRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRoleMenuRelation record);

    int updateByPrimaryKey(SysRoleMenuRelation record);

    List<SysMenu> selectMenuListByAdminId(Long id);

    int deleteByRoleId(Long roleId);

    void batchInsert(@Param("roleMenus") List<SysRoleMenuRelation> roleMenus);
}