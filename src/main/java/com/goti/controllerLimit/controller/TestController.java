
package com.goti.controllerLimit.controller;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.goti.controllerLimit.annotation.Limit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName TestController
 * @Description 测试 控制器
 * @Author goti
 * @Date 11:41 2022/6/30
 * @Version 1.0
 **/
@RestController
@RequestMapping("web/test")
@Slf4j
public class TestController {

    /**
     * 限制访问接口 限制为5秒内2次
     *
     * @param request 请求对象
     * @return 返回结果
     */
    @Limit(limit = 2, time = 5)
    @GetMapping("/limitFive")
    public String limitFive(HttpServletRequest request) {
        log.info("uri:{},ip：{}", request.getRequestURI(), request.getRemoteAddr());
        return "ip : " + request.getRemoteAddr();
    }

    /**
     * 限制访问接口 限制为1秒内1次
     *
     * @param request 请求对象
     * @return 返回结果
     */
    @Limit
    @GetMapping("/limit")
    public String limit(HttpServletRequest request) {
        log.info("uri:{},ip：{}", request.getRequestURI(), request.getRemoteAddr());
        return "ip : " + request.getRemoteAddr();
    }

    /**
     * 限制访问接口 限制为1秒内1次
     *
     * @param request 请求对象
     * @return 返回结果
     */
    @GetMapping("/unLimit")
    public String unLimit(HttpServletRequest request) {
        log.info("uri:{},ip：{}", request.getRequestURI(), request.getRemoteAddr());
        return "ip : " + request.getRemoteAddr();
    }
}
