package com.my.service.impl;

import com.my.mapper.PmsCategoryMapper;
import com.my.model.PmsCategory;
import com.my.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 16:16
 * @description
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private PmsCategoryMapper pmsCategoryMapper;

    @Override
    public List<PmsCategory> getcategoryList() {
        List<PmsCategory> pmsCategoryList = pmsCategoryMapper.selectAllCategory();
        return pmsCategoryList;
    }

    @Override
    public int updateCategoryById(Integer id,PmsCategory pmsCategory) {
        return pmsCategoryMapper.updateByPrimaryKeySelective(pmsCategory);
    }

    @Override
    public int insertCategory(PmsCategory pmsCategory) {
        return pmsCategoryMapper.insertSelective(pmsCategory);
    }

    @Override
    public int deleteCategory(Long id) {
        return pmsCategoryMapper.deleteByPrimaryKey(id);
    }
}
