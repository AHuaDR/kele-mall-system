package com.my.filter;

import com.my.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    @Qualifier("adminUserServiceImpl")
    private UserDetailsService adminUserDetailsService;
    @Resource
    @Qualifier("memberUserServiceImpl")
    private UserDetailsService memberUserDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authorizationHeader = request.getHeader("Authorization");

        // 调试日志 1
        // System.out.println(">>> Filter拦截请求: " + requestURI);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = null;

            try {
                username = jwtTokenUtil.getUserNameFromToken(token);
            } catch (Exception e) {
                System.out.println(">>> Token解析用户名失败: " + e.getMessage());
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;

                //================核心分流======================
                try {
                    if (requestURI.startsWith("/admin")) {
                        // System.out.println(">>> 进入Admin加载逻辑, 用户名: " + username);
                        userDetails = this.adminUserDetailsService.loadUserByUsername(username);
                    } else if (requestURI.startsWith("/api")) {
                        // System.out.println(">>> 进入Member加载逻辑, 用户名: " + username);
                        userDetails = this.memberUserDetailsService.loadUserByUsername(username);
                    } else {
                        System.out.println(">>> 警告: 未知路径前缀，未加载用户: " + requestURI);
                    }
                } catch (Exception e) {
                    // ⚠️ 这里是重点：如果报这个，说明数据库没查到人
                    System.out.println(">>> ❌ 用户加载异常 (数据库没这人?): " + e.getMessage());
                    e.printStackTrace(); // 打印完整堆栈
                }

                if (userDetails != null) {
                    boolean isValid = jwtTokenUtil.validateToken(token, userDetails);
                    if (isValid) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println(">>> 当前用户权限: " + userDetails.getAuthorities());
                        // System.out.println(">>> ✅ 认证成功！Context已设置");
                    } else {
                        System.out.println(">>> ❌ Token验证失败 (过期或不匹配)");
                    }
                } else {
                    System.out.println(">>> ❌ userDetails 为 null，无法认证");
                }
            }
        } else {
            // System.out.println(">>> Header为空或格式不对");
        }

        filterChain.doFilter(request, response);
    }
}