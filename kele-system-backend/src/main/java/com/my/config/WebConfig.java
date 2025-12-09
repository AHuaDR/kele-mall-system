package com.my.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/3 18:24
 * @description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    // 读取 yml 文件中的 file.upload-path 配置
    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 核心逻辑：
        // 1. 拦截所有 /files/ 开头的请求
        // 2. 映射到本地磁盘路径 (必须加 file: 前缀)
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
