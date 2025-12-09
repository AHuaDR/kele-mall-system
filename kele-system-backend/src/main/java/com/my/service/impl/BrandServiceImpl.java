package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.PmsBrandMapper;
import com.my.model.PmsBrand;
import com.my.service.BrandService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 17:18
 * @description
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private PmsBrandMapper pmsBrandMapper;

    @Override
    public PageInfo<PmsBrand> getBrandList(Integer page, Integer size) {
        PageHelper.startPage(page,size);
        List<PmsBrand> pmsBrandList = pmsBrandMapper.selectAllBrand();
        PageInfo<PmsBrand> pageInfo = new PageInfo<>(pmsBrandList);
        return pageInfo;
    }

    @Override
    public int updateBrandById(Integer id, PmsBrand pmsBrand) {
        return  pmsBrandMapper.updateByPrimaryKeySelective(pmsBrand);
    }

    @Override
    public int createBrand(PmsBrand pmsBrand) {
        return pmsBrandMapper.insertSelective(pmsBrand);
    }

    @Override
    public int deleteBrand(Long id) {
        return pmsBrandMapper.deleteByPrimaryKey(id);
    }
}
