package com.my.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 16:27
 * @description
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 1. 允许任何域名使用
        config.addAllowedOriginPattern("*");
        // 2. 允许任何头
        config.addAllowedHeader("*");
        // 3. 允许任何方法（post、get等）
        config.addAllowedMethod("*");
        // 4. 允许携带凭证
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
