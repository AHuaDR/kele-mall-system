package com.my.query;

import lombok.Data;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 23:43
 * @description
 */
@Data
public class UpdateRoleMenuQuery {
    private Long roleId;
    private List<Long> menuIds;
}
