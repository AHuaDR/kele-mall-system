package com.my.controller;

import com.my.model.MktCoupon;
import com.my.result.R;
import com.my.service.AdminCouponService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 19:50
 * @description 营销中心
 */
@RestController
@RequestMapping("/admin/coupon")
public class AdminCouponController {

    @Resource
    private AdminCouponService adminCouponService;

    /**
     * 获取优惠券列表
     * @return
     */
    @GetMapping("/list")
    public R getCouponList() {
        List<MktCoupon> mktCouponList = adminCouponService.getCouponList();
        return R.ok(mktCouponList);
    }

    /**
     * 创建优惠券
     * @param mktCoupon
     * @return
     */
    @PostMapping("/create")
    public R createCoupon(@RequestBody MktCoupon mktCoupon) {
        int result = adminCouponService.createCoupon(mktCoupon);
        return result > 0 ? R.ok() : R.error();
    }

    /**
     * 删除优惠券
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public R deleteCoupon(@PathVariable Long id) {
        int result = adminCouponService.deleteCouponById(id);
        return result > 0 ? R.ok() : R.error();
    }



}
