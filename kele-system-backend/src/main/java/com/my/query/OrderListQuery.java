package com.my.query;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 20:14
 * @description
 */
@Data
public class OrderListQuery {
    private Integer pageNum;
    private Integer pageSize;
    private Integer status;
    private String orderSn;
}
