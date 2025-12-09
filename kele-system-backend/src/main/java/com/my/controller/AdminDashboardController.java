package com.my.controller;

import com.my.result.R;
import com.my.service.AdminDashboardService;
import com.my.vo.DashboardSummaryVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/8 14:04
 * @description 数据统计
 */
@RestController
@RequestMapping("/admin/stats")
public class AdminDashboardController {

    @Resource
    private AdminDashboardService adminDashboardService;

    /**
     * 获取统计卡片数据
     * @return
     */
    @GetMapping("/summary")
    public R getSummary() {
        DashboardSummaryVO vo = adminDashboardService.getSummary();
        return R.ok(vo);
    }

    /**
     * 获取流量趋势图
     * @param type
     * @return
     */
    @GetMapping("/traffic")
    public R getTraffic(@RequestParam String type) {
        Map<String, Object> chartData = new HashMap<>();

        // 1. 根据前端传递的 type 参数生成不同的X轴和数据
        if ("today".equals(type)) {
            // 今日数据（24小时时段）
            chartData.put("xAxis", new String[]{"00:00", "02:00", "04:00", "06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"});

            chartData.put("series", new Integer[]{120, 82, 91, 154, 162, 140, 145, 250, 134, 190, 230, 180});
        } else if ("week".equals(type)) {
            // 本周数据（过去7天）
            chartData.put("xAxis", new String[]{"12-02", "12-03", "12-04", "12-05", "12-06", "12-07", "12-08"});
            chartData.put("series", new Integer[]{820, 932, 901, 934, 1290, 1330, 1320});
        } else {
            // 可以返回默认数据或错误信息
            chartData.put("xAxis", new String[]{});
            chartData.put("series", new Integer[]{});
        }
        // 2. 返回统一响应结构
        return R.ok(chartData);
    }
}
