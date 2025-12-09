package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.PmsBrand;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 17:17
 * @description
 */
public interface BrandService {

    PageInfo<PmsBrand> getBrandList(Integer page, Integer size);

    int updateBrandById(Integer id, PmsBrand pmsBrand);

    int createBrand(PmsBrand pmsBrand);

    int deleteBrand(Long id);
}
