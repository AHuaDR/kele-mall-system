package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.OmsOrder;
import com.my.query.DeliveryQuery;
import com.my.query.OrderListQuery;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 19:55
 * @description
 */
public interface AdminOrderService {
    PageInfo<OmsOrder> getOrderList(OrderListQuery query);

    OmsOrder getOrderDetailById(Long id);

    int delivery(DeliveryQuery deliveryQuery);
}
