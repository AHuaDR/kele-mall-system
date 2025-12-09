package com.my.query;

import lombok.Data;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 23:48
 * @description
 */
@Data
public class DeliveryQuery {
    private Long orderId;
    private String deliveryCompany;
    private String deliverySn;

}
