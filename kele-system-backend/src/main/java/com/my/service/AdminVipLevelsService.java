package com.my.service;

import com.my.model.MktVipLevel;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 11:38
 * @description
 */
public interface AdminVipLevelsService {

    List<MktVipLevel> getVipLevelList();

    int updateVipLevel(Byte id, MktVipLevel mktVipLevel);
}
