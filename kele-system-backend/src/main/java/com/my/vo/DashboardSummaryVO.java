package com.my.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 14:16
 * @description
 */
@Data
public class DashboardSummaryVO {
    /**
     * 总成交额
     */
    private BigDecimal totalGmv;
    /**
     * 总成交额同比趋势
     */
    private String gmvTrend;
    /**
     * 今日订单数
     */
    private Integer todayOrders;
    /**
     * 订单数同比趋势
     */
    private String orderTrend;
    /**
     * 新增会员数
     */
    private Integer newUsers;
    /**
     * 会员数同比趋势
     */
    private String userTrend;
    /**
     * 转化率
     */
    private Integer conversionRate;
    /**
     * 转化率同比趋势
     */
    private String rateTrend;

}
