package com.my.mapper;

import com.my.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OmsOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsOrder record);

    int insertSelective(OmsOrder record);

    OmsOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OmsOrder record);

    int updateByPrimaryKey(OmsOrder record);

    List<OmsOrder> selectAllOrderList(@Param("orderSn") String orderSn,@Param("status") Integer status);

    OmsOrder selectByDetailAndProductInfo(Long id);

    List<OmsOrder> selectTodayAllOrderList(int status);

    List<OmsOrder> selectYesterdayAllOrderList(int status);

    Integer selectTodayAllOrderListCount();

    Integer selectYesterdayAllOrderListCount();
}