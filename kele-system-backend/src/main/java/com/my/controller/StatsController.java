package com.my.controller;

import com.my.result.R;
import com.my.service.StatsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 19:57
 * @description
 */
@RestController
@RequestMapping("/admin/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("/tra")
    public R traffic(@RequestParam String type){
        statsService.loadChartData(type);

        return R.ok();
    }

}
