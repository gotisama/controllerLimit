package com.goti.controllerLimit.config;

import com.goti.controllerLimit.interceptor.LimitInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName LimitConfiguration
 * @Description 接口拦截配置类
 * @Author goti
 * @Date 14:36 2022/6/30
 * @Version 1.0
 **/
@Configuration
public class LimitConfiguration implements WebMvcConfigurer {

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加接口频率限制拦截器，拦截所有请求
        registry.addInterceptor(new LimitInterceptor()).addPathPatterns("/**");
    }
}
