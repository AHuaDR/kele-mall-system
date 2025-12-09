package com.my.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.model.PmsBrand;
import com.my.result.R;
import com.my.service.BrandService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 17:14
 * @description 品牌管理
 */
@RestController
@RequestMapping("/admin/brand")
public class BrandController {

    @Resource
    private BrandService brandService;

    /**
     * 获取品牌列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public R brandList(@RequestParam Integer page, @RequestParam Integer size) {
        PageInfo<PmsBrand> pmsBrandPageHelper = brandService.getBrandList(page,size);
        return R.ok(pmsBrandPageHelper);
    }

    /**
     * 更新品牌信息
     * @param id
     * @param pmsBrand
     * @return
     */
    @PostMapping("/update/{id}")
    public R updateBrand(@PathVariable Integer id,@RequestBody PmsBrand pmsBrand){
        int result = brandService.updateBrandById(id,pmsBrand);
        return result >= 1?R.ok():R.error();
    }

    /**
     * 新增品牌
     * @param pmsBrand
     * @return
     */
    @PostMapping("/create")
    public R createBrand(@RequestBody PmsBrand pmsBrand){
        int result = brandService.createBrand(pmsBrand);
        return result >= 1?R.ok():R.error();
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public R deleteBrand(@PathVariable Long id){
        int result = brandService.deleteBrand(id);
        return result >= 1?R.ok():R.error();
    }

}
