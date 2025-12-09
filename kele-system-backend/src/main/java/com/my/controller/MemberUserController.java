package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.model.UmsMember;
import com.my.query.MemberUserListQuery;
import com.my.response.MemberGroupResponse;
import com.my.result.R;
import com.my.service.MemberUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 11:26
 * @description 用户管理
 */
@RestController
@RequestMapping("/admin/member")
public class MemberUserController {

    @Resource
    private MemberUserService memberUserService;

    /**
     * 获取用户列表
     * @param memberUserListQuery
     * @return
     */
    @GetMapping("/list")
    public R list(MemberUserListQuery memberUserListQuery){
        PageInfo<UmsMember> umsMemberPageInfo = memberUserService.getAllMemberList(memberUserListQuery);
        return R.ok(umsMemberPageInfo);

    }

    @GetMapping("/memberlist")
    public R memberlist(){
        MemberGroupResponse umsMemberList = memberUserService.getMemberInfoList();
        return R.ok(umsMemberList);
    }

}
