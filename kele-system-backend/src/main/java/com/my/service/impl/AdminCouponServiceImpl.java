package com.my.service.impl;

import com.my.mapper.MktCouponMapper;
import com.my.model.MktCoupon;
import com.my.service.AdminCouponService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 19:51
 * @description
 */
@Service
public class AdminCouponServiceImpl implements AdminCouponService {

    @Resource
    private MktCouponMapper mktCouponMapper;

    @Override
    public List<MktCoupon> getCouponList() {
        List<MktCoupon> mktCouponList = mktCouponMapper.selectAllCouponList();
        return mktCouponList;
    }

    @Override
    public int createCoupon(MktCoupon mktCoupon) {
        return mktCouponMapper.insertSelective(mktCoupon);
    }

    @Override
    public int deleteCouponById(Long id) {
        return mktCouponMapper.deleteByPrimaryKey(id);
    }
}
