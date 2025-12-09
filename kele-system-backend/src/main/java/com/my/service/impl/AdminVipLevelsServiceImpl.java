package com.my.service.impl;

import com.my.mapper.MktVipLevelMapper;
import com.my.model.MktVipLevel;
import com.my.service.AdminVipLevelsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 11:38
 * @description
 */
@Service
public class AdminVipLevelsServiceImpl implements AdminVipLevelsService {

    @Resource
    private MktVipLevelMapper mktVipLevelMapper;

    @Override
    public List<MktVipLevel> getVipLevelList() {
        List<MktVipLevel> mktVipLevelList = mktVipLevelMapper.selectAllVipLevel();
        return mktVipLevelList;
    }

    @Override
    public int updateVipLevel(Byte id, MktVipLevel mktVipLevel) {
        mktVipLevel.setLevel(id);
        return mktVipLevelMapper.updateByPrimaryKeySelective(mktVipLevel);
    }
}
