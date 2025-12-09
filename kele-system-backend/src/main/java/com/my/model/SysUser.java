package com.my.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 后台用户表
 * sys_user
 */
@Data
public class SysUser implements Serializable {
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 (实际开发中应存BCrypt加密串)
     */
    private String password;

    /**
     * 头像
     */
    private String icon;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后登录时间
     */
    private Date loginTime;

    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

    //--------------------------------------------------------

}