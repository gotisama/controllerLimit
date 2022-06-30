package com.goti.controllerLimit.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName IPUtils
 * @Description ip工具
 * @Author goti
 * @Date 11:57 2022/6/30
 * @Version 1.0
 **/
public class IPUtils {
    /**
     * 获取用户真实ip地址
     * @param request 请求对象
     * @return ip地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个IP为客户端IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
}
