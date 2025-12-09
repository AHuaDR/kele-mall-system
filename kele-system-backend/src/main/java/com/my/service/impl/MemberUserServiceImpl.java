package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.UmsMemberMapper;
import com.my.model.UmsMember;
import com.my.query.MemberUserListQuery;
import com.my.response.MemberGroupResponse;
import com.my.result.R;
import com.my.security.MemberLoginUser;
import com.my.service.MemberUserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 13:50
 * @description
 */
@Service("memberUserServiceImpl")
public class MemberUserServiceImpl implements UserDetailsService, MemberUserService {

    @Resource
    private UmsMemberMapper umsMemberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UmsMember umsMember = umsMemberMapper.selectByPhone(username);
        if (umsMember == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new MemberLoginUser(umsMember);
    }

    @Override
    public PageInfo<UmsMember> getAllMemberList(MemberUserListQuery memberUserListQuery) {
        PageHelper.startPage(memberUserListQuery.getPage(), memberUserListQuery.getSize());
        List<UmsMember> umsMemberList = umsMemberMapper.selectAllMember(memberUserListQuery.getNickname());
        PageInfo<UmsMember> pageInfo = new PageInfo<>(umsMemberList);
        return pageInfo;
    }

    @Override
    public MemberGroupResponse getMemberInfoList() {
        //查询所有用户
        List<UmsMember> umsMemberList = umsMemberMapper.selectAllMember(null);
        //初始化响应对象
        MemberGroupResponse memberGroupResponse = new MemberGroupResponse();
        //遍历用户列表(根据会员等级level_id)
        for (UmsMember umsMember : umsMemberList) {
            if (umsMember.getLevelId() == 3) {
                //黄金会员
                memberGroupResponse.getGoldMembers().add(umsMember);
            } else if (umsMember.getLevelId() == 2) {
                //白银会员
                memberGroupResponse.getSilverMembers().add(umsMember);
            } else if (umsMember.getLevelId() == 1) {
                //普通会员
                memberGroupResponse.getNormalMembers().add(umsMember);
            }
        }

        return memberGroupResponse;
    }
}
