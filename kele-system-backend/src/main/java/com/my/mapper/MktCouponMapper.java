package com.my.mapper;

import com.my.model.MktCoupon;

import java.util.List;

public interface MktCouponMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MktCoupon record);

    int insertSelective(MktCoupon record);

    MktCoupon selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MktCoupon record);

    int updateByPrimaryKey(MktCoupon record);

    List<MktCoupon> selectAllCouponList();
}