package com.my.dto;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 18:09
 * @description
 */
@Data
public class MemberLoginParam {
    private String phone;
    private String password;
}
