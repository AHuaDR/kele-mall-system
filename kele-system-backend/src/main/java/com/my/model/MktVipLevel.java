package com.my.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 会员等级配置表
 * mkt_vip_level
 */
@Data
public class MktVipLevel implements Serializable {
    /**
     * 会员等级编号，1=普通, 2=黄金, 3=钻石...
     */
    private Byte level;

    /**
     * 等级名称，如：黄金会员
     */
    private String name;

    /**
     * 显示图标（emoji 或 icon class）
     */
    private String icon;

    /**
     * Tailwind 渐变类名
     */
    private String colorClass;

    /**
     * 权益列表，例如 ["积分2倍", "包邮券 x5"]
     */
    private Object equityJson;

    /**
     * 所需最低成长值（用于自动升级）
     */
    private Integer minGrowth;

    /**
     * 购物折扣率，默认1.00表示无折扣
     */
    private BigDecimal discountRate;

    /**
     * 年费金额（如有）
     */
    private BigDecimal annualFee;

    /**
     * 描述说明
     */
    private String description;

    /**
     * 是否启用：1=是, 0=否
     */
    private Byte isEnabled;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}