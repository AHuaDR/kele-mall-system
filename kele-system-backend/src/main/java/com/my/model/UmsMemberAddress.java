package com.my.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 会员收货地址表
 * ums_member_address
 */
@Data
public class UmsMemberAddress implements Serializable {
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 收货人名称
     */
    private String name;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省份/直辖市
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区
     */
    private String region;

    /**
     * 详细地址(街道)
     */
    private String detailAddress;

    /**
     * 是否为默认
     */
    private Boolean defaultStatus;

    private static final long serialVersionUID = 1L;
}