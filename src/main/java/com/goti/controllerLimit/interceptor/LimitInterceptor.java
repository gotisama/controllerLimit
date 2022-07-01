package com.goti.controllerLimit.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.goti.controllerLimit.annotation.Limit;
import com.goti.controllerLimit.util.IPUtils;
import com.goti.controllerLimit.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName LimitInterceptor
 * @Description 接口频率限制拦截器
 * @Author goti
 * @Date 11:41 2022/6/30
 * @Version 1.0
 **/
@Slf4j
public class LimitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        if (obj instanceof HandlerMethod) {
            String ipAddress = IPUtils.getClientIpAddress(request);
            String uri = request.getRequestURI();
            HandlerMethod handlerMethod = (HandlerMethod) obj;
            Method method = handlerMethod.getMethod();
            if (!method.isAnnotationPresent(Limit.class)) {
                log.info("未访问limit接口：{}", ipAddress);
                return true;
            } else {
                log.info("访问了limit接口：{}", ipAddress);
                Limit limit = method.getAnnotation(Limit.class);
                int num = limit.limit();
                long time = limit.time();
                String key = ipAddress + request.getRequestURI();
                boolean b = RedisUtils.checkLimit(key, num, time);
                if (b) {
                    log.info("允许访问：{}", ipAddress);
                    return true;
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("code", 400);
                    jsonObject.set("status", false);
                    jsonObject.set("data", "");
                    long l = RedisUtils.getLimitTime(key);
                    if (l == 0) {
                        log.info("限制访问：uri:{},ip:{}", uri, ipAddress);
                        jsonObject.set("msg", StrUtil.format("点击过快，请重试"));
                    } else {
                        log.info("限制访问：uri:{},ip:{}", uri, ipAddress);
                        jsonObject.set("msg", StrUtil.format("点击过快，请稍等{}秒后重试", l));
                    }
                    log.info("返回信息：{}", jsonObject.toString());
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().print(jsonObject.toString());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
