package com.my.query;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 20:28
 * @description 商品列表
 */
@Data
public class ProductListQuery {
    private Integer page;
    private Integer size;
    private String keyword;
}
