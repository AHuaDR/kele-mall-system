package com.my.config;

import com.my.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest; // 👈 必须引入这个
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6.x 配置类
 * @author 王建广
 * @date 2025/12/1 22:36
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 开启 CORS (使用默认配置，它会自动寻找 CorsFilter Bean)
                .cors(Customizer.withDefaults())

                // 3. 无状态 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 路径权限配置
                .authorizeHttpRequests(auth -> auth
                        // === 静态资源放行 (修复 PatternParseException 的关键) ===
                        // PathRequest.toStaticResources() 自动放行 /css/**, /js/**, /images/**, /webjars/**, /favicon.ico
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        //上传图片地址放行
                        .requestMatchers("/files/**").permitAll()
                        // 手动放行根目录下的 html (例如 index.html)
                        .requestMatchers("/*.html").permitAll()

                        // Swagger/Knife4j 文档放行 (如果以后用到)
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()

                        // === B端 登录接口放行 ===
                        .requestMatchers("/admin/sso/login").permitAll()

                        // === C端 登录注册及公开接口放行 ===
                        .requestMatchers("/api/sso/login", "/api/sso/register").permitAll()
                        .requestMatchers("/api/product/**", "/api/home/**").permitAll()

                        // === 权限卡点 ===
                        // 后台接口：必须有 ADMIN 角色
                        .requestMatchers("/admin/**").hasAuthority("ROLE_SUPER_ADMIN")

                        // 前台接口：必须有 MEMBER 角色
                        .requestMatchers("/api/**").hasRole("MEMBER")

                        // 其他请求必须认证
                        .anyRequest().authenticated()
                );

        // 5. 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        // 1. 允许的来源 (前端地址)
        // 为了方便调试，先写 "*" (允许所有)。正式上线建议改为 "http://localhost:5173"
        configuration.addAllowedOriginPattern("*");

        // 2. 允许的请求头 (Authorization, Content-Type 等)
        configuration.addAllowedHeader("*");

        // 3. 允许的请求方法 (GET, POST, PUT, DELETE, OPTIONS)
        configuration.addAllowedMethod("*");

        // 4. 允许携带凭证 (Cookie 等)
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        // 对所有接口应用该配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}