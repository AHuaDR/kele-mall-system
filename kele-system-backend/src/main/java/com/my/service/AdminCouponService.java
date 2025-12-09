package com.my.service;

import com.my.model.MktCoupon;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 19:51
 * @description
 */
public interface AdminCouponService {
    List<MktCoupon> getCouponList();

    int createCoupon(MktCoupon mktCoupon);

    int deleteCouponById(Long id);
}
