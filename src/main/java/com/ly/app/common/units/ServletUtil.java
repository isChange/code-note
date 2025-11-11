package com.ly.app.common.units;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Description: Servlet工具类
 *
 * @author: liuyi
 * @date: 2024-08-01 09:10
 */
public class ServletUtil {

    /**
     * 获取attributes
     */
    private static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }


    /**
     * 获取Head参数
     */
    public static String getHeader(String header) {
        return getRequest().getHeader(header);
    }

    /**
     * 获取String参数, 并设置默认值
     */
    public static String getParameter(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return NumberUtils.toInt(getParameter(name));
    }

    /**
     * 获取Integer参数, 并设置默认值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return NumberUtils.toInt(getParameter(name), defaultValue);
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        return Boolean.getBoolean(getParameter(name));
    }

    /**
     * 将字符串渲染到客户端
     */
    @SneakyThrows(Exception.class)
    public static void renderString(HttpServletResponse response, String str) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(str);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 从多个可能的header中获取IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 可能包含多个IP地址，取第一个
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        // 直接获取远程地址
        ip = request.getRemoteAddr();
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        return null;
    }

    /**
     * 获取请求体（Body）内容
     *
     * @param request HttpServletRequest
     * @return 请求体内容
     */
    public static String getBody(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = request.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String body = stringBuilder.toString();
            return StringUtils.isNotBlank(body) ? body : null;
        } catch (IOException e) {
            return null;
        }
    }
}
