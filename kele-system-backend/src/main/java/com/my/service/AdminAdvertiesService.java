package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.CmsBanner;
import com.my.query.AdminPageInfoQuery;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 21:02
 * @description
 */
public interface AdminAdvertiesService {
    PageInfo<CmsBanner> getAllAdvertise(AdminPageInfoQuery adminPageInfoQuery);

    int createAdvertise(CmsBanner cmsBanner);

    int deleteAdvertise(Long id);

    int updateAdvertise(Long id, CmsBanner cmsBanner);
}
