package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.UmsMember;
import com.my.query.MemberUserListQuery;
import com.my.response.MemberGroupResponse;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 11:32
 * @description
 */
public interface MemberUserService {
    PageInfo<UmsMember> getAllMemberList(MemberUserListQuery memberUserListQuery);

    MemberGroupResponse getMemberInfoList();
}
