package com.my.mapper;

import com.my.model.MktCouponHistory;

public interface MktCouponHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MktCouponHistory record);

    int insertSelective(MktCouponHistory record);

    MktCouponHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MktCouponHistory record);

    int updateByPrimaryKey(MktCouponHistory record);
}