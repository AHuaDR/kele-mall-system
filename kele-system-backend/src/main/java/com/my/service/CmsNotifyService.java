package com.my.service;

import com.github.pagehelper.PageInfo;
import com.my.model.CmsChatMessage;
import com.my.model.CmsNotify;
import com.my.query.AdminPageInfoQuery;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/5 22:16
 * @description
 */
public interface CmsNotifyService {
    PageInfo<CmsChatMessage> getAllNotiify(AdminPageInfoQuery adminPageInfoQuery);

    int createNotify(CmsNotify cmsNotify);
}
