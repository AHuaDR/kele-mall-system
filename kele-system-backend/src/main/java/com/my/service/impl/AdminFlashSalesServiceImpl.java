package com.my.service.impl;

import com.my.mapper.MktFlashSaleMapper;
import com.my.model.MktFlashSale;
import com.my.model.SysUser;
import com.my.security.AdminLoginUser;
import com.my.service.AdminFlashSalesService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/7 22:00
 * @description
 */
@Service
public class AdminFlashSalesServiceImpl implements AdminFlashSalesService {

    @Resource
    private MktFlashSaleMapper mktFlashSaleMapper;

    @Override
    public List<MktFlashSale> getFlashSalesList() {
        List<MktFlashSale> mktFlashSaleList = mktFlashSaleMapper.selectAllFlashSales();
        return mktFlashSaleList;
    }

    @Override
    @Transactional
    public int createFlashSales(MktFlashSale mktFlashSale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdminLoginUser principal = (AdminLoginUser)authentication.getPrincipal();
        SysUser sysUser = principal.getSysUser();
        mktFlashSale.setCreateTime(new Date());
        mktFlashSale.setUpdateTime(new Date());
        mktFlashSale.setCreator(sysUser.getUsername());

        return mktFlashSaleMapper.insertSelective(mktFlashSale);
    }
}
