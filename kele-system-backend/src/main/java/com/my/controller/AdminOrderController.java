package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.model.OmsOrder;
import com.my.query.DeliveryQuery;
import com.my.query.OrderListQuery;
import com.my.result.R;
import com.my.service.AdminOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 19:55
 * @description
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {


    @Resource
    private AdminOrderService adminOrderService;

    /**
     * 获取订单列表
     * @param query
     * @return
     */
    @GetMapping("/list")
    public R getOrderList(OrderListQuery query){
        PageInfo<OmsOrder> omsOrderPageInfo = adminOrderService.getOrderList(query);
        return R.ok(omsOrderPageInfo);
    }

    /**
     * 根据id获取订单详情(返回包含商品详情)
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public R getOrderDetail(@PathVariable("id") Long id){
        OmsOrder omsOrder = adminOrderService.getOrderDetailById(id);
        return R.ok(omsOrder);
    }

    /**
     * 发货
     * @param deliveryQuery
     * @return
     */
    @PostMapping("/delivery")
    public R delivery(@RequestBody DeliveryQuery deliveryQuery){
        int result = adminOrderService.delivery(deliveryQuery);
        return result >= 1 ? R.ok() : R.error();
    }

}
