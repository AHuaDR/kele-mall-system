package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 会员表
 * ums_member
 */
@Data
public class UmsMember implements Serializable {
    /**
     * 会员ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(加密)
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0->未知；1->男；2->女
     */
    private Boolean gender;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 会员等级ID
     */
    private Long levelId;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 帐号启用状态:0->禁用；1->启用
     */
    private Boolean status;

    /**
     * 注册时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}