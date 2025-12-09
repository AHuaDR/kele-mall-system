package com.my.response;

import com.my.model.UmsMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 23:47
 * @description 根据会员等级返回用户数据
 */
@Data
public class MemberGroupResponse {
    private List<UmsMember> goldMembers;    // 黄金会员列表
    private List<UmsMember> silverMembers;  // 白银会员列表
    private List<UmsMember> normalMembers; // 普通会员列表

    public MemberGroupResponse(){
        this.goldMembers = new ArrayList<>();
        this.silverMembers = new ArrayList<>();
        this.normalMembers = new ArrayList<>();
    }

}
