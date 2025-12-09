package com.my.mapper;

import com.my.model.UmsMember;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UmsMemberMapper {

    // 根据手机号查会员
    @Select("SELECT * FROM ums_member WHERE phone = #{phone} AND status = 1")
    UmsMember selectByPhone(String phone);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMember record);

    int insertSelective(UmsMember record);

    UmsMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMember record);

    int updateByPrimaryKey(UmsMember record);

    List<UmsMember> selectAllMember(String nickname);

    Integer selectAllTodayNewMember();

    Integer selectAllYesterdayNewMember();
}