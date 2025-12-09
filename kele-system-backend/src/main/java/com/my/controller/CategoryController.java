package com.my.controller;

import com.my.model.PmsCategory;
import com.my.result.R;
import com.my.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 16:12
 * @description 商品分类
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取类目列表
     * @return
     */
    @GetMapping("/list")
    public R list(){
        List<PmsCategory> pmsCategoryList = categoryService.getcategoryList();
        List<PmsCategory> level0List = pmsCategoryList.stream()
                .filter(item -> item.getLevel() == 0) // 只取一级
                .sorted(Comparator.comparing(PmsCategory::getSort).reversed()) // 按 sort 排序
                .collect(Collectors.toList());
        for (PmsCategory parent : level0List) {
            List<PmsCategory> children = pmsCategoryList.stream()
                    .filter(item -> item.getLevel() == 1 && item.getParentId().equals(parent.getId()))
                    .sorted(Comparator.comparing(PmsCategory::getSort).reversed())
                    .collect(Collectors.toList());

            // 把找到的孩子放入父对象中
            parent.setChildren(children);
        }
        System.out.println(level0List);
        return R.ok(level0List);
    }

    /**
     * 更新类目信息
     * @param id
     * @param pmsCategory
     * @return
     */
    @PostMapping("/update/{id}")
    public R updateCategory(@PathVariable("id") Integer id,@RequestBody PmsCategory pmsCategory){
        int result = categoryService.updateCategoryById(id,pmsCategory);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 新建类目
     * @param pmsCategory
     * @return
     */
    @PostMapping("/create")
    public R createCategory(@RequestBody PmsCategory pmsCategory){
        int result = categoryService.insertCategory(pmsCategory);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 删除类目
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public R deleteCategory(@PathVariable("id") Long id){
        int result = categoryService.deleteCategory(id);
        return result >= 1 ? R.ok() : R.error();
    }


}
