package com.my.query;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 11:28
 * @description
 */
@Data
public class MemberUserListQuery {
    private Integer page;
    private Integer size;
    private String nickname;
}
