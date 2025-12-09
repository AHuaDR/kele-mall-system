package com.my.service.impl;

import com.my.mapper.OmsOrderMapper;
import com.my.mapper.PmsProductMapper;
import com.my.mapper.UmsMemberMapper;
import com.my.model.OmsOrder;
import com.my.model.PmsProduct;
import com.my.model.UmsMember;
import com.my.service.AdminDashboardService;
import com.my.vo.DashboardSummaryVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 14:07
 * @description
 */
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Resource
    private OmsOrderMapper omsOrderMapper;
    @Resource
    private UmsMemberMapper umsMemberMapper;
    @Resource
    private PmsProductMapper pmsProductMapper;

    @Override
    @Transactional
    public DashboardSummaryVO getSummary() {
        //创建返回类型
        DashboardSummaryVO dashboardSummaryVO = new DashboardSummaryVO();
        //获取总成交额
        BigDecimal p = BigDecimal.ZERO;
        //已完成的订单状态
        int status = 3;
        //查询出已完成的订单,查出总销售额(已完成的订单)
        List<OmsOrder> omsOrders = omsOrderMapper.selectTodayAllOrderList(status);
        for (OmsOrder omsOrder : omsOrders) {
            p = p.add(omsOrder.getTotalAmount());
        }
        dashboardSummaryVO.setTotalGmv(p);
        p = BigDecimal.ZERO;

        //获取总成交额同比趋势
        String str = null;
        List<OmsOrder> omsOrdersYesterday = omsOrderMapper.selectYesterdayAllOrderList(status);
        for (OmsOrder omsOrder : omsOrdersYesterday) {
            p = p.add(omsOrder.getTotalAmount());
        }
        if (p.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal divide = dashboardSummaryVO.getTotalGmv().divide(p, 2, BigDecimal.ROUND_HALF_UP);
            if(divide.compareTo(BigDecimal.ZERO)>0){
                str = "+" +divide.toPlainString();
            }else if (divide.compareTo(BigDecimal.ZERO)==0){
                str = divide.toPlainString();
            }else {
                str = "-" +divide.toPlainString();
            }
        }else {
            str = "0";
        }
        dashboardSummaryVO.setGmvTrend(str);

        //获取今日订单数
        Integer todayOrders = omsOrderMapper.selectTodayAllOrderListCount();
        int todayOrders1 = (todayOrders != null) ? todayOrders : 0;
        dashboardSummaryVO.setTodayOrders(todayOrders1);

        //获取订单数同比趋势
        String orderTrendStr = null;
        Integer yesterdayOrders = omsOrderMapper.selectYesterdayAllOrderListCount();
        int yesterdayOrders1 = (yesterdayOrders != null) ? yesterdayOrders : 0;
        if (yesterdayOrders1 != 0) {
            Double orderTrend = (double) ((todayOrders1 / yesterdayOrders1) * 100);
            if (orderTrend > 0){
                orderTrendStr = "+" +orderTrend.toString();
            } else if (orderTrend == 0) {
                orderTrendStr = orderTrend.toString();
            }else {
                orderTrendStr = "-" +orderTrend.toString();
            }
        }else {
            orderTrendStr = "0";
        }
        dashboardSummaryVO.setOrderTrend(orderTrendStr);

        //获取新增会员数
        Integer newUsersTodayCount = umsMemberMapper.selectAllTodayNewMember();
        dashboardSummaryVO.setNewUsers(newUsersTodayCount);

        //获取会员数同比趋势
        Integer newUsersYesterdayCount = umsMemberMapper.selectAllYesterdayNewMember();
        int newUsersYesterdayCount1 =  (newUsersYesterdayCount != null) ? newUsersYesterdayCount : 0;
        String newUsersTrendStr = null;
        if (newUsersYesterdayCount1 != 0) {
            Double userTrend = (double) ((newUsersTodayCount / newUsersYesterdayCount1) * 100);
            if (userTrend > 0){
                newUsersTrendStr = "+" +userTrend.toString();
            }else if (userTrend == 0) {
                newUsersTrendStr = userTrend.toString();
            }else {
                newUsersTrendStr = "-" +userTrend.toString();
            }
        }else {
            newUsersTrendStr = "0";
        }
        dashboardSummaryVO.setUserTrend(newUsersTrendStr);

        //获取今天的商品转化率
        dashboardSummaryVO.setConversionRate(0);
        dashboardSummaryVO.setRateTrend("+0.2");


        return dashboardSummaryVO;
    }
}
