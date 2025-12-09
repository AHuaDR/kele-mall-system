package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.mapper.CmsNotifyMapper;
import com.my.model.CmsChatMessage;
import com.my.model.CmsNotify;
import com.my.model.SysUser;
import com.my.query.AdminPageInfoQuery;
import com.my.security.AdminLoginUser;
import com.my.service.CmsNotifyService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 22:16
 * @description
 */
@Service
public class CmsNotifyServiceImpl implements CmsNotifyService {

    @Resource
    private CmsNotifyMapper cmsNotifyMapper;


    @Override
    public PageInfo<CmsChatMessage> getAllNotiify(AdminPageInfoQuery adminPageInfoQuery) {
        PageHelper.startPage(adminPageInfoQuery.getPageNum(), adminPageInfoQuery.getPageSize());
        List<CmsNotify> cmsNotifyList = cmsNotifyMapper.selectAllNotify();
        PageInfo pageInfo = new PageInfo(cmsNotifyList);
        return pageInfo;
    }

    @Override
    public int createNotify(CmsNotify cmsNotify) {

        // 1. 从 Spring Security 上下文获取 Authentication 对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 2. 获取 Principal (这就是你在 UserDetailsService 里返回的 AdminLoginUser 对象)
        AdminLoginUser loginUser = (AdminLoginUser) authentication.getPrincipal();
        // 3. 直接拿出 SysUser
        SysUser sysUser = loginUser.getSysUser();
        cmsNotify.setPublisherId(sysUser.getId());
        Date sendTime = cmsNotify.getSendTime();
        int result = new Date().compareTo(sendTime);
        if (result >= 0) {
            //nowDate在sendTime之后,代表已发送
            cmsNotify.setSendStatus(1);
        }  else {
            //nowDate在sendTime之前,代表未发送
            cmsNotify.setSendStatus(0);
        }
        return cmsNotifyMapper.insertSelective(cmsNotify);
    }
}
