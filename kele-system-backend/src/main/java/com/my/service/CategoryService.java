package com.my.service;

import com.my.model.PmsCategory;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 16:15
 * @description
 */
public interface CategoryService {
    List<PmsCategory> getcategoryList();

    int updateCategoryById(Integer id,PmsCategory pmsCategory);

    int insertCategory(PmsCategory pmsCategory);

    int deleteCategory(Long id);
}
