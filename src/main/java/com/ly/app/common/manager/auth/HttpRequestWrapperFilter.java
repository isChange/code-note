package com.ly.app.common.manager.auth;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 请求包装过滤器
 * @createDate：2025/6/19 15:33
 * @email liuyia2022@163.com
 */
@Order(1)
@Component
public class HttpRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String contentType = servletRequest.getHeader(Header.CONTENT_TYPE.getValue());
            if (ContentType.JSON.getValue().equals(contentType)) {
                // TODO 可以再细粒度一些，只有需要进行空间权限校验的接口才需要包一层
                chain.doFilter(new RequestWrapper(servletRequest), response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }

}

