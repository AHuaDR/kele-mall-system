package com.my.dto;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 16:12
 * @description
 */
@Data
public class AdminInfoParam {
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
     * 帐号启用状态：0->禁用；1->启用
     */
    private Integer status;
}
