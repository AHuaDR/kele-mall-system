package com.my.query;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/4 17:42
 * @description
 */
@Data
public class AdminPageInfoQuery {
    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
}
