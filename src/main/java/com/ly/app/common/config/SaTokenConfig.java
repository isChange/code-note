package com.ly.app.common.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SaToken 配置类
 *
 * @author liu yi
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验
        registry.addInterceptor(new SaInterceptor(handle -> {
            if ("OPTIONS".equals(SaHolder.getRequest().getMethod())) {
                return;
            }
            // 指定需要拦截的路径
            SaRouter.match("/**")
                    // 排除不需要登录的路径
                    .notMatch("/user/login")
                    .notMatch("/user/register")
                    .notMatch("/doc.html",
                            "/api/doc.html",
                            "/swagger-resources/**",
                            "/v2/api-docs/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/favicon.ico")
                    // 校验登录
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
