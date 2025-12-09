package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.OmsOrderMapper;
import com.my.model.OmsOrder;
import com.my.query.DeliveryQuery;
import com.my.query.OrderListQuery;
import com.my.service.AdminOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/6 19:56
 * @description
 */
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Resource
    private OmsOrderMapper omsOrderMapper;

    @Override
    public PageInfo<OmsOrder> getOrderList(OrderListQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<OmsOrder> omsOrderList = omsOrderMapper.selectAllOrderList(query.getOrderSn(),query.getStatus());
        PageInfo<OmsOrder> pageInfo = new PageInfo<>(omsOrderList);
        return pageInfo;
    }

    @Override
    public OmsOrder getOrderDetailById(Long id) {
        //获取订单信息 + 商品信息
        OmsOrder omsOrder = omsOrderMapper.selectByDetailAndProductInfo(id);

        return omsOrder;
    }

    @Override
    public int delivery(DeliveryQuery deliveryQuery) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setId(deliveryQuery.getOrderId());
        omsOrder.setDeliverySn(deliveryQuery.getDeliverySn());
        omsOrder.setDeliveryCompany(deliveryQuery.getDeliveryCompany());
        omsOrder.setStatus(2);
        return omsOrderMapper.updateByPrimaryKeySelective(omsOrder);
    }
}
