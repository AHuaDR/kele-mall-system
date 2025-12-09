package com.my.dto;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 16:39
 * @description
 */
@Data
public class UpdatePasswordParam {
    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 老密码
     */
    private String oldPassword;
}
