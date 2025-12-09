package com.my.controller;

import com.github.pagehelper.PageInfo;
import com.my.model.CmsBanner;
import com.my.model.CmsChatMessage;
import com.my.model.CmsNotify;
import com.my.query.AdminPageInfoQuery;
import com.my.result.R;
import com.my.service.AdminAdvertiesService;
import com.my.service.AdminNoticeService;
import com.my.service.CmsNotifyService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 20:54
 * @description 广告配置
 */
@RestController
@RequestMapping("/admin/home")
public class AdminAdvertiseController {

    @Resource
    private AdminAdvertiesService adminAdvertiesService;

    @Resource
    private CmsNotifyService cmsNotifyService;


    /**
     * 获取广告列表(轮播图)
     * @param adminPageInfoQuery
     * @return
     */
    @GetMapping("/advertise/list")
    public R advertiseList(AdminPageInfoQuery adminPageInfoQuery){
        PageInfo<CmsBanner> pageInfo = adminAdvertiesService.getAllAdvertise(adminPageInfoQuery);
        return R.ok(pageInfo);
    }

    /**
     * 新增广告(轮播图)
     * @return
     */
    @PostMapping("/advertise/create")
    public R createAdvertise(@RequestBody CmsBanner cmsBanner){
        int result = adminAdvertiesService.createAdvertise(cmsBanner);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 删除广告(轮播图)
     * @param id
     * @return
     */
    @PostMapping("/advertise/delete/{id}")
    public R deleteAdvertise(@PathVariable Long id){
        int result = adminAdvertiesService.deleteAdvertise(id);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 更新广告信息(轮播图)
     * @param id
     * @param cmsBanner
     * @return
     */
    @PostMapping("/advertise/update/{id}")
    public R updateAdvertise(@PathVariable Long id,@RequestBody CmsBanner cmsBanner){
        int result = adminAdvertiesService.updateAdvertise(id,cmsBanner);
        return result >= 1 ? R.ok() : R.error();
    }

    /**
     * 获取消息推送历史记录
     * @param adminPageInfoQuery
     * @return
     */
    @GetMapping("/notice/list")
    public R noticeList(AdminPageInfoQuery adminPageInfoQuery){
        PageInfo<CmsChatMessage> cmsChatMessagePageInfo = cmsNotifyService.getAllNotiify(adminPageInfoQuery);
        return R.ok(cmsChatMessagePageInfo);
    }

    /**
     * 推送系统消息
     * @param cmsNotify
     * @return
     */
    @PostMapping("/notice/create")
    public R createNotice(@RequestBody CmsNotify cmsNotify){
        int result = cmsNotifyService.createNotify(cmsNotify);
        return result >= 1 ? R.ok() : R.error();
    }
}
