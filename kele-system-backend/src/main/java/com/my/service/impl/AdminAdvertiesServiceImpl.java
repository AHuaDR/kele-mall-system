package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.CmsBannerMapper;
import com.my.model.CmsBanner;
import com.my.query.AdminPageInfoQuery;
import com.my.service.AdminAdvertiesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 21:03
 * @description
 */
@Service
public class AdminAdvertiesServiceImpl implements AdminAdvertiesService {

    @Resource
    private CmsBannerMapper cmsBannerMapper;



    @Override
    public PageInfo<CmsBanner> getAllAdvertise(AdminPageInfoQuery adminPageInfoQuery) {
        PageHelper.startPage(adminPageInfoQuery.getPageNum(), adminPageInfoQuery.getPageSize());
        List<CmsBanner> cmsBannerList = cmsBannerMapper.selectAllAdvertise();
        PageInfo<CmsBanner> pageInfo = new PageInfo(cmsBannerList);
        return pageInfo;
    }

    @Override
    public int createAdvertise(CmsBanner cmsBanner) {
        return cmsBannerMapper.insertSelective(cmsBanner);
    }

    @Override
    public int deleteAdvertise(Long id) {
        return cmsBannerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateAdvertise(Long id, CmsBanner cmsBanner) {
        cmsBanner.setId(id);
        return cmsBannerMapper.updateByPrimaryKeySelective(cmsBanner);
    }
}
