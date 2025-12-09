package com.my.controller;

import com.my.model.MktVipLevel;
import com.my.result.R;
import com.my.service.AdminVipLevelsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 11:37
 * @description vip会员相关
 */
@RestController
@RequestMapping("/admin/vip-levels")
public class AdminVipLevelsController {

    @Resource
    private AdminVipLevelsService adminVipLevelsService;

    /**
     * 获取VIP等级列表
     * @return
     */
    @GetMapping("/list")
    public R getVipLevelList(){
        List<MktVipLevel> mktVipLevelList = adminVipLevelsService.getVipLevelList();
        return R.ok(mktVipLevelList);
    }

    @PutMapping("/{id}")
    public R updateVipLevel(@PathVariable Byte id, @RequestBody MktVipLevel mktVipLevel){
        int result  = adminVipLevelsService.updateVipLevel(id,mktVipLevel);
        return result >= 1 ? R.ok() : R.error();
    }
}
